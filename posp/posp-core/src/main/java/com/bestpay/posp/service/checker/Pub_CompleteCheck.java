package com.bestpay.posp.service.checker;

import java.util.Iterator;

import com.bestpay.posp.protocol.IsoMessage;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.service.impl.Pub_UpdateTransactionRecord;
import com.bestpay.posp.service.impl.RespCodeInformation;

/**
 * @author HR
 * 
 */
@Slf4j
@Component
public class Pub_CompleteCheck {
	@Autowired
	private Pub_UpdateTransactionRecord updateTransactionRecord;
	public IsoMessage completeCheck(IsoMessage iso) {
		printHead(iso);
		cloneMessage(iso);
		if(CheckDomainIntegrity(iso)){
			iso.setState(true);
			log.info(String.format("[%s,%s] 报文域完整性检查通过！", iso.getTranCode(), iso.getSeq()));
			return iso;
		}
		iso.setState(false);
		updateTransactionRecord.updatePublicFailure(iso);
		log.info(String.format("[%s,%s] 报文域完整性检查不通过！", iso.getTranCode(), iso.getSeq()));
		return iso;
	}
	/**
	 * 打印开始信息
	 * @param iso
	 */
	private void printHead(IsoMessage iso){
		StringBuffer buf = new StringBuffer();
		buf.delete(0, buf.length());
		buf.append("[START] onMsg(");
		buf.append(iso.getFlow().getTranCode());
		buf.append(",");
		buf.append(iso.getSeq());
		buf.append(")");
		log.info(buf.toString());
	}
	/**
	 * 克隆报文--留待后用
	 * @param iso
	 */
	private void cloneMessage(IsoMessage iso){
		Iterator<Integer> it = iso.getHashMapField().keySet().iterator();
		while (it.hasNext()) {
			Integer key = it.next();
			iso.getTransformerTmpField().put("FROMPOS_" + key,iso.getHashMapField().get(key));
		}
	}
	/**
	 * 检查报文必需域的完整性
	 * @param iso
	 * @return
	 */
	private boolean CheckDomainIntegrity(IsoMessage iso){
		if(isMessageDomainNotNull(iso)){
			return true;
		}
		RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A030,iso);
		log.info(String.format("[%s,%s] 报文格式错误！",iso.getFlow().getTranCode(), iso.getSeq()));
		return false;
	}
	/**
	 * 必需域是否为空
	 * @param iso
	 * @return
	 */
	private boolean isMessageDomainNotNull(IsoMessage iso){
		//获取必需域
		String[] Domain = iso.getTransformerTmpField().get("checkPosPackets").split(",");
		int[] a = new int[Domain.length];
		for (int i = 0; i < a.length; i++) {
			a[i] = Integer.parseInt(Domain[i].trim());
			//检查每个必需域是否为空
			if(StringUtils.isEmpty(iso.getField(a[i]))){
				return false;
			}
		}
		return true;	
	}
}
