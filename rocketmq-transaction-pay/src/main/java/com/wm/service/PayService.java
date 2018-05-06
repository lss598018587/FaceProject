package com.wm.service;

import com.wm.dao.PayMapper;
import com.wm.entity.Pay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wangmiao on 2018/3/1.
 */
@Component
public class PayService {
    @Autowired
    private PayMapper payMapper;

    public Pay selectPay(String id){
        return this.payMapper.selectByPrimaryKey(id);
    }

    public int insert (Pay pay){
        return  this.payMapper.insert(pay);
    }

    public int update(Pay pay) {
        return this.payMapper.updateByPrimaryKey(pay);
    }

    @Transactional
    public void updateAmount(Pay pay,String mode,int money){
        if("IN".equals(mode)){
            pay.setAmount(pay.getAmount().add(new BigDecimal(Math.abs(money))));
        }else if("OUT".equals(mode)){
            pay.setAmount(pay.getAmount().subtract(new BigDecimal(Math.abs(money))));
        }
        pay.setUpdateTime(new Date());
        this.payMapper.updateByPrimaryKey(pay);

        if(money==200){
            int a = 1/0;
        }
    }
}
