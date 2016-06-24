package com.bestpay.cupsf;


import com.bestpay.cupsf.netty.cupServer.CupServer;
import com.bestpay.cupsf.netty.pospServer.PospServer;
import com.bestpay.cupsf.protocol.PacketDefineInitializer;
import com.bestpay.cupsf.service.BufferService;

/**
 * Created by HR on 2016/5/21.
 */
public class nettyTest {
    public static void main(String[] args){
        try {
            BufferService.setConfiger();
            new PacketDefineInitializer().findPacketDefine();
            new CupServer().startup();
            new PospServer().startup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args){
//        String s = "0334";
//        byte[] b = s.getBytes();
//        System.out.println(new String(b));
//    }
}
