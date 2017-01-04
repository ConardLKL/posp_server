package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TStlCardBlackList;

/**
 * 卡号黑名单控制表
 * @author HR
 *
 */
public interface TStlCardBlackListService extends
		BaseService<TStlCardBlackList, Long> {
	/**
	 * 获取卡号黑名单控制表信息
	 * @param tCfgCardBlackControl
	 * @return
	 */
	public TStlCardBlackList getStlCardBlackList(TStlCardBlackList tStlCardBlackList);
}
