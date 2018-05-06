package com.wm.test;

import com.wm.entity.Pay;
import com.wm.service.PayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wangmiao on 2018/3/1.
 */
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@TransactionConfiguration(transactionManager = "transactionManager",defaultRollback = false)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional(rollbackFor = Exception.class)
public class BaseTest {

    @Autowired
    private PayService payService;

    @Test
    public void testSave(){
        Pay pay =new Pay();
        pay.setUserId("1");
        pay.setUserName("wm");
        pay.setAmount(new BigDecimal(5000));
        pay.setDetail("0");
        pay.setUpdateby("001");
        pay.setUpdateTime(new Date());
        payService.insert(pay);
    }

    @Test
    public void testUpdate(){
        Pay pay = this.payService.selectPay("1");
        pay.setAmount(pay.getAmount().subtract(new BigDecimal(1000)));
        pay.setUpdateTime(new Date());
        payService.update(pay);
    }
}
