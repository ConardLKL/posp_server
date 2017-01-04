package com.bestpay.posp.system.service;

import java.util.List;
import java.util.Map;

import com.bestpay.posp.system.entity.TCfgPktDef;

public interface TCfgPktDefService {
	
	/**
	 * 初始化报文拆组包类型
	 * @return
	 */
	Map<String, List<TCfgPktDef>> initPktDef(TCfgPktDef entity);

}
