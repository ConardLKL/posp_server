package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TLogSignePuchaseInfo;

/**
 * 电子化凭条
 * @author HR
 *
 */
public interface TLogSignePuchaseInfoService extends BaseService<TLogSignePuchaseInfo,Long> {
	
	/**
	 * 获取电子化凭条信息
	 * @param TLogSignePuchaseInfo
	 * @return
	 */
	public TLogSignePuchaseInfo getSignePuchaseInfo(TLogSignePuchaseInfo tLogSignePuchaseInfo);
	
	/**
	 * 保存电子化凭条信息
	 * @param TLogSignePuchaseInfo
	 * @return
	 */
	public boolean insertSignePuchaseInfo(TLogSignePuchaseInfo tLogSignePuchaseInfo);
	
	/**
	 * 更新电子化凭条信息
	 * @param TLogSignePuchaseInfo
	 * @return
	 */
	public boolean updateSignePuchaseInfo(TLogSignePuchaseInfo tLogSignePuchaseInfo);
}
