package com.bestpay.posp.system.service;

import com.bestpay.posp.system.entity.CipherCard;
import com.bestpay.posp.system.entity.TSymAppkey;



/**
 * 敏感数据加密业务数据处理
 * @author 
 *
 */
public interface TSymAppkeyService {

	/**
	 * 根据密钥标签查询AppKey 信息
	 * @param KeyId
	 * @return
	 */
	public TSymAppkey queryByKeyId(Long keyId);
	
	/**
	 * 加密卡号
	 * @param cipherCard
	 * @return
	 */
	public String cipherCard(CipherCard cipherCard); 
	/**
	 * 解密卡号
	 * @param cipherCard
	 * @return
	 */
	public String decipherCard(CipherCard cipherCard);
}
