package com.bestpay.posp.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bestpay.posp.system.dao.TPospPktDefDao;
import com.bestpay.posp.system.entity.TPospPktDef;
import com.bestpay.posp.system.entity.TPospPktDefExample;
import com.bestpay.posp.system.service.TPospPktDefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TPospPktDefServiceImpl implements TPospPktDefService {
	
	@Autowired
	private TPospPktDefDao tPospPktDefDao;

	@Override
	public List<TPospPktDef> selectByExample(TPospPktDefExample example) {
		return tPospPktDefDao.selectByExample(example);
	}

	@Override
	public Map<String, List<TPospPktDef>> initTPospPktDef(TPospPktDef entity) {
		
		TPospPktDefExample channelDef = new TPospPktDefExample();
		channelDef.setGroupByClause("CHANNEL_CODE,MSG_TYPE");
		channelDef.createCriteria().andChannelCodeEqualTo(entity.getChannelCode());
		List<TPospPktDef> msgTypes = tPospPktDefDao.selectExampleGroupBy(channelDef);
		
		if(msgTypes == null || msgTypes.size() == 0){
			return null;
		}
		Map<String, List<TPospPktDef>> hashMapPktDef = new HashMap<String, List<TPospPktDef>>();
		
		for (TPospPktDef tPospPktDef : msgTypes) {
			TPospPktDefExample example = new TPospPktDefExample();
			example.createCriteria().andMsgTypeEqualTo(tPospPktDef.getMsgType()).andChannelCodeEqualTo(entity.getChannelCode());
			example.setOrderByClause("BIT");
			hashMapPktDef.put(tPospPktDef.getMsgType(), selectByExample(example));
		}
		return hashMapPktDef;
	}

}
