package com.wm.action;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.common.message.Message;
import com.wm.mq.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by wangmiao on 2018/3/2.
 */
@Controller
public class ProdcuerController {

    @Autowired
    private MQProducer mqProducer;

    @Autowired
    @Qualifier("lt")
    private LocalTransactionExecuter localTransactionExecuter;
    @RequestMapping("/sendMq")
    public void sendMq(String money){
        if(money==null || money.trim().equals(""))return;
        try {
            String uuid = UUID.randomUUID().toString();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id","1");
            jsonObject.put("money",money);
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
