package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TSymRegularDao;
import com.bestpay.posp.system.entity.TSymRegular;
import com.bestpay.posp.system.service.TSymRegularService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.system.entity.CipherCard;

@Component("TSymRegularService")
public class TSymRegularServiceImpl implements TSymRegularService {
	
	@Autowired
	private TSymRegularDao tSymRegularDao;

	@Override
	public TSymRegular queryByKeyTip(String KeyTip) {
		
	   return tSymRegularDao.findByKeyTip(KeyTip);
	}

	@Override
	public String createAppKey(TSymRegular tSymRegular) {
		// TODO Auto-generated method stub
		return tSymRegularDao.findAppKey(tSymRegular);
	}

	@Override
	public String cipherCard(CipherCard cipherCard) {
		// TODO Auto-generated method stub
		return tSymRegularDao.cipherCard(cipherCard);
	}
	
	@Override
	public String decipherCard(CipherCard cipherCard) {
		// TODO Auto-generated method stub
		return tSymRegularDao.decipherCard(cipherCard);
	}

}
