package cn.com.bestpay.posp.transport.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

import cn.com.bestpay.posp.protocol.util.HexCodec;
/**
 * 解析报文
 * @author HR
 *
 */
public class ClientDecoder extends MessageToMessageDecoder<ByteBuf>{
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg,
			List<Object> out) throws Exception {
		byte[] buffer = new byte[msg.readableBytes()];
		msg.readBytes(buffer);
		out.add(HexCodec.hexEncode(buffer));
	} 
}
