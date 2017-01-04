package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TCfgTranControl;

/**
 * 交易控制表
 * @author HR
 *
 */
public interface TCfgTranControlService extends BaseService<TCfgTranControl, Long> {
	/**
	 * 获取交易控制表信息
	 * @param tCfgTranControl
	 * @return
	 */
	public TCfgTranControl getCfgTranControl(TCfgTranControl tCfgTranControl);
}
