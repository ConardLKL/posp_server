package com.bestpay.posp.system.dao;

import com.bestpay.posp.system.base.BaseDao;
import com.bestpay.posp.system.entity.CipherCard;
import com.bestpay.posp.system.entity.TSymAppkey;

public interface TSymAppkeyDao extends BaseDao<TSymAppkey, Long> {

	public TSymAppkey findByKeyId(Long keyId);
	//加密卡号
	public String cipherCard(CipherCard cipherCard); 
	//解密卡号
	public String decipherCard(CipherCard cipherCard);
}
