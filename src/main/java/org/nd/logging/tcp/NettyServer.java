package org.nd.logging.tcp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class NettyServer {

	@Value("${logging.server.port}")
	private int port;

	private EventLoopGroup bossLoopGroup;

	private EventLoopGroup workerLoopGroup;

	private ChannelGroup channelGroup;

	@Autowired
	private MessagePipelineFactory messagePipelineFactory;

	@PostConstruct
	public void init() {
		// Initialization private members

		this.bossLoopGroup = new NioEventLoopGroup();

		this.workerLoopGroup = new NioEventLoopGroup();

		this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

		try {
			startup();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void startup() throws Exception {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossLoopGroup, workerLoopGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
				.option(ChannelOption.AUTO_CLOSE, true).option(ChannelOption.SO_REUSEADDR, true).childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.TCP_NODELAY, true);

		ChannelInitializer<SocketChannel> initializer = messagePipelineFactory.createInitializer();

		bootstrap.childHandler(initializer);

		try {
			ChannelFuture channelFuture = bootstrap.bind(port).sync();
			channelGroup.add(channelFuture.channel());
		} catch (Exception e) {
			shutdown();
			throw e;
		}
	}

	@PreDestroy
	public final void shutdown() {
		try {
			channelGroup.close();
			bossLoopGroup.shutdownGracefully();
			workerLoopGroup.shutdownGracefully();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
