package com.bestpay.posp.system.service;

import com.bestpay.posp.system.entity.TSymRegular;
import com.bestpay.posp.system.entity.CipherCard;


/**
 * 敏感数据加密业务数据处理
 * @author 
 *
 */
public interface TSymRegularService {

	/**
	 * 根据密钥标签查询AppKey和DbKey 信息
	 * @param KeyTip
	 * @return
	 */
	public TSymRegular queryByKeyTip(String KeyTip);
	
	public String createAppKey(TSymRegular tSymRegular);
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
