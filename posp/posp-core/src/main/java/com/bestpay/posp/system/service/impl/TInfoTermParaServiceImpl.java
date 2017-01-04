package com.bestpay.posp.system.service.impl;

import java.util.List;

import com.bestpay.posp.system.entity.TInfoTermPara;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.system.dao.TInfoTermParaDao;
import com.bestpay.posp.system.service.TInfoTermParaService;

@Component
public class TInfoTermParaServiceImpl implements TInfoTermParaService {
	@Autowired
	TInfoTermParaDao tInfoTermParaDao;


	@Override
	public List<TInfoTermPara> getPospTermPara() {
		return tInfoTermParaDao.findAll();
	}

	@Override
	public TInfoTermPara findPospTermPara(TInfoTermPara tInfoTermPara) {
		return tInfoTermParaDao.findUnique(tInfoTermPara);
	}


}
