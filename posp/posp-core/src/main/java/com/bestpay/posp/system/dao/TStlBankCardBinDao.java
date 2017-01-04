package com.bestpay.posp.system.dao;

import com.bestpay.posp.system.entity.TStlBankCardBin;
import com.bestpay.posp.system.base.BaseDao;

import java.util.List;

public interface TStlBankCardBinDao extends BaseDao<TStlBankCardBin, Long> {

	/**
	 * 根据卡号查询card bin 信息
	 * @param cardNo
	 * @return
	 */
	public TStlBankCardBin findByCardNo(String cardNo);

	/**
	 * 查询所有卡号长度
	 * @return
     */
	public List<String> findAccountLen();

	/**
	 * 根据卡号长度查询card bin 信息
	 * @param accountLen
	 * @return
     */
	public List<TStlBankCardBin> findByAccountLen(String accountLen);
}
