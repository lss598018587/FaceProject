package com.wm.mq;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.wm.entity.Balance;
import com.wm.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by wangmiao on 2018/2/26.
 */
@Component
public class Consumer {
    private final String GROUP_NAME = "transaction-balance";
    private final String NAMESRV_ADDR = "h1:9876;h2:9876;h3:9876;h4:9876";

    private DefaultMQPushConsumer consumer;
    @Autowired
    private BalanceService balanceService;

    public Consumer() {
        try {
            consumer = new DefaultMQPushConsumer(GROUP_NAME);
            consumer.setNamesrvAddr(NAMESRV_ADDR);
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.subscribe("pay","*");
            consumer.registerMessageListener(new Listener());
            consumer.start();
            System.out.println("balance Consumer start");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }
    public class Listener implements MessageListenerConcurrently {

        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            MessageExt message = list.get(0);
            try {
                String topic = message.getTopic();
                JSONObject jsonObject = JSONObject.parseObject(new String (message.getBody(),"utf-8"));
                String tag = message.getTags();
                String key = message.getKeys();
                System.out.println("balance 服务器收到消息：topic="+topic+",msgBody="+jsonObject+",tag="+tag+",key="+key);

                String userId = jsonObject.getString("id");
                Integer money = Integer.parseInt(jsonObject.getString("money"));

                String balance_mode = jsonObject.getString("balance_mode");
                Balance balance = balanceService.selectBalance(userId);
                if(balance!=null){
                    balanceService.updateAmount(balance,balance_mode,money);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                if(message.getReconsumeTimes()==3){
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

}
