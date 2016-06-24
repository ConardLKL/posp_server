package cn.com.bestpay.posp.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.bestpay.posp.constant.SysConstant;
import cn.com.bestpay.posp.system.dao.TSysParaDao;
import cn.com.bestpay.posp.system.entity.TSysPara;
import cn.com.bestpay.posp.system.service.TSysParaService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TSysParaServiceImpl implements TSysParaService{
	
	@Autowired
	TSysParaDao tSysParaDao;
	
	@Override
	@Transactional(propagation=Propagation.SUPPORTS, isolation=Isolation.READ_COMMITTED)
	public TSysPara getPospSysPara(TSysPara tSysPara) {
		return tSysParaDao.findUnique(tSysPara);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, isolation=Isolation.READ_COMMITTED)
	public String getSerialNo() {
		TSysPara conditions = new TSysPara();
		conditions.setParaKey(SysConstant.SYSPARA_SERIAL_NO);
		TSysPara sn = tSysParaDao.findUnique(conditions);
		String serialNoStr = null;
		if(sn != null){
			Long serialNo = Long.valueOf(sn.getParaValues())+1;
			int len = Integer.valueOf(sn.getParaRange());
			sn.setParaValues(serialNo.toString());
			tSysParaDao.update(sn);
			serialNoStr = String.format("%0"+len+"d", serialNo);  
		}
		return serialNoStr;
	}

}
