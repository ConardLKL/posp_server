package com.bestpay.posp.remoting.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class PospEncoder extends MessageToMessageEncoder<ByteBuf> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg,
			List<Object> out) throws Exception {
//		// TODO Auto-generated method stub
//		ByteBuf destBuf = ctx.alloc().buffer(msg.readableBytes() + 2 + 11);
////		String tpdu = "6000020000603100315046";
//		String tpdu = ;
//		byte[] tpduBytes = HexCodec.hexDecode(tpdu);
//		
//		destBuf.writeShort(msg.readableBytes() + 11);
//		destBuf.writeBytes(tpduBytes);
//		destBuf.writeBytes(msg);	
		
		
		ByteBuf destBuf = ctx.alloc().buffer(msg.readableBytes());
////	String tpdu = "6000020000603100315046";
//	String tpdu = ;
//	byte[] tpduBytes = HexCodec.hexDecode(tpdu);
//	
//	destBuf.writeShort(msg.readableBytes() + 11);
//	destBuf.writeBytes(tpduBytes);
	destBuf.writeBytes(msg);	
		out.add(destBuf);
	}
}
