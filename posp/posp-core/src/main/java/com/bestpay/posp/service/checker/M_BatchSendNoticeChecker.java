package com.bestpay.posp.service.checker;

import java.math.BigDecimal;

import com.bestpay.posp.constant.CheckerConstant;
import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.system.entity.TLogTranFlow;
import com.bestpay.posp.system.service.TLogTranFlowService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bestpay.posp.service.impl.RespCodeInformation;

/**
 * 批上送通知交易
 * @author FENGLI
 *
 */
@Slf4j
@Component
public class M_BatchSendNoticeChecker {

	@Autowired
	TLogTranFlowService tLogTranFlowService;
	
	/**
	 * 批上送通知
	 * @param requestIso Pos机上送报文
	 * @return 处理结果
	 *  @throws Exception
	 *             by IsoMessage;
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public IsoMessage insertbatchSendNotice(IsoMessage requestIso){
		IsoMessage outIso=requestIso.clone();
		// 商户号
        String merchCode=requestIso.getField(42);
       // 终端号
        String termCode=requestIso.getField(41);
       // 批次号
        String batchNo=requestIso.getField(60).substring(2, 8);
        //交易流水号
        String termSerialNo=requestIso.getField(11);
        TLogTranFlow tLogTranFlow=new TLogTranFlow();
        tLogTranFlow.setMerchCode(merchCode);
        tLogTranFlow.setTermCode(termCode);
        tLogTranFlow.setBatchNo(batchNo);
        tLogTranFlow.setTermSerialNo(termSerialNo);
//      tLogTranFlow.setSerialNo(requestIso.getFlow().getSerialNo());
     // 根据条件获取原来交易流水
     // 根据对商户唯一条件获取对应终端流水
        TLogTranFlow logTranFlow=tLogTranFlowService.getLogTranFlow(tLogTranFlow);
		if (logTranFlow==null) {
			outIso.setState(false);
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A625,outIso);
			log.info("批上送通知：查找原始通知类交易失败");
			return outIso;
		}
		// 定义批量定义交易流水设定对象

		// 2：已批上送
		logTranFlow.setBatchState("2");
		BigDecimal orgTranAmt = new BigDecimal(String.format("%.2f",logTranFlow.getTranAmount()));
		BigDecimal upTranAmt = new BigDecimal(requestIso.getField(4)).movePointLeft(2);
		log.debug("批上送通知类交易交易记录金额：" + orgTranAmt.toString());
		log.debug("批上送通知类交易上送金额：" + upTranAmt.toString());
		if (orgTranAmt.compareTo(upTranAmt) ==0) {
			log.debug("批上送通知类交易金额匹配成功");
			// 3批上送正常
			logTranFlow.setBatchResult("3");
		} else {
			// 4批上送金额不符
			logTranFlow.setBatchResult("4");
			log.debug("批上送通知类交易金额匹配失败");
		}

		// 更新交易流水的批结状态和结果
		try {
			boolean updateCnt = tLogTranFlowService.updateLogTranFlow(logTranFlow);
			if (log.isDebugEnabled()){
				log.debug("批上送,更新交易流水批结算状态:" + updateCnt);
			}
			outIso.setPospCode(POSPConstant.POSP_00);
			outIso.setRspCode(CheckerConstant.RESP_CODE_00);
			return outIso;
		} catch (Exception e) {
			outIso.setState(false);
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_C696,outIso);
			log.warn(String.format("[%s] error：批上送,更新交易流水批结算状态失败! ", outIso.getSeq()));
			log.error(e.getMessage());
			return outIso;
		}
	}
	
}
