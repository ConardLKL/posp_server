package com.bestpay.cupsf.service;

import com.bestpay.cupsf.entity.Configure;
import com.bestpay.cupsf.entity.CupsfBuffer;
import com.bestpay.cupsf.entity.MessageBuffer;
import com.bestpay.cupsf.protocol.IsoMessage;
import com.bestpay.cupsf.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 添加缓存信息
 * Created by HR on 2016/5/24.
 */
@Slf4j
public class BufferService {
    /**
     * posp交易信息加入缓存
     * @param iso
     */
    public static void addPospBuffer(IsoMessage iso){
        MessageBuffer buffer = new MessageBuffer();
        buffer.setCtx(iso.getChannelHandlerContext());
        buffer.setDate(new Date());
        buffer.setSerialNo(SerialNo.createSerialNo(iso));
        CupsfBuffer.posp.put(buffer.getSerialNo(),buffer);
    }

    /**
     * cup交易信息加入缓存
     * @param iso
     * @param message
     */
    public static void addCupBuffer(IsoMessage iso,byte[] message){
        MessageBuffer buffer = new MessageBuffer();
        buffer.setSerialNo(SerialNo.createSerialNo(iso));
        buffer.setMessage(message);
        CupsfBuffer.cup.put(buffer.getSerialNo(),buffer);
    }

    /**
     * 设置系统配置信息
     * @return
     */
    public static void setConfiger(){
        String localIp = PropertiesUtil.getProperty("local.ip");
        String localPort = PropertiesUtil.getProperty("local.port");
        String localTimeout = PropertiesUtil.getProperty("local.timeout");
        String bankIp = PropertiesUtil.getProperty("bank.ip");
        String bankPort = PropertiesUtil.getProperty("bank.port");
        String bankTimeout = PropertiesUtil.getProperty("bank.timeout");
        String localBankPort = PropertiesUtil.getProperty("local.bank.port");
        String mangerIp = PropertiesUtil.getProperty("manger.ip");
        String mangerPort = PropertiesUtil.getProperty("manger.port");
        Configure configure = new Configure();
        configure.localIp = localIp;
        configure.localPort = Integer.valueOf(localPort);
        configure.localTimeout = Integer.valueOf(localTimeout);
        configure.bankIp = bankIp;
        configure.bankPort = Integer.valueOf(bankPort);
        configure.bankTimeout = Integer.valueOf(bankTimeout);
        configure.localBankPort = Integer.valueOf(localBankPort);
        configure.mangerIp = mangerIp;
        configure.mangerPort = Integer.valueOf(mangerPort);
    }
}
