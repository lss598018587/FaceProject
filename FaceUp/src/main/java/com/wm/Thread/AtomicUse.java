package com.wm.Thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangmiao on 2018/1/21.
 * 一个方法是一个原子性操作，多个方法就不收一个原子性操作
 * 调用一个multiAdd，打印出来肯定是一个数字加10，不会在各位有其他数字，只可能是0，
 * 但是因为有多个addAndGet，所以其他线程进来，并行就会改变一个方法里的结果，个位可能会有其他数字
 * 但最终结果是原子性的
 */
public class AtomicUse {
    private AtomicInteger count = new AtomicInteger(0);
    public int multiAdd(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        count.addAndGet(10);
//        count.addAndGet(1);
        //意思是，我如果在这里打印，不一定是加1，因为其他线程也在运行的原因，可能就加2或加3或加4
        //但一个方法肯定是加10的
//        count.addAndGet(2);
//        count.addAndGet(3);
//        count.addAndGet(4);
        return count.get();
    }

    public static void main(String[] args) {
        AtomicUse atomicUse = new AtomicUse();
        List<Thread> list= new ArrayList<>();
        for(int i=0;i<100;i++){
            list.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(atomicUse.multiAdd());
                }
            }));
        }
        for (Thread thread : list){
            thread.start();
        }
    }
}
