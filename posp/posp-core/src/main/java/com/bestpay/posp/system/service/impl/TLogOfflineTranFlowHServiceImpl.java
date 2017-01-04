package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TLogOfflineTranFlowDao;
import com.bestpay.posp.system.dao.TLogOfflineTranFlowHDao;
import com.bestpay.posp.system.entity.TLogOfflineTranFlow;
import com.bestpay.posp.system.entity.TLogOfflineTranFlowH;
import com.bestpay.posp.system.service.TLogOfflineTranFlowHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("TLogOfflineTranFlowHService")
public class TLogOfflineTranFlowHServiceImpl implements
		TLogOfflineTranFlowHService {

	@Autowired
	private TLogOfflineTranFlowHDao tLogOfflineTranFlowHDao;
	@Autowired
	private TLogOfflineTranFlowDao tLogOfflineTranFlowDao;

	@Override
	public TLogOfflineTranFlowH getLogOfflineTranFlowH(
			TLogOfflineTranFlowH tLogOfflineTranFlowH) {
		// TODO Auto-generated method stub
		return tLogOfflineTranFlowHDao.findUnique(tLogOfflineTranFlowH);
	}

	@Override
	public TLogOfflineTranFlow getLogOfflineTranFlowH(
			TLogOfflineTranFlow tLogOfflineTranFlow) {
		// TODO Auto-generated method stub
		return tLogOfflineTranFlowDao.findUnique(tLogOfflineTranFlow);
	}
}
