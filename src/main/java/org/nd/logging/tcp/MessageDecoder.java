package org.nd.logging.tcp;

import java.util.List;

import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

public class MessageDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		int readableBytes = in.readableBytes();
		if (readableBytes <= 0) {
			return;
		}

		String msg = in.toString(CharsetUtil.UTF_8);
		in.readerIndex(in.readerIndex() + in.readableBytes());

		Message message = new Message();
		message.setMessage(msg);

		out.add(message);

	}

}
