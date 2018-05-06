package com.wm.producer.filter;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;

/**
 * Created by wangmiao on 2018/2/26.
 */
public class Producer {
    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer producer=new DefaultMQProducer("fitler_producer");
        producer.setNamesrvAddr("h1:9876;h2:9876;h3:9876;h4:9876");
        producer.start();

        for (int i = 0; i < 10; i++) {
            try {
                Message msg=new Message("FilterTopic","TagC",("Hello RocketMQ"+i).getBytes());
                msg.putUserProperty("SequenceId",String.valueOf(i));
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
