package com.wm.mq;

import com.alibaba.fastjson.JSONObject;
import com.wm.entity.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.jms.*;

/**
 * Created by wangmiao on 2018/2/22.
 */
@Component
public class MailQueueMessageListener implements SessionAwareMessageListener {
    public void onMessage(Message message, Session session) throws JMSException {
        try {
            TextMessage msg = (TextMessage)message;
            final String ms = msg.getText();
            System.out.println("收到消息："+ms);
            Mail mail = JSONObject.parseObject(ms,Mail.class);
            if(mail==null){
                return;
            }
            System.out.println("要发送的对象："+mail.getTo());
        }finally {

        }
    }

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination mailQueue;



}
