package com.bestpay.posp.system.service.impl;

import java.util.List;

import com.bestpay.posp.system.dao.TInfoPublickeyManageDao;
import com.bestpay.posp.system.service.TInfoPublickeyManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.system.entity.TInfoPublickeyManage;

@Component
public class TInfoPublickeyManageServiceImpl implements TInfoPublickeyManageService {
	@Autowired
	TInfoPublickeyManageDao tInfoPublickeyManageDao;


	@Override
	public List<TInfoPublickeyManage> getInfoPublickeyManage(TInfoPublickeyManage tInfoPublickeyManage) {
		return tInfoPublickeyManageDao.find(tInfoPublickeyManage);
	}


	@Override
	public TInfoPublickeyManage findInfoPublickeyManage(
			TInfoPublickeyManage tInfoPublickeyManage) {
		return tInfoPublickeyManageDao.findUnique(tInfoPublickeyManage);
	}


}
