package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TLogTranFlowH;

/**
 * 历史流水表
 * @author HR
 *
 */
public interface TLogTranFlowHService extends BaseService<TLogTranFlowH,Long> {
	
	/**
	 * 获取流水信息
	 * @param TLogTranFlow
	 * @return
	 */
	public TLogTranFlowH getLogTranFlowH(TLogTranFlowH tLogTranFlowH);
//	public TLogTranFlow getLogTranFlowH(TLogTranFlow tLogTranFlow);
	public boolean updateLogTranFlowH(TLogTranFlowH tLogTranFlowH);
}
