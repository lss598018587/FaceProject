package com.wm;

import java.lang.instrument.Instrumentation;

/**
 * @Auther: miaomiao
 * @Date: 2019-08-16 16:28
 * @Description:
 */
public class MyAgent {


//    public static void premain(String agentOps)
//    {
//
//        System.out.println("====premain方法执行2====");
//        System.out.println("this is an agent.");
//        System.out.println("args:" + agentOps + "\n");
//    }


    public static void premain(String agentOps, Instrumentation inst)
    {

        System.out.println("====premain方法执行====");
        System.out.println("args:" + agentOps + "\n");
        inst.addTransformer(new MyTransformer());
    }

}
