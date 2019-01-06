package com.wm.producer.order;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.*;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * 顺序消费
 * Created by wangmiao on 2018/2/26.
 */
public class Consumer2 {
    public Consumer2() {
        String name = "miao_consumer";
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(name);
        consumer.setNamesrvAddr("rocketmq-nameserver1:9876;rocketmq-nameserver2:9876");
//        consumer.setNamesrvAddr("h1:9876;h2:9876;h3:9876;h4:9876");
        //广播消费，每个consumer收到同样的消息
//        consumer.setMessageModel(MessageModel.BROADCASTING);
        //集群消费，就是负载均衡，每个consumer消费的总和等于mq服务消息的总数（暂且不说重复消费）
//        consumer.setMessageModel(MessageModel.CLUSTERING);

        /**
         * 设置Consumer第一次启动是从列头开始消费还是队列尾部开始消费
         * 如果非第一次启动，那么安扎上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        try {
//            consumer.subscribe("TopicQuickStart","TagA || TagB || TagC");
            consumer.subscribe("morgana-partition-copper-succ","");
            consumer.registerMessageListener(new Listenner() );
            consumer.start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args)  {

        Consumer2 consumer = new Consumer2();
        System.out.println("Consumer2 start");
    }
//MessageListenerConcurrently
       class Listenner implements MessageListenerOrderly {
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext context) {
            //设置自动提交
            context.setAutoCommit(true);
            try {
                for(MessageExt message :list){
                    String topic = message.getTopic();
                    String msgBody = new String (message.getBody(),"utf-8");
                    String tag = message.getTags();
                    String key = message.getKeys();
                    System.out.println("topic="+topic+",msgBody="+msgBody+",tag="+tag+",key="+key);
                }
                Thread.sleep(500);
//
//                System.out.println("=============暂停了");
//                Thread.sleep(50000);
//                System.out.println("=====暂停结束");
            } catch (Exception e) {
                e.printStackTrace();
               // ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;//挂起一会再消费
            }
            return ConsumeOrderlyStatus.SUCCESS;
        }

}
}
