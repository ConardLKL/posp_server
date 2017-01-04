package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TInfoNonSecretParaDao;
import com.bestpay.posp.system.entity.TInfoNonSecretPara;
import com.bestpay.posp.system.service.TInfoNonSecretParaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TInfoNonSecretParaServiceImpl implements TInfoNonSecretParaService {
	@Autowired
	TInfoNonSecretParaDao tInfoNonSecretParaDao;


	public List<TInfoNonSecretPara> getInfoNonSecretPara(){
		return tInfoNonSecretParaDao.findAll();
	}

	@Override
	public TInfoNonSecretPara findInfoNonSecretPara(TInfoNonSecretPara tInfoNonSecretPara) {
		return tInfoNonSecretParaDao.findUnique(tInfoNonSecretPara);
	}


}
