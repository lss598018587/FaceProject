package com.wm.test;

import com.wm.entity.Balance;
import com.wm.service.BalanceService;
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
    private BalanceService balanceService;

    @Test
    public void testSave(){
        Balance balance =new Balance();
        balance.setUserId("1");
        balance.setUserName("wm");
        balance.setAmount(new BigDecimal(5000));
        balance.setUpdateby("001");
        balance.setUpdateTime(new Date());
        balanceService.insert(balance);
    }

    @Test
    public void testUpdate(){
        Balance balance = this.balanceService.selectBalance("1");
        balance.setAmount(balance.getAmount().subtract(new BigDecimal(1000)));
        balance.setUpdateTime(new Date());
        balanceService.update(balance);
    }
}
