/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.tongbanjie.rocketmqConsole.aspect.admin;

import com.tongbanjie.rocketmqConsole.aspect.admin.annotation.AuthWay;
import com.tongbanjie.rocketmqConsole.aspect.admin.annotation.MultiMQAdminCmdMethod;
import com.tongbanjie.rocketmqConsole.service.client.MQAdminInstance;
import com.tongbanjie.rocketmqConsole.support.JsonResult;
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

@Aspect
@Service
public class MQAdminAspect {
    private Logger logger = LoggerFactory.getLogger(MQAdminAspect.class);

    public MQAdminAspect() {
    }

    @Pointcut("execution(* com.tongbanjie.rocketmqConsole.service.client.MQAdminExtImpl..*(..))")
    public void mQAdminMethodPointCut() {

    }

    @Pointcut("@annotation(com.tongbanjie.rocketmqConsole.aspect.admin.annotation.MultiMQAdminCmdMethod)")
    public void multiMQAdminMethodPointCut() {

    }

    @Pointcut("@annotation(com.tongbanjie.rocketmqConsole.aspect.admin.annotation.AuthWay)")
    public void authWay() {
    }


    @Around(value = "mQAdminMethodPointCut() || multiMQAdminMethodPointCut() || authWay() ")
    public Object aroundMQAdminMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object obj = null;
        try {
            MethodSignature signature = (MethodSignature)joinPoint.getSignature();
            Method method = signature.getMethod();
            MultiMQAdminCmdMethod multiMQAdminCmdMethod = method.getAnnotation(MultiMQAdminCmdMethod.class);
            if (multiMQAdminCmdMethod != null && multiMQAdminCmdMethod.timeoutMillis() > 0) {
                MQAdminInstance.initMQAdminInstance(multiMQAdminCmdMethod.timeoutMillis());
            }
            else {
                MQAdminInstance.initMQAdminInstance(0);
            }

            AuthWay authWay = method.getAnnotation(AuthWay.class);
            if (authWay != null && StringUtils.isNotBlank(authWay.path())) {
                String path = authWay.path();
                boolean showFlag = isShow(path);
                if(!showFlag){
                    return new JsonResult<Object>(-1,"用户未有权限访问");
                }
            }
            obj = joinPoint.proceed();
        }
        finally {
            MQAdminInstance.destroyMQAdminInstance();
            logger.debug("op=look method={} cost={}", joinPoint.getSignature().getName(), System.currentTimeMillis() - start);
        }
        return obj;
    }




    private boolean isShow(String code) {
        try {
            TbjAuthorityCache authCache = SSOUtils.getAuth();
            Set<TbjAuthority> authorities = authCache.getAuthorities();

            TbjAuthority param=new TbjAuthority();
            param.setAuthorityCode(code);
            param.setSystemId(UserAuthUtil.getSystemId());

            return authorities.contains(param);
        } catch (Exception e) {
            logger.warn("获取权限列表异常", e);
        }
        return false;
    }

}