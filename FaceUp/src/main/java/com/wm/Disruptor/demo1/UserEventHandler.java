package com.wm.Disruptor.demo1;


import com.lmax.disruptor.EventHandler;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/25 .
 */

public class UserEventHandler implements EventHandler<UserEvent> {
    @Override
    public void onEvent(UserEvent userEvent, long sequence, boolean endOfBatch) throws Exception {
        System.out.printf("Event: %s ,  sequence:%s   ,  endOfBatch:%s" , userEvent,sequence,endOfBatch);
        System.out.println( );
    }
}
