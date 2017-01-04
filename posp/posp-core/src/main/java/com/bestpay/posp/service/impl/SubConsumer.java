/**
 * 
 */
package com.bestpay.posp.service.impl;

import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.utils.recombin.HexBinary;
import com.bestpay.posp.utils.recombin.TLVDecoder;
import com.bestpay.posp.utils.recombin.TLVList;
import com.tisson.sfip.api.message.ForwardingRequest;
import com.tisson.sfip.api.message.ForwardingResponse;
import com.tisson.sfip.api.service.ForwardingConsumer;
import com.tisson.sfip.api.service.ForwardingConsumerProxy;
import com.tisson.sfip.esb.service.AsyncForwardingConsumer;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

/**
 * @author yihaijun
 * 
 */
@Log4j
@Component
public class SubConsumer {

	@Autowired
	private ObtainObjectInformation obtainObjectInformation;

	public IsoMessage asyncCall(IsoMessage iso) {
		IsoMessage request = iso.clone();
		/**
		 * 记录流水
		 */
		request.setTranCode(request.getField(0)+request.getField(3).substring(0,2)+request.getField(25)+request.getField(60).substring(0,2));
		request.setChannelCode(SysConstant.CAPITAL_POOL_5001);
		DataManipulations dataManipulations = (DataManipulations) PospApplicationContext
				.getCtx().getBean("DataManipulations");
		try {
			dataManipulations.initialFlow(request);
		} catch (Exception e) {
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_B196, iso);
			log.error("InitializationFlow error :" + e.getMessage());
		}
		request.getFlow().setCardNo(obtainObjectInformation.decryptionAccountNumber(request.getFlow().getCardNo()));
		utField55(request);
		
		ForwardingConsumer pospCall = new ForwardingConsumerProxy();
		pospCall.setBootCtx(PospApplicationContext.getCtx());
//		ForwardingResponse response = null;
		ForwardingRequest esbRequest = createForwardingRequest(request);
		AsyncForwardingConsumer.append(esbRequest);
		return request;
	}

	public IsoMessage syncCall(IsoMessage iso) {
		IsoMessage request = iso.clone();
		/**
		 * 记录流水
		 */
		request.setChannelCode(SysConstant.CAPITAL_POOL_5001);
		DataManipulations dataManipulations = (DataManipulations) PospApplicationContext
				.getCtx().getBean("DataManipulations");
		try {
			dataManipulations.initialFlow(request);
		} catch (Exception e) {
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_B196, iso);
			log.error("InitializationFlow error :" + e.getMessage());
		}
		request.getFlow().setCardNo(obtainObjectInformation.decryptionAccountNumber(request.getFlow().getCardNo()));
		ForwardingConsumer pospCall = new ForwardingConsumerProxy();
		pospCall.setBootCtx(PospApplicationContext.getCtx());
		ForwardingResponse response = null;
		try {
			ForwardingRequest esbRequest = createForwardingRequest(iso);
			response = pospCall.call(esbRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (response != null && response.getPayload() != null
				&& response.getPayload() instanceof IsoMessage) {
			IsoMessage responseIso = (IsoMessage) response.getPayload();
			if (log.isDebugEnabled()) {
				log.debug("ForwardingConsumer response :" + response.toString());
			}
			return responseIso;
		}

		log.error("ForwardingConsumer response error!");
		return null;
	}

	private ForwardingRequest createForwardingRequest(IsoMessage iso) {
		ForwardingRequest esbRequest = new ForwardingRequest();
		esbRequest.setRootConsumer(iso.getTransformerTmpField().get("serviceName"));
		esbRequest.setCurrentConsumer(iso.getTransformerTmpField().get("serviceName"));
		esbRequest.setHistoryConsumerLocus("");
		esbRequest.setPayload(iso);
		return esbRequest;
	}
	
	private IsoMessage utField55(IsoMessage request){
		TLVDecoder decoder = new TLVDecoder();
	    TLVList tlvList = decoder.decode(ByteBuffer.wrap(HexBinary.decode(request.getField(55))));
//		if(!request.getFlow().getTranCode().equals("0400629147")){
		    String rid1 = HexBinary.encode(tlvList.get("95").getValue());
		    String rid2 = HexBinary.encode(tlvList.get("9F1E").getValue());
		    String rid3 = HexBinary.encode(tlvList.get("9F10").getValue());
		    String rid4 = HexBinary.encode(tlvList.get("9F36").getValue());
		    String rid = "9505"+rid1+"9F1E08"+rid2+"9F10"+String.format("%02d", Integer.valueOf(Integer.toHexString(rid3.length()/2)))+rid3+"9F3602"+rid4;
//		    if(request.getFlow().getTranCode().equals("0200629147")){
//		    	if(tlvList.get("DF31") !=null && !tlvList.get("DF31").equals("")){
//			    	String rid5 = HexBinary.encode(tlvList.get("DF31").getValue());
//			    	rid = "DF31"+rid.length()/2+rid;
//		    	}
//		    }
		    try {
				request.setField(55, rid);
			} catch (Exception e) {
				e.printStackTrace();
			}
//		}
		return request;
	}
}
