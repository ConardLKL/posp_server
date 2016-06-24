package com.bestpay.cupsf.netty.cupServer;

import com.bestpay.cupsf.entity.CupsfBuffer;
import com.bestpay.cupsf.entity.UnipayMessage;
import com.bestpay.cupsf.protocol.IsoMessage;
import com.bestpay.cupsf.service.BufferService;
import com.bestpay.cupsf.service.BusinessProcess;
import com.bestpay.cupsf.utils.HexCodec;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * Created by HR on 2016/5/19.
 */
@Slf4j
@ChannelHandler.Sharable
public class CupHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String) {
            String message = (String) msg;
            log.info("CUP:[" + message + "]");
        }
        if (msg instanceof UnipayMessage) {
            UnipayMessage unipayMessage = (UnipayMessage) msg;
            String header = unipayMessage.getHeader();
            String body = unipayMessage.getBody();
            IsoMessage iso = new IsoMessage();
            iso.setPktdef(CupsfBuffer.pkt_def);
            iso.setMessage(HexCodec.hexDecode(body));
            log.info("LOCAL_SERVER STAN:[" + iso.getField(37) + "]");
            byte[] message = HexCodec.hexDecode(header + body);
            if (StringUtils.equals(iso.getField(0), "0800")
                    && StringUtils.equals(iso.getField(70), "101")) {
                BusinessProcess businessProcess = new BusinessProcess();
                iso.printMessage("CUP");
                businessProcess.resetKey(iso, message);
            } else if (StringUtils.equals(iso.getField(0), "0820")
                    && (StringUtils.equals(iso.getField(70), "201")
                    || StringUtils.equals(iso.getField(70), "202"))) {
                BusinessProcess businessProcess = new BusinessProcess();
                iso.printMessage("CUP");
                businessProcess.dateSwitch(iso);
            } else {
                BufferService.addCupBuffer(iso, message);
                iso.printMessage("CUP");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        log.error(String.format("[%s]", cause.getMessage()));
        ctx.close();
    }
}
