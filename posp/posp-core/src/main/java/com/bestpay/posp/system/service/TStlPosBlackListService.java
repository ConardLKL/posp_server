package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TStlPosBlackList;

/**
 * 终端黑名单控制表
 * @author HR
 *
 */
public interface TStlPosBlackListService extends
		BaseService<TStlPosBlackList, Long> {
	/**
	 * 获取终端黑名单控制表信息
	 * @param tCfgCardBlackControl
	 * @return
	 */
	public TStlPosBlackList getStlPosBlackList(TStlPosBlackList tStlPosBlackList);
}
