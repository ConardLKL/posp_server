package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.entity.TLogSignePuchaseInfo;
import com.bestpay.posp.system.service.TLogSignePuchaseInfoService;
import com.bestpay.posp.system.dao.TLogSignePuchaseInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("TLogSignePuchaseInfoService")
public class TLogSignePuchaseInfoServiceImpl implements TLogSignePuchaseInfoService {
	
	@Autowired 
	private TLogSignePuchaseInfoDao tLogSignePuchaseInfoDao;
	

	@Override
	public TLogSignePuchaseInfo getSignePuchaseInfo(
			TLogSignePuchaseInfo tLogSignePuchaseInfo) {
		return tLogSignePuchaseInfoDao.findUnique(tLogSignePuchaseInfo);
	}


	@Override
	public boolean insertSignePuchaseInfo(
			TLogSignePuchaseInfo tLogSignePuchaseInfo) {
		int i = tLogSignePuchaseInfoDao.insert(tLogSignePuchaseInfo);
		if(i == 1){
			return true;
		}
		return false;
	}


	@Override
	public boolean updateSignePuchaseInfo(
			TLogSignePuchaseInfo tLogSignePuchaseInfo) {
		int i = tLogSignePuchaseInfoDao.update(tLogSignePuchaseInfo);
		if(i == 1){
			return true;
		}
		return false;
	}
}
