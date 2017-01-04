package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.system.dao.TSysParaDao;
import com.bestpay.posp.system.entity.TSysPara;
import com.bestpay.posp.system.service.TSysParaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TSysParaServiceImpl implements TSysParaService {
	
	@Autowired
	TSysParaDao tSysParaDao;
	
	@Override
	public TSysPara getPospSysPara(TSysPara tSysPara) {
		// TODO Auto-generated method stub
		return tSysParaDao.findUnique(tSysPara);
	}

	@Override
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
