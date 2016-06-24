package com.bestpay.cupsf.netty.pospServer;

import com.bestpay.cupsf.entity.Configure;
import com.bestpay.cupsf.netty.LengthFieldFrameDecoder;
import com.bestpay.cupsf.netty.MessageDecoder;
import com.bestpay.cupsf.netty.MessageEncoder;
import com.bestpay.cupsf.netty.client.CupClient;
import com.bestpay.cupsf.service.CupMonitor;
import com.bestpay.cupsf.service.HeartBeatReq;
import com.bestpay.cupsf.service.PospMonitor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by HR on 2016/5/17.
 */
@Slf4j
public class PospServer implements Runnable{

    /**用于分配处理业务线程的线程组个数 */
    protected static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors()*2;	//默认
    /** 业务出现线程大小*/
    protected static final int BIZTHREADSIZE = 100;
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE);
    @Override
    public void run() {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addFirst("LoggingHandler",new LoggingHandler(LogLevel.INFO));
                        ch.pipeline().addLast("ReadTimeOut",new ReadTimeoutHandler(60));
                        ch.pipeline().addLast("FrameDecoder",new LengthFieldFrameDecoder(Integer.MAX_VALUE,0,4));
                        ch.pipeline().addLast("Decoder",new MessageDecoder());
                        ch.pipeline().addLast("Encoder",new MessageEncoder());
                        ch.pipeline().addLast("Handler",new PospHandler());
                    }
                });
            ChannelFuture f = b.bind(Configure.localPort).sync();
            if(f.isSuccess()) {
                log.info("POSP SERVER START OK :" + Configure.localPort);
            }
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            log.error("银联前置服务端（对接POSP）关闭！");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void startup(){
        new Thread(this).start();
        new CupClient().startup();
//        new HeartBeatReq();//发起心跳
        new CupMonitor().startup();
        new PospMonitor().startup();
    }
}
