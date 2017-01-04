package com.bestpay.posp.system.dao;

import com.bestpay.posp.system.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import com.bestpay.posp.system.entity.TStlVoucher;

public interface TStlVoucherDao extends BaseDao<TStlVoucher, Long> {
	
	public TStlVoucher findVoucherByPoJo(TStlVoucher entity);
	
	public TStlVoucher findVoucherByParam(@Param("mctCode")String mctCode,@Param("posCode")String posCode);

}
