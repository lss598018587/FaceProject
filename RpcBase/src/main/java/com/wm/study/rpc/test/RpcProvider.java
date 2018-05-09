package com.wm.study.rpc.test;

import com.wm.study.rpc.framework.RpcFramework;

/**
 * 暴露服务
 */
public class RpcProvider {
    public static void main(String[] args) throws Exception {
        HelloService service = new HelloServiceImpl();
        RpcFramework.export(service, 1234);
    }
}
