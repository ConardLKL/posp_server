package com.bestpay.posp.remoting.transport.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

public class Listener implements Runnable {
	
	@Override
	public void run() {
		
        EventLoopGroup parentGroup = new NioEventLoopGroup(); 
        EventLoopGroup childGroup = new NioEventLoopGroup(); 
             
        try { 
            ServerBootstrap serverBootstrap = new ServerBootstrap(); 
            serverBootstrap.group(parentGroup, childGroup).channel(NioServerSocketChannel.class).childHandler(
                    new ChannelInitializer<SocketChannel>() {  
                        @Override
                        public void initChannel(SocketChannel socketChannel) throws Exception {
                        	socketChannel.pipeline().addLast(new ReadTimeoutHandler(10));  
                        	socketChannel.pipeline().addLast(new WriteTimeoutHandler(1));  
                        	socketChannel.pipeline().addFirst("frameDecoder",new LengthFieldBasedFrameDecoder(80960,0,2,0,2));
                        	socketChannel.pipeline().addLast("Decoder",new PospDecoder());
                        	//socketChannel.pipeline().addLast("Encoder",new PospEncoder());
                            socketChannel.pipeline().addLast("handler",new ServerHandler());
                        }
                    }).option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = serverBootstrap.bind(8000).sync();
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
	}
 
}
