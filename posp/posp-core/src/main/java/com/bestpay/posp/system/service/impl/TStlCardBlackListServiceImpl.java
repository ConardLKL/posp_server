package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TStlCardBlackListDao;
import com.bestpay.posp.system.entity.TStlCardBlackList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.system.service.TStlCardBlackListService;

@Component
public class TStlCardBlackListServiceImpl implements TStlCardBlackListService{
	@Autowired
	private TStlCardBlackListDao tStlCardBlackListDao;
	@Override
	public TStlCardBlackList getStlCardBlackList(TStlCardBlackList tStlCardBlackList) {
		// TODO Auto-generated method stub
		return tStlCardBlackListDao.findUnique(tStlCardBlackList);
	}

}
