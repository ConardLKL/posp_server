package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TStlMerchantBlackListDao;
import com.bestpay.posp.system.entity.TStlMerchantBlackList;
import com.bestpay.posp.system.service.TStlMerchantBlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TStlMerchantBlackListServiceImpl implements TStlMerchantBlackListService {
	@Autowired
	private TStlMerchantBlackListDao tStlMerchantBlackListDao;
	@Override
	public TStlMerchantBlackList getStlMerchantBlackList(TStlMerchantBlackList tStlMerchantBlackList) {
		// TODO Auto-generated method stub
		return tStlMerchantBlackListDao.findUnique(tStlMerchantBlackList);
	}

}
