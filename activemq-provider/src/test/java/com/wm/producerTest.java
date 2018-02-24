package com.wm;

import com.wm.entity.Mail;
import com.wm.mq.MQProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring-context.xml"})
public class producerTest
{
    @Autowired
    private MQProducer mqProducer;

    @Test
    public void send(){
        Mail mail = new Mail();
        mail.setTo("598015898@qq.com");
        mail.setSubject("shuang jie 幸福吗");
        mail.setContext("不幸福要记得说");
        this.mqProducer.sendMail(mail);
        System.out.println("发送成功");
    }
}
