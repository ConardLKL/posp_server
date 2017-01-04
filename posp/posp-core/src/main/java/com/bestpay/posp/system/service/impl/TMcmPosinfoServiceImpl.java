package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TMcmPosinfoDao;
import com.bestpay.posp.system.service.TMcmPosinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.system.entity.TMcmPosinfo;

@Component
public class TMcmPosinfoServiceImpl implements TMcmPosinfoService {
	@Autowired
	TMcmPosinfoDao tMcmPosinfoDao;
	@Override
	public TMcmPosinfo getMcmPosinfo(TMcmPosinfo tMcmPosinfo) {
		// TODO Auto-generated method stub
		return tMcmPosinfoDao.findUnique(tMcmPosinfo);
	}
	@Override
	public boolean updateMcmPosinfo(TMcmPosinfo tMcmPosinfo) {
		// TODO Auto-generated method stub
		int i = tMcmPosinfoDao.update(tMcmPosinfo);
		if(i==1){
			return true;
		}
		return false;
	}

}
