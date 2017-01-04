package com.bestpay.posp.service.checker;

import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.service.impl.RespCodeInformation;
import com.bestpay.posp.system.entity.TInfoNonSecretPara;
import com.bestpay.posp.system.service.TInfoNonSecretParaService;
import com.bestpay.posp.utils.recombin.HexBinary;
import com.bestpay.posp.utils.recombin.TLV;
import com.bestpay.posp.utils.recombin.TLVEncoder;
import com.bestpay.posp.utils.recombin.TLVList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 非接业务参数下载
 * Created by HR on 2016/10/13.
 */
@Slf4j
@Component
public class M_DownNonSecretParaChecker {

    @Autowired
    private TInfoNonSecretParaService tInfoNonSecretParaService;


    public IsoMessage downParameterInfo(IsoMessage parameters){
        IsoMessage output = parameters.clone();
        try{
            // 拼接60域，62域
            //60域
            //60.1 交易类型码 00
            StringBuffer strfield60 = new StringBuffer();
            strfield60.append("00");
            // 60.2 批次号
            strfield60.append(parameters.getField(60).substring(2,8));
            // 60.3 网络管理信息码
            strfield60.append("394");
            output.setField(60, strfield60.toString());

            // 62域返回报文拼写
            TInfoNonSecretPara tInfoNonSecretPara=new TInfoNonSecretPara();
//            tInfoTermPara.setAid(aid);
            StringBuffer field62 = new StringBuffer();
            TInfoNonSecretPara data = tInfoNonSecretParaService.findInfoNonSecretPara(tInfoNonSecretPara);
                //编辑TLV
            TLVList returnList = new TLVList();
            //非接交易通道开关
            TLV tlvff805d = TLVEncoder.genTLV("FF805D",data.getNonContactSwitch());
            //闪卡当笔重刷处理时间
            TLV tlvff803a = TLVEncoder.genTLV("FF803A",data.getQuickRepaitTime());
            //闪卡记录可处理时间
            TLV tlvff803c = TLVEncoder.genTLV("FF803C",data.getQuickRecordTime());
            //非接快速业务（QPS）免密限额
            TLV tlvff8058 = TLVEncoder.genTLV("FF8058",data.getQpsNonSecretAmount());
            //非接快速业务标识
            TLV tlvff8054 = TLVEncoder.genTLV("FF8054",data.getQpsNonSecretLabel());
            //BIN表A标识
            TLV tlvff8055 = TLVEncoder.genTLV("FF8055",data.getBinALabel());
            //BIN表B标识
            TLV tlvff8056 = TLVEncoder.genTLV("FF8056",data.getBinBLabel());
            //CDCVM标识
            TLV tlvff8057 = TLVEncoder.genTLV("FF8057",data.getCdcvmLabel());
            //免签限额
            TLV tlvff8059 = TLVEncoder.genTLV("FF8059",data.getNonSignLimit());
            //免签标识
            TLV tlvff805a = TLVEncoder.genTLV("FF805A",data.getNonSignLabel());
            returnList.add(tlvff803a);
            returnList.add(tlvff803c);
            returnList.add(tlvff8059);
            returnList.add(tlvff8054);
            returnList.add(tlvff8055);
            returnList.add(tlvff8056);
            returnList.add(tlvff8057);
            returnList.add(tlvff805d);
            returnList.add(tlvff8058);
            returnList.add(tlvff805a);
            field62.append(HexBinary.encode(returnList.toBinary()));
            /// 由于62域在签到中使用16进制bcd编码，因此使用此域的其他交易，如果使用asc作为返回值的话，
            // 将string转为asc编码的string HexBinary
            //output.setField62ReservedPrivate(HexBinary.encode(field62.toString().getBytes()));
            output.setField(62, field62.toString());
            output.setState(true);
            return output;
        }catch(Exception e){
            log.error(e.getMessage());
            RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_C296,output);
            log.info(String.format("[%s] 非接业务参数下载异常! ", output.getSeq()));
            output.setState(false);
            return output;
        }
    }
}
