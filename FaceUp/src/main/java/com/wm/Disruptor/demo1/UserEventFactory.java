package com.wm.Disruptor.demo1;

import com.lmax.disruptor.EventFactory;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/25 .
 */
public class UserEventFactory implements EventFactory<UserEvent> {
    @Override
    public UserEvent newInstance() {
        return new UserEvent();
    }
}
