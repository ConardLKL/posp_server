package com.bestpay.cupsf.netty.client;

import com.bestpay.cupsf.entity.CupsfBuffer;
import com.bestpay.cupsf.entity.UnipayMessage;
import com.bestpay.cupsf.protocol.IsoMessage;
import com.bestpay.cupsf.service.BufferService;
import com.bestpay.cupsf.utils.HexCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by HR on 2016/5/31.
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof UnipayMessage) {
            UnipayMessage unipayMessage = (UnipayMessage) msg;
            String header = unipayMessage.getHeader();
            String body = unipayMessage.getBody();
            IsoMessage iso = new IsoMessage();
            iso.setPktdef(CupsfBuffer.pkt_def);
            iso.setMessage(HexCodec.hexDecode(body));
            BufferService.addCupBuffer(iso, HexCodec.hexDecode(header + body));
            iso.printMessage("RETURN");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.error(String.format("[%s] %s", cause.getMessage()));
        ctx.close();
    }
}
