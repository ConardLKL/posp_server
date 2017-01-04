package com.bestpay.posp.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.bestpay.posp.protocol.FlowMessage;
/**
 * 获取指定格式基站信息
 * @author TISSON
 *
 */
@Slf4j
@Component
public class BaseStationInfo {

	/**
	 * 获取基站信息
	 * @param field63
	 */
	public static FlowMessage getBaseStationInfo(String field63){
		if(StringUtils.isEmpty(field63)
				|| field63.length() <= 3 ){
			return null;
		}
		FlowMessage flow = new FlowMessage();
		try {
			String baseStationType = getBaseStationType(field63);
			String baseStationValue = getBaseStationValues(baseStationType,field63.substring(13));
			flow.setBaseStationType(baseStationType);
			flow.setBaseStationValues(baseStationValue);
		} catch (Exception e) {
			flow = null;
			log.error("BASE STATION ERROR :" + e.getMessage());
		}
		return flow;
	}
	/**
	 * 获取基站类型
	 * @param field63
	 * @return
	 */
	private static String getBaseStationType(String field63) throws Exception{
		String baseStationType = field63.substring(3,13).trim();
		log.info("BASE STATION TYPE :" + baseStationType);
		return baseStationType;
	}
	/**
	 * 获取格式化后的基站信息值
	 * @param baseStationType
	 * @param baseStationValue
	 * @return
	 */
	private static String getBaseStationValues(String baseStationType,String baseStationValue) throws Exception{
		String baseStationValues = null;
		if(StringUtils.equals("GPRS", baseStationType)){
			baseStationValues = setBaseStationValues(baseStationValue,5,4);
			return baseStationValues;
		}
		if(!StringUtils.equals("LngLat", baseStationType)){
			baseStationValues = setBaseStationValues(baseStationValue,3,10);
			return baseStationValues;
		}
		setBaseStationValues(baseStationValue);
		return baseStationValues;
	}
	/**
	 * 设置基站信息格式（经纬度信息除外）
	 * @param baseStationValue --基站信息值
	 * @param count --每个基站信息子域的个数
	 * @param length --每个基站信息子域的长度
	 * @return
	 */
	private static String setBaseStationValues(String baseStationValue ,int count, int length) throws Exception{
		StringBuffer baseStationValues = new StringBuffer();
		int index = 0;
		for(int times=0; times<3; times++){
			//检查后续还有没有基站信息值
			try{
				if(StringUtils.isEmpty(baseStationValue.substring(index).trim())){
					break;
				}
			}catch(Exception e){
				log.error(e.getMessage());
				break;
			}
			//解析单个基站信息，并组装成固定格式
			for(int i=0; i<count-1; i++){
				baseStationValues.append(baseStationValue.substring(index, index+length).trim());
				index += length;
				baseStationValues.append(",");
			}
			baseStationValues.append(baseStationValue.substring(index, index+length).trim());
			baseStationValues.append(";");
			index += length;
		}
		log.info("BASE STATION VALUES :"+ baseStationValues.toString());
		return baseStationValues.toString();
	}
	/**
	 * 设置经纬度信息格式
	 * @param baseStationValue
	 * @return
	 */
	private static String setBaseStationValues(String baseStationValue) throws Exception{
		StringBuffer baseStationValues = new StringBuffer();
		baseStationValues.append(baseStationValue.substring(0, 20).trim());
		baseStationValues.append(",");
		baseStationValues.append(baseStationValue.substring(0, 20).trim());
		baseStationValues.append(";");
		log.info("BASE STATION VALUES :"+ baseStationValues.toString());
		return baseStationValues.toString();
	}
}
