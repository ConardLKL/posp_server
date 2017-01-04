package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TMcmMcttranspriv;

/**
 * 商户交易控制表
 * @author HR
 *
 */
public interface TMcmMcttransprivService extends BaseService<TMcmMcttranspriv, Long> {
	/**
	 * 获取商户交易控制表信息
	 * @param tCfgMerchTranControl
	 * @return
	 */
	public TMcmMcttranspriv getMcmMcttranspriv(TMcmMcttranspriv tMcmMcttranspriv);
}
