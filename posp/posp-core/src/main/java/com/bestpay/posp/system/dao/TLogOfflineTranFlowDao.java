package com.bestpay.posp.system.dao;

import com.bestpay.posp.system.entity.TLogOfflineTranFlow;
import com.bestpay.posp.system.base.BaseDao;

public interface TLogOfflineTranFlowDao extends BaseDao<TLogOfflineTranFlow, Long> {

	/**
	 * 更新脱机表批结算状态
	 * @return
	 */
	public int updateBatchResult(TLogOfflineTranFlow logOfflineTranFlow);
}
