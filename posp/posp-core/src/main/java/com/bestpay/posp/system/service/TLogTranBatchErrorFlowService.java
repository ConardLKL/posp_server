package com.bestpay.posp.system.service;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TLogTranBatchErrorFlow;

public interface TLogTranBatchErrorFlowService extends BaseService<TLogTranBatchErrorFlow, Long> {

	/**
	 * 记录新的流水
	 * @return
	 */
	boolean insertLogTranBatchErrorFlow(TLogTranBatchErrorFlow tLogTranBatchErrorFlow);
	/**
	 * 查询流水是否存在
	 * true 流水存在,报错；false 流水不存在，可以进行下步操作
	 */
	boolean isLogTranBatchErrorFlow(TLogTranBatchErrorFlow tLogTranBatchErrorFlow);
	/**
	 * 更新流水
	 */
	boolean updateLogTranBatchErrorFlow(TLogTranBatchErrorFlow tLogTranBatchErrorFlow);
	
	/**
	 * 保存流水信息 
	 * @param iso
	 */
	public void saveLogTranBatchErrorFlow(IsoMessage iso);
	
	/**
	 * 更新流水
	 * @param iso
	 */
	public void updateLogTranBatchErrorFlow(IsoMessage iso);
	/**
	 *  查询最大序列数
	 * @param iso
	 */
	String getMaxSeqErrorNum(TLogTranBatchErrorFlow tLogTranBatchErrorFlow);
}
