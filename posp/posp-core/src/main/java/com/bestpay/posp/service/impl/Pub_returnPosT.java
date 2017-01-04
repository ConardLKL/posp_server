/**
 * 
 */
package com.bestpay.posp.service.impl;

import java.util.Map;

import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.util.HexCodec;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yihaijun
 * 
 */
@Slf4j
@Component
public class Pub_returnPosT {
	@Autowired
	private Pub_UpdateTransactionRecord update;
	
	public IsoMessage createResponseMsg(IsoMessage requestPosIso) {
		Map<String, String> transformerTmpField = requestPosIso.getTransformerTmpField();
		String infoLogMessage = transformerTmpField.get("infoLogMessage");

		StringBuffer buf = new StringBuffer();
		buf.delete(0, buf.length());
		buf.append("[E N D] returnPosT(");
		buf.append(requestPosIso.getFlow().getTranCode());
		buf.append(","); 
		buf.append(requestPosIso.getSeq());
		buf.append(") ");
		buf.append(infoLogMessage);
		if(log.isDebugEnabled()){
			log.debug(buf.toString());
		}
		log.info(String.format("[%s]"+infoLogMessage, requestPosIso.getSeq()));
		//管理类更新流水
		if ("0330".equals(requestPosIso.getField(0))
				|| "0510".equals(requestPosIso.getField(0))// 管理类
				|| "0810".equals(requestPosIso.getField(0))
				|| "0830".equals(requestPosIso.getField(0))) {
			update.updatePublicFailure(requestPosIso);
		}
		if(StringUtils.equals("0800371",requestPosIso.getTranCode())){
			//参数下载要求
			String header = HexCodec.hexEncode(requestPosIso.getPospMessage().getHeader());
			requestPosIso.getPospMessage().setHeader(HexCodec.hexDecode(header.substring(0, 5)+"5"+header.substring(6)));
		}else if(StringUtils.equals("0800381",requestPosIso.getTranCode())){
			//参数下载要求
			String header = HexCodec.hexEncode(requestPosIso.getPospMessage().getHeader());
			requestPosIso.getPospMessage().setHeader(HexCodec.hexDecode(header.substring(0, 5)+"9"+header.substring(6)));
		}
		return requestPosIso;
	}
}
