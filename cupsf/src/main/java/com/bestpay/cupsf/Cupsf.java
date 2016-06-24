package com.bestpay.cupsf;

import com.bestpay.cupsf.netty.cupServer.CupServer;
import com.bestpay.cupsf.netty.pospServer.PospServer;
import com.bestpay.cupsf.protocol.PacketDefineInitializer;
import com.bestpay.cupsf.service.BufferService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Created by HR on 2016/5/17.
 */
public class Cupsf extends HttpServlet{

    @Override
    public void init() throws ServletException {
        try {
            BufferService.setConfiger();
            new PacketDefineInitializer().findPacketDefine();
            new CupServer().startup();
            new PospServer().startup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        init();
    }
}
