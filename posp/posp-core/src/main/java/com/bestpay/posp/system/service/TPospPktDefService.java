package com.bestpay.posp.system.service;

import java.util.List;
import java.util.Map;

import com.bestpay.posp.system.entity.TPospPktDef;
import com.bestpay.posp.system.entity.TPospPktDefExample;

/**
 * 
 * @author DengPengHai
 * @date 2014-7-16
 */
public interface TPospPktDefService {
	
	/**
	 * 
	 * @return
	 */
	Map<String, List<TPospPktDef>> initTPospPktDef(TPospPktDef entity);
	
	/**
	 * 
	 * @param example
	 * @return
	 */
	List<TPospPktDef> selectByExample(TPospPktDefExample example);
	

}
