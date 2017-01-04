package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TSymAreaDao;
import com.bestpay.posp.system.service.TSymAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.system.entity.TSymArea;

/**
 * 获取受理机构信息
 * @author FENGLI
 *
 */
@Component
public class TSymAreaServiceImpl implements TSymAreaService {
	@Autowired
	TSymAreaDao tSymAreaDao;

	@Override
	public TSymArea getCupBranchNo(TSymArea tSymArea) {
		return tSymAreaDao.findUnique(tSymArea);
	}
	
	@Override
	public TSymArea findByAreaCode(String areaCode) {
		return tSymAreaDao.findByAreaCode(areaCode);
	}

}
