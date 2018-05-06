package com.wm.dao;

import com.wm.entity.Pay;

public interface PayMapper {
    int deleteByPrimaryKey(String userId);

    int insert(Pay record);

    int insertSelective(Pay record);

    Pay selectByPrimaryKey(String userId);

    int updateByPrimaryKeySelective(Pay record);

    int updateByPrimaryKey(Pay record);
}