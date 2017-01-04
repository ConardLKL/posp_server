package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TSymAppkeyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.system.entity.CipherCard;
import com.bestpay.posp.system.entity.TSymAppkey;
import com.bestpay.posp.system.service.TSymAppkeyService;

@Component("TSymAppkeyService")
public class TSymAppkeyServiceImpl implements TSymAppkeyService {
	
	@Autowired
	private TSymAppkeyDao tSymAppkeyDao;

	@Override
	public TSymAppkey queryByKeyId(Long keyId) {
		
	   return tSymAppkeyDao.findByKeyId(keyId);
	}

	@Override
	public String cipherCard(CipherCard cipherCard) {
		// TODO Auto-generated method stub
		return tSymAppkeyDao.cipherCard(cipherCard);
	}

	@Override
	public String decipherCard(CipherCard cipherCard) {
		// TODO Auto-generated method stub
		return tSymAppkeyDao.decipherCard(cipherCard);
	}
}
