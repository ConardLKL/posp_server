package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TStlBankCardBinDao;
import com.bestpay.posp.system.entity.TStlBankCardBin;
import com.bestpay.posp.system.service.TStlBankCardBinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TStlBankCardBinServiceImpl implements TStlBankCardBinService {
	
	@Autowired
	private TStlBankCardBinDao tStlBankCardBinDao;

	@Override
	public TStlBankCardBin queryByCardNo(String cardNo) {
		
	   return tStlBankCardBinDao.findByCardNo(cardNo);
	}

}
