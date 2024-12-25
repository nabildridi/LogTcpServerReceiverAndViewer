package org.nd.logging.tcp;

import org.nd.logging.services.MessageProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

@Component
public class MessagePipelineFactory implements PipelineFactory {
	
	@Autowired
	private MessageProcessingService messageProcessingService;

	private final int availableProcessors;
	private final EventExecutorGroup executors;

	/**
	 * Constructor fott {@link MessagePipelineFactory}
	 */
	public MessagePipelineFactory() {
		availableProcessors = Runtime.getRuntime().availableProcessors();
		executors = new DefaultEventExecutorGroup(availableProcessors);
	}

	/**
	 * Pipeline Factory method for channel initialization
	 */
	@Override
	public ChannelInitializer<SocketChannel> createInitializer() {

		return new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				// Create chanel pipeline
				ChannelPipeline pipeline = ch.pipeline();

				pipeline.addLast("decoder", new MessageDecoder());

				pipeline.addLast(executors, "handler",new MessageHandler(messageProcessingService));

			}

		};
	}

}
