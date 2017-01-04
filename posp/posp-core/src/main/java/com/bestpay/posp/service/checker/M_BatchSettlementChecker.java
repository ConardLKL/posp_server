package com.bestpay.posp.service.checker;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.system.entity.TLogOfflineTranFlow;
import com.bestpay.posp.system.entity.TLogSettleFlowStatistics;
import com.bestpay.posp.system.service.TLogOfflineTranFlowService;
import com.bestpay.posp.system.service.TLogSettleFlowStatisticsService;
import com.bestpay.posp.system.service.TLogTranFlowService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bestpay.posp.service.impl.RespCodeInformation;
import com.bestpay.posp.system.entity.TLogTranFlow;

/**
 * 批结算交易检查
 */
@Slf4j
@Component
public class M_BatchSettlementChecker {

	@Autowired
	TLogSettleFlowStatisticsService tLogSettleFlowStatisticsService;
	@Autowired
	TLogTranFlowService logTranFlowService;
	@Autowired
	TLogOfflineTranFlowService logOfflineTranFlowService;

	
    /**
     * 批结算上送金额与流水记录总金额笔数对比
     * @param requestIso
     * @return
     */
	@Transactional(propagation=Propagation.REQUIRED)
	public IsoMessage updateBatchSettlementChecker(IsoMessage requestIso){
		IsoMessage outIso=requestIso.clone();    
		try{
			// 15域清算日期4位 MMDD获取                                  
			//requestIso.setTranDate(requestIso.getFlow().getTranDate().substring(4));
	        
	        /*拆分48域信息*/                                                              
	        /*内卡*/ 
	        String file48=requestIso.getField(48);
	        // 借记总金额N12                                                  
	        String strDebitSumI = file48.substring(0,12);  
	        // 借记总笔数N3                                                   
	        String strDebitCntI = file48.substring(12,15); 
	        // 贷记总金额                                                     
	        String strCreditSumI = file48.substring(15,27);
	        // 贷记总笔数                                                     
	        String strCreditCntI = file48.substring(27,30);
	        
	        //金额 单位 分 12位 使用Long(integer 超界)
	        //内卡
	        long debitSumI = Long.valueOf(strDebitSumI);
	        long creditSumI = Long.valueOf(strCreditSumI);
	        int debitCntI = Integer.parseInt(strDebitCntI);
	        int creditCntI = Integer.parseInt(strCreditCntI);
	
	        // 调用批结算统计方法
	        String merchCode=requestIso.getField(42);
	        String termCode=requestIso.getField(41);
	        String batchNo=requestIso.getField(60).substring(2, 8);
	        TLogSettleFlowStatistics logSettleFlowStatistics=new TLogSettleFlowStatistics();
	        logSettleFlowStatistics.setMerchCode(merchCode);
	        logSettleFlowStatistics.setTermCode(termCode);
	        logSettleFlowStatistics.setBatchNo(batchNo);
	        // 预授权完完成20 消费22 预授权完成离线24
	        TLogSettleFlowStatistics settleFlowStatistics
					=tLogSettleFlowStatisticsService.getLogSettleFlowStatistics(logSettleFlowStatistics);
	        // 添加交易响应码为00 的条件交易状态时0,1,9 正常 ,撤销，退货
	        //退货25 消费撤销23 预授权完成撤销21  PBOC小额现金充值 45,46,47
	        TLogSettleFlowStatistics offilceSettleFlowStatistics
					=tLogSettleFlowStatisticsService.getOffilceLogSettleFlowStatistics(logSettleFlowStatistics);
	
	        // 定义批量定义交易流水设定对象
	        TLogTranFlow logTranFlow = new TLogTranFlow();
	        logTranFlow.setBatchNo(batchNo);
	        logTranFlow.setMerchCode(merchCode);
	        logTranFlow.setTermCode(termCode);
	        // 1：已批结
	        logTranFlow.setBatchState("1");
	        logTranFlow.setSerialNo(requestIso.getFlow().getSerialNo());
	        
	        TLogOfflineTranFlow logOfflineTranFlow=new TLogOfflineTranFlow();
	        logOfflineTranFlow.setBatchNo(batchNo);
	        logOfflineTranFlow.setMerchCode(merchCode);
	        logOfflineTranFlow.setTermCode(termCode);
	        logOfflineTranFlow.setBatchState("1");
	        log.info("批结算上送借记金额：" + debitSumI);
	        log.info("批结算上送借记笔数：" + debitCntI);
	        log.info("批结算上送贷记金额：" + creditSumI);
	        log.info("批结算上送贷记笔数：" + creditCntI);
	        log.info("批结算中心借记金额：" + settleFlowStatistics.getDebitSum());
	        log.info("批结算中心借记笔数：" + settleFlowStatistics.getDebitCount());
	        log.info("批结算中心贷记金额：" + settleFlowStatistics.getCreditSum());
	        log.info("批结算中心贷记笔数：" + settleFlowStatistics.getCreditCount());
	        log.info("批结算中心脱机借记金额：" + offilceSettleFlowStatistics.getDebitSum());
	        log.info("批结算中心脱机借记笔数：" + offilceSettleFlowStatistics.getDebitCount());
	        String resCode="";
	        if ((settleFlowStatistics.getDebitCount().intValue()
					+ offilceSettleFlowStatistics.getDebitCount().intValue()) != debitCntI
	                || (settleFlowStatistics.getDebitSum() + offilceSettleFlowStatistics.getDebitSum()) != debitSumI
	                || settleFlowStatistics.getCreditCount().intValue() != creditCntI
	                || settleFlowStatistics.getCreditSum() != creditSumI) {
	        	
	            log.debug("批结算账不平");
	            resCode = "2";
	            // 拼接pos中心结算信息到48域
	            StringBuffer temp = new StringBuffer();
	            temp.append(String.format("%012d", settleFlowStatistics.getDebitSum()));
	            log.debug("temp1 = " + temp.toString());
	            temp.append(String.format("%03d", settleFlowStatistics.getDebitCount()));
	            log.debug("temp2 = " + temp.toString());
	            temp.append(String.format("%012d", settleFlowStatistics.getCreditSum()));
	            log.debug("temp3 = " + temp.toString());
	            temp.append(String.format("%03d", settleFlowStatistics.getCreditCount()));
	            log.debug("temp4 = " + temp.toString());
	            temp.append(resCode);
	            log.debug("temp5 = " + temp.toString());
	            // 拼接外卡部分
	            temp.append(file48.substring(31,61) + "1");
	            log.debug("temp6 = " + temp.toString());
				outIso.setField48(temp.toString());
	            // 批结算不平
	            logTranFlow.setBatchResult("2");
	            logOfflineTranFlow.setBatchResult("2");
	        } else {
	            // 对帐平 帐务信息原路返回+对帐平标志位1
	        	outIso.setField48(file48.substring(0,30) + "1" + file48.substring(31,61) + "1");
	
	            // 批结算平
	            logTranFlow.setBatchResult("1");
	            logOfflineTranFlow.setBatchResult("1");
	        }
	        // 更新交易流水的批结状态和结果
	       int updateCnt = logTranFlowService.updateBantchLogTranFlow(logTranFlow);
	       int updateCntoff=logOfflineTranFlowService.updateLogOfflineTranFlow(logOfflineTranFlow);
	        if (log.isDebugEnabled()){
	           log.debug("批结算,更新交易流水批结算状态:联机" + updateCnt+"脱机"+updateCntoff);
	        }
	        return outIso;
		}catch(Exception e){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_C896,outIso);
			log.warn(String.format("[%s] error：批结算交易检查异常! ", outIso.getSeq()));
			log.error(e.getMessage());
			return outIso;
		}
	}
}
