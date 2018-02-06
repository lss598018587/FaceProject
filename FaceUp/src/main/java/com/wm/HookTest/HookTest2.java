package com.wm.HookTest;

import java.util.concurrent.TimeUnit;

/**
 * Created by wangmiao on 2018/1/20.
 * 优雅关机
 */
public class HookTest2 {
    public void start()
    {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run()
            {
                System.out.println("Execute Hook2.....");
            }
        }));
    }

    public static void main(String[] args)
    {
        new HookTest2().start();
        System.out.println("The Application is doing something");
        if(true) throw new RuntimeException();
        try
        {
            System.out.println("进入等待5秒");
            TimeUnit.MILLISECONDS.sleep(5000);
            System.out.println("进入等待5秒 结束---11---");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
