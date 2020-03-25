package com.wm.utils;

/**
 * Created by liuruisen on 2018/11/18.
 */
public class JvmUtils {

    public static String jvmnameToJavaname(String jvmName) {
        if (jvmName == null) {
            throw new NullPointerException("jvmName must not be null");
        }
        return jvmName.replace('/', '.');
    }
}
