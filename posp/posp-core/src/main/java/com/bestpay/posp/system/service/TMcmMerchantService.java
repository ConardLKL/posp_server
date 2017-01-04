package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TMcmMerchant;

/**
 * 商户信息表操作
 * @author YZH
 *
 */
public interface TMcmMerchantService extends BaseService<TMcmMerchant,Long> {
	/**
	 * 获取商户信息
	 * @param TMcmMerchant 条件
	 * @return 商户信息
	 */
	public TMcmMerchant getMcmMerchant(TMcmMerchant tMcmMerchant);
}
