package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TLogOfflineTranFlow;

/**
 * 脱机流水表
 * @author HR
 *
 */
public interface TLogOfflineTranFlowService extends BaseService<TLogOfflineTranFlow,Long> {
	/**
	 * 获取脱机流水信息
	 * @param TLogOfflineTranFlow
	 * @return
	 */
	public TLogOfflineTranFlow getLogOfflineTranFlow(TLogOfflineTranFlow tLogOfflineTranFlow);
	/**
	 * 更新脱机流水批结算信息状态
	 * @param TLogOfflineTranFlow
	 * @return
	 */
	public int updateLogOfflineTranFlow(TLogOfflineTranFlow tLogOfflineTranFlow);
	/**
	 * 更新脱机流水信息
	 * @param tLogOfflineTranFlow
	 * @return
	 */
	public int updateTLogOfflineTranFlow(TLogOfflineTranFlow tLogOfflineTranFlow);
	/**
	 * 插入脱机流水信息
	 * @param tLogOfflineTranFlow
	 * @return
	 */
	public boolean insertLogOfflineTranFlow(TLogOfflineTranFlow tLogOfflineTranFlow);
}
