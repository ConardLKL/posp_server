package com.bestpay.posp.system.service;

import com.bestpay.posp.system.entity.TStlVoucher;

public interface TStlVoucherService {
	
	
	public String findVoucher(TStlVoucher entity);
	
	public String findVoucher(String mctCode, String posCode);

}
