package com.wm.attach2;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

/**
 * @Auther: miaomiao
 * @Date: 2019-09-06 17:10
 * @Description:
 */
public class AgentMainTraceAgent {

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("Premain 方法执行，参数args为：" + args);
        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        System.out.println(parentClassLoader);
        /*VirtualMachine virtualMachine = VirtualMachine.attach("123");
        virtualMachine.loadAgent();*/
    }

    public static void agentmain(String args, Instrumentation inst)
            throws UnmodifiableClassException, ClassNotFoundException {
        System.out.println("Agent Main called");
        System.out.println("agentArgs : " + args);
        inst.addTransformer(new ClassFileTransformer() {

            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    throws IllegalClassFormatException {
                System.out.println("agentmain load Class  :" + className);
                return classfileBuffer;
            }
        }, true);
        Class acc = Class.forName("com.miaomiao.agent.Account");
        inst.retransformClasses(acc);
    }
}
