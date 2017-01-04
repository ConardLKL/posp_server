package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TCfgRiskWhiteListDao;
import com.bestpay.posp.system.entity.TCfgRiskWhiteList;
import com.bestpay.posp.system.service.TCfgRiskWhiteListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class TCfgRiskWhiteListServiceImpl implements TCfgRiskWhiteListService {
	
	@Autowired
	private TCfgRiskWhiteListDao tCfgRiskWhiteListDao;

	@Override
	public List<TCfgRiskWhiteList> getTCfgRiskWhiteList(TCfgRiskWhiteList tCfgRiskWhiteList) {
		
	   return tCfgRiskWhiteListDao.find(tCfgRiskWhiteList);
	}

}
