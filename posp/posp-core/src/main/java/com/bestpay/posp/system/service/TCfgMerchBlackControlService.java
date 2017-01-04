package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TCfgMerchBlackControl;

/**
 * 商户黑名单控制表
 * @author HR
 *
 */
public interface TCfgMerchBlackControlService extends
		BaseService<TCfgMerchBlackControl, Long> {
	/**
	 * 获取商户黑名单控制表信息
	 * @param tCfgMerchBlackControl
	 * @return
	 */
	public TCfgMerchBlackControl getCfgMerchBlackControl(TCfgMerchBlackControl tCfgMerchBlackControl);
}
