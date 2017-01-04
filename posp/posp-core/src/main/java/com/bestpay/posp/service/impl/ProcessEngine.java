package com.bestpay.posp.service.impl;

import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.spring.PospApplicationContext;
import com.tisson.sfip.module.reboot.SfipContainerBootstrapUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.tisson.sfip.api.message.ForwardingRequest;
import com.tisson.sfip.api.message.ForwardingResponse;
import com.tisson.sfip.api.service.ForwardingConsumer;
import com.tisson.sfip.api.service.ForwardingConsumerProxy;
/**
 * 调用流程引擎
 * @author HR
 *
 */
@Slf4j
@Component
public class ProcessEngine {
	/**
	 * 调用流程引擎
	 * @param iso
	 * @return
	 */
	public IsoMessage callPospProcessEngine(IsoMessage iso){
		ForwardingResponse response = pospProcessEngine(iso);//调用POSP流程引擎
		IsoMessage out = checkPospProcessEngineResult(response);//检查调用结果
		return out;
	}
	
	/**
	 * 调用POSP流程引擎
	 * @param iso
	 * @return
	 */
	private ForwardingResponse pospProcessEngine(IsoMessage iso){
		log.info("[START]channelRead:tranCode=" + iso.getTranCode() + ",seq=" + iso.getSeq());
		ForwardingRequest esbRequest = new ForwardingRequest();
		String serviceName = setServiceName(iso.isPlatform(), iso.getTranCode());//业务调用处理
		esbRequest.setRootConsumer(serviceName);
		esbRequest.setCurrentConsumer(serviceName);
		esbRequest.setHistoryConsumerLocus("");
		esbRequest.setPayload(iso);
		ForwardingConsumer pospCall = new ForwardingConsumerProxy();
		pospCall.setBootCtx(PospApplicationContext.getCtx());
		ForwardingResponse response = null;
		try{
			//调用流程引擎
			response = pospCall.call(esbRequest);
		}catch(Exception e){
			e.printStackTrace();
			log.error("SfipHome="+SfipContainerBootstrapUtils.getSfipHome(),e.getMessage());
		}
		return response;
	}
	/**
	 * 调用POSP流程引擎结果检验
	 * @param response
	 * @return
	 */
	private IsoMessage checkPospProcessEngineResult(ForwardingResponse response){
		IsoMessage out = null;
		if (response != null && response.getPayload() != null
				&& response.getPayload() instanceof IsoMessage) {
			out = (IsoMessage) response.getPayload();
			log.info("ForwardingConsumer response :" + response.toString());
		}
		return out;
	}
	/**
	 * 第三方接入，管理类需要特殊处理
	 * 统一在前面加上“003”
	 * @param ServiceName
	 * @return
	 */
	private String changeServiceName(String ServiceName){
		if(ServiceName.subSequence(0, 4).equals(PosConstant.MSG_TYPE_0800_00)){
			ServiceName = "003"+ServiceName;
		}
		return ServiceName;
	}
	/**
	 * 业务调用处理
	 * @param isPlatform
	 * @param serviceName
     * @return
     */
	private String setServiceName(boolean isPlatform,String serviceName ){
		// 第三方平台接入，并且是管理类时，交易码特殊处理
		if(!isPlatform){
			serviceName = changeServiceName(serviceName);
		}
		// 部分电子签字交易会多次上送,交易码统一为一个交易处理
		if(PosConstant.TRANS_TYPE_SLIP.contains(serviceName)){
			serviceName = PosConstant.TRANS_TYPE_0820800;
		}
		return serviceName;
	}
}
