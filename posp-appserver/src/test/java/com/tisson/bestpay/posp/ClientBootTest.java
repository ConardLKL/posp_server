package com.tisson.bestpay.posp;

import cn.com.bestpay.posp.protocol.util.HexCodec;
import cn.com.bestpay.posp.system.entity.XmlMessage;
import cn.com.bestpay.posp.transport.client.ClientDecoder;
import cn.com.bestpay.posp.transport.client.ClientEncoder;
import cn.com.bestpay.posp.transport.client.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientBootTest {
	private static final Logger log = LoggerFactory.getLogger(ClientBootTest.class);
	private XmlMessage message;
	private int timeout = 50000;
	private String HOST = "127.0.0.1";
//	private int PORT = 1688;
//	private String HOST = "172.25.132.26";
	private int PORT = 8000;
	
	public static void main(String[] args) {
		ClientBootTest client = new ClientBootTest();
		try{
			client.connect();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 发送并做业务处理
	 * @return
	 * 
	 * @throws Exception
	 */
	private String connect() throws Exception{
		final ClientHandler handler = new ClientHandler();
		String out = null;
        //配置客户端线程组
        EventLoopGroup group=new NioEventLoopGroup();
        try{
            //配置客户端启动辅助类
            Bootstrap b=new Bootstrap();
            b.group(group);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                	//字符串类解析
                	socketChannel.pipeline().addLast("encoder",new ClientEncoder());
                	socketChannel.pipeline().addLast("decoder",new ClientDecoder());
                    //设置网络IO处理器
                 	socketChannel.pipeline().addLast(handler);
                }
            });
            //发起异步服务器连接请求 同步等待成功
            ChannelFuture f=b.connect(this.HOST,this.PORT).sync();
            f.channel().writeAndFlush(assemblyPospMessage(f));
//            log.info(String.format("[%s] 报文发往POSP平台成功" ,this.message.getTranCode()));
            out = handler.getReturnMessage(timeout);
            f.channel().close();
	        //等到客户端链路关闭
	        f.channel().closeFuture().sync();
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
            //优雅释放线程资源
            group.shutdownGracefully();
        }
        return out;
    }
	private ByteBuf assemblyPospMessage(ChannelFuture channelFuture){
		String mobilPhone = "18612345678";
		byte[] sign = "MPOS".getBytes();
		StringBuilder pospMessage = new StringBuilder();
		pospMessage.append("0");
//		pospMessage.append("MPOSP");
		pospMessage.append(mobilPhone);
		pospMessage.append("123456789987654321");
//		pospMessage.append("6000060000603100000000080000000000");
		byte[] message = HexCodec.hexDecode(pospMessage.toString());
//		int len = HexCodec.hexDecode(pospMessage.toString()).length;
		ByteBuf destBuf = channelFuture.channel().alloc().buffer();
		destBuf.writeShort(sign.length + message.length);
//		destBuf.writeShort(message.length);
		destBuf.writeBytes(sign);
		destBuf.writeBytes(message);
		return destBuf;
	}

}
