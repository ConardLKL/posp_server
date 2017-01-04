package com.bestpay.posp.service.impl;

import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.util.Utils;
import com.bestpay.posp.system.entity.TInfoRespCode;
import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.system.cache.RespCodeCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * 获取应答码信息
 * @author HR
 *
 */
@Slf4j
public class RespCodeInformation {

	/**
	 * 获取应答码信息并返回
	 * @param iso
	 * @param respCode
	 * @return
	 */
	public static void getAndReturnRespCodeInfo(String respCode,IsoMessage iso){
		try{
			TInfoRespCode infoRespCode = getTInfoRespCode(iso, respCode);
			setRespCodeInfo(infoRespCode,iso);
		}catch(Exception e){
//			e.printStackTrace();
			log.error(e.getMessage());
			log.error(String.format("[%s,%s] Get respCodeInfo error :" + respCode,iso.getSeq(),iso.getTranCode()));
		}
	}
	/**
	 * 获取应答码信息
	 * @param iso
	 * @param respCode
     * @return
     */
	private static TInfoRespCode getTInfoRespCode(IsoMessage iso, String respCode) {
		RespCodeCache respCodeCache = (RespCodeCache) PospApplicationContext.getBean("RespCodeCache");
		TInfoRespCode infoRespCode = respCodeCache.getCode(Long.valueOf(iso.getChannelCode()), respCode);
		if(Utils.isNull(infoRespCode)){
			infoRespCode = new TInfoRespCode();
			infoRespCode.setPospCode(respCode);
			if(StringUtils.equals(iso.getChannelCode(),SysConstant.CAPITAL_POOL_4001)) {
				infoRespCode.setRespCode("96");
			}else{
				infoRespCode.setRespCode(respCode.substring(respCode.length() - 2));
			}
			log.warn(String.format("应答码表无此应答码： " + respCode));
		}
		return infoRespCode;
	}
	/**
	 * 设置应答码信息
	 * @param infoRespCode
	 * @param iso
	 */
	private static void setRespCodeInfo(TInfoRespCode infoRespCode,IsoMessage iso) throws Exception{
		iso.setPospCode(infoRespCode.getPospCode());
		iso.setRspCode(infoRespCode.getRespCode());
		iso.setRspMsg(infoRespCode.getPospDesc());
		iso.setField(39,iso.getRspCode());
		setLogInfo(infoRespCode,iso);
	}
	/**
	 * 打印应答码信息
	 * @param infoRespCode
	 * @param iso
	 */
	private static void setLogInfo(TInfoRespCode infoRespCode,IsoMessage iso){
		switch (Integer.valueOf(iso.getChannelCode())) {
		case 4001:
			log.info(String.format("[%s,%s] ESSC_RESPONSE(%s):[%s] [%s]", iso.getSeq(), iso.getTranCode(), iso.getChannelCode(), infoRespCode.getPospCode() , infoRespCode.getPospDesc()));
			break;
		case 5001:
			log.info(String.format("[%s,%s] POSP_RESPONSE(%s):[%s] [%s]", iso.getSeq(), iso.getTranCode(), iso.getChannelCode(), infoRespCode.getPospCode() , infoRespCode.getPospDesc()));
			break;
		default:
			log.info(String.format("[%s,%s] BANK_RESPONSE(%s):[%s] [%s]", iso.getSeq(), iso.getTranCode(), iso.getChannelCode(), infoRespCode.getExtsysRespCode() , infoRespCode.getExtsysRespDesc()));
			break;
		}
	}
}
