package cn.com.bestpay.posp.transport.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import com.regaltec.nma.collector.common.socket.NmaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientHandler extends ChannelInboundHandlerAdapter{

	private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
	private String in = null;
	private NmaEvent evt = new NmaEvent();
	private boolean alreadyResponsed = false;

	public String getReturnMessage(int timeout) {
		if (!alreadyResponsed) {
			evt.waitEvent(timeout);
		}
		return in;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if(msg instanceof String){
			in = (String) msg;
//			log.info("reponse:"+ in);
			alreadyResponsed = true;
			evt.setEvent();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
		alreadyResponsed = true;
		evt.setEvent();
	}
}
