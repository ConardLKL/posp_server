package com.bestpay.cupsf.service;

import com.bestpay.cupsf.entity.Configure;
import com.bestpay.cupsf.utils.ByteUtil;
import com.bestpay.dubbo.CupsfService;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by HR on 2016/8/8.
 */
@Slf4j
public class CupsfServiceImpl implements CupsfService {
    @Override
    public byte[] send(byte[] message) {
        Socket socket = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        byte[] in8583 = null;
        try {
            socket = new Socket("127.0.0.1", Configure.localPort);
//            socket.setSoTimeout(6000);
            in  = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            out.write(message);
            out.flush();
            byte[] inLen = new byte[4];
            in.read(inLen,0, 4);
            int inMsgLen = Integer.parseInt(new String(inLen, "GBK"));
            byte[] inHeader = new byte[46];
            byte[] inBody = new byte[inMsgLen];

            in.read(inHeader, 0, 46);
            in.read(inBody, 0, inMsgLen);

            in8583 = new byte[inMsgLen];
            ByteUtil.setByteContext(in8583, inBody, inMsgLen, 0);
        } catch (Exception e) {
            log.error(e.getMessage());
        }finally{
            try {
                if(out != null)
                    out.close();
                if(in != null)
                    in.close();
                if(socket != null)
                    socket.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return in8583;
    }
}
