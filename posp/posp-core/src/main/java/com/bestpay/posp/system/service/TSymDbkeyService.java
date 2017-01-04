package com.bestpay.posp.system.service;

import com.bestpay.posp.system.entity.TSymDbkey;


/**
 * 敏感数据加密业务数据处理
 * @author 
 *
 */
public interface TSymDbkeyService {

	/**
	 * 根据密钥标签查询DbKey 信息
	 * @param KeyId
	 * @return
	 */
	public TSymDbkey queryByKeyId(Long keyId);
}
