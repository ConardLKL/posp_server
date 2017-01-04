package com.bestpay.posp.system.dao;

import com.bestpay.posp.system.entity.TLogSettleFlowStatistics;

public interface TLogSettleFlowStatisticsDao {
	/**
	 * 统计联机交易流水借记 和贷记总金额、总笔数
	 * @return
	 */
	public TLogSettleFlowStatistics querySettleFlowStatistics(TLogSettleFlowStatistics tLogSettleFlowStatistics);
	/**
	 * 统计脱机交易流水借记和贷记总金额、总笔数
	 * @return
	 */
	public TLogSettleFlowStatistics queryOffliceSettleFlowStatistics(TLogSettleFlowStatistics tLogSettleFlowStatistics);
	
}
