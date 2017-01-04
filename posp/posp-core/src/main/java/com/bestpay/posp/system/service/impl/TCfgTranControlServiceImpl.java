package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TCfgTranControlDao;
import com.bestpay.posp.system.entity.TCfgTranControl;
import com.bestpay.posp.system.service.TCfgTranControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TCfgTranControlServiceImpl implements TCfgTranControlService {
	
	@Autowired
	private TCfgTranControlDao tCfgTranControlDao;
	@Override
	public TCfgTranControl getCfgTranControl(TCfgTranControl tCfgTranControl) {
		// TODO Auto-generated method stub
		return tCfgTranControlDao.findUnique(tCfgTranControl);
	}

}
