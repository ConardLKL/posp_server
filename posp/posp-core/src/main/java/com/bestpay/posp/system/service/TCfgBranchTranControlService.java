package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TCfgBranchTranControl;

/**
 * 机构交易控制表
 * @author HR
 *
 */
public interface TCfgBranchTranControlService extends
		BaseService<TCfgBranchTranControl, Long> {
	/**
	 * 获取机构交易控制表信息
	 * @param tCfgBranchTranControl
	 * @return
	 */
	public TCfgBranchTranControl getCfgBranchTranControl(TCfgBranchTranControl tCfgBranchTranControl);
}
