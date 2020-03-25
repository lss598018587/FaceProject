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
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.client.hook.ConsumeMessageContext;
import com.alibaba.rocketmq.client.log.ClientLogger;
import com.alibaba.rocketmq.client.stat.ConsumerStatsManager;
import com.alibaba.rocketmq.common.ThreadFactoryImpl;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.common.protocol.body.CMResult;
import com.alibaba.rocketmq.common.protocol.body.ConsumeMessageDirectlyResult;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;


/**
 *
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2013-6-27
 */
public class ConsumeMessageOrderlyService implements ConsumeMessageService {
    private static final Logger log = ClientLogger.getLog();
    /**
     * 消费任务一次运行的最大时间。可以通过-Drocketmq.client.maxTimeConsumeContinuously来设置，默认为60s。
     */
    private final static long MaxTimeConsumeContinuously = Long.parseLong(System.getProperty(
        "rocketmq.client.maxTimeConsumeContinuously", "60000"));

    private volatile boolean stopped = false;

    /**
     * 消息消费者实现类。
     */
    private final DefaultMQPushConsumerImpl defaultMQPushConsumerImpl;
    /**
     * 消息消费者。
     */
    private final DefaultMQPushConsumer defaultMQPushConsumer;
    /**
     * 顺序消息消费监听器。
     */
    private final MessageListenerOrderly messageListener;
    /**
     * 消息消费任务。
     */
    private final BlockingQueue<Runnable> consumeRequestQueue;
    /**
     * ：消息消费线程池。
     */
    private final ThreadPoolExecutor consumeExecutor;
    /**
     * 消息消费组。
     */
    private final String consumerGroup;
    /**
     * 消息消费队列锁，
     */
    private final MessageQueueLock messageQueueLock = new MessageQueueLock();

    private final ScheduledExecutorService scheduledExecutorService;


    public ConsumeMessageOrderlyService(DefaultMQPushConsumerImpl defaultMQPushConsumerImpl,
            MessageListenerOrderly messageListener) {
        this.defaultMQPushConsumerImpl = defaultMQPushConsumerImpl;
        this.messageListener = messageListener;

        this.defaultMQPushConsumer = this.defaultMQPushConsumerImpl.getDefaultMQPushConsumer();
        this.consumerGroup = this.defaultMQPushConsumer.getConsumerGroup();
        //创建任务拉取队列，注意，这里使用的是无界队列。
        this.consumeRequestQueue = new LinkedBlockingQueue<Runnable>();

        //创建消费者消费线程池，注意由于消息任务队列consumeRequestQueue使用的是无界队列，故线程池中最大线程数量取自 consumeThreadMin。
        this.consumeExecutor = new ThreadPoolExecutor(//
            this.defaultMQPushConsumer.getConsumeThreadMin(),//
            this.defaultMQPushConsumer.getConsumeThreadMax(),//
            1000 * 60,//
            TimeUnit.MILLISECONDS,//
            this.consumeRequestQueue,//
            new ThreadFactoryImpl("ConsumeMessageThread_"));

        //创建调度线程，该线程主要调度定时任务，延迟延迟消费
        this.scheduledExecutorService =
                Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl(
                    "ConsumeMessageScheduledThread_"));
    }


    public void start() {
        if (MessageModel.CLUSTERING.equals(ConsumeMessageOrderlyService.this.defaultMQPushConsumerImpl
            .messageModel())) {
            this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    ConsumeMessageOrderlyService.this.lockMQPeriodically();
                }
            }, 1000 * 1, ProcessQueue.RebalanceLockInterval, TimeUnit.MILLISECONDS);
        }
    }


    public void shutdown() {
        this.stopped = true;
        this.scheduledExecutorService.shutdown();
        this.consumeExecutor.shutdown();
        if (MessageModel.CLUSTERING.equals(this.defaultMQPushConsumerImpl.messageModel())) {
            this.unlockAllMQ();
        }
    }


    public synchronized void unlockAllMQ() {
        this.defaultMQPushConsumerImpl.getRebalanceImpl().unlockAll(false);
    }


    public synchronized void lockMQPeriodically() {
        if (!this.stopped) {
            this.defaultMQPushConsumerImpl.getRebalanceImpl().lockAll();
        }
    }


    public synchronized boolean lockOneMQ(final MessageQueue mq) {
        if (!this.stopped) {
            return this.defaultMQPushConsumerImpl.getRebalanceImpl().lock(mq);
        }

        return false;
    }


    public void tryLockLaterAndReconsume(final MessageQueue mq, final ProcessQueue processQueue,
            final long delayMills) {
        this.scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                boolean lockOK = ConsumeMessageOrderlyService.this.lockOneMQ(mq);
                if (lockOK) {
                    ConsumeMessageOrderlyService.this.submitConsumeRequestLater(processQueue, mq, 10);
                }
                else {
                    ConsumeMessageOrderlyService.this.submitConsumeRequestLater(processQueue, mq, 3000);
                }
            }
        }, delayMills, TimeUnit.MILLISECONDS);
    }


    public ConsumerStatsManager getConsumerStatsManager() {
        return this.defaultMQPushConsumerImpl.getConsumerStatsManager();
    }

    class ConsumeRequest implements Runnable {
        /**
         * 消息处理队列
         */
        private final ProcessQueue processQueue;
        /**
         * 消息队列
         */
        private final MessageQueue messageQueue;


        public ConsumeRequest(ProcessQueue processQueue, MessageQueue messageQueue) {
            this.processQueue = processQueue;
            this.messageQueue = messageQueue;
        }


        /**
         * order顺序消费
         */
        @Override
        public void run() {
            if (this.processQueue.isDropped()) {
                log.warn("run, the message queue not be able to consume, because it's dropped. {}",
                    this.messageQueue);
                return;
            }
            // 获得 Consumer 消息队列锁
            /**
             * 获取MessageQueue对应的锁，在消费某一个消息消费队列时先加锁
             * 意味着一个消费者内消费线程池中的线程并发度是消息消费队列级别，同一个消费队列在同一时刻只会被一个线程消费，其他线程排队消费。
             */
            final Object objLock = messageQueueLock.fetchLockObject(this.messageQueue);
            synchronized (objLock) {
                // (广播模式) 或者 (集群模式 && Broker消息队列锁有效)
                if (MessageModel.BROADCASTING.equals(ConsumeMessageOrderlyService.this.defaultMQPushConsumerImpl.messageModel())
                        || (this.processQueue.isLocked() && !this.processQueue.isLockExpired())) {
                    final long beginTime = System.currentTimeMillis();
                    // 循环
                    for (boolean continueConsume = true; continueConsume;) {
                        if (this.processQueue.isDropped()) {
                            log.warn("the message queue not be able to consume, because it's dropped. {}",
                                this.messageQueue);
                            break;
                        }
                        // 消息队列分布式锁未锁定，提交延迟获得锁并消费请求
                        if (MessageModel.CLUSTERING
                            .equals(ConsumeMessageOrderlyService.this.defaultMQPushConsumerImpl
                                .messageModel())
                                && !this.processQueue.isLocked()) {
                            log.warn("the message queue not locked, so consume later, {}", this.messageQueue);
                            ConsumeMessageOrderlyService.this.tryLockLaterAndReconsume(this.messageQueue,
                                this.processQueue, 10);
                            break;
                        }
                        // 消息队列分布式锁已经过期，提交延迟获得锁并消费请求
                        if (MessageModel.CLUSTERING
                            .equals(ConsumeMessageOrderlyService.this.defaultMQPushConsumerImpl
                                .messageModel())
                                && this.processQueue.isLockExpired()) {
                            log.warn("the message queue lock expired, so consume later, {}",
                                this.messageQueue);
                            ConsumeMessageOrderlyService.this.tryLockLaterAndReconsume(this.messageQueue,
                                this.processQueue, 10);
                            break;
                        }
                        // 当前周期消费时间超过连续时长，默认：60s，提交延迟消费请求。默认情况下，每消费1分钟休息10ms。
                        /**
                         * 顺序消息消费处理逻辑，每一个ConsumeRequest消费任务不是以消费消息条数来计算，而是根据消费时间，
                         * 默认当消费时长大于MAX_TIME_CONSUME_CONTINUOUSLY，默认60s后，本次消费任务结束，由消费组内其他线程继续消费。
                         */
                        long interval = System.currentTimeMillis() - beginTime;
                        if (interval > MaxTimeConsumeContinuously) {
                            ConsumeMessageOrderlyService.this.submitConsumeRequestLater(processQueue,
                                messageQueue, 10);
                            break;
                        }
                        // 获取消费消息。此处和并发消息请求不同，并发消息请求已经带了消费哪些消息。
                        final int consumeBatchSize =
                                ConsumeMessageOrderlyService.this.defaultMQPushConsumer
                                    .getConsumeMessageBatchMaxSize();

                        List<MessageExt> msgs = this.processQueue.takeMessags(consumeBatchSize);
                        if (!msgs.isEmpty()) {
                            final ConsumeOrderlyContext context =
                                    new ConsumeOrderlyContext(this.messageQueue);

                            ConsumeOrderlyStatus status = null;

                            ConsumeMessageContext consumeMessageContext = null;
                            if (ConsumeMessageOrderlyService.this.defaultMQPushConsumerImpl.hasHook()) {
                                consumeMessageContext = new ConsumeMessageContext();
                                consumeMessageContext
                                    .setConsumerGroup(ConsumeMessageOrderlyService.this.defaultMQPushConsumer
                                        .getConsumerGroup());
                                consumeMessageContext.setMq(messageQueue);
                                consumeMessageContext.setMsgList(msgs);
                                consumeMessageContext.setSuccess(false);
                                ConsumeMessageOrderlyService.this.defaultMQPushConsumerImpl
                                    .executeHookBefore(consumeMessageContext);
                            }
                            // 执行消费
                            long beginTimestamp = System.currentTimeMillis();

                            try {
                                // 锁定队列消费锁
                                this.processQueue.getLockConsume().lock();
                                if (this.processQueue.isDropped()) {
                                    log.warn(
                                        "consumeMessage, the message queue not be able to consume, because it's dropped. {}",
                                        this.messageQueue);
                                    break;
                                }

                                status =
                                        messageListener.consumeMessage(Collections.unmodifiableList(msgs),
                                            context);
                            }
                            catch (Throwable e) {
                                log.warn("consumeMessage exception: {} Group: {} Msgs: {} MQ: {}",//
                                    RemotingHelper.exceptionSimpleDesc(e),//
                                    ConsumeMessageOrderlyService.this.consumerGroup,//
                                    msgs,//
                                    messageQueue);
                            }
                            finally {
                                // 锁定队列消费锁
                                this.processQueue.getLockConsume().unlock();
                            }

                            if (null == status //
                                    || ConsumeOrderlyStatus.ROLLBACK == status//
                                    || ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT == status) {
                                log.warn("consumeMessage Orderly return not OK, Group: {} Msgs: {} MQ: {}",//
                                    ConsumeMessageOrderlyService.this.consumerGroup,//
                                    msgs,//
                                    messageQueue);
                            }

                            long consumeRT = System.currentTimeMillis() - beginTimestamp;

                            if (null == status) {
                                status = ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                            }

                            if (ConsumeMessageOrderlyService.this.defaultMQPushConsumerImpl.hasHook()) {
                                consumeMessageContext.setStatus(status.toString());
                                consumeMessageContext.setSuccess(ConsumeOrderlyStatus.SUCCESS == status
                                        || ConsumeOrderlyStatus.COMMIT == status);
                                ConsumeMessageOrderlyService.this.defaultMQPushConsumerImpl
                                    .executeHookAfter(consumeMessageContext);
                            }

                            ConsumeMessageOrderlyService.this.getConsumerStatsManager().incConsumeRT(
                                ConsumeMessageOrderlyService.this.consumerGroup, messageQueue.getTopic(),
                                consumeRT);
                            // 处理消费结果
                            continueConsume =
                                    ConsumeMessageOrderlyService.this.processConsumeResult(msgs, status,
                                        context, this);
                        }
                        else {
                            continueConsume = false;
                        }
                    }
                }
                else {
                    if (this.processQueue.isDropped()) {
                        log.warn("the message queue not be able to consume, because it's dropped. {}",
                            this.messageQueue);
                        return;
                    }

                    ConsumeMessageOrderlyService.this.tryLockLaterAndReconsume(this.messageQueue,
                        this.processQueue, 100);
                }
            }
        }


        public ProcessQueue getProcessQueue() {
            return processQueue;
        }


        public MessageQueue getMessageQueue() {
            return messageQueue;
        }
    }


    /**
     * 描述：  处理消费结果，并返回是否继续消费
     *
     * @author: miaomiao
     * @date: 19/3/13 下午4:46
     * @param msgs  消息
     * @param status  消费结果状态
     * @param context  消费Context
     * @param consumeRequest  是否继续消费
     * @return 是否继续消费
     */
    public boolean processConsumeResult(//
            final List<MessageExt> msgs, //
            final ConsumeOrderlyStatus status, //
            final ConsumeOrderlyContext context, //
            final ConsumeRequest consumeRequest//
    ) {

        boolean continueConsume = true;
        long commitOffset = -1L;
        if (context.isAutoCommit()) {
            switch (status) {
            case COMMIT:
            case ROLLBACK:
                log.warn(
                    "the message queue consume result is illegal, we think you want to ack these message {}",
                    consumeRequest.getMessageQueue());
            case SUCCESS:
                // 提交消息已消费成功到消息处理队列
                commitOffset = consumeRequest.getProcessQueue().commit();
                // 统计
                this.getConsumerStatsManager().incConsumeOKTPS(consumerGroup,
                    consumeRequest.getMessageQueue().getTopic(), msgs.size());
                break;
            case SUSPEND_CURRENT_QUEUE_A_MOMENT:
                // 设置消息重新消费
                consumeRequest.getProcessQueue().makeMessageToCosumeAgain(msgs);
                // 提交延迟消费请求
                this.submitConsumeRequestLater(
                    consumeRequest.getProcessQueue(),
                    consumeRequest.getMessageQueue(),
                    context.getSuspendCurrentQueueTimeMillis());
                continueConsume = false;
                // 统计
                this.getConsumerStatsManager().incConsumeFailedTPS(consumerGroup,
                    consumeRequest.getMessageQueue().getTopic(), msgs.size());
                break;
            default:
                break;
            }
        }
        else {
            switch (status) {
            case SUCCESS:
                this.getConsumerStatsManager().incConsumeOKTPS(consumerGroup,
                    consumeRequest.getMessageQueue().getTopic(), msgs.size());
                break;
            case COMMIT:
                // 提交消息已消费成功到消息处理队列
                commitOffset = consumeRequest.getProcessQueue().commit();
                break;
            case ROLLBACK:
                // 设置消息重新消费
                consumeRequest.getProcessQueue().rollback();
                this.submitConsumeRequestLater(
                    consumeRequest.getProcessQueue(),
                    consumeRequest.getMessageQueue(),
                    context.getSuspendCurrentQueueTimeMillis());
                continueConsume = false;
                break;
            case SUSPEND_CURRENT_QUEUE_A_MOMENT:  // 计算是否暂时挂起（暂停）消费N毫秒，默认：10ms
                // 设置消息重新消费
                consumeRequest.getProcessQueue().makeMessageToCosumeAgain(msgs);
                // 提交延迟消费请求
                this.submitConsumeRequestLater(
                    consumeRequest.getProcessQueue(),
                    consumeRequest.getMessageQueue(),
                    context.getSuspendCurrentQueueTimeMillis());
                continueConsume = false;
                this.getConsumerStatsManager().incConsumeFailedTPS(consumerGroup,
                    consumeRequest.getMessageQueue().getTopic(), msgs.size());
                break;
            default:
                break;
            }
        }

        // 消息处理队列未dropped，提交有效消费进度
        if (commitOffset >= 0) {
            this.defaultMQPushConsumerImpl.getOffsetStore().updateOffset(consumeRequest.getMessageQueue(),
                commitOffset, false);
        }

        return continueConsume;
    }

    private void submitConsumeRequestLater(//
            final ProcessQueue processQueue, //
            final MessageQueue messageQueue,//
            final long suspendTimeMillis//
    ) {
        long timeMillis = suspendTimeMillis;
        if (timeMillis < 10) {
            timeMillis = 10;
        }
        else if (timeMillis > 30000) {
            timeMillis = 30000;
        }

        this.scheduledExecutorService.schedule(new Runnable() {

            @Override
            public void run() {
                ConsumeMessageOrderlyService.this
                    .submitConsumeRequest(null, processQueue, messageQueue, true);
            }
        }, timeMillis, TimeUnit.MILLISECONDS);
    }


    @Override
    public void submitConsumeRequest(//
            final List<MessageExt> msgs, //
            final ProcessQueue processQueue, //
            final MessageQueue messageQueue, //
            final boolean dispathToConsume) {
        if (dispathToConsume) {
            ConsumeRequest consumeRequest = new ConsumeRequest(processQueue, messageQueue);
            this.consumeExecutor.submit(consumeRequest);
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
    }


    @Override
    public void decCorePoolSize() {
    }


    @Override
    public int getCorePoolSize() {
        return this.consumeExecutor.getCorePoolSize();
    }


    @Override
    public ConsumeMessageDirectlyResult consumeMessageDirectly(MessageExt msg, String brokerName) {
        ConsumeMessageDirectlyResult result = new ConsumeMessageDirectlyResult();
        result.setOrder(true);

        List<MessageExt> msgs = new ArrayList<MessageExt>();
        msgs.add(msg);
        MessageQueue mq = new MessageQueue();
        mq.setBrokerName(brokerName);
        mq.setTopic(msg.getTopic());
        mq.setQueueId(msg.getQueueId());

        ConsumeOrderlyContext context = new ConsumeOrderlyContext(mq);

        final long beginTime = System.currentTimeMillis();

        log.info("consumeMessageDirectly receive new messge: {}", msg);

        try {
            ConsumeOrderlyStatus status = this.messageListener.consumeMessage(msgs, context);
            if (status != null) {
                switch (status) {
                case COMMIT:
                    result.setConsumeResult(CMResult.CR_COMMIT);
                    break;
                case ROLLBACK:
                    result.setConsumeResult(CMResult.CR_ROLLBACK);
                    break;
                case SUCCESS:
                    result.setConsumeResult(CMResult.CR_SUCCESS);
                    break;
                case SUSPEND_CURRENT_QUEUE_A_MOMENT:
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
                ConsumeMessageOrderlyService.this.consumerGroup,//
                msgs,//
                mq), e);
        }

        result.setAutoCommit(context.isAutoCommit());
        result.setSpentTimeMills(System.currentTimeMillis() - beginTime);

        log.info("consumeMessageDirectly Result: {}", result);

        return result;
    }

}
