package com.wm.demo.service;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/16 .
 */
public interface IUserService {
    public String addUser(String name, Integer id);
    public String query(Integer id);
}
