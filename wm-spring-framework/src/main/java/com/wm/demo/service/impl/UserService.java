package com.wm.demo.service.impl;

import com.wm.annotations.WmService;
import com.wm.demo.service.IUserService;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/16 .
 */
@WmService
public class UserService implements IUserService {
    public String addUser(String name, Integer id) {
        return name+"----------"+id;
    }

    public String query(Integer id) {
        return "打印"+id;
    }
}
