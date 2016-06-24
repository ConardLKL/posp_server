package cn.com.bestpay.posp.transport.client;

import cn.com.bestpay.posp.protocol.util.HexCodec;
import cn.com.bestpay.posp.system.entity.XmlMessage;
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

public class ClientBoot{
	private static final Logger log = LoggerFactory.getLogger(ClientBoot.class);
	private XmlMessage message;
	private int timeout = 50000;
//	private String HOST = "192.168.83.9";
	private String HOST = "172.25.132.26";
	private int PORT = 8000;
	
	public String call(XmlMessage message) {
		try{
			this.message = message;
			return connect();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
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
            log.info(String.format("[%s] 报文发往POSP平台成功" ,this.message.getTranCode()));
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
		String mobilPhone = message.getBody().element("REQUEST").elementText("UserID");
		byte[] sign = "MPOS".getBytes();
		StringBuilder pospMessage = new StringBuilder();
		pospMessage.append("0");
		pospMessage.append(mobilPhone);
		pospMessage.append(message.getSerialNo());
		pospMessage.append(message.getIso8583().substring(4));
		byte[] message = HexCodec.hexDecode(pospMessage.toString());
		ByteBuf destBuf = channelFuture.channel().alloc().buffer();
		destBuf.writeShort(sign.length + message.length);
		destBuf.writeBytes(sign);
		destBuf.writeBytes(message);
		return destBuf;
	}

}
