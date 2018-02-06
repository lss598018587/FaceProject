package com.wm.Thread.reentrantLock.demo1;

/**
 * @Author:wangmiao
 * @Desc
 * @Date Created in   2018/1/18 .
 */
public interface IBuffer {
    public void write();
    public void read() throws InterruptedException;
}
