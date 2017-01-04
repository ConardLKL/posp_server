package com.bestpay.posp.service.impl;

import com.bestpay.posp.constant.FieldConstant;
import com.bestpay.posp.protocol.DecipherAppKey;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.system.entity.*;
import com.bestpay.posp.system.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 信息获取
 * @author HR
 *
 */
@Component
public class ObtainObjectInformation {
	
	@Autowired
	private TSymAreaService tSymAreaService;
	@Autowired
	private TSymAppkeyService tSymAppkeyService;
	@Autowired
	public TLogTranFlowService tLogTranFlowService;
	@Autowired
	public TLogTranFlowHService tLogTranFlowHService;
	@Autowired
	public TMcmMerchantService tMcmMerchantService;
	@Autowired
	public TInfoTermSignService tInfoTermSignService;
	@Autowired
	public TMcmPosinfoService tMcmPosinfoService;
	@Autowired
	private TCfgTranControlService tCfgTranControlService;
	@Autowired
	private TMcmMcttransprivService tMcmMcttransprivService;
	@Autowired
	private TMcmPostransprivService tMcmPostransprivService;
	@Autowired
	private TCfgBranchTranControlService tCfgBranchTranControlService;
	@Autowired
	private TCfgMerchBlackControlService tCfgMerchBlackControlService;
	@Autowired
	private TCfgTermBlackControlService tCfgTermBlackControlService;
	@Autowired
	private TCfgCardBlackControlService tCfgCardBlackControlService;
	@Autowired
	private TStlBankCardBinService tStlBankCardBinService;
	@Autowired
	private TStlRiskTransCardService tStlRiskTransCardService;
	@Autowired
	private TCfgRiskWhiteListService tCfgRiskWhiteListService;

	/**
	 * 加密卡号
	 * @param accountNumber
	 * @return
	 */
	public String encryptionAccountNumber(String accountNumber){
		if(StringUtils.isNotEmpty(accountNumber)){
			CipherCard cipherCard = new CipherCard();
			cipherCard.setCardNo(accountNumber);
			cipherCard.setAppEncrypt(DecipherAppKey.getAPPKey());
			return tSymAppkeyService.cipherCard(cipherCard);
		}
		return null;
	}
	/**
	 * 解密卡号
	 * @param accountNumber
	 * @return
	 */
	public String decryptionAccountNumber(String accountNumber){
		if(StringUtils.isNotEmpty(accountNumber)){
			CipherCard cipherCard = new CipherCard();
			cipherCard.setCardNo(accountNumber);
			cipherCard.setAppEncrypt(DecipherAppKey.getAPPKey());
			return tSymAppkeyService.decipherCard(cipherCard);
		}
		return null;
	}
	/**
	 * 获取交易流水信息
	 * @param iso
	 * @return
	 */
	public TLogTranFlow getTLogTranFlow(IsoMessage iso){
		TLogTranFlow tLogTranFlow = new TLogTranFlow();
		tLogTranFlow.setMerchCode(iso.getField(42));// 获取商户编号
		tLogTranFlow.setTermCode(iso.getField(41));// 获取终端编号
		tLogTranFlow.setTermSerialNo(iso.getField(11));// 获取终端流水
		tLogTranFlow.setBatchNo(iso.getField(60).substring(2, 8));// 获取批次号
		return tLogTranFlowService.getLogTranFlow(tLogTranFlow);
	}
	/**
	 * 获取实时交易流水信息
	 * @param serialNo
	 * @return
	 */
	public TLogTranFlow getTLogTranFlow(String serialNo){
		TLogTranFlow tLogTranFlow = new TLogTranFlow();
		tLogTranFlow.setSerialNo(serialNo);
		return tLogTranFlowService.getLogTranFlow(tLogTranFlow);
	}
	/**
	 * 获取历史交易流水信息
	 * @param serialNo
	 * @return
	 */
	public TLogTranFlowH getTLogTranFlowH(String serialNo){
		TLogTranFlowH tLogTranFlowH = new TLogTranFlowH();
		tLogTranFlowH.setSerialNo(serialNo);
		return tLogTranFlowHService.getLogTranFlowH(tLogTranFlowH);
	}
	/**
	 * 获取商户信息
	 * @param iso
	 * @return
	 */
	public TMcmMerchant getTMcmMerchant(IsoMessage iso){
		TMcmMerchant tMcmMerchant = new TMcmMerchant();
		tMcmMerchant.setMctCode(iso.getField(42));// 获取商户编号
		return tMcmMerchantService.getMcmMerchant(tMcmMerchant);
	}
	/**
	 * 获取终端信息
	 * @param iso
	 * @return
	 */
	public TMcmPosinfo getTMcmPosinfo(IsoMessage iso){
		TMcmPosinfo tMcmPosinfo = new TMcmPosinfo();
		tMcmPosinfo.setPosCode(iso.getField(41));// 获取终端编号
		tMcmPosinfo.setMctCode(iso.getField(42));//获取商户编号
		return tMcmPosinfoService.getMcmPosinfo(tMcmPosinfo);
	}
	/**
	 * 获取终端签到信息
	 * @param iso
	 * @return
	 */
	public TInfoTermSign getTerminalRegistrationInformation(IsoMessage iso){
		TInfoTermSign tInfoTermSign = new TInfoTermSign();
		tInfoTermSign.setTermCode(iso.getField(FieldConstant.FIELD64_41));// 获取终端编号
		tInfoTermSign.setMerchCode(iso.getField(FieldConstant.FIELD64_42));//获取商户编号
		return tInfoTermSignService.getPospTermSign(tInfoTermSign);
	}
	/**
	 * 获取交易控制表信息
	 * @param iso
	 * @return
	 */
	public TCfgTranControl getTCfgTranControl(IsoMessage iso){
		TCfgTranControl tCfgTranControl = new TCfgTranControl();
		tCfgTranControl.setTranCode(iso.getTranCode());// 获取交易码
		return tCfgTranControlService.getCfgTranControl(tCfgTranControl);
	}
	/**
	 * 获取商户交易控制信息
	 * @param iso
	 * @return
	 */
	public TMcmMcttranspriv getTMcmMcttranspriv(IsoMessage iso){
		TMcmMcttranspriv tMcmMcttranspriv = new TMcmMcttranspriv();
		tMcmMcttranspriv.setMctCode(iso.getField(42));// 获取商户代码
		tMcmMcttranspriv.setTransCode(iso.getField(0));// 获取交易处理码
		tMcmMcttranspriv.setProcessCode(iso.getField(3).substring(0,2));
		tMcmMcttranspriv.setChannelCode(iso.getField(60).substring(0,2));
		return tMcmMcttransprivService.getMcmMcttranspriv(tMcmMcttranspriv);
	}
	/**
	 * 获取商户交易控制信息
	 * @param iso
	 * @return
	 */
	public TMcmMcttranspriv getTMcmMcttransprivByCardType(IsoMessage iso){
		TMcmMcttranspriv tMcmMcttranspriv = new TMcmMcttranspriv();
		tMcmMcttranspriv.setMctCode(iso.getField(42));// 获取商户代码
		tMcmMcttranspriv.setTransId(iso.getFlow().getCardType());// 获取交易处理码
		return tMcmMcttransprivService.getMcmMcttranspriv(tMcmMcttranspriv);
	}
	/**
	 * 获取终端交易控制表信息
	 * @param iso
	 * @return
	 */
	public TMcmPostranspriv getTMcmPostranspriv(IsoMessage iso){
		TMcmPostranspriv tMcmPostranspriv = new TMcmPostranspriv();
		tMcmPostranspriv.setPosCode(iso.getField(41));// 获取终端代码
		tMcmPostranspriv.setMctCode(iso.getField(42));
		tMcmPostranspriv.setTransCode(iso.getField(0));// 获取交易处理码
		tMcmPostranspriv.setProcessCode(iso.getField(3).substring(0,2));
		tMcmPostranspriv.setChannelCode(iso.getField(60).substring(0,2));
		return tMcmPostransprivService.getMcmPostranspriv(tMcmPostranspriv);
	}
	/**
	 * 获取区域交易控制表信息
	 * @param iso
	 * @return
	 */
	public TCfgBranchTranControl getTCfgBranchTranControl(IsoMessage iso){
		TCfgBranchTranControl tCfgBranchTranControl = new TCfgBranchTranControl();
		tCfgBranchTranControl.setBranchNo(iso.getFlow().getRcvBankCode().substring(4, 8));// 获取机构代码
		tCfgBranchTranControl.setTranCode(iso.getFlow().getTranCode());// 获取交易码
		return tCfgBranchTranControlService.getCfgBranchTranControl(tCfgBranchTranControl);
	}
	/**
	 * 获取商户黑名单信息
	 * @param iso
	 * @return
	 */
	public TCfgMerchBlackControl getTCfgMerchBlackControl(IsoMessage iso){
		TCfgMerchBlackControl tCfgMerchBlackControl = new TCfgMerchBlackControl();
		tCfgMerchBlackControl.setMerchCode(iso.getField(FieldConstant.FIELD64_42));// 获取商户代码
		tCfgMerchBlackControl.setOrigin("B0I");//商户黑名单
		return tCfgMerchBlackControlService.getCfgMerchBlackControl(tCfgMerchBlackControl);
	}
	/**
	 * 获取终端黑名单信息
	 * @param iso
	 * @return
	 */
	public TCfgTermBlackControl getTCfgTermBlackControl(IsoMessage iso){
		TCfgTermBlackControl tCfgTermBlackControl = new TCfgTermBlackControl();
		tCfgTermBlackControl.setTermCode(iso.getField(FieldConstant.FIELD64_41));// 获取终端代码
		tCfgTermBlackControl.setMerchCode(iso.getField(FieldConstant.FIELD64_42));// 获取商户代码
		return tCfgTermBlackControlService.getCfgTermBlackControl(tCfgTermBlackControl);
	}
	/**
	 * 获取卡黑名单信息
	 * @param iso
	 * @return
	 */
	public TCfgCardBlackControl getTCfgCardBlackControl(IsoMessage iso){
		TCfgCardBlackControl tCfgCardBlackControl = new TCfgCardBlackControl();
		tCfgCardBlackControl.setCardNo(iso.getFlow().getCardNo());// 获取卡号
		return tCfgCardBlackControlService.getCfgCardBlackControl(tCfgCardBlackControl);
	}

	/**
	 * 获取卡控制信息
	 * @param iso
	 * @return
     */
	public TStlRiskTransCard getTStlRiskTransCard(IsoMessage iso){
		TStlRiskTransCard tStlRiskTransCard = new TStlRiskTransCard();
		tStlRiskTransCard.setCardType(iso.getFlow().getCardType());
		return tStlRiskTransCardService.findUnique(tStlRiskTransCard);
	}

	/**
	 * 获取代理商基站信息
	 * @param servAgent
	 * @return
     */
	public List<TCfgRiskWhiteList> getTCfgRiskWhiteLists(String servAgent){
		TCfgRiskWhiteList tCfgRiskWhiteList = new TCfgRiskWhiteList();
		tCfgRiskWhiteList.setServAgent(servAgent);
		tCfgRiskWhiteList.setStat("S0A");
		return tCfgRiskWhiteListService.getTCfgRiskWhiteList(tCfgRiskWhiteList);
	}
	/**
	 * 获取卡bin信息
	 * @param card
	 * @return
	 */
	public TStlBankCardBin getTStlBankCardBin(String card){
		return tStlBankCardBinService.queryByCardNo(card);
	}
	/**
	 * 通过地区码获取地区机构信息
	 * @param areaCode
	 * @return
	 */
	public TSymArea findByAreaCode(String areaCode){
		return tSymAreaService.findByAreaCode(areaCode);
	}
}
