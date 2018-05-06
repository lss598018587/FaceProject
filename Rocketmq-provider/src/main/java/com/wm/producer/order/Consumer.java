package com.wm.producer.order;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.*;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 顺序消费
 * Created by wangmiao on 2018/2/26.
 */
public class Consumer {
    public Consumer() {
        String name = "order_consumer";
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(name);
        consumer.setNamesrvAddr("h1:9876;h2:9876;h3:9876;h4:9876");
        //广播消费，每个consumer收到同样的消息
//        consumer.setMessageModel(MessageModel.BROADCASTING);
        //集群消费，就是负载均衡，每个consumer消费的总和等于mq服务消息的总数（暂且不说重复消费）
//        consumer.setMessageModel(MessageModel.CLUSTERING);

        //消费线程池最小数量（针对mq服务里不同的queue，不同的队列，同一个队列不能同步消费）
        consumer.setConsumeThreadMin(5);
        //消费线程池最大数量
        consumer.setConsumeThreadMax(10);

        /**
         * 设置Consumer第一次启动是从猎头开始消费还是队列尾部开始消费
         * 如果非第一次启动，那么安扎上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        try {
            consumer.subscribe("TopicQuickStart","TagA || TagB || TagC");
            consumer.registerMessageListener(new Listenner() );
            consumer.start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args)  {

        Consumer consumer = new Consumer();
        System.out.println("Consumer start");
    }

    public static class Listenner implements MessageListenerOrderly {
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext context) {
            //设置自动提交
            context.setAutoCommit(true);
            int size = list.size();
            System.out.println("size>>>"+size);
            try {
                for(MessageExt message :list){
                    String topic = message.getTopic();
                    String msgBody = new String (message.getBody(),"utf-8");
                    String tag = message.getTags();
                    String key = message.getKeys();
                    System.out.println("topic="+topic+",msgBody="+msgBody+",tag="+tag+",key="+key);
                }
                SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
                System.out.println("size>>>开始了："+format.format(new Date()));
                Thread.sleep(500);
                System.out.println("size>>>结束了："+format.format(new Date()));
//
//                System.out.println("=============暂停了");
//                Thread.sleep(50000);
//                System.out.println("=====暂停结束");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ConsumeOrderlyStatus.SUCCESS;
        }
    }
}
