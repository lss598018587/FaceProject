package com.tongbanjie.rocketmqConsole.aspect.admin;

import com.alibaba.fastjson.JSON;
import com.tongbanjie.rocketmqConsole.aspect.admin.annotation.AuthWay;
import com.tongbanjie.sso.model.TbjAuthority;
import com.tongbanjie.sso.model.TbjAuthorityCache;
import com.tongbanjie.sso.utils.SSOUtils;
import com.tongbanjie.sso.utils.UserAuthUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: miaomiao
 * @Date: 19/1/8 15:08
 * @Description: 拦截路劲
 */

//@Aspect
//@Service
public class AuthWayAspect {

    private final static Logger logger = LoggerFactory.getLogger(AuthWayAspect.class);

//    @Pointcut("@annotation(com.tongbanjie.rocketmqConsole.aspect.admin.annotation.AuthWay)")
    public void authWay() {
    }

//    @Around("authWay()")
//    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        try {
//            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//            Method method = signature.getMethod();
//            AuthWay authWay = method.getAnnotation(AuthWay.class);
//            if (authWay != null && StringUtils.isNotBlank(authWay.path())) {
//                String path = authWay.path();
//                boolean showFlag = isShow(path);
//                if(!showFlag){
//                    Map<String,Object> map = new HashMap<String, Object>(2);
//                    map.put("status",-1);
//                    map.put("errMsg","用户未有权限访问");
//                    return map;
//                }
//            }
//
//            Object object = joinPoint.proceed();
//            return object;
//        } catch (Throwable e) {
//            logger.error("调用方法出错了");
//            throw e;
//        }
//    }


//    private boolean isShow(String code) {
//        try {
//            TbjAuthorityCache authCache = SSOUtils.getAuth();
//            Set<TbjAuthority> authorities = authCache.getAuthorities();
//
//            TbjAuthority param=new TbjAuthority();
//            param.setAuthorityCode(code);
//            param.setSystemId(UserAuthUtil.getSystemId());
//
//            return authorities.contains(param);
//        } catch (Exception e) {
//            logger.warn("获取权限列表异常", e);
//        }
//        return false;
//    }
}
