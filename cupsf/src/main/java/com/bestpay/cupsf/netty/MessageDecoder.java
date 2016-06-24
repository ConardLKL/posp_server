package com.bestpay.cupsf.netty;

import com.bestpay.cupsf.entity.UnipayMessage;
import com.bestpay.cupsf.utils.HexCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by HR on 2016/5/18.
 */
@Slf4j
public class MessageDecoder extends MessageToMessageDecoder{
    @Override
    protected void decode(ChannelHandlerContext ctx, Object msg, List out){
        try {
            ByteBuf message = (ByteBuf) msg;
            ByteBuf copy = message.copy();
            ByteBuf heart = copy.readBytes(4);
            byte[] heartBeat = new byte[4];
            heart.getBytes(0, heartBeat);
            if (StringUtils.equals(new String(heartBeat), "0000")) {
                out.add(new String(heartBeat));
            } else {
                ByteBuf head = message.readBytes(50);
                byte[] header = new byte[50];
                head.getBytes(0, header);
                byte[] body = new byte[message.readableBytes()];
                message.readBytes(body);
                UnipayMessage unipayMessage = new UnipayMessage();
                unipayMessage.setHeader(HexCodec.hexEncode(header));
                unipayMessage.setBody(HexCodec.hexEncode(body));
                out.add(unipayMessage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
