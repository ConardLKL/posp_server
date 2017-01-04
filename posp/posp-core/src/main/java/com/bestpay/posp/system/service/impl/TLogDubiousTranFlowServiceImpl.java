package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TLogDubiousTranFlowDao;
import com.bestpay.posp.system.entity.TLogDubiousTranFlow;
import com.bestpay.posp.system.service.TLogDubiousTranFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TLogDubiousTranFlowServiceImpl implements TLogDubiousTranFlowService {

	@Autowired
	TLogDubiousTranFlowDao tLogDubiousTranFlowDao;
	@Override
	public boolean insertLogDubiousTranFlow(
			TLogDubiousTranFlow tLogDubiousTranFlow) {
		// TODO Auto-generated method stub
		int i = tLogDubiousTranFlowDao.insert(tLogDubiousTranFlow);
		if(i==1){
			return true;
		}
		return false;
	}

}
