package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TLogOfflineTranFlow;
import com.bestpay.posp.system.entity.TLogOfflineTranFlowH;

/**
 * 历史脱机流水表
 * @author HR
 *
 */
public interface TLogOfflineTranFlowHService extends BaseService<TLogOfflineTranFlowH,Long> {
	/**
	 * 获取历史脱机流水信息
	 * @param tLogOfflineTranFlowH
	 * @return
	 */
	public TLogOfflineTranFlowH getLogOfflineTranFlowH(TLogOfflineTranFlowH tLogOfflineTranFlowH);
	public TLogOfflineTranFlow getLogOfflineTranFlowH(TLogOfflineTranFlow tLogOfflineTranFlow);
}
