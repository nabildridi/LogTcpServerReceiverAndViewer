package org.nd.logging.tcp;

import org.nd.logging.dto.RawLog;
import org.nd.logging.services.MessageProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Component
public class MessageHandler extends SimpleChannelInboundHandler<Message> {

	
	private MessageProcessingService messageProcessingService;	
	

	public MessageHandler() {
		super();
	}

	public MessageHandler(MessageProcessingService messageProcessingService) {
		super();
		this.messageProcessingService = messageProcessingService;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

		handleMessage(ctx, msg);

	}

	/**
	 * Actual Message handling and reply to server.
	 * 
	 * @param ctx {@link ChannelHandlerContext}
	 * @param msg {@link Message}
	 */
	private void handleMessage(ChannelHandlerContext ctx, Message msg) {

		messageProcessingService.putInQueue(new RawLog(msg.getMessage(), ctx.channel().remoteAddress().toString()));

		/*
		 * ByteBuf buf = Unpooled.wrappedBuffer("".getBytes());
		 * 
		 * // Send reply final WriteListener listener = new WriteListener() {
		 * 
		 * @Override public void messageRespond(boolean success) { System.out.println(success ? "reply success" : "reply fail"); } };
		 * 
		 * ctx.writeAndFlush(buf).addListener(new ChannelFutureListener() {
		 * 
		 * @Override public void operationComplete(ChannelFuture future) throws Exception { if (listener != null) { listener.messageRespond(future.isSuccess());
		 * } } });
		 */

	}

	/**
	 * {@link WriteListener} is the lister message status interface.
	 * 
	 * @author Sameer Narkhede See <a href="https://narkhedesam.com">https://narkhedesam.com</a>
	 * @since Sept 2020
	 * 
	 */
	public interface WriteListener {
		void messageRespond(boolean success);
	}

}
