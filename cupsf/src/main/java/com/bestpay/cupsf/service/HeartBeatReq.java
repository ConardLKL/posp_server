package com.bestpay.cupsf.service;

import com.bestpay.cupsf.entity.CupsfBuffer;
import com.bestpay.cupsf.utils.HexCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 发起心跳
 * Created by HR on 2016/5/17.
 */
@Slf4j
public class HeartBeatReq{
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public HeartBeatReq(){
        executor.scheduleAtFixedRate(new HeartBeatTask(),
                2,30, TimeUnit.SECONDS);
    }

    /**
     * 发送心跳包（“0000”）
     */
    private class HeartBeatTask implements Runnable{

        @Override
        public void run() {
            CupsfBuffer.channel.writeAndFlush(buildHeartBeat());
        }

        /**
         * 心跳包内容
         * @return
         */
        private ByteBuf buildHeartBeat(){
            String message = "0000";
            ByteBuf msg = Unpooled.buffer();
            msg.writeBytes(message.getBytes());
            log.info("[" + message+"]");
            return msg;
        }

    }
}
