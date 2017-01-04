package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TLogTranFlowDao;
import com.bestpay.posp.system.entity.TLogTranFlowH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.system.dao.TLogTranFlowHDao;
import com.bestpay.posp.system.service.TLogTranFlowHService;

@Component("TLogTranFlowHService")
public class TLogTranFlowHServiceImpl implements TLogTranFlowHService{
	
	@Autowired
	private TLogTranFlowHDao tLogTranFlowHDao;
	@Autowired
	private TLogTranFlowDao tLogTranFlowDao;
	@Override
	public TLogTranFlowH getLogTranFlowH(TLogTranFlowH tLogTranFlowH) {
		// TODO Auto-generated method stub
		return tLogTranFlowHDao.findUnique(tLogTranFlowH);
	}
//	@Override
//	public TLogTranFlow getLogTranFlowH(TLogTranFlow tLogTranFlow) {
//		return tLogTranFlowDao.findUnique(tLogTranFlow);
//	}
	
	@Override
	public boolean updateLogTranFlowH(TLogTranFlowH tLogTranFlowH) {
		int i = tLogTranFlowHDao.update(tLogTranFlowH);
		if(i == 1){
			return true;
		}
		return false;
	}

}
