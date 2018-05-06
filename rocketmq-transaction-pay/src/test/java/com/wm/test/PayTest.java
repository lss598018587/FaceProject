package com.wm.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.common.message.Message;
import com.wm.mq.MQProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by wangmiao on 2018/3/1.
 */
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@TransactionConfiguration(transactionManager = "transactionManager" ,defaultRollback = false)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional(rollbackFor = Exception.class)
public class PayTest {

    @Autowired
    private MQProducer mqProducer;

    @Autowired
    @Qualifier("lt")
    private LocalTransactionExecuter localTransactionExecuter;

    @Test
    public void test1(){
        try {
            String uuid = UUID.randomUUID().toString();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id","1");
            jsonObject.put("money","1000");
            jsonObject.put("pay_mode","OUT");
            jsonObject.put("balance_mode","IN");

            Message message = new Message("pay","tag",uuid,jsonObject.toJSONString().getBytes());

            Map<String,Object> map = new HashMap<String,Object>();

            map.put("attr1",1);
            map.put("attr2",2);
            map.put("attr3",3);

            mqProducer.sendTransactionMessage(message,localTransactionExecuter,map);
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }
}
