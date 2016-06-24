package com.bestpay.cupsf.netty.client;

import com.bestpay.cupsf.entity.Configure;
import com.bestpay.cupsf.entity.CupsfBuffer;
import com.bestpay.cupsf.netty.MessageDecoder;
import com.bestpay.cupsf.netty.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 银联客户端
 * Created by HR on 2016/5/17.
 */
@Slf4j
public class CupClient implements Runnable{

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    public SocketChannel socketChannel;
    @Override
    public void run(){
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addFirst("LoggingHandler",new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast("IdleHandler",new IdleStateHandler(30,0,0));
                            ch.pipeline().addLast("Encoder",new MessageEncoder());
                            ch.pipeline().addLast("HeartBeatHandler",new HeartBeatHandler(30,0,0));
                        }
                    });
            ChannelFuture future =b.connect(new InetSocketAddress(Configure.bankIp,Configure.bankPort)).sync();
            socketChannel = (SocketChannel)future.channel();
            CupsfBuffer.channel = socketChannel;
            socketChannel.closeFuture().sync();
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            //所有资源释放完成以后，清空资源，再次发起重连操作
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        log.info("与服务器断开连接，尝试1秒后与服务器重新连接......");
                        TimeUnit.SECONDS.sleep(1);
                        startup();//发起重连操作
                    }catch (Exception e){
                        log.info("无法连接到服务器，尝试1秒后与服务器重新连接......");
                        log.error(e.getMessage());
                    }
                }
            });
        }
    }

    /**
     * 重置秘钥专用客户端
     * @param msg
     */
    public void connect(ByteBuf msg){
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("Encoder", new MessageEncoder());
                            ch.pipeline().addLast("Decoder", new MessageDecoder());
                            ch.pipeline().addLast("Handler", new ClientHandler());
                        }
                    });
            ChannelFuture future = b.connect(new InetSocketAddress(Configure.mangerIp, Configure.mangerPort)).sync();
            SocketChannel channel = (SocketChannel) future.channel();
            channel.writeAndFlush(msg);
            channel.closeFuture().sync();
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
    public void startup(){
        new Thread(this).start();
    }
}
