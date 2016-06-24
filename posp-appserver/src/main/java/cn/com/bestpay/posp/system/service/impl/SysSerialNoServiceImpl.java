package cn.com.bestpay.posp.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.bestpay.posp.system.dao.SysSerialNoDao;
import cn.com.bestpay.posp.system.service.SysSerialNoService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component("SysSerialNoService")
public class SysSerialNoServiceImpl implements SysSerialNoService {
	
	@Autowired
	private SysSerialNoDao sysSerialNoDao;

	@Override
	@Transactional(propagation= Propagation.SUPPORTS, isolation= Isolation.READ_COMMITTED)
	public String querySerialNo() {
		return sysSerialNoDao.querySerialNo().getSerialNo();
	}

}
