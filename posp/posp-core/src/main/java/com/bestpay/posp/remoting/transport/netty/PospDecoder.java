package com.bestpay.posp.remoting.transport.netty;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.service.impl.ServiceProcess;
import com.bestpay.posp.spring.PospApplicationContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 解析报文并设置来源基本信息
 * @author HR
 *
 */
@Slf4j
@Sharable
public class PospDecoder extends MessageToMessageDecoder<ByteBuf>{
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg,
			List<Object> out) throws Exception {
		log.info(">>>>>>>>>>Parse the tcp message>>>>>>>>>>");
		MessageAnalysis decode =(MessageAnalysis) PospApplicationContext.getBean("MessageAnalysis");
		ServiceProcess serviceProcess =(ServiceProcess)PospApplicationContext.getBean("ServiceProcess");
		IsoMessage iso = decode.decode(msg);//解析报文
		String xRealIp = serviceProcess.setXRealIp(ctx);
		if(iso == null){
			out.add(String.valueOf("refresh:"+xRealIp));
		}else {
			iso.setXRealIp(xRealIp);//设置远程IP
			serviceProcess.setNetworkChannel("TCP");//设置交易接入方式
			serviceProcess.setPlatform(iso);//设置平台标志
			iso.setIp(serviceProcess.setIp(ctx));//设置本机IP
			out.add(iso);
		}
	}
}
