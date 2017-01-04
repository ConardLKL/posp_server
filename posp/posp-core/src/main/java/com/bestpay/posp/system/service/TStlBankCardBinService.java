package com.bestpay.posp.system.service;

import com.bestpay.posp.system.entity.TStlBankCardBin;

/**
 * 卡BIN 业务数据处理
 * @author 
 *
 */
public interface TStlBankCardBinService {

	/**
	 * 根据卡号查询card bin 信息
	 * @param CardNo
	 * @return
	 */
	public TStlBankCardBin queryByCardNo(String cardNo);
}
