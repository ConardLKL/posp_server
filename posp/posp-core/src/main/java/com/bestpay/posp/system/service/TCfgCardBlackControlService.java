package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TCfgCardBlackControl;

/**
 * 卡号黑名单控制表
 * @author HR
 *
 */
public interface TCfgCardBlackControlService extends
		BaseService<TCfgCardBlackControl, Long> {
	/**
	 * 获取卡号黑名单控制表信息
	 * @param tCfgCardBlackControl
	 * @return
	 */
	public TCfgCardBlackControl getCfgCardBlackControl(TCfgCardBlackControl tCfgCardBlackControl);
}
