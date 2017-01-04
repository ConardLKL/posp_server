package com.bestpay.posp.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bestpay.posp.system.dao.TCfgPktDefDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.system.entity.TCfgPktDef;
import com.bestpay.posp.system.service.TCfgPktDefService;

@Component
public class TCfgPktDefServiceImpl implements TCfgPktDefService {
	
	@Autowired
	private TCfgPktDefDao tCfgPktDefDao;

	@Override
	public Map<String, List<TCfgPktDef>> initPktDef(TCfgPktDef entity) {
		
		List<TCfgPktDef> msgTypes = tCfgPktDefDao.selectGroupBy(entity);
		
		if(msgTypes == null || msgTypes.size() == 0){
			return null;
		}
		Map<String, List<TCfgPktDef>> hashMapPktDef = new HashMap<String, List<TCfgPktDef>>();
		
		for (TCfgPktDef pktDef : msgTypes) {
			entity.setMsgType(pktDef.getMsgType());
			hashMapPktDef.put(pktDef.getMsgType(), tCfgPktDefDao.find(entity));
		}
		return hashMapPktDef;
	}


}
