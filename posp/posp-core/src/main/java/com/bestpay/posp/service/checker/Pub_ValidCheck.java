/**
 * 
 */
package com.bestpay.posp.service.checker;

import com.bestpay.posp.constant.*;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.protocol.util.Utils;
import com.bestpay.posp.service.impl.ObtainObjectInformation;
import com.bestpay.posp.system.entity.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.service.impl.Pub_UpdateTransactionRecord;
import com.bestpay.posp.service.impl.RespCodeInformation;

import java.math.BigDecimal;

/**
 * @author HR
 * 
 */
@Slf4j
@Component
public class Pub_ValidCheck {
	
	@Autowired
	private Pub_ThirdPartyCheck pub_ThirdPartyCheck;
	@Autowired
	private Pub_PositionControl pub_positionControl;
	@Autowired
	private ObtainObjectInformation obtainObjectInformation;
	@Autowired
	private Pub_UpdateTransactionRecord updateTransactionRecord;
	@Autowired
	private ObtainObjectInformation objectInformation;
	/**
	 * 请求的交易报文检查
	 * @param iso
	 * @return
	 * @throws Exception 
	 */
	public IsoMessage validCheck(IsoMessage iso){
		if (pub_ThirdPartyCheck.check(iso).isState()
				&& merchantInformationCheck(iso) 
				&& terminalInformationCheck(iso)
				&& terminalRegistrationInformationCheck(iso) 
				&& terminalBatchNumberCheck(iso)
				&& positionControlCheck(iso)) {
			iso.setState(true);
			log.info(String.format("[%s,%s] 报文合法有效性检查通过！", iso.getTranCode(), iso.getSeq()));
			return iso;
		}
		iso.setState(false);
		updateTransactionRecord.updatePublicFailure(iso);
		log.info(String.format("[%s,%s] 报文合法有效性检查不通过！", iso.getTranCode(), iso.getSeq()));
		return iso;
	}
	/**
	 * 商户检查
	 * 用于状态监控交易和电子化凭条交易
	 * @param iso
	 * @return
	 */
	public IsoMessage merchantInfoCheck(IsoMessage iso){
		if(merchantInformationCheck(iso)){
			iso.setState(true);
			log.info(String.format("[%s,%s] 报文合法有效性检查通过！", iso.getTranCode(), iso.getSeq()));
			return iso;
		}
		iso.setState(false);
		log.info(String.format("[%s,%s] 报文合法有效性检查不通过！", iso.getTranCode(), iso.getSeq()));
		return iso;
	}
	/**
	 * 商户终端检查
	 * 用于参数以及公钥的查询和下载交易
	 * @param iso
	 * @return
	 */
	public IsoMessage merchantAndterminalCheck(IsoMessage iso){
		if(!iso.isPlatform()
				|| (merchantInformationCheck(iso)
				&& terminalInformationCheck(iso))){
			iso.setState(true);
			log.info(String.format("[%s,%s] 报文合法有效性检查通过！", iso.getTranCode(), iso.getSeq()));
			return iso;
		}
		iso.setState(false);
		log.info(String.format("[%s,%s] 报文合法有效性检查不通过！", iso.getTranCode(), iso.getSeq()));
		return iso;
	}
	/**
	 * 交易流水检查
	 * @param iso
	 * @return
	 */
	public boolean transactionFlowCheck(IsoMessage iso){
		if(!PosConstant.FLOW_CHECK_TYPE.contains(iso.getTranCode())){
			return true;
		}
		TLogTranFlow tlogTranFlow = null;
		try {
			tlogTranFlow = obtainObjectInformation.getTLogTranFlow(iso);
		}catch (Exception e){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A094,iso);
			return false;
		}
		if(Utils.isNotNullAndReturn(tlogTranFlow,POSPConstant.POSP_A094,iso)){
			return false;
		}
		return true;
	}

	/**
	 * 商户信息检查
	 * @param iso
	 * @return
	 */
	public boolean merchantInformationCheck(IsoMessage iso){
		TMcmMerchant tMcmMerchant = obtainObjectInformation.getTMcmMerchant(iso);
		// 检索商户是否存在，不存在则返回03
		if(Utils.isNullAndReturn(tMcmMerchant,POSPConstant.POSP_A003,iso)
				|| !merchantStatusCheck(tMcmMerchant,iso)){
			return false;
		}
		freePasswordCheck(tMcmMerchant,iso);
		return true;
	}
	
	/**
	 * 商户状态检查（包括第三方商户）
	 * @param mcmMerchant
	 * @param iso
	 * @return
	 */
	private boolean merchantStatusCheck(TMcmMerchant mcmMerchant,IsoMessage iso){
		// 检查商户基本信息表中该商户状态是否有效即对应字段stat是否为S0A，不是则返回03，是则通过
		// 第三方平台交易检查商户所属机构代码是否一致,不是则返回03
		if(!StringUtils.equals(CheckerConstant.POS_STATE_S0A,mcmMerchant.getStat())
				|| (!iso.isPlatform() && !StringUtils.equals(mcmMerchant.getJoinWay(), iso.getField(32)))){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A103,iso);
			return false;
		}
		return true;
	}

	/**
	 * 小额双免检查
	 * 1、小额双免商户
	 * 2、消费和预授权交易以及相应冲正交易
	 * 3、金额小于300元
	 * 4、快速PBOC借贷记IC卡读入（非接触式）
	 * 5、不包含密码域
	 * @param mcmMerchant
	 * @param iso
     * @return
     */
	private void freePasswordCheck(TMcmMerchant mcmMerchant,IsoMessage iso){
		iso.setFreePasswordSign("0");
		try {
			if (StringUtils.equals("0", mcmMerchant.getMinFree())) { //检查是否为小额双免商户
				return;
			}
			if (!StringUtils.equals(PosConstant.TRANS_TYPE_0200000022, iso.getTranCode())
					&& !StringUtils.equals(PosConstant.TRANS_TYPE_0100030610, iso.getTranCode())
					) {
				return;
			}
			TStlRiskTransCard stlRiskTransCard = objectInformation.getTStlRiskTransCard(iso);
			String freeLimit = stlRiskTransCard.getFreeLimit().toString();
			if (Integer.parseInt(iso.getField(4))
					> new BigDecimal(freeLimit).movePointRight(2).intValue()) {
				return;
			}
			if (StringUtils.isEmpty(iso.getField(55))
					|| !SysConstant.NON_CONN.contains(iso.getField(22).substring(0, 2))) {
				return;
			}
			if (StringUtils.isNotEmpty(iso.getField(52))) {
				return;
			}
			iso.setFreePasswordSign("1");
		}catch (Exception e){
			log.error(e.getMessage());
		}
	}
	/**
	 * 终端信息检查
	 * @param iso
	 * @return
	 */
	public boolean terminalInformationCheck(IsoMessage iso){
		TMcmPosinfo tMcmPosinfo = obtainObjectInformation.getTMcmPosinfo(iso);
		// 检索终端是否存在，不存在则返回97
		if(Utils.isNullAndReturn(tMcmPosinfo,POSPConstant.POSP_A097,iso)
				|| !terminalStatusCheck(tMcmPosinfo,iso)
				|| !terminalBindingNumberCheck(iso)){
			return false;
		}
		return true;
	}
	
	/**
	 * 终端状态检查
	 * @param tMcmPosinfo
	 * @param iso
	 * @return
	 */
	private boolean terminalStatusCheck(TMcmPosinfo tMcmPosinfo,IsoMessage iso){
		// 检查商户终端基本信息表中该终端状态是否有效即对应字段POS_STATE是否为S0A,是则通过，不是则返回58
		if(!StringUtils.equals(CheckerConstant.POS_STATE_S0A,tMcmPosinfo.getPosState())) {
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A058,iso);
			return false;
		}
		return true;
	}
	/**
	 * 电话接入终端绑定号码检查
	 * @param iso
	 * @return
	 */
	private boolean terminalBindingNumberCheck(IsoMessage iso){
		//主叫号码检验，一致通过，不一致则返回
		if(StringUtils.equals(iso.getFlow().getIsMatch(), "0")){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A158,iso);
			return false;
		}
		return true;
	}

	/**
	 * 终端签到信息检查
	 * @param iso
	 * @return
	 */
	public boolean terminalRegistrationInformationCheck(IsoMessage iso){
		//签到交易或者第三方平台不检查终端签到信息
		if(isSignTradeOrThirdPartyPlatform(iso)){
			return true;
		}
		TInfoTermSign tInfoTermSign = obtainObjectInformation.getTerminalRegistrationInformation(iso);
		// 检查终端签到状态sign_state值，若无此终端返回码为97
		if(Utils.isNullAndReturn(tInfoTermSign,POSPConstant.POSP_A097,iso)
				|| !terminalSignStatusCheck(tInfoTermSign,iso)){
			return false;
		}
		return true;
	}
	
	/**
	 * 终端签到状态检查
	 * @param tInfoTermSign
	 * @param iso
	 * @return
	 */
	private boolean terminalSignStatusCheck(TInfoTermSign tInfoTermSign,IsoMessage iso){
		// 非签到交易时终端签到状态sign_state为1通过,0、2、3则返回码为77，sign_state为4返回码为58
		if(StringUtils.equals(CheckerConstant.SIGN_STATE_1,tInfoTermSign.getSignState())){
			return true;
		}
		if(StringUtils.equals(CheckerConstant.SIGN_STATE_4,tInfoTermSign.getSignState())){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A258,iso);
			return false;
		}
		RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A277,iso);
		return false;
	}
	/**
	 * 终端批次号检查
	 * @param iso
	 * @return
	 */
	public boolean terminalBatchNumberCheck(IsoMessage iso){
		//签到交易或者第三方平台不检查终端批次号
		if(isSignTradeOrThirdPartyPlatform(iso)){
			return true;
		}
		TMcmPosinfo tMcmPosinfo = obtainObjectInformation.getTMcmPosinfo(iso);
		if(Utils.isNullAndReturn(tMcmPosinfo,POSPConstant.POSP_A097,iso)
				|| !isBatchNumberConsistent(tMcmPosinfo,iso)){
			return false;
		}
		return true;
	}
	/**
	 * 检查终端批次号是否一致
	 * @param tMcmPosinfo
	 * @param iso
	 * @return
	 */
	private boolean isBatchNumberConsistent(TMcmPosinfo tMcmPosinfo,IsoMessage iso){
		// 检查终端密钥表中的批次号batch_no,若和请求批次号相同则过，反之则返回码为77
		if(StringUtils.equals(iso.getField(60).substring(2, 8),tMcmPosinfo.getBatchNo())){
			return true;
		}
		//签退交易允许批次号差别1
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0820002,iso.getTranCode())
				&& (Math.abs(Integer.parseInt(iso.getField(60).substring(2, 8))
						- Integer.parseInt(tMcmPosinfo.getBatchNo())) <= 1)){
			return true;
		}
		RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A077, iso);
		return false;
	}

	/**
	 * 位置控制
	 * @param iso
	 * @return
     */
	private boolean positionControlCheck(IsoMessage iso){
		return pub_positionControl.positionControl(iso);
	}
	
	/**
	 * 检查是否为签到交易或者是否为第三方平台接入
	 * @param iso
	 * @return
	 */
	private boolean isSignTradeOrThirdPartyPlatform(IsoMessage iso){
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0800004,iso.getTranCode())
				|| StringUtils.equals(PosConstant.TRANS_TYPE_0800006,iso.getTranCode())
				|| !iso.isPlatform()){
			return true;
		}
		return false;
	}
}
