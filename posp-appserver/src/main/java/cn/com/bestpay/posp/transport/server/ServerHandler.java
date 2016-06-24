package cn.com.bestpay.posp.transport.server;

import cn.com.bestpay.posp.service.impl.MessageAnalysis;
import cn.com.bestpay.posp.service.impl.ServiceProcess;
import cn.com.bestpay.posp.spring.AppServer;
import cn.com.bestpay.posp.system.entity.XmlMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);
    private HttpRequest request;
    private String realIp;
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
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
            response.headers().set(CONTENT_TYPE, "application/xml;charset=GBK");
            response.headers().set(CONTENT_LENGTH,response.content().readableBytes());
            response.headers().set(SERVER,"Access-Guard-1000-Software/1.0");
            response.headers().set(CONNECTION,Values.CLOSE);
            ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            ctx.close();
        }
    }
	
	/**
	 * POSP前置业务流程
	 * @param ctx
	 * @param msg
	 * @return
	 */
	public ByteBuf channelReadProcess(ChannelHandlerContext ctx, ByteBuf msg) {
		MessageAnalysis messageAnalysis = (MessageAnalysis) AppServer.getBean("MessageAnalysis");
		ServiceProcess serviceProcess = (ServiceProcess)AppServer.getBean("ServiceProcess");
		ByteBuf destBuf = null;
		try {
			XmlMessage xmlMessage = messageAnalysis.xmldecode(msg);
			destBuf = serviceProcess.process(ctx,xmlMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return destBuf;
    }
}
