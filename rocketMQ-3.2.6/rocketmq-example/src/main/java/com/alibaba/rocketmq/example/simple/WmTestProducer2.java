package com.alibaba.rocketmq.example.simple;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;

/**
 * @Auther: miaomiao
 * @Date: 2019-04-12 16:54
 * @Description:
 */
public class WmTestProducer2 {

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("COST_MQ");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.start();
        int totalMessagesToSend = 10;
        for (int i = 0; i < totalMessagesToSend; i++) {
            Message message = new Message("costMQ456", ("Hello scheduled message " + i).getBytes());
            // 10秒后传递给consumer
            message.setDelayTimeLevel(3);
            // 发送消息
            producer.send(message);
        }
        producer.shutdown();
    }

}

