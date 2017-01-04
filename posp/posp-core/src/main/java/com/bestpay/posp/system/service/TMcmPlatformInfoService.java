package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TMcmPlatformInfo;

/**
 * 电子化凭条
 * @author HR
 *
 */
public interface TMcmPlatformInfoService extends BaseService<TMcmPlatformInfo,Long> {
	
	/**
	 * 获取平台信息
	 * @param TMcmPlatformInfo
	 * @return
	 */
	public TMcmPlatformInfo getMcmPlatformInfo(TMcmPlatformInfo tMcmPlatformInfo);
}
