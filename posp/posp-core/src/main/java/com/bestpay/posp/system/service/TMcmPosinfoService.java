package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TMcmPosinfo;

/**
 * 商户终端POS信息表
 * @author YZH
 *
 */
public interface TMcmPosinfoService extends BaseService<TMcmPosinfo, Long> {
	/**
	 * 获取终端POS信息
	 * @param tMcmPosinfo
	 * @return
	 */
	public TMcmPosinfo getMcmPosinfo(TMcmPosinfo tMcmPosinfo);
	/**
	 * 更新终端签到信息
	 * @param tMcmPosinfo
	 * @return
     */
	public boolean updateMcmPosinfo(TMcmPosinfo tMcmPosinfo);
}
