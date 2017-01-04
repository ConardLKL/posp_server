package com.bestpay.posp.system.dao;

import com.bestpay.posp.system.base.BaseDao;
import com.bestpay.posp.system.entity.TLogTranBatchErrorFlow;

public interface TLogTranBatchErrorFlowDao extends BaseDao<TLogTranBatchErrorFlow, Long> {
	
	/**
	 * 获取最大序列数
	 * @return
	 */
	public String findMaxSeqNumError(TLogTranBatchErrorFlow tLogTranBatchErrorFlow);

}
