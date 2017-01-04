package com.bestpay.posp.system.dao;

import java.util.List;

import com.bestpay.posp.system.base.BaseDao;
import com.bestpay.posp.system.entity.TCfgPktDef;

public interface TCfgPktDefDao extends BaseDao<TCfgPktDef, Long> {

	
	public List<TCfgPktDef> selectGroupBy(TCfgPktDef entity);
}
