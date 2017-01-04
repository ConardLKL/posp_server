/**
 * 
 */
package com.bestpay.posp.service.impl;

import com.bestpay.posp.constant.CheckerConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.PacketDefineInitializer;
import com.bestpay.posp.constant.POSPConstant;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.tisson.sfip.api.message.SfipInvokerProcessorExceptionMessage;

/**
 * @author HR
 * 
 */
@Slf4j
@Service
public class Pub_Exception2PosT {

	@Autowired
	private Pub_UpdateTransactionRecord update;
	@Autowired
	@Qualifier("PacketDefineInitializer")
	private PacketDefineInitializer packetDefineInitializer;


	public IsoMessage createResponseMsg(
			SfipInvokerProcessorExceptionMessage sfipIPEM) {
		log.info("################系统异常################");
		IsoMessage out = (IsoMessage) sfipIPEM.getPayload();
		try {
			out.setMessageDefine(packetDefineInitializer.findPacketDefine(out
					.getTransformerTmpField().get("FROMPOS_0")+10));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			// 返回相关交易的错误返回码定义
			int file0 = Integer.parseInt(out.getTransformerTmpField().get(
					"FROMPOS_0")) + 10;
			out.setField(0, String.format("%04d", file0));
			out.setPospCode(POSPConstant.POSP_AA96);
			out.setRspCode(CheckerConstant.RESP_CODE_96);
			out.setField(39, out.getRspCode());
			out.setRspMsg("系统异常");
			if ("0330".equals(out.getField(0))
					|| "0510".equals(out.getField(0))// 管理类
//					|| "0630".equals(out.getField(0)) 
					|| "0810".equals(out.getField(0))
					|| "0830".equals(out.getField(0))) {
				update.updatePublicFailure(out);
			}else{
				update.updateFlowSign(out);
			}
//			update.insertDubious(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		in.printMessage(in.getSeq());
		StringBuffer buf = new StringBuffer();
		buf.delete(0, buf.length());
		buf.append("[E N D] SfipInvokerProcessorExceptionMessage:rootConsumer=");
		buf.append(sfipIPEM.getRootConsumer());
		buf.append(",currentConsumer="); 
		buf.append(sfipIPEM.getCurrentConsumer());
		buf.append(",historyConsumerLocus="); 
		buf.append(sfipIPEM.getHistoryConsumerLocus());
		buf.append(",exception="); 
		buf.append(sfipIPEM.getException().toString());
		log.error(buf.toString());
		
		return out;
	}
}
