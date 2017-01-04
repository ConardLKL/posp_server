package com.bestpay.posp.remoting.transport.netty;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.SERVER;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import com.bestpay.posp.protocol.IsoMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import com.bestpay.posp.spring.PospApplicationContext;
@Slf4j
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private HttpRequest request;
    private String realIp;
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
		throws Exception{
        if (msg instanceof HttpRequest) {
        	//http报文header
            request = (HttpRequest) msg;
            List<String> list = request.headers().getAll("X-Real-IP");
            realIp = list.toString().substring(1, list.toString().length()-1);
            log.info("ip:" + realIp);
            log.info("request:" + request.toString());
        }
        if (msg instanceof HttpContent) {
        	//http报文正文
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            //调用业务流程
            ByteBuf destBuf = channelReadProcess(ctx, buf);
            buf.release();
            //组装响应报文
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,OK, destBuf);
            response.headers().set(CONTENT_TYPE, "x-ISO-TPDU/x-auth");
            response.headers().set(CONTENT_LENGTH,response.content().readableBytes());
            response.headers().set(SERVER,"Access-Guard-1000-Software/1.0");
            response.headers().set(CONNECTION,Values.CLOSE);
            ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            ctx.close();
        }
    }
	
	/**
	 * POSP公网业务流程
	 * @param ctx
	 * @param msg
	 * @return
	 */
	public ByteBuf channelReadProcess(ChannelHandlerContext ctx, Object msg) {
		ServerHandler serverHandler = (ServerHandler) PospApplicationContext.getBean("ServerHandler");
		HttpPospDecoder httpPospDecoder = (HttpPospDecoder)PospApplicationContext.getBean("HttpPospDecoder");
		IsoMessage iso = new IsoMessage();
		ByteBuf destBuf = null;
    	try {
    		iso = httpPospDecoder.decode(ctx, (ByteBuf)msg);
    		iso.setXRealIp(realIp);//记录终端IP
    		destBuf = serverHandler.serviceProcess(ctx, iso);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("[E N D]channelRead Exception:" + e.getMessage());
		} 
    	return destBuf;
    }
}
