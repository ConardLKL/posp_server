package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TSymArea;

/**
 * 受理机构表
 * @author FENGLI
 *
 */

public interface TSymAreaService extends BaseService<TSymArea, Long> {
	
	/**
	 * 获取地区机构码
	 * @param TInfoBranchInfo
	 * @return
	 */
	public TSymArea getCupBranchNo(TSymArea tSymArea);
	
	/**
	 * 通过地区码获取地区机构信息
	 * @param areaCode
	 * @return
	 */
	public TSymArea findByAreaCode(String areaCode);

}
