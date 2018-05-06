package com.wm.producer.order;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * 顺序消费
 * 放queue里的顺序消费，
 *
 * 接收的listener 一定要继承MessageListenerOrderly 而不是 MessageListenerConcurrently
 *
 *  MessageListenerOrderly ->是有序的
 *  MessageListenerConcurrently ->是无序的
 *
 * Created by wangmiao on 2018/2/26.
 */
public class Producer {
    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer producer=new DefaultMQProducer("quickstart_producer");
        producer.setNamesrvAddr("h1:9876;h2:9876;h3:9876;h4:9876");
        producer.start();

        for (int i = 1; i <=10; i++) {
            try {
                Message msg=new Message("TopicQuickStart","TagC","key1"+i,("p1---"+i).getBytes());
                SendResult sendResult=producer.send(msg, new MessageQueueSelector() {
                    public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                        Integer id = (Integer)o;
                        return list.get(id); //mq服务器queue列表中，取出哪个queue
                    }
                },0); //0是队列的下表，这样就是把消息放到一个队列里
                System.out.println(sendResult);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        for (int i = 1; i <=10; i++) {
            try {
                Message msg=new Message("TopicQuickStart","TagC","key2"+i,("p2---"+i).getBytes());
                SendResult sendResult=producer.send(msg, new MessageQueueSelector() {
                    public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                        Integer id = (Integer)o;
                        return list.get(id); //mq服务器queue列表中，取出哪个queue
                    }
                },1); //0是队列的下表，这样就是把消息放到一个队列里
                System.out.println(sendResult);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
//
        for (int i = 1; i <=10; i++) {
            try {
                Message msg=new Message("TopicQuickStart","TagC","key3"+i,("p3---"+i).getBytes());
                SendResult sendResult=producer.send(msg, new MessageQueueSelector() {
                    public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                        Integer id = (Integer)o;
                        return list.get(id); //mq服务器queue列表中，取出哪个queue
                    }
                },2); //0是队列的下表，这样就是把消息放到一个队列里
                System.out.println(sendResult);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        producer.shutdown();
    }
}
