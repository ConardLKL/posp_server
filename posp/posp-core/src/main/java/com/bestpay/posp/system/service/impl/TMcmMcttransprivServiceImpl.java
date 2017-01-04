package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TMcmMcttransprivDao;
import com.bestpay.posp.system.entity.TMcmMcttranspriv;
import com.bestpay.posp.system.service.TMcmMcttransprivService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TMcmMcttransprivServiceImpl implements TMcmMcttransprivService {
	@Autowired
	private TMcmMcttransprivDao tMcmMcttransprivDao;
	@Override
	public TMcmMcttranspriv getMcmMcttranspriv(TMcmMcttranspriv tMcmMcttranspriv) {
		// TODO Auto-generated method stub
		return tMcmMcttransprivDao.findUnique(tMcmMcttranspriv);
	}

}
