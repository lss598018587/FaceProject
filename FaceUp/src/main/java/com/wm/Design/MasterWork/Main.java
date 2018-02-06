package com.wm.Design.MasterWork;

import java.util.Random;

/**
 * Created by wangmiao on 2018/1/19.
 */
public class Main {
    public static void main(String[] args) {
        Master master = new Master(new Worker(),10);
        Random r= new Random();
        for (int i = 0; i < 100; i++) {
            Task t = new Task();
            t.setId(i);
            t.setMoney(r.nextInt());
            master.submit(t);
        }
        master.execuse();
        long start = System.currentTimeMillis();
        while (true){
            if(master.isComplete()){
                long end = System.currentTimeMillis();
                int prince = master.getResult();
                System.out.printf("最终结果%s,执行时间%s",prince,end-start);
                break;
            }
        }
    }
}
