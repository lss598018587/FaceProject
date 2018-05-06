package com.wm.producer.filter;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 过滤消费
 * Created by wangmiao on 2018/2/26.
 */
public class Consumer {
    public Consumer() {
        String name = "filter_consumer";
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(name);
        consumer.setNamesrvAddr("h1:9876;h2:9876;h3:9876;h4:9876");

        String filterCode = MixAll.file2String("/Users/wangmiao/gitProject/FaceProject/Rocketmq-provider/src/main/java/com/wm/producer/filter/MessageFilterImpl.java");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        try {
            consumer.subscribe("FilterTopic","com.wm.producer.filter.MessageFilterImpl",filterCode);
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
//                SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
//                System.out.println("size>>>开始了："+format.format(new Date()));
//                Thread.sleep(15000);
//                System.out.println("size>>>结束了："+format.format(new Date()));
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
