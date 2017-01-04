package com.bestpay.posp.system.dao;

import com.bestpay.posp.system.entity.TSymArea;
import com.bestpay.posp.system.base.BaseDao;

public interface TSymAreaDao extends BaseDao<TSymArea, Long> {
	
	/**
	 * 
	 * @param areaCode
	 * @return
	 */
	public TSymArea findByAreaCode(String areaCode);
}
