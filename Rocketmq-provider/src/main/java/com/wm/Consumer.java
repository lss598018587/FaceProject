package com.wm;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
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
        //有堆积的消息，这才有用，不然都是一条一条接收的
        //不过这最多拉10条，可能5条，可能6条，都有可能
        consumer.setConsumeMessageBatchMaxSize(10);
        /**
         * 设置Consumer第一次启动是从猎头开始消费还是队列尾部开始消费
         * 如果非第一次启动，那么安扎上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.subscribe("TopicQuickStart","TagA");

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                    MessageExt message =list.get(0);
                try {
                        String topic = message.getTopic();
                        String msgBody = new String (message.getBody(),"utf-8");
                        String tag = message.getTags();
                        System.out.println("topic="+topic+",msgBody="+msgBody+",tag="+tag);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    if(message.getReconsumeTimes() ==3){ //当重试次数达到3次
                        //做jdbc操作，记录发送错误的queue消息，返回给mq服务器成功，
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    //返回给mq服务器失败，让他继续重发
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        System.out.println("Consumer start");
    }
}
