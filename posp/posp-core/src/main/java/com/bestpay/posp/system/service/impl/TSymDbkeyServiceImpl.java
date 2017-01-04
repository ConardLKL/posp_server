package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TSymDbkeyDao;
import com.bestpay.posp.system.entity.TSymDbkey;
import com.bestpay.posp.system.service.TSymDbkeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("TSymDbkeyService")
public class TSymDbkeyServiceImpl implements TSymDbkeyService {
	
	@Autowired
	private TSymDbkeyDao tSymDbkeyDao;

	@Override
	public TSymDbkey queryByKeyId(Long keyId) {
		
	   return tSymDbkeyDao.findByKeyId(keyId);
	}
}
