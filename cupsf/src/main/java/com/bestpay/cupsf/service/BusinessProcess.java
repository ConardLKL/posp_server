package com.bestpay.cupsf.service;

import com.bestpay.cupsf.Cupsf;
import com.bestpay.cupsf.entity.CupsfBuffer;
import com.bestpay.cupsf.protocol.IsoMessage;
import com.bestpay.cupsf.utils.ByteUtil;
import com.bestpay.cupsf.utils.HexCodec;
import com.bestpay.dubbo.PabsService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * Created by HR on 2016/5/31.
 */
@Slf4j
public class BusinessProcess {

    /**
     * 重置秘钥
     * @param iso
     */
    public void resetKey(IsoMessage iso,byte[] message) {
        try {
            BufferService.addPospBuffer(iso);
//        CupClient client = new CupClient();
//        ByteBuf msg = Unpooled.buffer();
//        msg.writeBytes(message);
//        client.connect(msg);
            PabsService pabsService = (PabsService) Cupsf.ctx.getBean("pabsService");
            byte[] msg = pabsService.send(message);
            if (msg != null) {
                byte[] body = new byte[msg.length - 50];
                ByteUtil.getByteContext(body, msg, msg.length - 50, 50);
                IsoMessage out = new IsoMessage();
                out.setPktdef(CupsfBuffer.pkt_def);
                out.setMessage(body);
                BufferService.addCupBuffer(out, msg);
                out.printMessage("RETURN");
            }
        }catch (Exception e) {
            log.error("resetKey send error:" + e.getMessage());
        }
    }

    /**
     * 日期切换
     * @param iso
     */
    public void dateSwitch(IsoMessage iso){
        try {
            iso.setField(0,String.format("%04d",Integer.valueOf(iso.getField(0))+10));
            iso.setField(39,"00");
            ByteBuf msg = assemblyMessage(iso);
            CupsfBuffer.channel.writeAndFlush(msg);
            iso.printMessage("RETURN");
        } catch (Exception e) {
            log.error("Date switch error:"+e.getMessage());
        }
    }

    /**
     * 组装返回报文
     * @param iso
     * @return
     */
    public static ByteBuf assemblyMessage(IsoMessage iso) {
        ByteBuf msg = Unpooled.buffer();
        try {
            byte[] body = iso.getMessage();
            byte[] header = Header.getHeader(body.length);
//            msg.writeShort(body.length + header.length);
            msg.writeBytes(header);
            msg.writeBytes(body);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Assembly message error:"+e.getMessage());
        }
        return msg;
    }

    /**
     * 银联报文头
     */
    private static class Header{
        private static int headerLength = 46; // 头长度
        private static int headerFlagAndVersion = 02; // 头标识和版本号
        // private int totalMessageLength = 0; // 整个报文长度
        // private int packetMessageLength = 0; // 包体报文长度
        private static String destinationID = "00010000"; // 目的ID
        private static String sourceID = "48370000"; // 源ID
        //	private String reservedforUse = null; // 保留使用
        private static int batchNumber = 0; // 批次号
        private static String transactionInfo = "00000000"; // 交易信息
        // private String userInfo = null; // 用户信息
        private static String rejectCode = "00000"; // 拒绝码
        private static byte[] reservedforUse = new byte[]{0x00,0x00,0x00};

        /**
         * 组装银联报文头
         * @param body_length
         * @return
         * @throws UnsupportedEncodingException
         */
        private static byte[] getHeader(int body_length) throws UnsupportedEncodingException {

            byte[] header = new byte[50];

            StringBuffer strText = new StringBuffer();

            String totalLength = String.format("%04d", body_length + 46);

            ByteUtil.setByteContext(header, totalLength.getBytes("GBK"), 4, 0);
//          Field1 头长度（Header Length）1
            byte f1 = (byte)headerLength;
            ByteUtil.setByteContext(header, f1, 4);

//          Field2 头标识和版本号（Header Flag and Version）1
            strText.append(headerFlagAndVersion);
            byte f2 = (byte)headerFlagAndVersion;
            ByteUtil.setByteContext(header, f2, 5);

//          Field3 整个报文长度（Total Message Length）4
            strText.append(totalLength);
            ByteUtil.setByteContext(header, totalLength.getBytes("GBK"), 4, 6);

//		    Field4 目的ID（Destination ID）11
            strText.append(String.format("%-11s", destinationID));
            ByteUtil.setByteContext(header, String.format("%-11s", destinationID).getBytes("GBK"), 11, 10);


//		    Field5 源ID（Source ID） 11
            strText.append(String.format("%-11s", sourceID));
            ByteUtil.setByteContext(header, String.format("%-11s", sourceID).getBytes("GBK"), 11, 21);

//		    Field6 保留使用（Reserved for Use） 3
            strText.append("000");

            ByteUtil.setByteContext(header, reservedforUse, 3, 32);

//		    Field7 批次号（Batch Number） 1
            strText.append(batchNumber);
            byte f7 = (byte)(batchNumber >> 8);
            ByteUtil.setByteContext(header, f7, 35);

//		    Field8 交易信息（Transaction Information） 8
            strText.append(String.format("%8s", transactionInfo));
            ByteUtil.setByteContext(header, String.format("%8s", transactionInfo).getBytes("GBK"), 8, 36);

//		    Field9 用户信息（User Information） 1
            strText.append("0");
            byte f9 = (byte)(0 >> 8);
            ByteUtil.setByteContext(header, f9, 44);

//		    Field10 拒绝码（Reject Code） 5
            strText.append(String.format("%5s", rejectCode));
            ByteUtil.setByteContext(header, String.format("%5s", rejectCode).getBytes("GBK"), 5, 45);
            return header;
        }
    }
}
