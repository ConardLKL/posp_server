package com.bestpay.cupsf.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * Created by HR on 2016/6/3.
 */
@Slf4j
public class HeartBeatHandler extends IdleStateHandler {

    public HeartBeatHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        switch (evt.state()) {
            case WRITER_IDLE:
            case READER_IDLE:
                String message = "0000";
                ByteBuf msg = Unpooled.buffer();
                msg.writeBytes(message.getBytes());
                ctx.writeAndFlush(msg);
                log.info("[" + message+"]");
                break;
            default:
                break;
        }
    }
}
