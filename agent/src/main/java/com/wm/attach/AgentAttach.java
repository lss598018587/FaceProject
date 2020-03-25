//package com.wm.attach;
//
//import com.sun.tools.attach.AgentInitializationException;
//import com.sun.tools.attach.AgentLoadException;
//import com.sun.tools.attach.VirtualMachine;
//
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.concurrent.TimeUnit;
//
///**
// * @Auther: miaomiao
// * @Date: 2019-09-06 14:26
// * @Description:
// */
//public class AgentAttach {
//
//    private static  String agentPath ="/Users/miaomiao/myProject/FaceProject/agent/target/agent.jar";
//
//    public static void main(String[] args) throws IOException {
//        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        Class vmClass = null;
//        try{
//            vmClass = classLoader.loadClass("com.sun.tools.attach.VirtualMachine");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        Object vmObject =null;
//        VirtualMachine virtualMachine=null;
//        try{
//            vmObject = vmClass.getMethod("attach", String.class).invoke(null, args[0]);
//
//              virtualMachine = (VirtualMachine) vmObject;
//            System.out.println(virtualMachine.id());
//            virtualMachine.loadAgent(agentPath);
////            Method loadAgentMethod = vmClass.getMethod("loadAgent", String.class, String.class);
////
////            loadAgentMethod.invoke(vmObject, agentPath, "");
//
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (AgentInitializationException e) {
//            e.printStackTrace();
//        } catch (AgentLoadException e) {
//            e.printStackTrace();
//        } finally {
////            if(null!=vmObject){
////                try {
////                    vmClass.getMethod("detach",(Class<?>[])null).invoke(vmObject,(Object[])null);
////                    System.out.println("detach success");
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
////            }
//            virtualMachine.detach();
//        }
//
//
//        try{
//            while(true){
//                TimeUnit.SECONDS.sleep(10);
//                System.out.println("miaomiao");
//            }
//        } catch (InterruptedException e) {
//            virtualMachine.detach();
////            if (null != vmObject) {
////                try {
////                    vmClass.getMethod("detach", (Class<?>[]) null).invoke(vmObject,
////                            (Object[]) null);
////                    System.out.println("Detach Success");
////                } catch (Exception exx) {
////
////                }
////            }
//            e.printStackTrace();
//        }
//
//    }
//}
