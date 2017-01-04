package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.entity.TCfgBranchTranControl;
import com.bestpay.posp.system.dao.TCfgBranchTranControlDao;
import com.bestpay.posp.system.service.TCfgBranchTranControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TCfgBranchTranControlServiceImpl implements TCfgBranchTranControlService {
	@Autowired
	private TCfgBranchTranControlDao tCfgBranchTranControlDao;
	@Override
	public TCfgBranchTranControl getCfgBranchTranControl(
			TCfgBranchTranControl tCfgBranchTranControl) {
		return tCfgBranchTranControlDao.findUnique(tCfgBranchTranControl);
	}

}
