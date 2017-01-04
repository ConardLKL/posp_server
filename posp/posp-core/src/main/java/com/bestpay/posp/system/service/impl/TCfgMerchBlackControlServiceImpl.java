package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.entity.TCfgMerchBlackControl;
import com.bestpay.posp.system.dao.TCfgMerchBlackControlDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.system.service.TCfgMerchBlackControlService;

@Component
public class TCfgMerchBlackControlServiceImpl implements TCfgMerchBlackControlService{
	@Autowired
	private TCfgMerchBlackControlDao tCfgMerchBlackControlDao;
	@Override
	public TCfgMerchBlackControl getCfgMerchBlackControl(
			TCfgMerchBlackControl tCfgMerchBlackControl) {
		// TODO Auto-generated method stub
		return tCfgMerchBlackControlDao.findUnique(tCfgMerchBlackControl);
	}

}
