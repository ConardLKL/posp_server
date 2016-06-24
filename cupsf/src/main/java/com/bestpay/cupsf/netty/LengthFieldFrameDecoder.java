package com.bestpay.cupsf.netty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

/**
 * Created by HR on 2016/5/25.
 */
public class LengthFieldFrameDecoder extends LengthFieldBasedFrameDecoder {

    public LengthFieldFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    protected long getUnadjustedFrameLength(ByteBuf buf, int offset, int length, ByteOrder order) {
        buf = buf.order(order);
        long frameLength;
        switch (length) {
            case 1:
                frameLength = buf.getUnsignedByte(offset);
                break;
            case 2:
                frameLength = buf.getUnsignedShort(offset);
                break;
            case 3:
                frameLength = buf.getUnsignedMedium(offset);
                break;
            case 4:
                frameLength = getUnsignedInt(buf);
//                frameLength = buf.getUnsignedInt(offset);
                break;
            case 8:
                frameLength = buf.getLong(offset);
                break;
            default:
                throw new DecoderException(
                        "unsupported lengthFieldLength: " + length + " (expected: 1, 2, 3, 4, or 8)");
        }
        return frameLength;
    }
    private int getUnsignedInt(ByteBuf buf){
        int length = 0;
        byte[] message = new byte[4];
        buf.getBytes(0,message);
        try {
            length = Integer.valueOf(new String(message));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return length;
    }
}
