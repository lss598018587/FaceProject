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
        DefaultMQProducer producer=new DefaultMQProducer("quickstart_producer");
        producer.setNamesrvAddr("h1:9876;h2:9876");
        producer.start();

        for (int i = 0; i < 100; i++) {
            try {
                Message msg=new Message("TopicQuickStart","TagA",("Hello RocketMQ"+i).getBytes());
                SendResult sendResult=producer.send(msg);
                System.out.println(sendResult);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        producer.shutdown();
    }
}
