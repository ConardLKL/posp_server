package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TCfgTermBlackControl;

/**
 * 终端黑名单控制表
 * @author HR
 *
 */
public interface TCfgTermBlackControlService extends
		BaseService<TCfgTermBlackControl, Long> {
	/**
	 * 获取终端黑名单控制表信息
	 * @param tCfgTermBlackControl
	 * @return
	 */
	public TCfgTermBlackControl getCfgTermBlackControl(TCfgTermBlackControl tCfgTermBlackControl);
}
