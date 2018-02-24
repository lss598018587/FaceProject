package com.wm;

import sun.security.rsa.RSASignature;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wangmiao on 2017/12/22.
 */
public class Test {
    public int sum(){
        return 1+2;
    }

}

class Test2 extends  Test{
    @Override
    public int sum() {
        int c= super.sum();
        System.out.println(c);
        return 0;
    }
}
class client{
    public static void main(String args[]){
        Test2 c = new Test2();
        System.out.println(c.sum());
        String m = "accountId=1120140210111812001&name=张成&cardNo=623625856258000&orderId=201706200940&purpose=学费&amount=0.01&responseUrl=http:// IP:PORT&key=123456";
    }
}