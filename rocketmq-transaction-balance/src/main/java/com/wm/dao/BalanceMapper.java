package com.wm.dao;

import com.wm.entity.Balance;

public interface BalanceMapper {
    int deleteByPrimaryKey(String userId);

    int insert(Balance record);

    int insertSelective(Balance record);

    Balance selectByPrimaryKey(String userId);

    int updateByPrimaryKeySelective(Balance record);

    int updateByPrimaryKey(Balance record);
}