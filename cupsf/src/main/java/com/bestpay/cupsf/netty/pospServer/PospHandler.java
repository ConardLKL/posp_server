package com.bestpay.cupsf.netty.pospServer;

import com.bestpay.cupsf.entity.Configure;
import com.bestpay.cupsf.entity.CupsfBuffer;
import com.bestpay.cupsf.entity.UnipayMessage;
import com.bestpay.cupsf.protocol.IsoMessage;
import com.bestpay.cupsf.service.BufferService;
import com.bestpay.cupsf.utils.HexCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * posp服务端
 * Created by HR on 2016/5/19.
 */
@Slf4j
@ChannelHandler.Sharable
public class PospHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof UnipayMessage) {
            log.info("---------------START---------------");
            UnipayMessage unipayMessage = (UnipayMessage) msg;
            String header = unipayMessage.getHeader();
            String body = unipayMessage.getBody();
            IsoMessage iso = new IsoMessage();
            iso.setPktdef(CupsfBuffer.pkt_def);
            iso.setMessage(HexCodec.hexDecode(body));
            iso.setChannelHandlerContext(ctx);
            if(StringUtils.isEmpty(iso.getField(33))
                    || StringUtils.equals(Configure.field_33,iso.getField(33))) {
                ByteBuf byteBuf = Unpooled.buffer();
                byteBuf.writeBytes(HexCodec.hexDecode(header + body));
                BufferService.addPospBuffer(iso);
                CupsfBuffer.channel.writeAndFlush(byteBuf);
            }else{
                log.warn("不支持的机构："+ iso.getField(33));
                ctx.close();
            }
            iso.printMessage("CLIENT");
            log.info("LOCAL_CLIENT STAN:["+iso.getField(37)+"]");
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        log.error(String.format("[%s] %s", cause.getMessage()));
        ctx.close();
    }
}
