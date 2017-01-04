package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TCfgTermBlackControlDao;
import com.bestpay.posp.system.entity.TCfgTermBlackControl;
import com.bestpay.posp.system.service.TCfgTermBlackControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TCfgTermBlackControlServiceImpl implements TCfgTermBlackControlService {
	@Autowired
	private TCfgTermBlackControlDao tCfgTermBlackControlDao;
	@Override
	public TCfgTermBlackControl getCfgTermBlackControl(
			TCfgTermBlackControl tCfgTermBlackControl) {
		// TODO Auto-generated method stub
		return tCfgTermBlackControlDao.findUnique(tCfgTermBlackControl);
	}

}
