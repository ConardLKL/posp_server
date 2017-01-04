package com.bestpay.posp.system.dao;

import com.bestpay.posp.system.base.BaseDao;
import com.bestpay.posp.system.entity.TSymDbkey;

public interface TSymDbkeyDao extends BaseDao<TSymDbkey, Long> {

	public TSymDbkey findByKeyId(Long keyId);
	
}
