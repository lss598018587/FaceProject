package com.wm;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

/**
 * Created by wangmiao on 2018/2/26.
 */
public class Producer {
    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer producer =  new DefaultMQProducer("miao_producer");
        producer.setNamesrvAddr("10.211.55.7:9876;10.211.55.8:9876");
        producer.start();

        for (int i = 0; i <100 ; i++) {
            try {
                Message msg = new Message("topic_miao_producer","TagA",("hello RocketMQ"+i).getBytes());
                SendResult send = producer.send(msg);
                System.out.println(send);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RemotingException e) {
                e.printStackTrace();
            } catch (MQBrokerException e) {
                e.printStackTrace();
            }
        }
        producer.shutdown();
    }
}
