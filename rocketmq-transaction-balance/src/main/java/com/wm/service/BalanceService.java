package com.wm.service;

import com.wm.dao.BalanceMapper;
import com.wm.dao.BalanceMapper;
import com.wm.entity.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wangmiao on 2018/3/1.
 */
@Service
public class BalanceService {
    @Autowired
    private BalanceMapper balanceMapper;

    public Balance selectBalance(String id){
        return this.balanceMapper.selectByPrimaryKey(id);
    }

    public int insert (Balance Balance){
        return  this.balanceMapper.insert(Balance);
    }

    public int update(Balance Balance) {
        return this.balanceMapper.updateByPrimaryKey(Balance);
    }

    @Transactional
    public void updateAmount(Balance Balance,String mode,int money){
        if("IN".equals(mode)){
            Balance.setAmount(Balance.getAmount().add(new BigDecimal(Math.abs(money))));
        }else if("OUT".equals(mode)){
            Balance.setAmount(Balance.getAmount().subtract(new BigDecimal(Math.abs(money))));
        }
        Balance.setUpdateTime(new Date());
        this.balanceMapper.updateByPrimaryKey(Balance);

    }
}
