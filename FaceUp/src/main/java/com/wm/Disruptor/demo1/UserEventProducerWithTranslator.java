package com.wm.Disruptor.demo1;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

import java.util.Map;

/**
 * @Author:wangmiao
 * @Desc
 * Disruptor 3.0提供了lambda式的API，这样可以吧一些复杂的操作放在Ring Buffer，
 * 所以在Disruptor3.0以后的版本最好使用Event Publisher或者Event Translator来发布事件
 *
 * @Date Created in   2018/1/25 .
 */
public class UserEventProducerWithTranslator {

    //一个translator可以看做一个事件初始化器，publicEvent方法会调用它
    //填充Event
    private static final EventTranslatorOneArg<UserEvent,Map<String,Object>> TRANSLATOR =
            new EventTranslatorOneArg<UserEvent, Map<String,Object>>() {
                @Override
                public void translateTo(UserEvent userEvent, long sequeue, Map<String, Object> map) {
                    userEvent.setUserName(map.get("userName").toString());
                    userEvent.setAge((Integer) map.get("age"));
                }
            };
    private final RingBuffer<UserEvent> ringBuffer;

    public UserEventProducerWithTranslator(RingBuffer<UserEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }
    public void onData(Map<String,Object> map){
        ringBuffer.publishEvent(TRANSLATOR,map);
    }
}
