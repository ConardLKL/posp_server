package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.entity.TStlPosBlackList;
import com.bestpay.posp.system.dao.TStlPosBlackListDao;
import com.bestpay.posp.system.service.TStlPosBlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TStlPosBlackListServiceImpl implements TStlPosBlackListService {
	@Autowired
	private TStlPosBlackListDao tStlPosBlackListDao;
	@Override
	public TStlPosBlackList getStlPosBlackList(TStlPosBlackList tStlPosBlackList) {
		// TODO Auto-generated method stub
		return tStlPosBlackListDao.findUnique(tStlPosBlackList);
	}

}
