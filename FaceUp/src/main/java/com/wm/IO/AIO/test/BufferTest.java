package com.wm.IO.AIO.test;

import java.nio.ByteBuffer;

/**
 * Created by wangmiao on 2018/1/30.
 */
public class BufferTest {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        String m = "sdfasdf";
        buffer.put(m.getBytes());
        System.out.println(buffer+"/n"+" remain"+buffer.remaining());
        buffer.flip();
        System.out.println(buffer+"/n"+" remain"+buffer.remaining());
    }
}
