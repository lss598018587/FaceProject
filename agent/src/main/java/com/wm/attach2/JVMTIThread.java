//package com.wm.attach2;
//
//import com.sun.tools.attach.*;
//
//import java.io.IOException;
//import java.util.List;
//
///**
// * @Auther: miaomiao
// * @Date: 2019-09-06 17:09
// * @Description:
// */
//public class JVMTIThread {
//    public static void main(String[] args)
//            throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
////        List<VirtualMachineDescriptor> list = VirtualMachine.list();
////        for (VirtualMachineDescriptor vmd : list) {
////            if (vmd.displayName().endsWith("Demo")) {
////                System.out.println(vmd.id());
////                VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
////                virtualMachine.loadAgent("/Users/miaomiao/myProject/FaceProject/agent/target/agent.jar ", "cxs");
////                System.out.println("ok");
////                virtualMachine.detach();
////            }
////        }
//        VirtualMachine vm = null;
//        String agentjarpath = "/Users/miaomiao/myProject/FaceProject/agent/target/agent.jar"; //agentjar路径
//        vm = VirtualMachine.attach("27647");//目标JVM的进程ID（PID）
//        vm.loadAgent(agentjarpath, "This is Args to the Agent.");
//        vm.detach();
//    }
//}
