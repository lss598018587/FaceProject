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
    private final BlockingQueue<Runnable> consumeRequestQueue;
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
        private final List<MessageExt> msgs;
        private final ProcessQueue processQueue;
        private final MessageQueue messageQueue;


        public ConsumeRequest(List<MessageExt> msgs, ProcessQueue processQueue, MessageQueue messageQueue) {
            this.msgs = msgs;
            this.processQueue = processQueue;
            this.messageQueue = messageQueue;
        }


        @Override
        public void run() {
            if (this.processQueue.isDropped()) {
                log.info("the message queue not be able to consume, because it's dropped {}",
                    this.messageQueue);
                return;
            }

            MessageListenerConcurrently listener = ConsumeMessageConcurrentlyService.this.messageListener;
            ConsumeConcurrentlyContext context = new ConsumeConcurrentlyContext(messageQueue);
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
                ConsumeMessageConcurrentlyService.this.resetRetryTopic(msgs);
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

            if (null == status) {
                log.warn("consumeMessage return null, Group: {} Msgs: {} MQ: {}",//
                    ConsumeMessageConcurrentlyService.this.consumerGroup,//
                    msgs,//
                    messageQueue);
                status = ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

            if (ConsumeMessageConcurrentlyService.this.defaultMQPushConsumerImpl.hasHook()) {
                consumeMessageContext.setStatus(status.toString());
                consumeMessageContext.setSuccess(ConsumeConcurrentlyStatus.CONSUME_SUCCESS == status);
                ConsumeMessageConcurrentlyService.this.defaultMQPushConsumerImpl
                    .executeHookAfter(consumeMessageContext);
            }

            ConsumeMessageConcurrentlyService.this.getConsumerStatsManager().incConsumeRT(
                ConsumeMessageConcurrentlyService.this.consumerGroup, messageQueue.getTopic(), consumeRT);

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

        if (consumeRequest.getMsgs().isEmpty())
            return;

        switch (status) {
        case CONSUME_SUCCESS:
            if (ackIndex >= consumeRequest.getMsgs().size()) {
                ackIndex = consumeRequest.getMsgs().size() - 1;
            }
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
            this.getConsumerStatsManager().incConsumeFailedTPS(consumerGroup,
                consumeRequest.getMessageQueue().getTopic(), consumeRequest.getMsgs().size());
            break;
        default:
            break;
        }

        switch (this.defaultMQPushConsumer.getMessageModel()) {
        case BROADCASTING:
            for (int i = ackIndex + 1; i < consumeRequest.getMsgs().size(); i++) {
                MessageExt msg = consumeRequest.getMsgs().get(i);
                log.warn("BROADCASTING, the message consume failed, drop it, {}", msg.toString());
            }
            break;
        case CLUSTERING:
            List<MessageExt> msgBackFailed = new ArrayList<MessageExt>(consumeRequest.getMsgs().size());
            for (int i = ackIndex + 1; i < consumeRequest.getMsgs().size(); i++) {
                MessageExt msg = consumeRequest.getMsgs().get(i);
                boolean result = this.sendMessageBack(msg, context);
                if (!result) {
                    msg.setReconsumeTimes(msg.getReconsumeTimes() + 1);
                    msgBackFailed.add(msg);
                }
            }
            //存在mq消费失败的数据
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

        long offset = consumeRequest.getProcessQueue().removeMessage(consumeRequest.getMsgs());
        if (offset >= 0) {
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
        if (msgs.size() <= consumeBatchSize) {
            ConsumeRequest consumeRequest = new ConsumeRequest(msgs, processQueue, messageQueue);
            this.consumeExecutor.submit(consumeRequest);
        }
        else {
            for (int total = 0; total < msgs.size();) {
                List<MessageExt> msgThis = new ArrayList<MessageExt>(consumeBatchSize);
                for (int i = 0; i < consumeBatchSize; i++, total++) {
                    if (total < msgs.size()) {
                        msgThis.add(msgs.get(total));
                    }
                    else {
                        break;
                    }
                }

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
            String retryTopic = msg.getProperty(MessageConst.PROPERTY_RETRY_TOPIC);
            if (retryTopic != null && groupTopic.equals(msg.getTopic())) {
                msg.setTopic(retryTopic);
            }
        }
    }
}
