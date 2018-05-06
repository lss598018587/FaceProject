package com.wm.mq;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.*;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 事物producer
 * sysflag  0代表普通消息，4代表prepare消息 8代表commit消息（事物提交的消息，已二次确认）12代表事物回滚的消息
 * Created by wangmiao on 2018/2/26.
 */
@Component
public class MQProducer {

    private final String GROUP_NAME = "transaction_producer";
    private final String NAMESRV_ADDR = "h1:9876;h2:9876;h3:9876;h4:9876";
    private TransactionMQProducer producer;

    public MQProducer() {
        producer=new TransactionMQProducer(GROUP_NAME);
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.setCheckThreadPoolMinSize(5);//回查最小并发数
        producer.setCheckThreadPoolMaxSize(20);//回查最大并发数
        producer.setCheckRequestHoldMax(2000); //队列数
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
        try {
            producer.start();

        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public void sendTransactionMessage(Message message, LocalTransactionExecuter localTransactionExecuter, Map<String,Object> arg) throws MQClientException {
        TransactionSendResult result = this.producer.sendMessageInTransaction(message,localTransactionExecuter,arg);
        System.out.println("第一次发送prepare消息 返回内容："+result);
    }

    public void shutDown(){
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                producer.shutdown();
            }
        }));
        System.exit(0);
    }




}
