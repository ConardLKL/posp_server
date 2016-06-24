package com.bestpay.cupsf.service;

import com.bestpay.cupsf.protocol.IsoMessage;
import org.apache.commons.lang.StringUtils;

/**
 * 生成交易唯一标识号
 * Created by HR on 2016/5/27.
 */
public class SerialNo {
    /**
     * 生成唯一标识号
     * @param iso
     * @return
     */
    public static String createSerialNo(IsoMessage iso){
        StringBuffer serialNo = new StringBuffer();
        serialNo.append(iso.getField(11));
        if(StringUtils.equals(iso.getField(0),"0620")
                || StringUtils.equals(iso.getField(0),"0630")){
            serialNo.append(iso.getField(2));
            serialNo.append(iso.getField(12));
        }else if(StringUtils.equals(iso.getField(0),"0820")
                || StringUtils.equals(iso.getField(0),"0830")
                || StringUtils.equals(iso.getField(0),"0800")
                || StringUtils.equals(iso.getField(0),"0810")){
            serialNo.append(iso.getField(70));
//        }else if(StringUtils.equals(iso.getField(0),"0420")){
//            serialNo.append(iso.getField(37));
        }else{
            serialNo.append(iso.getField(37));
        }
        return serialNo.toString();
    }
}
