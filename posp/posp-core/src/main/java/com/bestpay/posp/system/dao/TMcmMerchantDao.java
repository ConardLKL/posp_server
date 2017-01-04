package com.bestpay.posp.system.dao;

import com.bestpay.posp.system.entity.TMcmMerchant;
import com.bestpay.posp.system.base.BaseDao;

public interface TMcmMerchantDao extends BaseDao<TMcmMerchant, Long> {
	
	public TMcmMerchant findByMctCode(String mctCode);

}
