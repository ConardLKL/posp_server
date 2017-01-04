package com.bestpay.posp.remoting.transport.netty;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.service.impl.ServiceProcess;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.bestpay.posp.spring.PospApplicationContext;

@Slf4j
@Component("HttpPospDecoder")
public class HttpPospDecoder{
	
	/**
	 * 报文解析
	 * @param ctx
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public IsoMessage decode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		log.info(">>>>>>>>>>Parse the http message>>>>>>>>>>");
		MessageAnalysis decode =(MessageAnalysis)PospApplicationContext.getBean("MessageAnalysis"); 
		ServiceProcess serviceProcess =(ServiceProcess)PospApplicationContext.getBean("ServiceProcess");
		IsoMessage iso = decode.HttpDecode(msg);
		serviceProcess.setNetworkChannel("HTTP");
		serviceProcess.setPlatform(iso);
		return iso;
	}
}
