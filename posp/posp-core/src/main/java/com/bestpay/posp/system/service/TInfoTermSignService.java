package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TInfoTermSign;

/**
 * 终端签到签退信息表
 * @author YZH
 *
 */
public interface TInfoTermSignService extends BaseService<TInfoTermSign, Long> {
	/**
	 * 更新终端签到签退信息表
	 * @return
	 */
	public boolean updatePospTermSign(TInfoTermSign tInfoTermSign);
	/**
	 * 获取商户终端签到信息
	 * @param TInfoTermSign
	 * @return
	 */
	public TInfoTermSign getPospTermSign(TInfoTermSign tInfoTermSign);
	/**
	 * POS机首次签到时插入数据
	 * @param tInfoTermSign
	 * @return
	 */
	public boolean insertPospTermSign(TInfoTermSign tInfoTermSign); 

}
