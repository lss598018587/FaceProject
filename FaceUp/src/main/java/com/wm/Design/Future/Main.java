package com.wm.Design.Future;

/**
 * Created by wangmiao on 2018/1/19.
 */
public class Main {
    public static void main(String args[]){
        FutureClient client = new FutureClient();
        Data data = client.request("请求参数");
        System.out.println("请求发送成功");
        System.out.println("做其他的事情");
        String result = data.getRequest();
        System.out.println(result);
    }
}
