package com.bestpay.posp.remoting.transport.netty;

import com.bestpay.posp.system.cache.ConfigCache;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.spring.PospApplicationContext;

import com.tisson.sfip.api.DefaultSfipApiContext;
import com.tisson.sfip.api.SfipApiContext;
import com.tisson.sfip.esb.service.AsyncForwardingConsumer;

/**
 * 
 * @date 2014-7-15
 */
@Slf4j
public class ServerBoot implements Runnable {
	
	@Autowired
	@Qualifier("ConfigCache")
	private ConfigCache configCache;
	private static AsyncForwardingConsumer asyncForwardingConsumer;
	
	
	@Override
	public void run() {
		 
		
		int port  = Integer.valueOf(configCache.getParaValues(SysConstant.CL7001, "100007"));
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
                        	
                        	socketChannel.pipeline().addFirst("frameDecoder",new LengthFieldBasedFrameDecoder(80960,0,2,0,2));
                        	socketChannel.pipeline().addLast("Decoder",new PospDecoder());
                        	socketChannel.pipeline().addLast("Encoder",new PospEncoder());
                        	socketChannel.pipeline().addLast("handler",new ServerHandler());
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
    		log.info("POSP应用服务准备就绪");
            f.channel().closeFuture().sync();
        } catch (Throwable e) {
        	e.printStackTrace();
        	log.error("POSP start is Throwable.",e);
        }finally {
            childGroup.shutdownGracefully();
            parentGroup.shutdownGracefully();
        }
	}
	
	public void startup(){
		new Thread(this).start();
	}
	
	public void destroy(){
		log.info("POSP应用服务开始退出服务");
		asyncForwardingConsumer.toBeginExit();
		PospApplicationContext.destroy();
		log.info("POSP应用服务退出服务完成");
	}
}
