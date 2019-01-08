package com.tongbanjie.rocketmqConsole.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Auther: miaomiao
 * @Date: 19/1/8 19:40
 * @Description:
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(){
        return "/index.html";
    }
}
