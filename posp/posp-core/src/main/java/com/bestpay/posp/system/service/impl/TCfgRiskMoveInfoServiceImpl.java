package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TCfgRiskMoveInfoDao;
import com.bestpay.posp.system.entity.TCfgRiskMoveInfo;
import com.bestpay.posp.system.service.TCfgRiskMoveInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class TCfgRiskMoveInfoServiceImpl implements TCfgRiskMoveInfoService {
	
	@Autowired
	private TCfgRiskMoveInfoDao tCfgRiskMoveInfoDao;


	@Override
	public boolean insertRiskMoveInfo(TCfgRiskMoveInfo tCfgRiskMoveInfo) {
		int i = tCfgRiskMoveInfoDao.insert(tCfgRiskMoveInfo);
		if(i==1){
			return true;
		}
		return false;
	}
}
