package com.bestpay.posp.system.dao;

import com.bestpay.posp.system.base.BaseDao;
import com.bestpay.posp.system.entity.TLogTranFlowBatch;

public interface TLogTranFlowBatchDao extends BaseDao<TLogTranFlowBatch, Long> {

	/**
	 * 获取最大序列数
	 * @return
	 */
	public String findMaxSeqNum(TLogTranFlowBatch tLogTranFlowBatch);
}
