package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TMcmPostransprivDao;
import com.bestpay.posp.system.entity.TMcmPostranspriv;
import com.bestpay.posp.system.service.TMcmPostransprivService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TMcmPostransprivServiceImpl implements TMcmPostransprivService {
	@Autowired
	private TMcmPostransprivDao tMcmPostransprivDao;
	@Override
	public TMcmPostranspriv getMcmPostranspriv(TMcmPostranspriv tMcmPostranspriv) {
		// TODO Auto-generated method stub
		return tMcmPostransprivDao.findUnique(tMcmPostranspriv);
	}

}
