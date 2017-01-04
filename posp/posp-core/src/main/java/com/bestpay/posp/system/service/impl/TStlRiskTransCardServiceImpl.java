package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.entity.TStlRiskTransCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.system.dao.TStlRiskTransCardDao;
import com.bestpay.posp.system.service.TStlRiskTransCardService;

@Component("TStlRiskTransCardService")
public class TStlRiskTransCardServiceImpl implements TStlRiskTransCardService {
	
	@Autowired
	private TStlRiskTransCardDao tStlRiskTransCardDao;

	@Override
	public TStlRiskTransCard findUnique(TStlRiskTransCard tStlRiskTransCard) {
		return tStlRiskTransCardDao.findUnique(tStlRiskTransCard);
	}

	

}
