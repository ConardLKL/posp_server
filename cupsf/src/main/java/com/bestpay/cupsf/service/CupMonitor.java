package com.bestpay.cupsf.service;

import com.bestpay.cupsf.entity.CupsfBuffer;
import com.bestpay.cupsf.entity.MessageBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * cup缓存监控
 * Created by HR on 2016/5/24.
 */
@Slf4j
public class CupMonitor implements Runnable{
    @Override
    public void run() {
        while(true){
            try {
                if (CupsfBuffer.cup == null || CupsfBuffer.cup.size()<1) {
//                    Thread.sleep(100);
                    continue;
                }
                for (String serialNo : CupsfBuffer.cup.keySet()) {
                    if (CupsfBuffer.posp.containsKey(serialNo)) {
                        MessageBuffer messageBuffer = CupsfBuffer.posp.get(serialNo);
                        byte[] msg = CupsfBuffer.cup.get(serialNo).getMessage();
                        ByteBuf message = Unpooled.buffer();
                        message.writeBytes(msg);
                        if(messageBuffer.getCtx() != null) {
                            messageBuffer.getCtx().channel().writeAndFlush(message)
                                    .addListener(ChannelFutureListener.CLOSE);
                            messageBuffer.getCtx().close();
                        }else{
                            CupsfBuffer.channel.writeAndFlush(message);
                        }
                        CupsfBuffer.posp.remove(serialNo);
                        CupsfBuffer.cup.remove(serialNo);
                        log.info("已返回！");
                    } else {
                        CupsfBuffer.cup.remove(serialNo);
                        log.info("已删除！");
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public void startup(){
        new Thread(this).start();
    }
}
