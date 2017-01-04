package com.bestpay.posp.service.impl;

import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.UnipayDefineInitializer;
import com.bestpay.posp.protocol.util.Utils;
import com.bestpay.posp.remoting.transport.client.ClientCall;
import com.bestpay.posp.remoting.transport.client.UnipayClientCall;
import com.bestpay.posp.service.unipay.UnipayInterface;
import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.system.cache.ConfigCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;

/**
 * 调银联
 * @author HR
 *
 */
@Slf4j
@Component
public class Pub_UtService implements UnipayInterface {

	@Autowired
	@Qualifier("UnipayDefineInitializer")
	public UnipayDefineInitializer unipayDefineInitializer;
	
	@Autowired
	private AssemblyMessage assemblyMessage;
	
	@Override
	public IsoMessage createUpiso(IsoMessage iso) throws Exception{
		iso = assemblyMessage.posToUnion(iso);
		log.info(String.format("[%s]银联域转化成功！",iso.getSeq()));
		return iso;
	}

	@Override
	public IsoMessage call(IsoMessage request) {
		ConfigCache cache = (ConfigCache)PospApplicationContext.getBean("ConfigCache");
		int timesOut = Integer.valueOf(cache.getParaValues(SysConstant.CL6001, "100021"));
		
		request.setState(false);
		//银联60域要求值域POS终端不同特殊处理
		try {
			try{
				request = createUpiso(request);
			}catch(Exception e){
				request.setState(true);
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A196, request);
				log.warn(String.format("[%s] 银联域转化失败! [%s] ", request.getSeq(), e.getMessage()));
				return request;
			}
			ClientCall clientCall = null;
			IsoMessage upRecv = null;
//			if(StringUtils.isNotEmpty(request.getPospMessage().getMpos())){
//				//银联线上入口
//				clientCall = new FullChannelClientCall(request);
//			}else{
				//银联线下入口
				clientCall = new UnipayClientCall(unipayDefineInitializer.findPacketDefine("6001"));
//			}
//			log.info("Send to unipay:"+request.getMessageWithHex().toString());
			try {
				upRecv = clientCall.call(request, timesOut);
			} catch (SocketTimeoutException e) {
				log.warn(String.format("[%s] Connect time out!", request.getSeq()));
			}
			if(Utils.isNull(upRecv)){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A098, request);
				log.info(String.format("[%s] 银联超时! ", request.getSeq()));
				return request;
			}
			try {
				upRecv.setCardProducts(upRecv.getField(60).substring(28,30));
			} catch (Exception e) {
				log.warn(String.format("[%s]卡产品域不存在"+e.getMessage(),request.getSeq()));
			}
			RespCodeInformation.getAndReturnRespCodeInfo(upRecv.getField(39), upRecv);
			log.info(String.format("[%s] 银联返回成功！",request.getSeq()));
			upRecv.setRspCode(upRecv.getField(39));
			upRecv.setPospCode(upRecv.getField(39));
			try{
				upRecv = assemblyMessage.unionToPos(upRecv);
			}catch(Exception e){
				upRecv.setChannelCode(SysConstant.CAPITAL_POOL_5001);
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A296, upRecv);
				log.error(String.format("[%s] 银联域返回转化失败! [%s] ", request.getSeq(), e.getMessage()));
			}
			upRecv.setState(true);
			
			return upRecv;
		} catch (Exception e) {
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A396, request);
			request.setState(true);
			log.error(String.format("[%s] 调用银联过程失败! [%s] ", request.getSeq(), e.getMessage()));
			return request;
		}
	}
}
