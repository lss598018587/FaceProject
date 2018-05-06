package com.wm.producer.transaction;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.*;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 事物producer
 * sysflag  0代表普通消息，4代表prepare消息 8代表commit消息（事物提交的消息，已二次确认）12代表事物回滚的消息
 * Created by wangmiao on 2018/2/26.
 */
public class Producer {
    public static void main(String[] args) throws MQClientException {
        TransactionMQProducer producer=new TransactionMQProducer("transaction_producer");
        producer.setNamesrvAddr("h1:9876;h2:9876;h3:9876;h4:9876");
        producer.start();

        producer.setTransactionCheckListener(new TransactionCheckListener() {
            public LocalTransactionState checkLocalTransactionState(MessageExt messageExt) {
                /**
                 * 要发送一条消息来更改之前发送的prepare的消息状态 的时候 网络断掉了，或其他原因导致（修改prepare消息）的消息没有送达到mq服务
                 * mq服务会来请求服务端，确认这条消息是要回滚还是提交
                 * 此处可以做业务逻辑，也可以dao查询，来返回给mq状态
                 */
                System.out.println("state --" + new String(messageExt.getBody()));
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });

        /**
         * 下面这段代码表明一个Producer对象可以发送多个topic，多个tag的消息
         * 注意：send方法是同步调用，只要不抛异常就标识成功，单但是发送成功也可会有多种状态
         * 例如消息写入Master成功，单身Slave不成功，这种情况消息属于成功，但是对于个别应用如果对消息可靠性要求极高
         * 需要对这种情况做处理，另外，消息可能会存在发送失败的情况，失败重来由应用程序来处理
         */

        TransactionExecutor transactionExecutor = new TransactionExecutor();

        for (int i = 1; i <=2; i++) {
            try {
                Message msg=new Message("TopicTransaction","Transaction"+i,"key1"+i,("p1---"+i).getBytes());
                SendResult sendResult=producer.sendMessageInTransaction(msg,transactionExecutor,"tq");
                System.out.println(sendResult);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        producer.shutdown();
    }


}
