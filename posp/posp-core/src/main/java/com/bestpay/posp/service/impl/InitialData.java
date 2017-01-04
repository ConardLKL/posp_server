package com.bestpay.posp.service.impl;

import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.protocol.FlowMessage;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.protocol.util.Utils;
import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.system.cache.CardBinCache;
import com.bestpay.posp.system.cache.ConfigCache;
import com.bestpay.posp.system.entity.*;
import com.bestpay.posp.system.service.SysSerialNoService;
import com.bestpay.posp.system.service.TStlVoucherService;
import com.bestpay.posp.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
/**
 * 初始化报文数据
 * @author HR
 *
 */
@Slf4j
@Component
public class InitialData {
	
	@Autowired
	private SysSerialNoService sysSerialNoService;
	@Autowired
	private ObtainObjectInformation obtainObjectInformation;
	@Autowired
	private TStlVoucherService voucherService;
	
	/**
	 * 初始化报文数据
	 * @param iso
	 * @return
	 */
	public TLogTranFlow dataInitial(IsoMessage iso)throws Exception{
		TLogTranFlow entity = initialFlowData(iso);
		setFlowMessage(entity,iso);
		return entity;
	}

	/**
	 * 初始化流水数据
	 * @param iso
	 */
	private TLogTranFlow initialFlowData(IsoMessage iso) throws Exception{
		TLogTranFlow entity = new TLogTranFlow();
		String serialNo = null;
		if(StringUtils.isNotEmpty(iso.getPospMessage().getMpos())){
			serialNo = iso.getSeq();
		}else{
			serialNo = sysSerialNoService.querySerialNo();
		}
		Date tranDate = new Date();
		entity.setSerialNo(serialNo);//系统流水号
		entity.setTranDate(DateUtils.paraDate(tranDate,DateUtils.YYYYMMDD));//交易日期
		entity.setTranTime(DateUtils.paraDate(tranDate,DateUtils.HHmmss));//交易时间
//		entity.setSendDate();//发出时间
		entity.setChannelCode(Long.valueOf(SysConstant.CAPITAL_POOL_5001));//渠道代码
		entity.setTranCode(iso.getTranCode());//交易码
		if(StringUtils.isNotEmpty(iso.getField(4))){
			entity.setTranAmount(Double.valueOf(iso.getField(4))/100);
			entity.setRemainAmount(entity.getTranAmount());
		}
		entity.setTermSerialNo(iso.getField(11));//终端流水号
		entity.setTermDate(entity.getTranDate());
		entity.setTermTime(entity.getTranTime());
		entity.setReferNo(serialNo.substring(6, 18));//系统参考号
		entity.setConditionMode(iso.getField(25));
		entity.setTermCode(iso.getField(41));//终端号
		entity.setMerchCode(iso.getField(42));//商户号
		entity.setCcyCode(iso.getField(49));
		entity.setXRealIp(iso.getXRealIp());
		if(StringUtils.isNotEmpty(iso.getField(60))){
			entity.setTranType(iso.getField(60).substring(0,2));
		    entity.setBatchNo(iso.getField(60).substring(2,8));//批次号
		}
		String field62 = null;
		if(StringUtils.isNotEmpty(iso.getField(62))){
			//非指定账户圈存，转入账号存放在62域
			//国密密码域存放在62域，固定长度42
			if((iso.getTranCode().equals(PosConstant.TRANS_TYPE_0200629147)
					|| iso.getTranCode().equals(PosConstant.TRANS_TYPE_0400629147))
					&& iso.getField(62).length() > 42) {
				field62 = Utils.getString(HexCodec.hexDecode(
						iso.getField(62).substring(0, iso.getField(62).length() - 42)));
			}else{
				field62 = Utils.getString(HexCodec.hexDecode(iso.getField(62)));
			}
			//设置终端设备序列号、版本号
			if(field62.contains("Sequence")){
				setDeviceIdentification(field62,entity);
			}
		}
		//非指定帐户圈存交易，48域和62域分别存放转入卡的服务点输入方式码和卡号
		if(iso.getTranCode().equals(PosConstant.TRANS_TYPE_0200629147)
				|| iso.getTranCode().equals(PosConstant.TRANS_TYPE_0400629147)){
			entity.setInputMode(iso.getField(48).substring(0, 3));
			entity.setAddCardNo(obtainObjectInformation.encryptionAccountNumber(field62));
			entity.setIntoCard(field62);
		}else{
			entity.setInputMode(iso.getField(22));
		}
		entity.setIp(iso.getIp());
		//mpos电话号码
		if(StringUtils.isNotEmpty(iso.getPospMessage().getMobilPhone())){
			entity.setPhoneNumber(iso.getPospMessage().getMobilPhone());
		}
		//主叫号码
		if(StringUtils.isNotEmpty(iso.getPospMessage().getAni())){
			entity.setPhoneNumber(iso.getPospMessage().getAni().substring(iso.getPospMessage().getAni().length()-12));
		}
		//被叫号码
		if(StringUtils.isNotEmpty(iso.getPospMessage().getDnis())){
			entity.setCalledNumber(iso.getPospMessage().getDnis().substring(iso.getPospMessage().getDnis().length()-12));
		}
		//基站信息
		FlowMessage flowMessage = BaseStationInfo.getBaseStationInfo(iso.getField(63));
		if(flowMessage != null){
			entity.setBaseStationType(flowMessage.getBaseStationType());
			entity.setBaseStationValues(flowMessage.getBaseStationValues());
		}
		entity.setTpdu(HexCodec.hexEncode(iso.getPospMessage().getTpdu()).substring(0,6)+"0000");
		if(iso.getTranCode().substring(0, 4).equals(PosConstant.MSG_TYPE_0400_00)){
			entity.setTranFlag("2");//冲正交易的标志状态为“2”
		}else{
			entity.setTranFlag("0");
		}
		entity.setCardFlag("01");
		entity.setBatchState("0");
		entity.setTranState("0");
		entity.setBatchResult("0");
		if(StringUtils.isNotEmpty(iso.getField(2))) {
			entity.setCardNo(obtainObjectInformation.encryptionAccountNumber(iso.getField(2)));
			//获取卡BIN 信息
			CardBinCache cardBinCache = (CardBinCache) PospApplicationContext.getBean("CardBinCache");
			TStlBankCardBin tStlBankCardBin = cardBinCache.getCardBin(iso.getField(2));
			if(Utils.isNull(tStlBankCardBin)){
				tStlBankCardBin = obtainObjectInformation.getTStlBankCardBin(iso.getField(2));
			}
			if(!Utils.isNull(tStlBankCardBin)){
				entity.setCardBin(tStlBankCardBin.getCardBin());
				if(tStlBankCardBin.getAccountType().toString().length() == 1){
					entity.setCardType(0+tStlBankCardBin.getAccountType().toString());
				}else{
					entity.setCardType(tStlBankCardBin.getAccountType().toString());
				}
			}
		}
		//获取受理机构表信息
		if(StringUtils.isNotEmpty(entity.getMerchCode())){
			TMcmMerchant tMerchant = obtainObjectInformation.getTMcmMerchant(iso);
			if(!Utils.isNull(tMerchant)){
				ConfigCache configCache = (ConfigCache) PospApplicationContext.getBean("ConfigCache");
				String acquiringInstitution = configCache.getAcquiringInstitution();//受理机构标识码
				String sendingInstitution = configCache.getSendingInstitution();//发送机构标识码
				TSymArea branch = obtainObjectInformation.findByAreaCode(tMerchant.getAreaCode());
				entity.setRcvBankCode(acquiringInstitution+branch.getMacroValue());
				if(entity.getRcvBankCode().length() < 8){
					log.warn(String.format("%s RcvBankCode值不正确："+ entity.getRcvBankCode()));
				}
				entity.setRcvBranchCode(sendingInstitution);
				entity.setMcc(entity.getMerchCode().substring(7,11));
				entity.setMctName(tMerchant.getMctName().replace("&#39;", "'"));//商户名称
				entity.setServAgent(tMerchant.getServAgent());//代理商
			}
		}
		//获取绑定号码以及与主叫号码是否一致
		//0--不一致   1--一致
		if(StringUtils.isNotEmpty(iso.getPospMessage().getAni())){
			TMcmPosinfo tMcmPosinfo = obtainObjectInformation.getTMcmPosinfo(iso);
			if(Utils.isNull(tMcmPosinfo) || StringUtils.isEmpty(tMcmPosinfo.getComnuNum())){
				entity.setIsMatch("0");
			}
			entity.setBindNumber(tMcmPosinfo.getComnuNum());
			if(StringUtils.equals(iso.getPospMessage().getAni().substring(iso.getPospMessage().getAni().length()-8), 
					tMcmPosinfo.getComnuNum().substring(tMcmPosinfo.getComnuNum().length()-8))){
				entity.setIsMatch("1");
			}
		}
		entity.setCardProperties(setCardProperties(iso));//卡属性
		return entity;
	}
	/**
	 * 初始化flow
	 * @param entity
	 * @param iso
	 */
	private void setFlowMessage(TLogTranFlow entity,IsoMessage iso){
		FlowMessage flow = new FlowMessage();
		BeanUtils.copyProperties(entity, flow);
		flow.setYYYYMMDD(entity.getTranDate());
		flow.setMMDD(entity.getTranDate().substring(4));
		flow.setMMDDhhmmss(flow.getMMDD()+entity.getTranTime());
		flow.setHhmmss(entity.getTranTime());
		flow.setCardType(setCardType(entity.getCardType()));
		flow.setAuthAmount(iso.getField(4));
		flow.setField63(setVoucherAdvertising(iso));
		flow.setField32(iso.getField(32));
		iso.setTranCode(entity.getTranCode());
		iso.setSeq(entity.getSerialNo());
		iso.setFlow(flow);
		iso.printMessage(entity.getSerialNo());
	}
	/**
	 * 设置卡类型
	 * @param cardType
	 * @return
	 */
	public String setCardType(String cardType){
		if(StringUtils.isEmpty(cardType)){
			return null;
		}
		switch(Integer.valueOf(cardType)){
			case 1: 
				return SysConstant.CARD_DBC;
			default : 
				return SysConstant.CARD_CDC;
		}
	}
	/**
	 * 设置卡属性
	 * @param iso
	 * @return
	 */
	public String setCardProperties(IsoMessage iso){
		//卡属性初始值为“00000000”
		//前三个字段为预留字段默认“000”
		//第四个字段为卡介质，0、1、2、3分别表示未知、纯磁条卡、复合卡、纯IC卡
		//第五个字段为是否有第三磁道，0表示没有，1表示有第三磁道
		//后三个字段为22域
		String cardProperties = "00000000";
		String cardMedium = "0";//卡介质
		String thirdTrack = "0";
		//管理类交易为默认值
		if(!PosConstant.TRANSACTION_TYPES.contains(iso.getTranCode())){
			return "";
		}
		//冲正类交易、脚本通知以及电子现金指定账户圈存不送磁道信息，所以复合卡和纯IC卡时卡介质表示为未知
		if(StringUtils.isEmpty(iso.getField(55))
				&& StringUtils.isEmpty(iso.getField(23))){
			cardMedium = "1";
		}else if(StringUtils.isNotEmpty(iso.getField(35))
				|| StringUtils.isNotEmpty(iso.getField(36))){
			cardMedium = "2";
		}else if(!StringUtils.equals(iso.getTranCode().substring(0,4)
				, PosConstant.MSG_TYPE_0400_00)
				&& !StringUtils.equals(iso.getTranCode(), PosConstant.TRANS_TYPE_0620951)
				&& !StringUtils.equals(iso.getTranCode(), PosConstant.TRANS_TYPE_0200609145)){
			cardMedium = "3";
		}
		if(StringUtils.isNotEmpty(iso.getField(36))){
			thirdTrack = "1";
		}
		if(StringUtils.isNotEmpty(iso.getField(22))){
			cardProperties = "000"+cardMedium+thirdTrack+iso.getField(22);
		}else{
			cardProperties = "000"+cardMedium+thirdTrack+"000";
		}
		log.info(String.format("Card Properties:"+cardProperties));
		return cardProperties; 
	}
	/**
	 * 设置签购单广告
	 * @param iso
	 */
	private String setVoucherAdvertising(IsoMessage iso){
		String messageType = iso.getField(0);
		//签购单
		if("0100".equals(messageType) ||"0200".equals(messageType)||"0220".equals(messageType)){
			String field_63 = voucherService.findVoucher(iso.getField(42), iso.getField(41));
			String field63 = String.format("%-43s", "CUP");
			if(field_63.length() <= 40){
				field63 += String.format("%-40s", field_63);
			}
			return field63;
		}
		return null;
	}

	/**
	 * 设置设备标识（设备序列号和软件版本号）
	 * @param field62
	 * @param entity
     */
	private void setDeviceIdentification(String field62,TLogTranFlow entity){
		String deviceIdentification = field62.substring(field62.indexOf("Sequence No")+11);
		int length = Integer.valueOf(deviceIdentification.substring(0,2));
		String deviceSerialNo = "";
		if(deviceIdentification.substring(2).length() == length){
			deviceSerialNo = deviceIdentification.substring(6);
		}else if(deviceIdentification.substring(2).length() >= length){
			deviceSerialNo = deviceIdentification.substring(6,length+6);
			String softwareVersionNo = deviceIdentification.substring(length+2);
			int len = Integer.valueOf(softwareVersionNo.substring(softwareVersionNo.length()-2));
			softwareVersionNo = softwareVersionNo.substring(0,len);
			entity.setSoftwareVersionNo(softwareVersionNo);
		}else{
			log.warn("62域数据有问题！");
		}
		entity.setDeviceSerialNo(deviceSerialNo);
	}
	/**
	 * 根据报文内容组装spring中的实例名称,即交易码TranCode
	 * 
	 * @param iso
	 * @return
	 * @throws Exception
	 */
	public String generatingTranCode(IsoMessage iso){
		// 获取messageType		
		String messageType = iso.getField(0);
		StringBuffer tranCode = new StringBuffer();
		tranCode.append(messageType);
		if ("0320".equals(messageType)
				|| "0500".equals(messageType)
				|| "0620".equals(messageType) 
				|| "0800".equals(messageType)
				|| "0820".equals(messageType)) {
			// 获取60.3域域值
			String field60_3 = iso.getField(60).substring(8, 11);
			tranCode.append(field60_3);
		} else {// 交易类
			// 拼接3域的前两位
			tranCode.append(iso.getField(3).substring(0, 2));
			// 拼接25域
			tranCode.append(iso.getField(25));
			// 拼接60.1域
			tranCode.append(iso.getField(60).substring(0, 2));
		}
		return tranCode.toString();
	}

}
