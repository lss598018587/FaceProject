package com.wm.Disruptor.demo1;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangmiao on 2018/1/24.
 */
public class Main {
    public static void main(String[] args) throws Exception{
        long begin = System.currentTimeMillis();

        //创建缓冲池
        ExecutorService execut = Executors.newCachedThreadPool();
        //创建工厂
        UserEventFactory userEventFactory = new UserEventFactory();
        //创建buffersize，也就是ringbuffer大小，必须是2的N次方
        int buffSize = 1024*1024;
        /**
            //BlockingWaitStrategy 是最低效的策略，但其对cpu的消耗最小并且在各种不同部署环境中能提供更加一致的性能表现
            WaitStrategy BLOCKING_WAIT = new BlockingWaitStrategy();
            //SleepingWaitStrategy的性能表现跟BlockingWaitStrategy差不多，对CPU的消耗也类似，但其对生产者线程的影响最小，适合用于异步日志类的场景
            WaitStrategy SLEEPING_WAIT = new SleepingWaitStrategy();
            //YieldingWaitStrategy 的性能是最好的，适合用于低延迟的系统，在要求极高性能且事件处理线程数小于CPU逻辑核心数的场景中，推荐使用此策略：例如，CPU开启超线程的特性
            WaitStrategy YIELDING_WAIT = new YieldingWaitStrategy();
            BusySpinWaitStrategy是性能最高的等待策略，同时也是对部署环境要求最高的策略。这个性能最好用在事件处理线程比物理内核数目还要小的时候。例如：在禁用超线程技术的时候。
            WaitStrategy BUSYSPIN_WAIT = new BusySpinWaitStrategy();
        **/
        /**
         *  创建disruptor
         *  1. 第一个参数为工厂类对象,勇于创建一个个的UserEvent，UserEvent是实际的消费数据
         *  2. 第二个参数为缓冲区大小
         *  3. 第三个参数 线程池进行Disruptor 内部的数据接收处理调度
         *  4. 第四个参数 生产者个数 单个 or 多个 producerType.SINGLE 和 producerType.MULTI
         *  5. 第五个参数 是一种策略 waitstrategy
         */
        Disruptor<UserEvent> disruptor = new Disruptor<UserEvent>(userEventFactory,buffSize,execut, ProducerType.SINGLE,new YieldingWaitStrategy());
        //连接消费者事件方法
        disruptor.handleEventsWith(new UserEventHandler());
        //启动
        disruptor.start();
        //使用该方法获得具体存放数据的容器
        RingBuffer<UserEvent> ringBuffer = disruptor.getRingBuffer();

        //Disruptor 的事件发布过程是一个两阶段提交的过程
        //发布事件
//        UserEventProducer producer = new UserEventProducer(ringBuffer);
        UserEventProducerWithTranslator producer = new UserEventProducerWithTranslator(ringBuffer);
        Map<String,Object> map = new HashMap(2);
        for (int i = 0; i <6 ; i++) {
            map.put("userName","lyj"+i);
            map.put("age",10+i);
            producer.onData(map);
            Thread.sleep(1000);
        }
//        for (int i = 0; i <6 ; i++) {
//            producer.onData("lyj"+i,i+10);
//            Thread.sleep(1000);
//        }
        Thread.sleep(5000);
        disruptor.shutdown();
        execut.shutdown();
    }
}
