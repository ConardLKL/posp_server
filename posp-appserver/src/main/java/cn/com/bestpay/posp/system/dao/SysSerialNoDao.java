package cn.com.bestpay.posp.system.dao;

import cn.com.bestpay.posp.system.entity.SysSerialNo;

public interface SysSerialNoDao {

	/**
	 * 生成系统流水
	 * @return
	 */
	public SysSerialNo querySerialNo();
	
}
