package cn.com.bestpay.posp.system.dao;

import cn.com.bestpay.posp.system.entity.TSysPara;

public interface TSysParaDao extends BaseDao<TSysPara, Long> {
	
	public TSysPara findByParaKey(String paraKey);

}
