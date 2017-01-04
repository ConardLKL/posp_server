package com.bestpay.posp.service.checker;

import java.nio.ByteBuffer;
import java.util.List;

import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.system.entity.TInfoTermPara;
import com.bestpay.posp.utils.recombin.TLV;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.service.impl.RespCodeInformation;
import com.bestpay.posp.system.service.TInfoTermParaService;
import com.bestpay.posp.utils.recombin.HexBinary;
import com.bestpay.posp.utils.recombin.TLVDecoder;
import com.bestpay.posp.utils.recombin.TLVEncoder;
import com.bestpay.posp.utils.recombin.TLVList;

@Slf4j
@Component
public class M_DownParameterChecker {
	@Autowired
	TInfoTermParaService tInfoTermParaService;

	/**
     * IC卡参数下载
     */
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
	        // 60.3 网络管理信息码 ?返回380还是381
	        strfield60.append("380");
			output.setField(60, strfield60.toString());
	        // 62域 请求时为TLV格式的AID
	        TLVDecoder decoder = new TLVDecoder();
	        ByteBuffer byteBuff = ByteBuffer.wrap(HexBinary.decode(parameters.getField(62)));
	        TLVList tlvList = decoder.decode(byteBuff);
	        String aid = HexBinary.encode(tlvList.get("9F06").getValue());  
	        log.debug("应用AID：" + aid);
	        
	        // 62域返回报文拼写
	        TInfoTermPara tInfoTermPara=new TInfoTermPara();
	        tInfoTermPara.setAid(aid);
	        StringBuffer field62 = new StringBuffer();
	        TInfoTermPara data = tInfoTermParaService.findPospTermPara(tInfoTermPara);
	        if (data == null) {
	            // 没有该参数信息
	            field62.append("30");
	        } else {
	            // 后续有参数信息
	            field62.append("31");
	            //编辑TLV
	            TLVList returnList = new TLVList();
                TLV tlv9f06 = TLVEncoder.genTLV("9F06",data.getAid(),true);
                TLV tlvdf01 = TLVEncoder.genTLV("DF01",data.getAppSelectId(),true);
                // 应用有效期为9F08 pos规范的9F09是错误的
                TLV tlv9f09 = TLVEncoder.genTLV("9F08",data.getAppVersion(),true);
                TLV tlvdf11 = TLVEncoder.genTLV("DF11",data.getTacDefault(),true);
                TLV tlvdf12 = TLVEncoder.genTLV("DF12",data.getTacOnline(),true);
                TLV tlvdf15 = TLVEncoder.genTLV("DF15",data.getBiasRandValue(),true);               
                TLV tlvdf13 = TLVEncoder.genTLV("DF13",data.getTacDeny(),true);
                TLV tlv9f1b = TLVEncoder.genTLV("9F1B",data.getTermMinLimit(),true);
                TLV tlv9f7b = TLVEncoder.genTLV("9F7B",data.getTermEcashLimit(),true);
                TLV tlvdf16 = TLVEncoder.genTLV("DF16",data.getBiasRandMaxTargetPer(),true);
                TLV tlvdf17 = TLVEncoder.genTLV("DF17",data.getRandomTargetPer(),true);
                TLV tlvdf14 = TLVEncoder.genTLV("DF14",data.getDefDdol(),true);
                TLV tlvdf18 = TLVEncoder.genTLV("DF18",data.getTermOnlinePinCap(),true);
                TLV tlvdf19 = TLVEncoder.genTLV("DF19",data.getContactlessOfflineLimit(),true);
                TLV tlvdf20 = TLVEncoder.genTLV("DF20",data.getContactlessLimit(),true);
                TLV tlvdf21 = TLVEncoder.genTLV("DF21",data.getCvmLimit(),true);
                returnList.add(tlv9f06);
                returnList.add(tlvdf01);
                returnList.add(tlv9f09);
                returnList.add(tlvdf11);
                returnList.add(tlvdf12);
                returnList.add(tlvdf13);
                returnList.add(tlv9f1b);
                returnList.add(tlvdf15);
                returnList.add(tlvdf16);
                returnList.add(tlvdf17);
                returnList.add(tlvdf14);
                returnList.add(tlvdf18);
                returnList.add(tlv9f7b);
                returnList.add(tlvdf19);
                returnList.add(tlvdf20);
                returnList.add(tlvdf21);
                field62.append(HexBinary.encode(returnList.toBinary()));
	        }
	        /// 由于62域在签到中使用16进制bcd编码，因此使用此域的其他交易，如果使用asc作为返回值的话，
	        // 将string转为asc编码的string HexBinary
	        //output.setField62ReservedPrivate(HexBinary.encode(field62.toString().getBytes()));
			output.setField(62, field62.toString());
			output.setState(true);
			return output;
    	}catch(Exception e){
			log.error(e.getMessage());
    		RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_C296,output);
    		log.info(String.format("[%s] IC卡参数下载异常! ", output.getSeq()));
    		output.setState(false);
    		return output;
    	}
    }
    /**
     * IC卡参数查询
     */
    public IsoMessage queryParameterInfo(IsoMessage parameters){
    	IsoMessage output = parameters.clone();
    	try{
	        // 拼接60域，62域
	        //60域
	        //60.1 交易类型码 00
	        StringBuffer strfield60 = new StringBuffer();
	        strfield60.append("00");
	        // 60.2 批次号
	        strfield60.append(parameters.getField(60).substring(2,8));
	        // 60.3 网络管理信息码 ?返回382
	        strfield60.append("382");
			output.setField(60, strfield60.toString());
	        // 62域 请求时为TLV格式的AID
	        byte [] field62_bt = HexBinary.decode(parameters.getField(62));
	        String request_62 = "";
			request_62 = new String(field62_bt,"GBK");
	        if("1".equals(request_62.substring(0, 1))){//查询请求
		        StringBuffer field62 = new StringBuffer();
		        List<TInfoTermPara> datas = tInfoTermParaService.getPospTermPara();
		        // 后续有参数信息
		        if(datas!=null){
		        	int complete = Integer.parseInt(request_62.substring(1));//已经下载的条数
		        	if(datas.size()-complete > 10){//剩余不止10条aid
		        		field62.append("32");
		        	}else{
		        		field62.append("31");
		        	}
		            //编辑TLV
		            TLVList returnList = new TLVList();
	            	for(int i=0;i<datas.size();i++){
	            		if(i>=complete){
	            			TInfoTermPara data = datas.get(i);
	            			TLV tlv9f06 = TLVEncoder.genTLV("9F06",data.getAid(),true);
		                	returnList.add(tlv9f06);
	            		}
	            	}
	                field62.append(HexBinary.encode(returnList.toBinary()));
		        }else{
		        	field62.append("30");
		        }
				output.setField(62, field62.toString());
	        }
	        output.setState(true);
	        return output;
    	}catch(Exception e){
    		output.setState(false);
			log.error(e.getMessage());
    		RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_C396,output);
    		log.info(String.format("[%s] IC卡参数查询异常! ", output.getSeq()));
    		return output;
    	}
    }
}
