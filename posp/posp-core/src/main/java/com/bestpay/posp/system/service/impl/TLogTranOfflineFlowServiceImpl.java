package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TLogOfflineTranFlowDao;
import com.bestpay.posp.system.entity.TLogOfflineTranFlow;
import com.bestpay.posp.system.service.TLogOfflineTranFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("TLogTranOfflineFlowService")
public class TLogTranOfflineFlowServiceImpl implements TLogOfflineTranFlowService {
	@Autowired
	private TLogOfflineTranFlowDao tLogOfflineTranFlowDao;

	@Override
	public TLogOfflineTranFlow getLogOfflineTranFlow(TLogOfflineTranFlow tLogOfflineTranFlow) {
		return tLogOfflineTranFlowDao.findUnique(tLogOfflineTranFlow);
	}

	@Override
	public int updateLogOfflineTranFlow(TLogOfflineTranFlow tLogOfflineTranFlow) {
		return tLogOfflineTranFlowDao.updateBatchResult(tLogOfflineTranFlow);
	}

	@Override
	public boolean insertLogOfflineTranFlow(TLogOfflineTranFlow tLogOfflineTranFlow) {
		int i = tLogOfflineTranFlowDao.insert(tLogOfflineTranFlow);
		if(i == 1){
			return true;
		}
		return false;
	}

	@Override
	public int updateTLogOfflineTranFlow(TLogOfflineTranFlow tLogOfflineTranFlow) {
		return tLogOfflineTranFlowDao.update(tLogOfflineTranFlow);
	}
	
	
}
