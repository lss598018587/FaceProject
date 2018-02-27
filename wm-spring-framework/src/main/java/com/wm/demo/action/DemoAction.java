package com.wm.demo.action;


import com.wm.annotations.WmAutowired;
import com.wm.annotations.WmController;
import com.wm.annotations.WmRequestMapping;
import com.wm.annotations.WmRequestParams;
import com.wm.demo.service.IUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/16 .
 */
@WmController
@WmRequestMapping("/demo")
public class DemoAction {
    @WmAutowired
    private IUserService userService;

    @WmRequestMapping("/add")
    public void add(HttpServletResponse resp, HttpServletRequest req, @WmRequestParams("name") String name, @WmRequestParams("id") Integer id){
        String result =  userService.addUser(name,id);
        out(resp,result);
    }
    @WmRequestMapping("/query")
    public void query(HttpServletResponse resp, @WmRequestParams("id") Integer id){
        out(resp,userService.query( id));
    }

    private void out(HttpServletResponse resp,String str){
//        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/JavaScript; charset=utf-8");
        try{
            resp.getWriter().write(str);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
