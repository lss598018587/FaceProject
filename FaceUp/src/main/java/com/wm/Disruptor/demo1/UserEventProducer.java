package com.wm.Disruptor.demo1;

import com.lmax.disruptor.RingBuffer;

/**
 * @Author:wangmiao
 * @Desc
 * 很明显的是：当用一个简单队列来发布事件的时候会牵涉更多的细节，这是因为事件对象还需要预先创建
 * 发布事件最少需要两步：获取下一个事件槽并发布事件（发布事件的时候要使用try/finally保证事件一定会被发布）
 * 如果我们使用RingBuffer.next()获取一个事件槽，那么一定要发布对应的事件。
 * 如果不发布事件，那么就会引起Disruptor状态的混乱
 * 尤其是在多个生产者的情况下回导致事件消费者失速，从而不得不重启应用才会恢复
 *
 * @Date Created in   2018/1/25 .
 */
public class UserEventProducer {
    private final RingBuffer<UserEvent> ringBuffer;
    public UserEventProducer(RingBuffer<UserEvent> ringBuffer){
        this.ringBuffer = ringBuffer;
    }

    /**
     * onData用来发布事件，每调用一次就发布一次事件
     * 它的参数会用事件传递给消费者
     * @param name
     * @param age
     */
    public void onData(String name,int age){
        //可以把ringbuffer看做是一个事件队列，那么next就是得到下面一个事件槽
        long sequence = ringBuffer.next();
        try{
            //用上面的索引取出一个空的事件用于填充（获取该序号对应的事件对象）
            UserEvent event = ringBuffer.get(sequence);
            //获取要通过事件传递的业务数据
            event.setAge(age);
            event.setUserName(name);
        }catch (Exception e){

        }finally {
            //发布事件
            //注意，最后的ringbuffer.publish 方法必须包含在finally中以确保必须得到调用，如果某个请求的sequence 未提交，之后的sequence会一直等他发布
            ringBuffer.publish(sequence);
        }
    }
}
