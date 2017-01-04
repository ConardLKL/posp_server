package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TInfoTermSignDao;
import com.bestpay.posp.system.entity.TInfoTermSign;
import com.bestpay.posp.system.service.TInfoTermSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TInfoTermSignServiceImpl implements TInfoTermSignService {
	@Autowired
	TInfoTermSignDao tInfoTermSignDao;

	@Override
	public boolean updatePospTermSign(TInfoTermSign tInfoTermSign) {
		// TODO Auto-generated method stub
		int i = tInfoTermSignDao.update(tInfoTermSign);
		if(i==1){
			return true;
		}
		return false;
	}

	@Override
	public TInfoTermSign getPospTermSign(TInfoTermSign tInfoTermSign) {
		// TODO Auto-generated method stub
		return tInfoTermSignDao.findUnique(tInfoTermSign);
	}

	@Override
	public boolean insertPospTermSign(TInfoTermSign tInfoTermSign) {
		// TODO Auto-generated method stub
		int i = tInfoTermSignDao.insert(tInfoTermSign);
		if(i==1){
			return true;
		}
		return false;
	}

}
