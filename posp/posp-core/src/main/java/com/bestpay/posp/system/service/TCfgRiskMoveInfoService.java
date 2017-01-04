package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TCfgRiskMoveInfo;

import java.util.List;

/**
 * 代理商基站信息表
 * @author HR
 *
 */
public interface TCfgRiskMoveInfoService extends BaseService<TCfgRiskMoveInfo, Long> {
	/**
	 * 生成告警信息
	 * @param tCfgRiskMoveInfo
	 * @return
	 */
	public boolean insertRiskMoveInfo(TCfgRiskMoveInfo tCfgRiskMoveInfo);
}
