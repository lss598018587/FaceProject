package com.wm.netty.intermediate.modal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static com.wm.netty.intermediate.modal.LaopopoProtocol.*;


/**
 * 
 * @author BazingaLyn
 * @description
 * @time
 * @modifytime
 */
@SuppressWarnings("deprecation")
public class Heartbeats {

    private static final ByteBuf HEARTBEAT_BUF;
    
    static {
        byte isCompress = LaopopoProtocol.UNCOMPRESS;
        ByteBuf buf = Unpooled.buffer(HEAD_LENGTH);
        buf.writeShort(MAGIC);
        buf.writeByte(HEARTBEAT);
        buf.writeByte(0);
        buf.writeLong(0);
        buf.writeInt(0);
        buf.writeByte(isCompress);
        buf.writeBytes(new byte[0]);
        //Unpooled.unmodifiableBuffer创建对象，所有的写请求都将被禁止
        HEARTBEAT_BUF = Unpooled.unmodifiableBuffer(Unpooled.unreleasableBuffer(buf));
    }

    /**
     * Returns the shared heartbeat content.
     */
    public static ByteBuf heartbeatContent() {
        /**
         * ridx是readerIndex读取数据索引，位置从0开始
         * widx是writeIndex写数据索引，位置从0开始
         * cap是capacity缓冲区初始化的容量，默认256，可以通过Unpooled.buffer(8)设置，初始化缓冲区容量是8。
         */
        System.out.println("HEARTBEAT_BUF》》"+HEARTBEAT_BUF);
        return HEARTBEAT_BUF.duplicate();
    }
}
