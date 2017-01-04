package com.bestpay.posp.service.checker;

import com.bestpay.posp.constant.CheckerConstant;
import com.bestpay.posp.constant.FieldConstant;
import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.service.impl.ObtainObjectInformation;
import com.bestpay.posp.service.impl.Pub_UpdateTransactionRecord;
import com.bestpay.posp.system.entity.TCfgTranControl;
import com.bestpay.posp.system.service.TCfgBranchTranControlService;
import com.bestpay.posp.system.service.TLogTranFlowService;
import com.bestpay.posp.system.service.TSymAppkeyService;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.protocol.DecipherAppKey;
import com.bestpay.posp.service.impl.DataManipulations;
import com.bestpay.posp.service.impl.RespCodeInformation;
import com.bestpay.posp.system.entity.CipherCard;
import com.bestpay.posp.system.entity.TCfgBranchTranControl;
import com.bestpay.posp.system.entity.TCfgCardBlackControl;
import com.bestpay.posp.system.entity.TCfgMerchBlackControl;
import com.bestpay.posp.system.entity.TCfgTermBlackControl;
import com.bestpay.posp.system.entity.TLogTranFlow;
import com.bestpay.posp.system.entity.TLogTranFlowH;
import com.bestpay.posp.system.entity.TMcmMcttranspriv;
import com.bestpay.posp.system.entity.TMcmMerchant;
import com.bestpay.posp.system.entity.TMcmPosinfo;
import com.bestpay.posp.system.entity.TMcmPostranspriv;
import com.bestpay.posp.system.entity.TStlRiskTransCard;
import com.bestpay.posp.system.service.TCfgCardBlackControlService;
import com.bestpay.posp.system.service.TCfgMerchBlackControlService;
import com.bestpay.posp.system.service.TCfgTermBlackControlService;
import com.bestpay.posp.system.service.TCfgTranControlService;
import com.bestpay.posp.system.service.TLogTranFlowHService;
import com.bestpay.posp.system.service.TMcmMcttransprivService;
import com.bestpay.posp.system.service.TMcmMerchantService;
import com.bestpay.posp.system.service.TMcmPosinfoService;
import com.bestpay.posp.system.service.TMcmPostransprivService;
import com.bestpay.posp.system.service.TStlRiskTransCardService;

/**
 * 商户风险控制
 * 
 * @author HR
 * 
 */
@Slf4j
@Component
public class Pub_MerchRiskControl {
	@Autowired
	private Pub_UpdateTransactionRecord updateTransactionRecord;
	@Autowired
	private ObtainObjectInformation objectInformation;
	@Autowired
	private Pub_AmountControl pub_amountControl;
	/**
	 * 请求的交易--商户风险控制
	 * @param iso
	 * @return
	 * @throws Exception
     */
	public IsoMessage merchRiskControl(IsoMessage iso) throws Exception {
		if (tranOpenFlag(iso).isState() && merchTranOpenFlag(iso).isState()
				&& termTranOpenFlag(iso).isState()
				&& branchTranOpenFlag(iso).isState()
				&& merchBlackFlag(iso).isState()
				&& termBlackFlag(iso).isState()
				&& cardBlackFlag(iso).isState()
				&& amountRiskControl(iso).isState()) {
			iso.setState(true);
			log.info(String.format("[%s,%s] 商户风险控制检查通过！", iso.getFlow()
					.getTranCode(), iso.getSeq()));

		} else {
			iso.setState(false);
			updateTransactionRecord.updatePublicFailure(iso);
			log.info(String.format("[%s,%s] 商户风险控制检查不通过！", iso.getFlow()
					.getTranCode(), iso.getSeq()));
		}
		return iso;
	}
	/**
	 * 查询交易控制表
	 * @param iso
	 * @return
     */
	public IsoMessage tranOpenFlag(IsoMessage iso) {
		iso.setState(false);
		// 通过交易代码查询交易控制表(T_CFG_TRAN_CONTROL)
		TCfgTranControl cfgTranControl = objectInformation.getTCfgTranControl(iso);
		// 是否开通标志open_flag，如果为0没有开通，返回码12，如果为1开通或没有查询到相关信息，返回码00,若查询出错，返回码96。
		if(cfgTranControl == null || StringUtils.equals("1", cfgTranControl.getOpenFlag())){
			iso.setState(true);
			return iso;
		}
		if(StringUtils.equals("0",cfgTranControl.getOpenFlag())){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A012,iso);
			log.info(String.format("[%s] tranOpenFlag：暂不支持此交易! ", iso.getSeq()));
			iso.setState(false);
			return iso;
		}else{
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A496,iso);
			log.info(String.format("[%s] tranOpenFlag：交易控制出现故障! ", iso.getSeq()));
			iso.setState(false);
			return iso;
		} 	
	}

	/**
	 *查询商户交易控制表
	 * @param iso
	 * @return
     */
	public IsoMessage merchTranOpenFlag(IsoMessage iso) {
		iso.setState(false);
		//通过卡类型判断该商户是否允许该类型卡做交易
		//01（借记卡）对应数据库值“DBC”，02（贷记卡）、03（准贷记卡）对应数据库值“CDC”
		//查询到结果则允许做交易，查询不到，返回无效交易
		if(StringUtils.isEmpty(iso.getFlow().getCardType())){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A015,iso);
			log.info(String.format("[%s] merchTranOpenFlag：无此发卡行! ", iso.getSeq()));
			return iso;
		}
		TMcmMcttranspriv mcmMcttranspriv1 = objectInformation.getTMcmMcttransprivByCardType(iso);
		if(mcmMcttranspriv1 == null){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A358,iso);
			log.info(String.format("[%s] merchTranOpenFlag：商户不支持该类型卡! ", iso.getSeq()));
			return iso;
		}
		// 通过商户代码和交易码查询商户交易控制表(T_CFG_MERCH_TRAN_CONTROL)
		// 是否开通标志open_flag，如果为S0A开通，返回码00,否则返回12，无效交易。
		//因管理平台控制无效交易是直接删除这条记录，所以在查询不到的情况下，返回无效交易
		TMcmMcttranspriv mcmMcttranspriv = objectInformation.getTMcmMcttranspriv(iso);
		if(mcmMcttranspriv == null){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A458,iso);
			log.info(String.format("[%s] merchTranOpenFlag：不允许商户做此交易! ", iso.getSeq()));
			return iso;
		}
		if(StringUtils.equals(CheckerConstant.POS_STATE_S0A, mcmMcttranspriv.getStat())){
			iso.setState(true);
			return iso;
		}else{
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A458,iso);
			log.info(String.format("[%s] merchTranOpenFlag：商户暂不支持此交易! ", iso.getSeq()));
			return iso;
		}
	}

	/**
	 *查询终端交易控制表
	 * @param iso
	 * @return
     */
	public IsoMessage termTranOpenFlag(IsoMessage iso) {
		iso.setState(false);
		// 通过终端代码和交易码查询终端交易控制表(T_CFG_TERM_TRAN_CONTROL)
		TMcmPostranspriv mcmPostranspriv = objectInformation.getTMcmPostranspriv(iso);
		// 是否开通标志open_flag，如果为S0A开通，返回码00,否则返回03，无效交易。
		//因管理平台控制无效交易是直接删除这条记录，所以在查询不到的情况下，返回无效交易
		if(mcmPostranspriv == null){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A558,iso);
			log.info(String.format("[%s] termTranOpenFlag：不允许终端做此交易! ", iso.getSeq()));
			return iso;
		}
		if(StringUtils.equals(CheckerConstant.POS_STATE_S0A, mcmPostranspriv.getStat())){
			iso.setState(true);
			return iso;
		}else{
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A558,iso);
			log.info(String.format("[%s] termTranOpenFlag：终端暂不支持此交易! ", iso.getSeq()));
			return iso;
		}
	}

	/**
	 * 查询机构交易控制表
	 * @param iso
	 * @return
     */
	public IsoMessage branchTranOpenFlag(IsoMessage iso) {
		iso.setState(false);
		// 通过机构代码和交易码查询机构交易控制表(T_CFG_BRANCH_TRAN_CONTROL)
		TCfgBranchTranControl cfgBranchTranControl = objectInformation.getTCfgBranchTranControl(iso);
		// 是否开通标志open_flag，如果为0没有开通，返回码12，如果为1开通或没有查询到相关信息，返回码00,若查询出错，返回码96。
		if(cfgBranchTranControl == null || StringUtils.equals("1",cfgBranchTranControl.getOpenFlag())){
			iso.setState(true);
			return iso;
		}
		if(StringUtils.equals("0",cfgBranchTranControl.getOpenFlag())){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A040,iso);
			log.info(String.format("[%s] branchTranOpenFlag：不允许机构做此交易! ", iso.getSeq()));
			return iso;
		}else{
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A596,iso);
			log.info(String.format("[%s] branchTranOpenFlag：机构交易控制出现故障! ", iso.getSeq()));
			return iso;
		}
	}

	/**
	 * 查询商户黑名单控制表
	 * @param iso
	 * @return
     */
	public IsoMessage merchBlackFlag(IsoMessage iso) {
		iso.setState(false);
		// 通过商户代码查询商户黑名单控制表(T_CFG_MERCH_ BLACK_CONTROL)
		TCfgMerchBlackControl cfgMerchBlackControl = objectInformation.getTCfgMerchBlackControl(iso);
		// 是否黑名单标志black_flag，如果为0是黑名单，返回码58，如果为1开通或没有查询到相关信息，返回码00,若查询出错，返回码96。
		if(cfgMerchBlackControl == null || StringUtils.equals("S0X",cfgMerchBlackControl.getState())){
			iso.setState(true);
			return iso;
		}
		if(StringUtils.equals("S0A",cfgMerchBlackControl.getState())){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A658,iso);
			log.info(String.format("[%s] merchBlackFlag：该商户在黑名单中! ", iso.getSeq()));
			return iso;
		}else{
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A696,iso);
			log.info(String.format("[%s] merchBlackFlag：商户黑名单控制出现故障! ", iso.getSeq()));
			return iso;
		}
	}
	
	/**
	 * 查询终端黑名单控制表
	 * @param iso
	 * @return
     */
	public IsoMessage termBlackFlag(IsoMessage iso) {
		iso.setState(false);
		TCfgTermBlackControl cfgTermBlackControl = objectInformation.getTCfgTermBlackControl(iso);
		if(cfgTermBlackControl == null || StringUtils.equals("S0X",cfgTermBlackControl.getState())){
			iso.setState(true);
			return iso;
		}
		if(StringUtils.equals("S0A",cfgTermBlackControl.getState())){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A758,iso);
			log.info(String.format("[%s] termBlackFlag：该终端在黑名单中! ", iso.getSeq()));
			return iso;
		}else{
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A796,iso);
			log.info(String.format("[%s] termBlackFlag：终端黑名单控制出现故障! ", iso.getSeq()));
			return iso;
		}
	}
	
	/**
	 * 查询卡号黑名单控制表
	 * @param iso
	 * @return
     */
	public IsoMessage cardBlackFlag(IsoMessage iso) {
		iso.setState(false);
		TCfgCardBlackControl cfgCardBlackControl = objectInformation.getTCfgCardBlackControl(iso);
		if(cfgCardBlackControl == null || StringUtils.equals("S0X",cfgCardBlackControl.getState())){
			iso.setState(true);
			return iso;
		}
		if(StringUtils.equals("S0A",cfgCardBlackControl.getState())){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A062,iso);
			log.info(String.format("[%s] cardBlackFlag：该卡号在黑名单中! ", iso.getSeq()));
			return iso;
		}else{
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A896,iso);
			log.info(String.format("[%s] cardBlackFlag：卡号黑名单控制出现故障! ", iso.getSeq()));
			return iso;
		}
	}

	/**
	 * 金额风险控制
	 * @param iso
	 * @return
	 */
	public IsoMessage amountRiskControl(IsoMessage iso) {
		pub_amountControl.amountControl(iso);
		return iso;
	}
}

