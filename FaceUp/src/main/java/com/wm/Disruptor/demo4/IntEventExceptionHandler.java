package com.wm.Disruptor.demo4;

import com.lmax.disruptor.ExceptionHandler;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/26 .
 */
public class IntEventExceptionHandler implements ExceptionHandler {
    @Override
    public void handleEventException(Throwable ex, long sequence, Object event) {
        System.out.println("handleEventException"+ex);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        System.out.println(("handleEventException"+ex));
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        System.out.println("handleOnShutdownException"+ex);
    }

}
