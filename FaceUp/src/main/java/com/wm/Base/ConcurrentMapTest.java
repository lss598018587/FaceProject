package com.wm.Base;

import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangmiao on 2018/3/11.
 */
public class ConcurrentMapTest {
     static ConcurrentHashMap<String,Integer> map = new ConcurrentHashMap();
//    static Hashtable<String,Integer> map = new Hashtable<>();
    static CountDownLatch latch = new CountDownLatch(100);
    static AtomicInteger jisuan = new AtomicInteger(0);
    static AtomicInteger jisuan2 = new AtomicInteger(0);
    static AtomicInteger jisuan3 = new AtomicInteger(0);
    public static void main(String[] args) throws Exception {
        for(int i=0;i<100;i++){
            Thread t = new Thread(new Test());
            t.start();
        }



        latch.await();

        System.out.println("key>>>"+map.get("key"));
    }

    static class Test implements Runnable{

        @Override
        public void run() {
//            Integer num = map.get("key");
            Integer num = map.putIfAbsent("key",0);
//            if(num==100) return;
            if(num==null){
                System.out.println("---------"+jisuan2.incrementAndGet());
                map.put("key",1);
            }else{
                System.out.println(jisuan.incrementAndGet());
//                map.put("key",num+1L);
                map.put("key",jisuan3.incrementAndGet());
            }
            latch.countDown();
        }
    }
}
