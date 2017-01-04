package com.bestpay.posp.service.checker;

import java.util.HashMap;

import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.system.cache.ConfigCache;
import com.bestpay.posp.system.entity.TMcmPlatformInfo;
import com.bestpay.posp.system.service.TMcmPlatformInfoService;
import com.bestpay.posp.spring.PospApplicationContext;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.service.impl.RespCodeInformation;

@Slf4j
@Component("pub_ThirdPartyCheck")
public class Pub_ThirdPartyCheck {
	
	@Autowired
	private TMcmPlatformInfoService tMcmPlatformInfoService;
	/**
	 * 管理类调用
	 * @param iso
	 * @return
	 */
	public IsoMessage check(IsoMessage iso){
		if(iso.isPlatform()
				|| (isThirdPartyPlatform(iso) 
				&& isPlatformState(iso))){
			iso.setState(true); 
			return iso;
		}
		RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A203, iso);
		iso.setState(false);
		log.info(String.format("[%s,%s] 第三方机构非法！", iso.getTranCode(), iso.getSeq()));
		return iso;
	}
	/**
	 * 检查交易tpdu是否为第三方平台指定数值
	 * @param iso
	 * @return true 相等   
	 * 		false 不相等
	 */
	public boolean isThirdPartyPlatformTpdu(IsoMessage iso){
		ConfigCache configCache = (ConfigCache) PospApplicationContext.getBean("ConfigCache");
		if(iso.getPospMessage() != null &&StringUtils.equals(HexCodec.hexEncode(iso.getPospMessage().getTpdu())
				, configCache.getThirdPartyPlatformTpdu())){
			return true;
		}
		return false;
	}
	
	/**
	 * 检查第三方平台是否合法
	 * @param iso
	 * @return true 合法   
	 * 		false 不合法
	 */
	private boolean isThirdPartyPlatform(IsoMessage iso) {
		if(!iso.isPlatform()
				&& StringUtils.isEmpty(iso.getField(32))){
			return false;
		}
		//检查平台是否合法
		ConfigCache configCache = (ConfigCache) PospApplicationContext.getBean("ConfigCache");
		HashMap<String, String> map = configCache.getParaValues(SysConstant.CL3001);
		for(String agency: map.values()){
			if(StringUtils.equals(agency, iso.getField(32))){
				return true;
			}
		}
		//管理类检查41、42域是否与32域一致
		if(!PosConstant.TRANSACTION_TYPES.contains(iso.getTranCode())
				&& (!StringUtils.equals(iso.getField(41).trim(), iso.getField(32))
						|| !StringUtils.equals(iso.getField(42).trim(), iso.getField(32)))){
			return false;
		}
		return false;
	}
	/**
	 * 第三方平台接入，控制平台接入权限
	 * 查询平台信息表T_MCM_PLATFORM_ACCT，IS_TRAD是否可以做交易，0-不可以做交易，1-可以做交易
	 * 字段STAT如果为值为S0A，有效，负责S0X，无效
	 * @param iso
	 * @return
	 */
	private boolean isPlatformState(IsoMessage iso){
		TMcmPlatformInfo tMcmPlatformInfo = new TMcmPlatformInfo();
		tMcmPlatformInfo.setOrgId(iso.getField(32));
		TMcmPlatformInfo mcmPlatformInfo = tMcmPlatformInfoService.getMcmPlatformInfo(tMcmPlatformInfo);
		if(mcmPlatformInfo == null 
				|| StringUtils.equals(mcmPlatformInfo.getState(), "S0X")
				|| StringUtils.equals(mcmPlatformInfo.getIsTrade(), "0")){
			return false;
		}
		return true;
	}
}
