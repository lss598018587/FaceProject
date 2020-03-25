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
public class WmTestProducer {
    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer producer=new DefaultMQProducer("COST_MQ");
        producer.setNamesrvAddr("192.168.1.121:9876");
//        producer.setNamesrvAddr("127.0.0.1:9876");
//        producer.setNamesrvAddr("192.168.1.121:9876;192.168.1.104:9876");
        producer.start();

        int m = 20190227;
        for (int i = 0; i < 1; i++) {
            try {
//                Message msg=new Message("TopicQuickStart","TagA",("Hello RocketMQ"+i).getBytes());
                Message msg=new Message("costMQ456","",(""+m).getBytes());
                SendResult sendResult=producer.send(msg);
                System.out.println(sendResult);
                m++;
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        producer.shutdown();
    }

}

