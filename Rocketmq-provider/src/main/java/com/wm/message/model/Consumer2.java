package com.wm.message.model;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by wangmiao on 2018/2/26.
 */
public class Consumer2 {
    public Consumer2() {
        String name = "order_consumer";
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(name);
        consumer.setNamesrvAddr("10.211.55.7:9876;10.211.55.8:9876");
        //广播消费，每个consumer收到同样的消息
        consumer.setMessageModel(MessageModel.BROADCASTING);
        //集群消费，就是负载均衡，每个consumer消费的总和等于mq服务消息的总数（暂且不说重复消费）
//        consumer.setMessageModel(MessageModel.CLUSTERING);

        /**
         * 设置Consumer第一次启动是从猎头开始消费还是队列尾部开始消费
         * 如果非第一次启动，那么安扎上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        try {
            consumer.subscribe("TopicQuickStart","TagC");
            consumer.registerMessageListener(new Listenner() );
            consumer.start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args)  {

        Consumer2 consumer2 = new Consumer2();
        System.out.println("Consumer2 start");
    }

    public static class Listenner implements MessageListenerConcurrently{
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            try {
                for(MessageExt message :list){
                    String topic = message.getTopic();
                    String msgBody = new String (message.getBody(),"utf-8");
                    String tag = message.getTags();
                    System.out.println("topic="+topic+",msgBody="+msgBody+",tag="+tag);
                }
                System.out.println("=============暂停了");
                Thread.sleep(50000);
                System.out.println("=====暂停结束");
            } catch ( Exception e) {
                e.printStackTrace();
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}
