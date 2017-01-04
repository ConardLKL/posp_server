package com.bestpay.posp.remoting.transport.netty;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.service.impl.ServiceProcess;
import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.system.cache.ConfigCache;
import com.bestpay.posp.system.cache.RespCodeCache;
import com.bestpay.posp.utils.PropertiesUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component("ServerHandler")
public class ServerHandler extends ChannelInboundHandlerAdapter {
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	try {
    		if (msg instanceof IsoMessage) {
    			ByteBuf destBuf = serviceProcess(ctx, msg);
    			ctx.channel().writeAndFlush(destBuf).addListener(ChannelFutureListener.CLOSE);
	    	}
			if (msg instanceof String) {
				ByteBuf destBuf = serviceProcess(msg);
				ctx.channel().writeAndFlush(destBuf).addListener(ChannelFutureListener.CLOSE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("[E N D]channelRead Exception:" + e.getMessage());
		} finally{
			ctx.close();
		} 
    }

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		log.error(String.format("[%s] %s", cause.getMessage()));
		ctx.close();
	}
	/**
	 * 业务处理
	 * @param ctx
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public ByteBuf serviceProcess(ChannelHandlerContext ctx, Object msg) throws Exception{
		IsoMessage iso = (IsoMessage)msg;
		ServiceProcess serviceProcess = (ServiceProcess)PospApplicationContext.getBean("ServiceProcess");
		serviceProcess.process(iso);
		ByteBuf destBuf = serviceProcess.assemblyReturnMessage(ctx, iso);
		//连接银联超时不返回终端
		if(StringUtils.equals("98",iso.getField(39))
//				&& StringUtils.equals("5001",iso.getChannelCode())
				){
			ctx.close();
		}
		return destBuf;
	}
	/**
	 * 业务处理-刷新缓存
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public ByteBuf serviceProcess(Object msg)  throws Exception{
		String message = (String) msg;
		ServiceProcess serviceProcess = (ServiceProcess)PospApplicationContext.getBean("ServiceProcess");
		return serviceProcess.process(message);
	}
}
