package com.wm.mq;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.common.message.Message;
import com.wm.entity.Pay;
import com.wm.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by wangmiao on 2018/3/1.
 */
@Component("lt")
public class LocalTransactionImpl implements LocalTransactionExecuter {
    @Autowired
    private PayService payService;
    public LocalTransactionState executeLocalTransactionBranch(Message message, Object arg) {

        try {
            JSONObject jsonObject = JSONObject.parseObject(new String(message.getBody(),"utf-8"));

            Map<String,Object> map = (Map<String,Object>)arg;

//            --------------------------------------
            System.out.println("message body ="+jsonObject);
            System.out.println("message Map Arg =" +arg);
            System.out.println("message tag=" + message.getTags());

//            --------------------------------------

            String userId = jsonObject.getString("id");
            Integer money = Integer.parseInt(jsonObject.getString("money"));
            String pay_mode = jsonObject.getString("pay_mode");
            Pay pay = payService.selectPay(userId);
            if(pay!=null){

                payService.updateAmount(pay,pay_mode,money);
                if(money!=100){
                    return LocalTransactionState.COMMIT_MESSAGE;
                }else{
                    return LocalTransactionState.UNKNOW;
                }
            }else {
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }

    }
}
