package com.wm;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;

/**
 * Created by wangmiao on 2018/2/26.
 */
public class Producer {
    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer producer=new DefaultMQProducer("miaomiaoTest");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.start();

        int m = 20190227;
        for (int i = 0; i < 1; i++) {
            try {
//                Message msg=new Message("TopicQuickStart","TagA",("Hello RocketMQ"+i).getBytes());
                Message msg=new Message("miaoTp8","",(""+m).getBytes());
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
