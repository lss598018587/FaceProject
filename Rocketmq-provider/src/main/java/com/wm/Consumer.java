package com.wm;

import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by wangmiao on 2018/2/26.
 */
public class Consumer {
    public static void main(String[] args) throws MQClientException {
        String name = "order_consumer";
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(name);
        consumer.setNamesrvAddr("10.211.55.7:9876;10.211.55.8:9876");

        /**
         * 设置Consumer第一次启动是从猎头开始消费还是队列尾部开始消费
         * 如果非第一次启动，那么安扎上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.subscribe("topic_miao_producer","TagA");

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                try {
                    for(MessageExt message :list){
                        String topic = message.getTopic();
                        String msgBody = new String (message.getBody(),"utf-8");
                        String tag = message.getTags();
                        System.out.println("topic="+topic+",msgBody="+msgBody+",tag="+tag);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        System.out.println("Consumer start");
    }
}
