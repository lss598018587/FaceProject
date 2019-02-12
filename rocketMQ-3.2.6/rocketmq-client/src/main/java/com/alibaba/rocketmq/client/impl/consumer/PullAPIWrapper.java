/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.rocketmq.client.impl.consumer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;

import com.alibaba.rocketmq.client.VirtualEnvUtil;
import com.alibaba.rocketmq.client.consumer.PullCallback;
import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.consumer.PullStatus;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.hook.FilterMessageContext;
import com.alibaba.rocketmq.client.hook.FilterMessageHook;
import com.alibaba.rocketmq.client.impl.CommunicationMode;
import com.alibaba.rocketmq.client.impl.FindBrokerResult;
import com.alibaba.rocketmq.client.impl.factory.MQClientInstance;
import com.alibaba.rocketmq.client.log.ClientLogger;
import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.UtilAll;
import com.alibaba.rocketmq.common.message.MessageAccessor;
import com.alibaba.rocketmq.common.message.MessageConst;
import com.alibaba.rocketmq.common.message.MessageDecoder;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.common.protocol.header.PullMessageRequestHeader;
import com.alibaba.rocketmq.common.protocol.heartbeat.SubscriptionData;
import com.alibaba.rocketmq.common.protocol.route.TopicRouteData;
import com.alibaba.rocketmq.common.sysflag.PullSysFlag;
import com.alibaba.rocketmq.remoting.exception.RemotingException;


/**
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2013-7-24
 */
public class PullAPIWrapper {
    private final Logger log = ClientLogger.getLog();

    /**
     * 消息队列 与 拉取Broker 的映射
     * 当拉取消息时，会通过该映射获取拉取请求对应的Broker
     */
    private ConcurrentHashMap<MessageQueue, AtomicLong/* brokerId */> pullFromWhichNodeTable =
            new ConcurrentHashMap<MessageQueue, AtomicLong>(32);

    private final MQClientInstance mQClientFactory;
    private final String consumerGroup;
    private final boolean unitMode;
    /**
     * 是否使用默认Broker
     */
    private volatile boolean connectBrokerByUser = false;
    /**
     * 默认Broker编号
     */
    private volatile long defaultBrokerId = MixAll.MASTER_ID;


    public PullAPIWrapper(MQClientInstance mQClientFactory, String consumerGroup, boolean unitMode) {
        this.mQClientFactory = mQClientFactory;
        this.consumerGroup = consumerGroup;
        this.unitMode = unitMode;
    }


    public void updatePullFromWhichNode(final MessageQueue mq, final long brokerId) {
        AtomicLong suggest = this.pullFromWhichNodeTable.get(mq);
        if (null == suggest) {
            this.pullFromWhichNodeTable.put(mq, new AtomicLong(brokerId));
        }
        else {
            suggest.set(brokerId);
        }
    }

    private Random random = new Random(System.currentTimeMillis());


    public int randomNum() {
        int value = random.nextInt();
        if (value < 0) {
            value = Math.abs(value);
            if (value < 0)
                value = 0;
        }
        return value;
    }

    private String computPullFromWhichFilterServer(final String topic, final String brokerAddr)
            throws MQClientException {
        ConcurrentHashMap<String, TopicRouteData> topicRouteTable = this.mQClientFactory.getTopicRouteTable();
        if (topicRouteTable != null) {
            TopicRouteData topicRouteData = topicRouteTable.get(topic);
            List<String> list = topicRouteData.getFilterServerTable().get(brokerAddr);

            if (list != null && !list.isEmpty()) {
                return list.get(randomNum() % list.size());
            }
        }

        throw new MQClientException("Find Filter Server Failed, Broker Addr: " + brokerAddr + " topic: "
                + topic, null);
    }



    /**
     *
     * 功能描述: 对消息的具体字节流进行了反序列化，并且根据订阅的tag在这里完成对于消息的过滤。
     *
     *
     * 处理拉取结果
     * 1. 更新消息队列拉取消息Broker编号的映射
     * 2. 解析消息，并根据订阅信息消息tagCode匹配合适消息
     *
     * @param mq 消息队列
     * @param pullResult 拉取结果
     * @param subscriptionData 订阅信息
     * @return 拉取结果
     */
    public PullResult processPullResult(final MessageQueue mq, final PullResult pullResult,
            final SubscriptionData subscriptionData) {
        final String projectGroupPrefix = this.mQClientFactory.getMQClientAPIImpl().getProjectGroupPrefix();
        PullResultExt pullResultExt = (PullResultExt) pullResult;

        // 更新消息队列拉取消息Broker编号的映射
        this.updatePullFromWhichNode(mq, pullResultExt.getSuggestWhichBrokerId());

        // 解析消息，并根据订阅信息消息tagCode匹配合适消息
        if (PullStatus.FOUND == pullResult.getPullStatus()) {
            // 解析消息
            ByteBuffer byteBuffer = ByteBuffer.wrap(pullResultExt.getMessageBinary());
            List<MessageExt> msgList = MessageDecoder.decodes(byteBuffer);

            // 根据订阅信息消息tagCode匹配合适消息
            List<MessageExt> msgListFilterAgain = msgList;
            if (!subscriptionData.getTagsSet().isEmpty() && !subscriptionData.isClassFilterMode()) {
                msgListFilterAgain = new ArrayList<MessageExt>(msgList.size());
                for (MessageExt msg : msgList) {
                    if (msg.getTags() != null) {
                        if (subscriptionData.getTagsSet().contains(msg.getTags())) {
                            msgListFilterAgain.add(msg);
                        }
                    }
                }
            }

            if (this.hasHook()) {
                FilterMessageContext filterMessageContext = new FilterMessageContext();
                filterMessageContext.setUnitMode(unitMode);
                filterMessageContext.setMsgList(msgListFilterAgain);
                this.executeHook(filterMessageContext);
            }

            if (!UtilAll.isBlank(projectGroupPrefix)) {
                subscriptionData.setTopic(VirtualEnvUtil.clearProjectGroup(subscriptionData.getTopic(),
                    projectGroupPrefix));
                mq.setTopic(VirtualEnvUtil.clearProjectGroup(mq.getTopic(), projectGroupPrefix));
                // 设置消息队列当前最小/最大位置到消息拓展字段
                for (MessageExt msg : msgListFilterAgain) {
                    msg.setTopic(VirtualEnvUtil.clearProjectGroup(msg.getTopic(), projectGroupPrefix));

                    MessageAccessor.putProperty(msg, MessageConst.PROPERTY_MIN_OFFSET,
                        Long.toString(pullResult.getMinOffset()));
                    MessageAccessor.putProperty(msg, MessageConst.PROPERTY_MAX_OFFSET,
                        Long.toString(pullResult.getMaxOffset()));
                }
            }
            else {
                for (MessageExt msg : msgListFilterAgain) {
                    MessageAccessor.putProperty(msg, MessageConst.PROPERTY_MIN_OFFSET,
                        Long.toString(pullResult.getMinOffset()));
                    MessageAccessor.putProperty(msg, MessageConst.PROPERTY_MAX_OFFSET,
                        Long.toString(pullResult.getMaxOffset()));
                }
            }
            // 设置消息列表
            pullResultExt.setMsgFoundList(msgListFilterAgain);
        }
        // 清空消息二进制数组
        pullResultExt.setMessageBinary(null);

        return pullResult;
    }

    /**
     * 计算消息队列拉取消息对应的Broker编号
     *
     * @param mq 消息队列
     * @return Broker编号
     */
    public long recalculatePullFromWhichNode(final MessageQueue mq) {
        // 若开启默认Broker开关，则返回默认Broker编号
        if (this.isConnectBrokerByUser()) {
            return this.defaultBrokerId;
        }
        // 若消息队列映射拉取Broker存在，则返回映射Broker编号
        AtomicLong suggest = this.pullFromWhichNodeTable.get(mq);
        if (suggest != null) {
            return suggest.get();
        }
        // 返回Broker主节点编号
        return MixAll.MASTER_ID;
    }
    /**
     * 拉取消息核心方法
     *
     * @param mq 消息队列
     * @param subExpression 订阅表达式
     * @param subVersion 订阅版本号
     * @param offset 拉取队列开始位置
     * @param maxNums 拉取消息数量
     * @param sysFlag 拉取请求系统标识
     * @param commitOffset 提交消费进度
     * @param brokerSuspendMaxTimeMillis broker挂起请求最大时间
     * @param timeoutMillis 请求broker超时时长
     * @param communicationMode 通讯模式
     * @param pullCallback 拉取回调
     * @return 拉取消息结果。只有通讯模式为同步时，才返回结果，否则返回null。
     * @throws MQClientException 当寻找不到 broker 时，或发生其他client异常
     * @throws RemotingException 当远程调用发生异常时
     * @throws MQBrokerException 当 broker 发生异常时。只有通讯模式为同步时才会发生该异常。
     * @throws InterruptedException 当发生中断异常时
     */

    public PullResult pullKernelImpl(//
            final MessageQueue mq,// 1
            final String subExpression,// 2
            final long subVersion,// 3
            final long offset,// 4
            final int maxNums,// 5
            final int sysFlag,// 6
            final long commitOffset,// 7
            final long brokerSuspendMaxTimeMillis,// 8
            final long timeoutMillis,// 9
            final CommunicationMode communicationMode,// 10
            final PullCallback pullCallback// 11
    ) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        // 获取Broker信息
        FindBrokerResult findBrokerResult =
                this.mQClientFactory.findBrokerAddressInSubscribe(mq.getBrokerName(),
                    this.recalculatePullFromWhichNode(mq), false);
        if (null == findBrokerResult) {
            this.mQClientFactory.updateTopicRouteInfoFromNameServer(mq.getTopic());
            findBrokerResult =
                    this.mQClientFactory.findBrokerAddressInSubscribe(mq.getBrokerName(),
                        this.recalculatePullFromWhichNode(mq), false);
        }
        // 请求拉取消息
        if (findBrokerResult != null) {
            int sysFlagInner = sysFlag;

            if (findBrokerResult.isSlave()) {
                sysFlagInner = PullSysFlag.clearCommitOffsetFlag(sysFlagInner);
            }

            PullMessageRequestHeader requestHeader = new PullMessageRequestHeader();
            requestHeader.setConsumerGroup(this.consumerGroup);
            requestHeader.setTopic(mq.getTopic());
            requestHeader.setQueueId(mq.getQueueId());
            requestHeader.setQueueOffset(offset);
            requestHeader.setMaxMsgNums(maxNums);
            requestHeader.setSysFlag(sysFlagInner);
            requestHeader.setCommitOffset(commitOffset);
            requestHeader.setSuspendTimeoutMillis(brokerSuspendMaxTimeMillis);
            requestHeader.setSubscription(subExpression);
            requestHeader.setSubVersion(subVersion);

            String brokerAddr = findBrokerResult.getBrokerAddr();
            if (PullSysFlag.hasClassFilterFlag(sysFlagInner)) {
                brokerAddr = computPullFromWhichFilterServer(mq.getTopic(), brokerAddr);
            }

            PullResult pullResult = this.mQClientFactory.getMQClientAPIImpl().pullMessage(//
                brokerAddr,//
                requestHeader,//
                timeoutMillis,//
                communicationMode,//
                pullCallback);

            return pullResult;
        }
        // Broker信息不存在，则抛出异常
        throw new MQClientException("The broker[" + mq.getBrokerName() + "] not exist", null);
    }

    private ArrayList<FilterMessageHook> filterMessageHookList = new ArrayList<FilterMessageHook>();


    public boolean hasHook() {
        return !this.filterMessageHookList.isEmpty();
    }


    public void registerFilterMessageHook(ArrayList<FilterMessageHook> filterMessageHookList) {
        this.filterMessageHookList = filterMessageHookList;
    }


    public void executeHook(final FilterMessageContext context) {
        if (!this.filterMessageHookList.isEmpty()) {
            for (FilterMessageHook hook : this.filterMessageHookList) {
                try {
                    hook.filterMessage(context);
                }
                catch (Throwable e) {
                    log.error("execute hook error. hookName={}", hook.hookName());
                }
            }
        }
    }


    public long getDefaultBrokerId() {
        return defaultBrokerId;
    }


    public void setDefaultBrokerId(long defaultBrokerId) {
        this.defaultBrokerId = defaultBrokerId;
    }


    public boolean isConnectBrokerByUser() {
        return connectBrokerByUser;
    }


    public void setConnectBrokerByUser(boolean connectBrokerByUser) {
        this.connectBrokerByUser = connectBrokerByUser;

    }
}
