package com.bestpay.cupsf.netty.cupServer;

import com.bestpay.cupsf.entity.Configure;
import com.bestpay.cupsf.netty.LengthFieldFrameDecoder;
import com.bestpay.cupsf.netty.MessageDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 银联服务端
 * Created by HR on 2016/5/17.
 */
@Slf4j
public class CupServer  implements Runnable{

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
//                            ch.pipeline().addFirst("LoggingHandler",new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast("frameDecoder",new LengthFieldFrameDecoder(Integer.MAX_VALUE,0,4));
                            ch.pipeline().addLast("Decoder",new MessageDecoder());
                            ch.pipeline().addLast("Handler",new CupHandler());
                        }
                    });
            ChannelFuture f = b.bind(Configure.localBankPort).sync();
            if(f.isSuccess()) {
                log.info("CUP SERVER START OK :" + Configure.localBankPort);
            }
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            log.error("银联前置服务端（对接银联）关闭！");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void startup(){
        new Thread(this).start();
    }
}
