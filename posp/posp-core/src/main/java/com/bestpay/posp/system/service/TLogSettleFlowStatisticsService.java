package com.bestpay.posp.system.service;

import com.bestpay.posp.system.entity.TLogSettleFlowStatistics;
import com.bestpay.posp.system.base.BaseService;

public interface TLogSettleFlowStatisticsService extends BaseService<TLogSettleFlowStatistics, Long> {
	
	/**
	 * 统计联机交易流水借记 和贷记总金额、总笔数
	 * @param TLogSettleFlowStatistics
	 * @return
	 */
	public TLogSettleFlowStatistics getLogSettleFlowStatistics(TLogSettleFlowStatistics logSettleFlowStatistics);
	
	/**
	 * 统计脱机交易流水借记和贷记总金额、总笔数
	 * @param TLogSettleFlowStatistics
	 * @return
	 */
	public TLogSettleFlowStatistics getOffilceLogSettleFlowStatistics(TLogSettleFlowStatistics logSettleFlowStatistics);

}
