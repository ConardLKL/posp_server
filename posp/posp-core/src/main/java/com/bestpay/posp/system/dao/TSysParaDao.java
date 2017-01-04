package com.bestpay.posp.system.dao;

import com.bestpay.posp.system.base.BaseDao;
import com.bestpay.posp.system.entity.TSysPara;

public interface TSysParaDao extends BaseDao<TSysPara, Long> {
	
	public TSysPara findByParaKey(String paraKey);

}
