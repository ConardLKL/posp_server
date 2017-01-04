package com.bestpay.posp.remoting.transport.netty;

import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.system.cache.ConfigCache;
import com.bestpay.posp.spring.PospApplicationContext;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.tisson.sfip.api.DefaultSfipApiContext;
import com.tisson.sfip.api.SfipApiContext;
import com.tisson.sfip.esb.service.AsyncForwardingConsumer;

/**
 * 
 * @date 2014-7-15
 */
@Slf4j
public class ServerHttpBoot implements Runnable {
	
	@Autowired
	@Qualifier("ConfigCache")
	private ConfigCache configCache;
	private static AsyncForwardingConsumer asyncForwardingConsumer;
	
	
	public void text(){
		
	}

	@Override
	public void run() {
		 
		int port  = Integer.valueOf(configCache.getParaValues(SysConstant.CL7001, "100015"));;
		int parentThreads = Integer.valueOf(configCache.getParaValues(SysConstant.CL7001, "100008"));
		int childThreads  = Integer.valueOf(configCache.getParaValues(SysConstant.CL7001, "100009"));
		
        EventLoopGroup parentGroup = new NioEventLoopGroup(parentThreads); 
        EventLoopGroup childGroup =  new NioEventLoopGroup(childThreads);
 
        
        try { 
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            
            serverBootstrap.group(parentGroup, childGroup).channel(NioServerSocketChannel.class).childHandler(
                    new ChannelInitializer<SocketChannel>() {  
                        @Override
                        public void initChannel(SocketChannel socketChannel) throws Exception {
                        	socketChannel.pipeline().addLast(new WriteTimeoutHandler(1)); 
                        	// server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                        	socketChannel.pipeline().addLast("Decoder",new HttpRequestDecoder());
                        	//定义缓冲数据量
                        	socketChannel.pipeline().addLast("aggegator",new HttpObjectAggregator(1024*1024*64));
                        	// server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                        	socketChannel.pipeline().addLast("Encoder",new HttpResponseEncoder());
                        	socketChannel.pipeline().addLast("handler",new HttpServerHandler());
                        }
                    }).option(ChannelOption.TCP_NODELAY,true)
					.option(ChannelOption.SO_KEEPALIVE, true);
            
			SfipApiContext sfipContext = new DefaultSfipApiContext();
			sfipContext.setContextName("SfipAppBootstrap");
			sfipContext.setContext(PospApplicationContext.getCtx());
			asyncForwardingConsumer = new AsyncForwardingConsumer();
			asyncForwardingConsumer.setSfipApiContext(sfipContext);
			asyncForwardingConsumer.start();
            
            ChannelFuture f = serverBootstrap.bind(port).sync();
    		log.info("POSP HTTP应用服务准备就绪");
            f.channel().closeFuture().sync();
        } catch (Exception e) {
        	e.printStackTrace();
        }finally {
            childGroup.shutdownGracefully();
            parentGroup.shutdownGracefully();
        }
	}
	
	public void startup(){
		new Thread(this).start();
	}
	
	public void destroy(){
		log.info("POSP HTTP应用服务开始退出服务");
		asyncForwardingConsumer.toBeginExit();
		PospApplicationContext.destroy();
		log.info("POSP HTTP应用服务退出服务完成");
	}
}
