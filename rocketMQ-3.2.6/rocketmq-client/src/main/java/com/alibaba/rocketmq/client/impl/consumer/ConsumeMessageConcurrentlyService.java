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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.hook.ConsumeMessageContext;
import com.alibaba.rocketmq.client.log.ClientLogger;
import com.alibaba.rocketmq.client.stat.ConsumerStatsManager;
import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.ThreadFactoryImpl;
import com.alibaba.rocketmq.common.message.MessageConst;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.common.protocol.body.CMResult;
import com.alibaba.rocketmq.common.protocol.body.ConsumeMessageDirectlyResult;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;


/**
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2013-7-24
 */
public class ConsumeMessageConcurrentlyService implements ConsumeMessageService {
    private static final Logger log = ClientLogger.getLog();
    private final DefaultMQPushConsumerImpl defaultMQPushConsumerImpl;
    private final DefaultMQPushConsumer defaultMQPushConsumer;
    private final MessageListenerConcurrently messageListener;
    //消费线程池队列
    private final BlockingQueue<Runnable> consumeRequestQueue;
    //消费线程池
    private final ThreadPoolExecutor consumeExecutor;
    private final String consumerGroup;

    private final ScheduledExecutorService scheduledExecutorService;


    public ConsumeMessageConcurrentlyService(DefaultMQPushConsumerImpl defaultMQPushConsumerImpl,
            MessageListenerConcurrently messageListener) {
        this.defaultMQPushConsumerImpl = defaultMQPushConsumerImpl;
        this.messageListener = messageListener;

        this.defaultMQPushConsumer = this.defaultMQPushConsumerImpl.getDefaultMQPushConsumer();
        this.consumerGroup = this.defaultMQPushConsumer.getConsumerGroup();
        this.consumeRequestQueue = new LinkedBlockingQueue<Runnable>();

        this.consumeExecutor = new ThreadPoolExecutor(//
            this.defaultMQPushConsumer.getConsumeThreadMin(),//
            this.defaultMQPushConsumer.getConsumeThreadMax(),//
            1000 * 60,//
            TimeUnit.MILLISECONDS,//
            this.consumeRequestQueue,//
            new ThreadFactoryImpl("ConsumeMessageThread_"));

        this.scheduledExecutorService =
                Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl(
                    "ConsumeMessageScheduledThread_"));
    }


    public void start() {
    }


    public void shutdown() {
        this.scheduledExecutorService.shutdown();
        this.consumeExecutor.shutdown();
    }


    public ConsumerStatsManager getConsumerStatsManager() {
        return this.defaultMQPushConsumerImpl.getConsumerStatsManager();
    }

    class ConsumeRequest implements Runnable {
        //消费消息列表
        private final List<MessageExt> msgs;
        //消息处理队列
        private final ProcessQueue processQueue;
        //消息队列
        private final MessageQueue messageQueue;


        public ConsumeRequest(List<MessageExt> msgs, ProcessQueue processQueue, MessageQueue messageQueue) {
            this.msgs = msgs;
            this.processQueue = processQueue;
            this.messageQueue = messageQueue;
        }


        @Override
        public void run() {
            // 废弃队列不进行消费
            if (this.processQueue.isDropped()) {
                log.info("the message queue not be able to consume, because it's dropped {}",
                    this.messageQueue);
                return;
            }
            // 监听器
            MessageListenerConcurrently listener = ConsumeMessageConcurrentlyService.this.messageListener;
            // 消费Context
            ConsumeConcurrentlyContext context = new ConsumeConcurrentlyContext(messageQueue);
            // 消费结果状态
            ConsumeConcurrentlyStatus status = null;

            ConsumeMessageContext consumeMessageContext = null;
            if (ConsumeMessageConcurrentlyService.this.defaultMQPushConsumerImpl.hasHook()) {
                consumeMessageContext = new ConsumeMessageContext();
                consumeMessageContext
                    .setConsumerGroup(ConsumeMessageConcurrentlyService.this.defaultMQPushConsumer
                        .getConsumerGroup());
                consumeMessageContext.setMq(messageQueue);
                consumeMessageContext.setMsgList(msgs);
                consumeMessageContext.setSuccess(false);
                ConsumeMessageConcurrentlyService.this.defaultMQPushConsumerImpl
                    .executeHookBefore(consumeMessageContext);
            }

            long beginTimestamp = System.currentTimeMillis();

            try {
//                if(msgs.get(0).getTopic().equals("%RETRY%miaomiaoTest")){
//                    System.out.println("xixixixix>>>>>"+msgs.get(0));
//                }
                // 当消息为重试消息，设置Topic为原始Topic
                ConsumeMessageConcurrentlyService.this.resetRetryTopic(msgs);
                // 进行消费
                status = listener.consumeMessage(Collections.unmodifiableList(msgs), context);
            }
            catch (Throwable e) {
                log.warn("consumeMessage exception: {} Group: {} Msgs: {} MQ: {}",//
                    RemotingHelper.exceptionSimpleDesc(e),//
                    ConsumeMessageConcurrentlyService.this.consumerGroup,//
                    msgs,//
                    messageQueue);
            }
            long consumeRT = System.currentTimeMillis() - beginTimestamp;

            // 消费结果状态为空时，则设置为稍后重新消费
            if (null == status) {
                log.warn("consumeMessage return null, Group: {} Msgs: {} MQ: {}",//
                    ConsumeMessageConcurrentlyService.this.consumerGroup,//
                    msgs,//
                    messageQueue);
                status = ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

            // Hook
            if (ConsumeMessageConcurrentlyService.this.defaultMQPushConsumerImpl.hasHook()) {
                consumeMessageContext.setStatus(status.toString());
                consumeMessageContext.setSuccess(ConsumeConcurrentlyStatus.CONSUME_SUCCESS == status);
                ConsumeMessageConcurrentlyService.this.defaultMQPushConsumerImpl
                    .executeHookAfter(consumeMessageContext);
            }

            // 统计
            ConsumeMessageConcurrentlyService.this.getConsumerStatsManager().incConsumeRT(
                ConsumeMessageConcurrentlyService.this.consumerGroup, messageQueue.getTopic(), consumeRT);

            // 处理消费结果
            if (!processQueue.isDropped()) {
                ConsumeMessageConcurrentlyService.this.processConsumeResult(status, context, this);
            }
            else {
                log.warn(
                    "processQueue is dropped without process consume result. messageQueue={}, msgTreeMap={}, msgs={}",
                    new Object[] { messageQueue, processQueue.getMsgTreeMap(), msgs });
            }
        }


        public List<MessageExt> getMsgs() {
            return msgs;
        }


        public ProcessQueue getProcessQueue() {
            return processQueue;
        }


        public MessageQueue getMessageQueue() {
            return messageQueue;
        }
    }


    public boolean sendMessageBack(final MessageExt msg, final ConsumeConcurrentlyContext context) {
        int delayLevel = context.getDelayLevelWhenNextConsume();
        try {
            this.defaultMQPushConsumerImpl.sendMessageBack(msg, delayLevel, context.getMessageQueue()
                .getBrokerName());
            return true;
        }
        catch (Exception e) {
            log.error("sendMessageBack exception, group: " + this.consumerGroup + " msg: " + msg.toString(),
                e);
        }

        return false;
    }


    /**
     *
     * 功能描述: 如果消息消费成功，则将在这里进行对ack也就是消费成功的消息数量进行赋值。
     *
     * 在广播模式下，即使消息消费失败，也只是进行日志记录，并没有别的操作。
     *
     * 在集群模式下，如果消息消费失败，将会先尝试将消息发送回broker，如果发送回给broker也失败了，
     * 那么将会将这条消息在一定时间后重新尝试消费，否则就从消息失败数组中移除这个消息。
     *
     * 最后，更新消息队列的消费进度。PushConsumer的消息消费结束。
     *
     * @auther: miaomiao
     * @date: 19/1/23 下午12:03
     */
    public void processConsumeResult(//
            final ConsumeConcurrentlyStatus status, //
            final ConsumeConcurrentlyContext context, //
            final ConsumeRequest consumeRequest//
    ) {
        int ackIndex = context.getAckIndex();

        //消息为空，直接返回
        if (consumeRequest.getMsgs().isEmpty())
            return;

        // 计算从consumeRequest.msgs[0]到consumeRequest.msgs[ackIndex]的消息消费成功
        switch (status) {
        case CONSUME_SUCCESS:
            if (ackIndex >= consumeRequest.getMsgs().size()) {
                ackIndex = consumeRequest.getMsgs().size() - 1;
            }
            // 统计成功/失败数量
            int ok = ackIndex + 1;
            int failed = consumeRequest.getMsgs().size() - ok;
            //统计时间，和消费次数的累加
            this.getConsumerStatsManager().incConsumeOKTPS(consumerGroup,
                consumeRequest.getMessageQueue().getTopic(), ok);
            //统计失败
            this.getConsumerStatsManager().incConsumeFailedTPS(consumerGroup,
                consumeRequest.getMessageQueue().getTopic(), failed);
            break;
        case RECONSUME_LATER:
            ackIndex = -1;
            // 统计成功/失败数量
            this.getConsumerStatsManager().incConsumeFailedTPS(consumerGroup,
                consumeRequest.getMessageQueue().getTopic(), consumeRequest.getMsgs().size());
            break;
        default:
            break;
        }

        // 处理消费失败的消息
        switch (this.defaultMQPushConsumer.getMessageModel()) {
        case BROADCASTING: // 广播模式，无论是否消费失败，不发回消息到Broker，只打印Log
            for (int i = ackIndex + 1; i < consumeRequest.getMsgs().size(); i++) {
                MessageExt msg = consumeRequest.getMsgs().get(i);
                log.warn("BROADCASTING, the message consume failed, drop it, {}", msg.toString());
            }
            break;
        case CLUSTERING:
            // 发回消息失败到Broker。
            List<MessageExt> msgBackFailed = new ArrayList<MessageExt>(consumeRequest.getMsgs().size());
            for (int i = ackIndex + 1; i < consumeRequest.getMsgs().size(); i++) {
                MessageExt msg = consumeRequest.getMsgs().get(i);
                boolean result = this.sendMessageBack(msg, context);
                if (!result) {
                    msg.setReconsumeTimes(msg.getReconsumeTimes() + 1);
                    msgBackFailed.add(msg);
                }
            }
            // 发回Broker失败的消息，直接提交延迟重新消费
            if (!msgBackFailed.isEmpty()) {
                consumeRequest.getMsgs().removeAll(msgBackFailed);
                //放入队列重新进行下次消费
                this.submitConsumeRequestLater(msgBackFailed, consumeRequest.getProcessQueue(),
                    consumeRequest.getMessageQueue());
            }
            break;
        default:
            break;
        }
        // 移除消费成功消息，并更新最新消费进度
        long offset = consumeRequest.getProcessQueue().removeMessage(consumeRequest.getMsgs());
        if (offset >= 0) {
//            MessageExt ext =  consumeRequest.getMsgs().get(0);
//            System.out.println("11111111进来更新offset："+offset+"，msg消息里的topic:"+ext.getTopic()+",msg消息里的queueid:"+ext.getQueueId()+",msg消息里的offset:"+ext.getQueueOffset());
            this.defaultMQPushConsumerImpl.getOffsetStore().updateOffset(consumeRequest.getMessageQueue(),
                offset, true);
        }
    }


    private void submitConsumeRequestLater(//
            final List<MessageExt> msgs, //
            final ProcessQueue processQueue, //
            final MessageQueue messageQueue//
    ) {

        this.scheduledExecutorService.schedule(new Runnable() {

            @Override
            public void run() {
                ConsumeMessageConcurrentlyService.this.submitConsumeRequest(msgs, processQueue, messageQueue,
                    true);
            }
        }, 5000, TimeUnit.MILLISECONDS);
    }


    /**
     *
     * 功能描述: 在消费消息的一开始会判断消息个数与一次最高允许消费的消息条数，如果小于，
     * 那直接回封装成ConsumeRequest消费请求丢入线程池中进行消费，而一旦大于，将会按照一次最大批量处理条数进行分次处理。
     *
     * @auther: miaomiao
     * @date: 19/1/23 上午11:40
     */
    @Override
    public void submitConsumeRequest(//
            final List<MessageExt> msgs, //
            final ProcessQueue processQueue, //
            final MessageQueue messageQueue, //
            final boolean dispatchToConsume) {
        final int consumeBatchSize = this.defaultMQPushConsumer.getConsumeMessageBatchMaxSize();
        /**
         * 提交消息小于批量消息数，直接提交消费请求
         */
        if (msgs.size() <= consumeBatchSize) {
            ConsumeRequest consumeRequest = new ConsumeRequest(msgs, processQueue, messageQueue);
            this.consumeExecutor.submit(consumeRequest);
        }
        else { // 提交消息大于批量消息数，进行分拆成多个消费请求
            for (int total = 0; total < msgs.size();) {
                // 计算当前拆分请求包含的消息
                List<MessageExt> msgThis = new ArrayList<MessageExt>(consumeBatchSize);
                for (int i = 0; i < consumeBatchSize; i++, total++) {
                    if (total < msgs.size()) {
                        msgThis.add(msgs.get(total));
                    }
                    else {
                        break;
                    }
                }
                // 提交拆分消费请求
                ConsumeRequest consumeRequest = new ConsumeRequest(msgThis, processQueue, messageQueue);
                this.consumeExecutor.submit(consumeRequest);
            }
        }
    }


    @Override
    public void updateCorePoolSize(int corePoolSize) {
        if (corePoolSize > 0 //
                && corePoolSize <= Short.MAX_VALUE //
                && corePoolSize < this.defaultMQPushConsumer.getConsumeThreadMax()) {
            this.consumeExecutor.setCorePoolSize(corePoolSize);
        }
    }


    @Override
    public void incCorePoolSize() {
        long corePoolSize = this.consumeExecutor.getCorePoolSize();
        if (corePoolSize < this.defaultMQPushConsumer.getConsumeThreadMax()) {
            this.consumeExecutor.setCorePoolSize(this.consumeExecutor.getCorePoolSize() + 1);
        }

        log.info("incCorePoolSize Concurrently from {} to {}, ConsumerGroup: {}", //
            corePoolSize,//
            this.consumeExecutor.getCorePoolSize(),//
            this.consumerGroup);
    }


    @Override
    public void decCorePoolSize() {
        long corePoolSize = this.consumeExecutor.getCorePoolSize();
        if (corePoolSize > this.defaultMQPushConsumer.getConsumeThreadMin()) {
            this.consumeExecutor.setCorePoolSize(this.consumeExecutor.getCorePoolSize() - 1);
        }

        log.info("decCorePoolSize Concurrently from {} to {}, ConsumerGroup: {}", //
            corePoolSize,//
            this.consumeExecutor.getCorePoolSize(),//
            this.consumerGroup);
    }


    @Override
    public int getCorePoolSize() {
        return this.consumeExecutor.getCorePoolSize();
    }


    @Override
    public ConsumeMessageDirectlyResult consumeMessageDirectly(MessageExt msg, String brokerName) {
        ConsumeMessageDirectlyResult result = new ConsumeMessageDirectlyResult();
        result.setOrder(false);
        result.setAutoCommit(true);

        List<MessageExt> msgs = new ArrayList<MessageExt>();
        msgs.add(msg);
        MessageQueue mq = new MessageQueue();
        mq.setBrokerName(brokerName);
        mq.setTopic(msg.getTopic());
        mq.setQueueId(msg.getQueueId());

        ConsumeConcurrentlyContext context = new ConsumeConcurrentlyContext(mq);

        this.resetRetryTopic(msgs);

        final long beginTime = System.currentTimeMillis();

        log.info("consumeMessageDirectly receive new messge: {}", msg);

        try {
            ConsumeConcurrentlyStatus status = this.messageListener.consumeMessage(msgs, context);
            if (status != null) {
                switch (status) {
                case CONSUME_SUCCESS:
                    result.setConsumeResult(CMResult.CR_SUCCESS);
                    break;
                case RECONSUME_LATER:
                    result.setConsumeResult(CMResult.CR_LATER);
                    break;
                default:
                    break;
                }
            }
            else {
                result.setConsumeResult(CMResult.CR_RETURN_NULL);
            }
        }
        catch (Throwable e) {
            result.setConsumeResult(CMResult.CR_THROW_EXCEPTION);
            result.setRemark(RemotingHelper.exceptionSimpleDesc(e));

            log.warn(String.format("consumeMessageDirectly exception: %s Group: %s Msgs: %s MQ: %s",//
                RemotingHelper.exceptionSimpleDesc(e),//
                ConsumeMessageConcurrentlyService.this.consumerGroup,//
                msgs,//
                mq), e);
        }

        result.setSpentTimeMills(System.currentTimeMillis() - beginTime);

        log.info("consumeMessageDirectly Result: {}", result);

        return result;
    }


    public void resetRetryTopic(final List<MessageExt> msgs) {
        final String groupTopic = MixAll.getRetryTopic(consumerGroup);
        for (MessageExt msg : msgs) {
            //这里拿出来的是原始的topic
            String retryTopic = msg.getProperty(MessageConst.PROPERTY_RETRY_TOPIC);
            if (retryTopic != null && groupTopic.equals(msg.getTopic())) {
                msg.setTopic(retryTopic);
            }
        }
    }
}
