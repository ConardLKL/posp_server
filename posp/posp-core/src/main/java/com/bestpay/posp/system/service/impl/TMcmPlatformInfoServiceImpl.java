package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TMcmPlatformInfoDao;
import com.bestpay.posp.system.entity.TMcmPlatformInfo;
import com.bestpay.posp.system.service.TMcmPlatformInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("TMcmPlatformInfoService")
public class TMcmPlatformInfoServiceImpl implements TMcmPlatformInfoService {
	
	@Autowired 
	private TMcmPlatformInfoDao tMcmPlatformInfoDao;
	
	@Override
	public TMcmPlatformInfo getMcmPlatformInfo(TMcmPlatformInfo tMcmPlatformInfo) {
		return tMcmPlatformInfoDao.findUnique(tMcmPlatformInfo);
	}
}
