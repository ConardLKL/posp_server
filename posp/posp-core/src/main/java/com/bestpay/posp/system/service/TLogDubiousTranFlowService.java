package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TLogDubiousTranFlow;

/**
 * 联机交易流水表
 * @author HR
 *
 */
public interface TLogDubiousTranFlowService extends
		BaseService<TLogDubiousTranFlow, Long> {

	/**
	 * 高于预警金额时 插入数据
	 * @param tLogDubiousTranFlow
	 * @return
	 */
	public boolean insertLogDubiousTranFlow(TLogDubiousTranFlow tLogDubiousTranFlow);
	
	
}
