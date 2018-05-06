package com.wm.Base;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangmiao on 2018/3/12.
 */
public class ConcurrentMapTest2 {
    static ConcurrentHashMap<String ,Integer> map =new ConcurrentHashMap<>();
    public static void main(String[] args) {
        map.put("key",2);
        Thread t1 = new Thread(new FinalConcurrentMapTest(3));
        Thread t2 = new Thread(new FinalConcurrentMapTest(6));
        t1.start();
        t2.start();
    }

    static class FinalConcurrentMapTest implements Runnable{
        private int sleep;

        public FinalConcurrentMapTest(int sleep) {
            this.sleep = sleep;
        }

        @Override
        public void run() {
            Integer m = map.get("key");
            try {
                TimeUnit.SECONDS.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("m>>"+m);
            map.put("key",m+1);
            System.out.println("成功！"+map.get("key"));
        }
    }
}
