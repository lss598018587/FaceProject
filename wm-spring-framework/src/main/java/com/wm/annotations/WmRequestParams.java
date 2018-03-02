package com.wm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/2/24 .
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface WmRequestParams {
    String value() default "";
    boolean required() default true;
}
