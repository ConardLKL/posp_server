package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TStlVoucherDao;
import com.bestpay.posp.system.entity.TStlVoucher;
import com.bestpay.posp.system.service.TStlVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("TStlVoucherService")
public class TStlVoucherServiceImpl implements TStlVoucherService {
	
	//默认广告语
	private final static String VOUCHER_DEF= "";
	
	@Autowired
	private TStlVoucherDao stlVoucherDao;

	@Override
	public String findVoucher(TStlVoucher entity) {
		TStlVoucher e = stlVoucherDao.findVoucherByPoJo(entity);
		String voucher = VOUCHER_DEF;
		if(e != null){
			voucher = e.getSlogan();
		} 
		return voucher;
	}

	@Override
	public String findVoucher(String mctCode, String posCode) {
		TStlVoucher e = stlVoucherDao.findVoucherByParam(mctCode, posCode);
		String voucher = VOUCHER_DEF;
		if(e != null){
			voucher = e.getSlogan();
		} 
		return voucher;
	}

}
