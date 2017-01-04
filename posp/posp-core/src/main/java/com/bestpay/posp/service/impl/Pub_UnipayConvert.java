package com.bestpay.posp.service.impl;

import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.UnionAPI;
import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.spring.PospApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Pub_UnipayConvert {

	@Autowired
	private Pub_UpdateTransactionRecord updateTransactionRecord;
	
	public IsoMessage unipay52(IsoMessage iso){
		try{
			iso.setState(true);
			if(!StringUtils.equals(iso.getField(22).substring(2), "1")){
				return iso;
			}
			UnionAPI unionAPI = (UnionAPI)PospApplicationContext.getBean("UnionAPI");
			String pinBlock = iso.getField(52);
			if(iso.isStateKeySign()
					&& StringUtils.isNotEmpty(iso.getField(62))
					&& StringUtils.equals(iso.getTranCode(), PosConstant.TRANS_TYPE_0200629147)) {
				pinBlock = iso.getField(62).substring(iso.getField(62).length() - 32);
			}else if(iso.isStateKeySign()
					&& StringUtils.isNotEmpty(iso.getField(62))){
				pinBlock = iso.getField(62).substring(10);
			}else if(iso.isStateKeySign()
					&& StringUtils.isEmpty(iso.getField(62))){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A030, iso);
				iso.setState(false);
				updateTransactionRecord.updatePublicFailure(iso);
				return iso;
			}
			if(iso.getField(52) != null){
				if(iso.isPlatform()){
					iso.getFlow().setUnipay52(unionAPI.unionTranslatePin(iso.getField(42)+iso.getField(41),
							pinBlock, iso.getField(2)));
				}else{
					iso.getFlow().setUnipay52(unionAPI.unionTranslatePin(iso.getField(32),
							pinBlock, iso.getField(2)));
				}
				if(StringUtils.equals(iso.getFlow().getUnipay52().substring(0,1),"-")){
					iso.setChannelCode(SysConstant.CAPITAL_POOL_4001);
					RespCodeInformation.getAndReturnRespCodeInfo(iso.getFlow().getUnipay52().substring(1), iso);
					iso.setState(false);
					updateTransactionRecord.updatePublicFailure(iso);
				}
				iso.setISO8859("ISO-8859-1");
				iso.setField(63,"SM016"+new String(HexCodec.hexDecode(iso.getFlow().getUnipay52()),"ISO-8859-1"));
				iso.getFlow().setUnipay52("0000000000000000");
			}else{
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A030, iso);
				iso.setState(false);
				updateTransactionRecord.updatePublicFailure(iso);
			}
			return iso;
		}catch(Exception e){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_B096, iso);
			iso.setState(false);
			log.error(e.getMessage());
			updateTransactionRecord.updatePublicFailure(iso);
			return iso;
		}
	}
	public static String decode(String bytes)
	{
		String hexString="0123456789ABCDEF";
		ByteArrayOutputStream baos=new ByteArrayOutputStream(bytes.length()/2);
		//将每2位16进制整数组装成一个字节
		for(int i=0;i<bytes.length();i+=2)
			baos.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1))));
		return new String(baos.toByteArray());
	}
}
