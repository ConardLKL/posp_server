package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TStlMerchantBlackList;

/**
 * 商户黑名单控制表
 * @author HR
 *
 */
public interface TStlMerchantBlackListService extends
		BaseService<TStlMerchantBlackList, Long> {
	/**
	 * 获取商户黑名单控制表信息
	 * @param tCfgCardBlackControl
	 * @return
	 */
	public TStlMerchantBlackList getStlMerchantBlackList(TStlMerchantBlackList tStlMerchantBlackList);
}
