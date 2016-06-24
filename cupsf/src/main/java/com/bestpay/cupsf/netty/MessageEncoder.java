package com.bestpay.cupsf.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created by HR on 2016/5/18.
 */
public class MessageEncoder extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        ByteBuf destBuf = Unpooled.buffer();
        destBuf.writeBytes(msg);
        out.add(destBuf);
    }
}
