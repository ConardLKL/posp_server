package com.bestpay.posp.system.dao;

import com.bestpay.posp.system.entity.TSymRegular;
import com.bestpay.posp.system.base.BaseDao;
import com.bestpay.posp.system.entity.CipherCard;

public interface TSymRegularDao extends BaseDao<TSymRegular, Long> {

	public TSymRegular findByKeyTip(String keyTip);
	
	public String findAppKey(TSymRegular tSymRegular);
	//加密卡号
	public String cipherCard(CipherCard cipherCard); 
	//解密卡号
	public String decipherCard(CipherCard cipherCard);
}
