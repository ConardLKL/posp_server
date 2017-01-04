package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TCfgRiskWhiteList;

import java.util.List;

/**
 * 代理商基站信息表
 * @author HR
 *
 */
public interface TCfgRiskWhiteListService extends BaseService<TCfgRiskWhiteList, Long> {
	/**
	 * 获取代理商基站信息
	 * @param tCfgRiskWhiteList
	 * @return
	 */
	public List<TCfgRiskWhiteList> getTCfgRiskWhiteList(TCfgRiskWhiteList tCfgRiskWhiteList);
}
