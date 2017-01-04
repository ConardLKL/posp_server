package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TMcmMerchantDao;
import com.bestpay.posp.system.entity.TMcmMerchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.system.service.TMcmMerchantService;

@Component
public class TMcmMerchantServiceImpl implements TMcmMerchantService {
	
	@Autowired
	TMcmMerchantDao tMcmMerchantDao;

	@Override
	public TMcmMerchant getMcmMerchant(TMcmMerchant tMcmMerchant) {
		// TODO Auto-generated method stub
		return tMcmMerchantDao.findUnique(tMcmMerchant);
	}
	
}
