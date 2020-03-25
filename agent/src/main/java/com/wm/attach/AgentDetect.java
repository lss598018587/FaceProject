package com.wm.attach;

import com.wm.constants.AopClassAndMethos;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * @Auther: miaomiao
 * @Date: 2019-09-06 14:28
 * @Description:
 */
public class AgentDetect {

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("Premain 方法执行，参数args为：" + args);

        /*VirtualMachine virtualMachine = VirtualMachine.attach("123");
        virtualMachine.loadAgent();*/
    }

    public static void agentmain(String args, Instrumentation inst) throws UnmodifiableClassException
        {
            System.out.println("args is " + args);
            System.out.println("====== Begin=======");
            try {
                inst.addTransformer(new AgentTransForm(), true);
                Class[] classes = inst.getAllLoadedClasses();
                for (Class clazz : classes) {
                    if (matchClazz(clazz)) {
                        System.out.println("Class mathed " + clazz.getName());
                        inst.retransformClasses(clazz);
                    }
                }
            } catch (UnmodifiableClassException e) {
                e.printStackTrace();
            }
            System.out.println("===== END =====");
        }
        private static boolean matchClazz (Class clazz){
            //System.out.println(clazz.getName());
            return clazz.getName().equalsIgnoreCase(AopClassAndMethos.springMVCClass);
        }


        /**
         * 校验传入参数是否正常，重点是校验args中是否有javaagent-plugin的路径
         * @param args
         */
        private void checkArgs (String args){
            if (args == null || args.length() <= 0) {
                throw new IllegalArgumentException("Args is Null, Please Check");
            }

            // 解析args，格式为agent=/home/xxxx/path/to/agent.jar
            String[] argsList = args.split("=");
            if (!argsList[0].equalsIgnoreCase("agent")) {
                throw new IllegalArgumentException("Agent parameter is Null, Please Check");
            }
            System.out.println("Agent path = " + argsList[1]);
        }
    }
