package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.SysSerialNoDao;
import com.bestpay.posp.system.service.SysSerialNoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("SysSerialNoService")
public class SysSerialNoServiceImpl implements SysSerialNoService {
	
	@Autowired
	private SysSerialNoDao sysSerialNoDao;

	@Override
	public String querySerialNo() {
		return sysSerialNoDao.querySerialNo().getSerialNo();
	}

}
