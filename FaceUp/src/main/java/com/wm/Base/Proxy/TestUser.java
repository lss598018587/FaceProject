package com.wm.Base.Proxy;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by wangmiao on 2018/3/13.
 */
public class TestUser {
    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
        for(int i=0;i<100;i++){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    User user = User.getInstance();
                    user.add();
                }
            });
            thread.start();
        }
    }
}
