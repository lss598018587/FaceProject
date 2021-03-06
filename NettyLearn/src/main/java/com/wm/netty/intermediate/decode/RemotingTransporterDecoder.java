package com.wm.netty.intermediate.decode;

import com.wm.netty.intermediate.modal.LaopopoProtocol;
import com.wm.netty.intermediate.modal.RemotingTransporter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * 
 * @author BazingaLyn
 * @description Netty 对{@link RemotingTransporter}的解码器
 * @time 2016年8月10日
 * @modifytime
 */
public class RemotingTransporterDecoder extends ReplayingDecoder<RemotingTransporterDecoder.State> {
	
	private static final Logger logger = LoggerFactory.getLogger(RemotingTransporterDecoder.class);

	private static final int MAX_BODY_SIZE = 1024 * 1024 * 5;

	private final LaopopoProtocol header = new LaopopoProtocol();

	public RemotingTransporterDecoder() {
		//设置(下文#state()的默认返回对象)
		super(State.HEADER_MAGIC);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		System.out.println("进入到解密的地方了");
		switch (state()) {
		case HEADER_MAGIC:
			checkMagic(in.readShort()); // MAGIC
			checkpoint(State.HEADER_TYPE);
		case HEADER_TYPE :
			header.type(in.readByte());
			checkpoint(State.HEADER_SIGN);
		case HEADER_SIGN:
			header.sign(in.readByte()); // 消息标志位
			checkpoint(State.HEADER_ID);
		case HEADER_ID:
			header.id(in.readLong()); // 消息id
			checkpoint(State.HEADER_BODY_LENGTH);
		case HEADER_BODY_LENGTH:
			header.bodyLength(in.readInt()); // 消息体长度
			checkpoint(State.HEADER_COMPRESS);
		case HEADER_COMPRESS:
			header.setCompress(in.readByte()); // 消息是否压缩
			checkpoint(State.BODY);
		case BODY:
				int bodyLength = checkBodyLength(header.bodyLength());
				byte[] bytes = new byte[bodyLength];
				in.readBytes(bytes);
				out.add(RemotingTransporter.newInstance(header.id(), header.sign(),header.type(), bytes));
				break;
		default:
			break;
		}
		//checkpoint的方法作用有两个，一是改变state的值的状态，二是获取到最新的读指针的下标
		checkpoint(State.HEADER_MAGIC);
	}
	
	private int checkBodyLength(int bodyLength) throws RuntimeException {
		if (bodyLength > MAX_BODY_SIZE) {
            throw new RuntimeException("body of request is bigger than limit value "+ MAX_BODY_SIZE);
        }
        return bodyLength;
	}
	
	private void checkMagic(short magic) throws RuntimeException {
		if (header.MAGIC != magic) {
			logger.error("Magic is not match");
			System.out.println("Magic is not match");
            throw new RuntimeException("magic value is not equal "+header.MAGIC);
        }
	}

	enum State {
		HEADER_MAGIC, HEADER_TYPE, HEADER_SIGN, HEADER_ID, HEADER_BODY_LENGTH,HEADER_COMPRESS, BODY
	}

}
