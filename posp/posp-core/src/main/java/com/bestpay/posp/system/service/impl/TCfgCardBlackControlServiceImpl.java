package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.entity.TCfgCardBlackControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.system.dao.TCfgCardBlackControlDao;
import com.bestpay.posp.system.service.TCfgCardBlackControlService;

@Component
public class TCfgCardBlackControlServiceImpl implements TCfgCardBlackControlService{
	@Autowired
	private TCfgCardBlackControlDao tCfgCardBlackControlDao;
	@Override
	public TCfgCardBlackControl getCfgCardBlackControl(
			TCfgCardBlackControl tCfgCardBlackControl) {
		// TODO Auto-generated method stub
		return tCfgCardBlackControlDao.findUnique(tCfgCardBlackControl);
	}

}
