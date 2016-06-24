package cn.com.bestpay.posp.transport.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;
public class ClientEncoder extends MessageToMessageEncoder<ByteBuf> {
	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg,
			List<Object> out) throws Exception {
		
		ByteBuf destBuf = ctx.alloc().buffer(msg.readableBytes());
		destBuf.writeBytes(msg);
		out.add(destBuf);
	}
}
