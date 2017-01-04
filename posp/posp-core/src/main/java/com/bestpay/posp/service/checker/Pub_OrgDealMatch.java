package com.bestpay.posp.service.checker;

import com.bestpay.posp.constant.FieldConstant;
import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.protocol.FlowMessage;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.util.Utils;
import com.bestpay.posp.service.impl.AssemblyMessage;
import com.bestpay.posp.service.impl.ObtainObjectInformation;
import com.bestpay.posp.service.impl.Pub_UpdateTransactionRecord;
import com.bestpay.posp.service.impl.RespCodeInformation;
import com.bestpay.posp.system.entity.TLogTranFlow;
import com.bestpay.posp.system.entity.TLogTranFlowH;
import com.bestpay.posp.system.service.TLogTranFlowHService;
import com.bestpay.posp.system.service.TLogTranFlowService;
import com.bestpay.posp.utils.CommondUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 原始交易匹配
 * 
 * @author HR
 * 
 */
@Slf4j
@Component
public class Pub_OrgDealMatch {

	@Autowired
	private TLogTranFlowService tLogTranFlowService;
	@Autowired
	private TLogTranFlowHService tLogTranFlowHService;
	@Autowired
	private ObtainObjectInformation obtainObjectInformation;
	@Autowired
	private AssemblyMessage assemblyMessage;
	@Autowired
	private Pub_UpdateTransactionRecord updateTransactionRecord;
	/**
	 * 请求的交易匹配
	 * @param iso
	 * @return
	 */
	public IsoMessage orgDealMatch(IsoMessage iso) {
		try {
			if (reverseMatch(iso).isState() && writtenNoticeMatch(iso).isState()
					&& preauthMatch(iso).isState()
					&& tradeReturnMatch(iso).isState()
					&& revokeMatch(iso).isState()) {
				assemblyMessage.createField90(iso);
				iso.setState(true);
				log.info(String.format("[%s,%s] 原始交易匹配检查通过！", iso.getTranCode(), iso.getSeq()));
			} else {
				iso.setState(false);
				updateTransactionRecord.updatePublicFailure(iso);
				log.info(String.format("[%s,%s] 原始交易匹配检查不通过！", iso.getTranCode(), iso.getSeq()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(String.format("[%s,%s]"+e.getMessage(), iso.getTranCode(), iso.getSeq()));
			iso.setState(false);
			updateTransactionRecord.updatePublicFailure(iso);
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_AA96,iso);
			log.info(String.format("[%s,%s] 原始交易匹配检查不通过！", iso.getTranCode(), iso.getSeq()));
		}
		return iso;
	}

	/**
	 * 冲正匹配
	 * @param iso
	 * @return
	 * @throws Exception
	 */
	public IsoMessage reverseMatch (IsoMessage iso) throws Exception {
		iso.setState(false);
		if(!iso.getField(0).equals(PosConstant.MSG_TYPE_0400_00)){
			iso.setState(true);
			return iso;
		}
		//通过商户代码、终端代码、原交易码、原批次号、原终端流水号从交易流水表(SD_POSP_TRAN_FLOW)中匹配
		TLogTranFlow tLogTranFlow = new TLogTranFlow();
		tLogTranFlow.setMerchCode(iso.getField(42));// 商户代码获取
		tLogTranFlow.setTermCode(iso.getField(41));// 终端代码获取
		tLogTranFlow.setTranCode(Utils.getOrgTranCode(iso));//原交易码
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0400000022,iso.getFlow().getTranCode()) 
				|| StringUtils.equals(PosConstant.TRANS_TYPE_0400030610,iso.getTranCode())
				 || StringUtils.equals(PosConstant.TRANS_TYPE_0400609145,iso.getTranCode())
				 || StringUtils.equals(PosConstant.TRANS_TYPE_0400629147,iso.getTranCode()) 
				 || StringUtils.equals(PosConstant.TRANS_TYPE_0400639146,iso.getTranCode())
				 || StringUtils.equals(PosConstant.TRANS_TYPE_0400179151,iso.getTranCode())){
			tLogTranFlow.setBatchNo(iso.getField(FieldConstant.FIELD64_60).substring(2, 8));// 原批次号获取
		}else{
			tLogTranFlow.setBatchNo(iso.getField(FieldConstant.FIELD64_61).substring(0, 6));// 原批次号获取
		}
		tLogTranFlow.setTermSerialNo(iso.getFlow().getTermSerialNo());// 原终端流水号获取
		TLogTranFlow logTranFlow = tLogTranFlowService.getLogTranFlow(tLogTranFlow);
		
		if(logTranFlow != null){
			//交易状态tran_flag是2冲正状态，则返回25，原交易已经冲正。
			if(StringUtils.equals("2",logTranFlow.getTranFlag())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A125,iso);
				log.info(String.format("[%s] reverseMatch：原交易已经冲正! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//交易状态tran_flag不是0，则返回25，原交易非正常状态
			if(!StringUtils.equals("0",logTranFlow.getTranFlag())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A225,iso);
				log.info(String.format("[%s] reverseMatch：原交易非正常状态! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//交易日期与系统日期对比不相等，返回25，不支持隔日冲正。
			if(!StringUtils.equals(logTranFlow.getTranDate(),iso.getFlow().getTranDate())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A325,iso);
				log.info(String.format("[%s] reverseMatch：不支持隔日冲正! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//交易返回码EXTSYS_RESP_CODE不是00,不是98和不是(null或””)返回01。
			if(StringUtils.isNotEmpty(logTranFlow.getExtsysRespCode())
					&& !StringUtils.equals("00",logTranFlow.getExtsysRespCode())
					&& !StringUtils.equals("98",logTranFlow.getExtsysRespCode())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A001,iso);
				log.info(String.format("[%s] reverseMatch：原交易失败，请联系发卡行! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//判断冲正交易与原交易金额匹配，不匹配，返回64
			if(CommondUtils.bigDecimal(logTranFlow.getTranAmount().toString())
					!= Double.valueOf(iso.getField(FieldConstant.FIELD64_4))){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A164,iso);
				log.info(String.format("[%s] reverseMatch：冲正交易与原交易金额不匹配! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			
			iso.setState(true);
			iso.setOrgFlow(getValue(logTranFlow));
			//消费冲正，预授权冲正小额双免状态与原交易保持一致
			iso.setFreePasswordSign(logTranFlow.getFreePasswordSign());
			return iso;
		}else{
			//查询不到交易，交易返回01，返回POS端转化成00（冲正交易查询失败为了防止重复发送返回00,并不更新冲正流水表）。
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_00,iso);
			log.info(String.format("[%s] reverseMatch：查询不到交易! ", iso.getSeq()));
			iso.setState(false);
			return iso;
		}
	}
	/**
	 * 写卡通知匹配
	 * @param iso
	 * @return
	 * @throws Exception
	 */
	public IsoMessage writtenNoticeMatch (IsoMessage iso) throws Exception{
		iso.setState(false);
		if(!StringUtils.equals(PosConstant.TRANS_TYPE_0620951,iso.getFlow().getTranCode())){
			iso.setState(true);
			return iso;
		}
		if(StringUtils.equals("31", iso.getField(FieldConstant.FIELD64_3).substring(0,2))){
			try {
				iso.setField(FieldConstant.FIELD64_3, "30"+iso.getField(FieldConstant.FIELD64_3).substring(2,6));
			} catch (Exception e) {
				log.error(e.getMessage());
//				e.printStackTrace();
			}
		}
		//通过商户代码、终端代码、批次号和终端流水号从交易流水表(SD_POSP_TRAN_FLOW)查询
		TLogTranFlow tLogTranFlow = new TLogTranFlow();
		tLogTranFlow.setMerchCode(iso.getField(FieldConstant.FIELD64_42));// 商户代码获取
		tLogTranFlow.setTermCode(iso.getField(FieldConstant.FIELD64_41));// 终端代码获取
		tLogTranFlow.setBatchNo(iso.getField(FieldConstant.FIELD64_61).substring(0,6));// 原批次号获取
		tLogTranFlow.setTermSerialNo(iso.getField(FieldConstant.FIELD64_61).substring(6, 12));// 原终端流水号获取
		TLogTranFlow logTranFlow = tLogTranFlowService.getLogTranFlow(tLogTranFlow);
		//是否有交易，无返回25,终端交易不匹配。则返回00成功。
		if(logTranFlow != null){
			iso.setState(true);
			iso.setOrgFlow(getValue(logTranFlow));
//			utField90(iso);
			return iso;
		}else{
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A425,iso);
			log.info(String.format("[%s] writtenNoticeMatch：写卡通知-无原始交易! ", iso.getSeq()));
			iso.setState(false);
			return iso;
		}
	}
	/**
	 * 预授权完成请求、预授权完成通知、预授权撤销
	 * @param iso
	 * @return
	 * @throws Exception
	 */
	public IsoMessage preauthMatch (IsoMessage iso) throws Exception{
		iso.setState(false);
		if(!StringUtils.equals(PosConstant.TRANS_TYPE_0200000620,iso.getFlow().getTranCode()) 
				&& !StringUtils.equals(PosConstant.TRANS_TYPE_0220000624,iso.getFlow().getTranCode())
				&& !StringUtils.equals(PosConstant.TRANS_TYPE_0100200611,iso.getFlow().getTranCode())){
			iso.setState(true);
			return iso;
		}
		//通过商户代码、交易日期、授权码、卡号和预授权交易码从交易流水表(SD_POSP_TRAN_FLOW)或历史交易流水表(SD_POSP_TRAN_FLOW_h)中匹配原交易
		TLogTranFlow tLogTranFlow = new TLogTranFlow();
		tLogTranFlow.setCardNo(iso.getFlow().getCardNo());// 卡号获取
		tLogTranFlow.setMerchCode(iso.getFlow().getMerchCode());// 商户代码获取
		if(Integer.parseInt(iso.getFlow().getTranDate().substring(4, 8)) >= Integer.parseInt(iso.getField(FieldConstant.FIELD64_61).substring(12, 16)))
			tLogTranFlow.setTranDate(iso.getFlow().getTranDate().substring(0, 4)+iso.getField(FieldConstant.FIELD64_61).substring(12, 16));// 交易日期获取
		else
			tLogTranFlow.setTranDate(Integer.parseInt(iso.getFlow().getTranDate().substring(0, 4)) - 1 + iso.getField(FieldConstant.FIELD64_61).substring(12, 16));// 交易日期获取
		tLogTranFlow.setAuthNo(iso.getField(FieldConstant.FIELD64_38));// 授权码获取
		tLogTranFlow.setTranCode(Utils.getOrgTranCode(iso));// 获取交易码
		TLogTranFlow logTranFlow = tLogTranFlowService.getLogTranFlow(tLogTranFlow);
		TLogTranFlowH tLogTranFlowH = new TLogTranFlowH();
		tLogTranFlowH.setCardNo(tLogTranFlow.getCardNo());// 卡号获取
		tLogTranFlowH.setMerchCode(tLogTranFlow.getMerchCode());// 商户代码获取
		tLogTranFlowH.setTranDate(tLogTranFlow.getTranDate());
		tLogTranFlowH.setAuthNo(tLogTranFlow.getAuthNo());// 授权码获取
		tLogTranFlowH.setTranCode(tLogTranFlow.getTranCode());// 获取交易码
		TLogTranFlowH logTranFlowH = tLogTranFlowHService.getLogTranFlowH(tLogTranFlowH);
		//查询不到交易则返回25，若查询出错，返回码96。
		if(logTranFlow != null){
			//计算两个日期相差的天数
			long diff = 0;
			try {
				DateFormat df = new SimpleDateFormat("yyyyMMdd");
				Date d1 = df.parse(iso.getFlow().getTranDate());
				Date d2 = df.parse(logTranFlow.getTranDate());
				diff = d1.getTime() - d2.getTime();
				diff = diff/1000/24/60/60;
			} catch (ParseException e) {
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A996,iso);
				log.error(String.format("[%s] preauthMatch：计算预授权交易时间间隔异常! ", iso.getSeq()));
				log.error(e.getMessage());
				iso.setState(false);
				return iso;
			}
			//匹配原交易日期是否相等，不等返回25
			if(!StringUtils.equals(iso.getField(FieldConstant.FIELD64_61).substring(12, 16),logTranFlow.getTranDate().substring(4, 8))){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A525,iso);
				log.info(String.format("[%s] preauthMatch：原交易日期不正确! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//计算系统日期与原交易日期间隔，间隔日期不能大于29天，如果大于就返回25.
			if(diff > 29){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A022,iso);
				log.info(String.format("[%s] preauthMatch：预授权交易时间已大于29天! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//原交易状态tran_flag是否是0（正常）或5（预授权），不正常返回25
			if(!StringUtils.equals("0",logTranFlow.getTranFlag())
					&& !StringUtils.equals("5",logTranFlow.getTranFlag())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A225,iso);
				log.info(String.format("[%s] preauthMatch：原交易状态非正常状态! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//原交易类型tran_type是否是10，不是返回25
			if(!StringUtils.equals("10",logTranFlow.getTranType())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A725,iso);
				log.info(String.format("[%s] preauthMatch：交易类型不正确! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			if(StringUtils.equals(PosConstant.TRANS_TYPE_0100200611,iso.getFlow().getTranCode())){
				//撤销卡号与原交易匹配，不匹配返回14，撤销卡号与原交易不匹配
				if(!StringUtils.equals(iso.getFlow().getCardNo(),logTranFlow.getCardNo())){
					RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A014,iso);
					log.info(String.format("[%s] preauthMatch：预授权撤销卡号与原交易不匹配! ", iso.getSeq()));
					iso.setState(false);
					return iso;
				}
				//预授权撤销金额等于预授权金额，不等则返回64
				if(Double.valueOf(iso.getField(FieldConstant.FIELD64_4))
						!= CommondUtils.bigDecimal(logTranFlow.getTranAmount().toString())){
					RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A264,iso);
					log.info(String.format("[%s] preauthMatch：预授权撤销金额不等于预授权金额! ", iso.getSeq()));
					iso.setState(false);
					return iso;
				}else{
					iso.setState(true);
					iso.setOrgFlow(getValue(logTranFlow));
//					utField90(iso);
					return iso;
				}
			}
			if(StringUtils.equals(PosConstant.TRANS_TYPE_0200000620,iso.getFlow().getTranCode()) 
					|| StringUtils.equals(PosConstant.TRANS_TYPE_0220000624,iso.getFlow().getTranCode())){
				//预授权完成金额不大于预授权金额。返回64
				if(Double.valueOf(iso.getField(FieldConstant.FIELD64_4))
						> CommondUtils.multiply(CommondUtils.bigDecimal(logTranFlow.getAuthAmount().toString())
							,Double.valueOf(1.15))){
					RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A364,iso);
					log.info(String.format("[%s] preauthMatch：预授权完成金额错误! ", iso.getSeq()));
					iso.setState(false);
					return iso;
				}else{
					iso.setState(true);
					iso.setOrgFlow(getValue(logTranFlow));
//					utField90(iso);
					return iso;
				}
			}else{
				iso.setState(true);
				iso.setOrgFlow(getValue(logTranFlow));
//				utField90(iso);
				return iso;
			}
		}
		if(logTranFlowH != null){
			TLogTranFlow tranFlow = new TLogTranFlow();
			BeanUtils.copyProperties(tranFlow,logTranFlowH);
			//计算两个日期相差的天数
			long diff = 0;
			try {
				DateFormat df = new SimpleDateFormat("yyyyMMdd");
				Date d1 = df.parse(iso.getFlow().getTranDate());
				Date d2 = df.parse(logTranFlowH.getTranDate());
				diff = d1.getTime() - d2.getTime();
				diff = diff/1000/24/60/60;
			} catch (ParseException e) {
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A996,iso);
				log.error(String.format("[%s] preauthMatch：计算预授权交易时间间隔异常! ", iso.getSeq()));
				log.error(e.getMessage());
				iso.setState(false);
				return iso;
			}
			//匹配原交易日期是否相等，不等返回25
			if(!StringUtils.equals(iso.getField(FieldConstant.FIELD64_61).substring(12, 16)
					,logTranFlowH.getTranDate().substring(4, 8))){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A525,iso);
				log.info(String.format("[%s] preauthMatch：原交易日期不正确! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//计算系统日期与原交易日期间隔，间隔日期不能大于29天，如果大于就返回25.
			if(diff > 29){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A022,iso);
				log.info(String.format("[%s] preauthMatch：预授权交易时间已大于29天! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//原交易状态tran_flag是否是0或5，不正常返回25
			if(!StringUtils.equals("0",logTranFlowH.getTranFlag())
					&& !StringUtils.equals("5",logTranFlowH.getTranFlag())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A225,iso);
				log.info(String.format("[%s] preauthMatch：原交易状态非正常状态! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//原交易类型tran_type是否是10，不是返回25
			if(!StringUtils.equals("10",logTranFlowH.getTranType())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A725,iso);
				log.info(String.format("[%s] preauthMatch：交易类型不正确! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			if(StringUtils.equals(PosConstant.TRANS_TYPE_0100200611,iso.getFlow().getTranCode())){
				//撤销卡号与原交易匹配，不匹配返回14，撤销卡号与原交易不匹配
				if(!StringUtils.equals(iso.getFlow().getCardNo(),logTranFlowH.getCardNo())){
					RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A014,iso);
					log.info(String.format("[%s] preauthMatch：预授权撤销卡号与原交易不匹配! ", iso.getSeq()));
					iso.setState(false);
					return iso;
				}
				//预授权撤销金额等于预授权金额，不等则返回64
				if(Double.valueOf(iso.getField(FieldConstant.FIELD64_4))
						!= CommondUtils.bigDecimal(logTranFlowH.getTranAmount().toString())){
					RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A264,iso);
					log.info(String.format("[%s] preauthMatchh：预授权撤销金额不等于预授权金额! ", iso.getSeq()));
					iso.setState(false);
					return iso;
				}
				
				iso.setState(true);
				
				iso.setOrgFlow(getValue(tranFlow));
//				utField90(iso);
				return iso;
			}
			if(StringUtils.equals(PosConstant.TRANS_TYPE_0200000620,iso.getFlow().getTranCode()) 
					|| StringUtils.equals(PosConstant.TRANS_TYPE_0220000624,iso.getFlow().getTranCode())){
				//预授权完成金额不大于预授权金额。否则返回64
				if(Double.valueOf(iso.getField(FieldConstant.FIELD64_4))
						> CommondUtils.bigDecimal(logTranFlowH.getAuthAmount().toString())){
					RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A364,iso);
					log.info(String.format("[%s] preauthMatch：预授权完成金额错误! ", iso.getSeq()));
					iso.setState(false);
					return iso;
				}else{
					iso.setState(true);
					iso.setOrgFlow(getValue(tranFlow));
//					utField90(iso);
					return iso;
				}
				
			}else{
				iso.setState(true);
				iso.setOrgFlow(getValue(logTranFlow));
//				utField90(iso);
				return iso;
			}
		}else{
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A025,iso);
			log.info(String.format("[%s] preauthMatch：原预授权交易不存在! ", iso.getSeq()));
			iso.setState(false);
			return iso;
		}
	}
	/**
	 * 退货交易匹配
	 * @param iso
	 * @return
	 * @throws Exception
	 */
	public IsoMessage tradeReturnMatch (IsoMessage iso) throws Exception {
		iso.setState(false);
		if(!StringUtils.equals(PosConstant.TRANS_TYPE_0220200025,iso.getFlow().getTranCode())){
			iso.setState(true);
			return iso;
		}
		//通过交易日期和检索参考号从交易流水表(SD_POSP_TRAN_FLOW)或历史交易流水表(SD_POSP_TRAN_FLOW_h)中匹配原交易
		TLogTranFlow tLogTranFlow = new TLogTranFlow();
		TLogTranFlowH tLogTranFlowH = new TLogTranFlowH();
		tLogTranFlow.setSerialNo(iso.getFlow().getTranDate().substring(2,4)+iso.getField(FieldConstant.FIELD64_61).substring(12, 16)+iso.getField(FieldConstant.FIELD64_37));// 交易日期获取
		tLogTranFlow.setCardNo(iso.getFlow().getCardNo());
		tLogTranFlow.setMerchCode(iso.getField(42));
//		tLogTranFlow.setReferNo(iso.getField(37));// 系统参考号获取
		TLogTranFlow logTranFlow = tLogTranFlowService.getLogTranFlow(tLogTranFlow);
		tLogTranFlowH.setSerialNo(tLogTranFlow.getSerialNo());
		tLogTranFlowH.setCardNo(tLogTranFlow.getCardNo());
		tLogTranFlowH.setMerchCode(tLogTranFlow.getMerchCode());
		TLogTranFlowH logTranFlowH = tLogTranFlowHService.getLogTranFlowH(tLogTranFlowH);
		//询不到交易则返回25，若查询出错，返回码96。
		if(logTranFlow != null){
			//退货时 用field_48来识别是哪一个流水表用，“1”为流水表，否则为历史流水表
			iso.setField48("1");
			
			//计算两个日期相差的天数
			long diff = 0;
			try {
				DateFormat df = new SimpleDateFormat("yyyyMMdd");
				Date d1 = df.parse(iso.getFlow().getTranDate());
				Date d2 = df.parse(logTranFlow.getTranDate());
				diff = d1.getTime() - d2.getTime();
				diff = diff/1000/24/60/60;
			} catch (ParseException e) {
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A996,iso);
				log.error(String.format("[%s] preauthMatch：计算原交易时间间隔异常! ", iso.getSeq()));
				log.error(e.getMessage());
				iso.setState(false);
				return iso;
			}
			//原交易的交易类型tran_type非22，返回错误码25
			if(!StringUtils.equals("22",logTranFlow.getTranType())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A725,iso);
				log.info(String.format("[%s] tradeReturnMatch：原交易类型不正确! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//交易标志tran_flag非0、非9.返回错误码25
			if(!StringUtils.equals("0",logTranFlow.getTranFlag()) 
					&& !StringUtils.equals("9",logTranFlow.getTranFlag())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A225,iso);
				log.info(String.format("[%s] tradeReturnMatch：原交易非正常或者非退货标志! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//退货金额与原始消费金额及剩余金额比较，如果大于返回错误码61
			if(CommondUtils.bigDecimal(logTranFlow.getTranAmount().toString())
						< Double.valueOf(iso.getField(FieldConstant.FIELD64_4))
					|| CommondUtils.bigDecimal(logTranFlow.getRemainAmount().toString())
						< Double.valueOf(iso.getField(FieldConstant.FIELD64_4))){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A361,iso);
				log.info(String.format("[%s] tradeReturnMatch：退货金额大于原始消费金额或剩余金额! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//计算系统日期与原交易日期间隔，大于30天。返回25
			if(diff > 30){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A022,iso);
				log.info(String.format("[%s] tradeReturnMatch：原消费交易日期间隔已超过30天! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//联机退货交易须判断原始交易流水的服务点条件码是否为64（分期付款），若是则不允许退货，返回码12
			if(StringUtils.equals("64",logTranFlow.getConditionMode())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A112,iso);
				log.info(String.format("[%s] tradeReturnMatch：分期付款，不允许退货! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}else{
				iso.setState(true);
				iso.setOrgFlow(getValue(logTranFlow));
//				utField90(iso);
				return iso;
			}
		}
		if(logTranFlowH != null){
			iso.setField48("0");
			//计算两个日期相差的天数
			long diff = 0;
			try {
				DateFormat df = new SimpleDateFormat("yyyyMMdd");
				Date d1 = df.parse(iso.getFlow().getTranDate());
				Date d2 = df.parse(logTranFlowH.getTranDate());
				diff = d1.getTime() - d2.getTime();
				diff = diff/1000/24/60/60;
			} catch (ParseException e) {
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A996,iso);
				log.error(String.format("[%s] preauthMatch：计算原交易时间间隔异常! ", iso.getSeq()));
				log.error(e.getMessage());
				iso.setState(false);
				return iso;
			}
			//原交易的交易类型tran_type非22，返回错误码25
			if(!StringUtils.equals("22",logTranFlowH.getTranType())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A725,iso);
				log.info(String.format("[%s] tradeReturnMatch：原交易类型不正确! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//交易标志tran_flag非0、非9.返回错误码25
			if(!StringUtils.equals("0",logTranFlowH.getTranFlag()) 
					&& !StringUtils.equals("9",logTranFlowH.getTranFlag())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A225,iso);
				log.info(String.format("[%s] tradeReturnMatch：原交易非正常或者非退货标志! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//退货金额与原始消费金额及剩余金额比较，如果大于返回错误码61
			if(CommondUtils.bigDecimal(logTranFlowH.getTranAmount().toString())
						< Double.valueOf(iso.getField(FieldConstant.FIELD64_4))
					|| CommondUtils.bigDecimal(logTranFlowH.getRemainAmount().toString())
						< Double.valueOf(iso.getField(FieldConstant.FIELD64_4))){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A361,iso);
				log.info(String.format("[%s] tradeReturnMatch：退货金额大于原始消费金额或剩余金额! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//计算系统日期与原交易日期间隔，大于30天。返回25
			if(diff > 30){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A022,iso);
				log.info(String.format("[%s] tradeReturnMatch：原消费交易日期间隔已超过30天! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//联机退货交易须判断原始交易流水的服务点条件码是否为64（分期付款），若是则不允许退货，返回码12
			if(StringUtils.equals("64",logTranFlowH.getConditionMode())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A112,iso);
				log.info(String.format("[%s] tradeReturnMatch：分期付款，不允许退货! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}else{
				iso.setState(true);
				TLogTranFlow tranFlow = new TLogTranFlow();
				BeanUtils.copyProperties(tranFlow,logTranFlowH);
				iso.setOrgFlow(getValue(tranFlow));
//				utField90(iso);
				return iso;
			}	
		}else{
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A025,iso);
			log.info(String.format("[%s] tradeReturnMatch：原消费交易不存在! ", iso.getSeq()));
			iso.setState(false);
			return iso;
		}
	}
	/**
	 * 撤销（各种撤销）匹配
	 * @param iso
	 * @return
	 * @throws Exception
	 */
	public IsoMessage revokeMatch (IsoMessage iso) throws Exception{
		iso.setState(false);
		if(!StringUtils.equals(PosConstant.TRANS_TYPE_0200200023,iso.getFlow().getTranCode())
				&& !StringUtils.equals(PosConstant.TRANS_TYPE_0200200621,iso.getFlow().getTranCode())
				&& !StringUtils.equals(PosConstant.TRANS_TYPE_0200179151,iso.getFlow().getTranCode())){
			iso.setState(true);
			return iso;
		}
		//通过检索参考号查询交易
		TLogTranFlow tLogTranFlow = new TLogTranFlow();
		tLogTranFlow.setMerchCode(iso.getField(FieldConstant.FIELD64_42));// 商户代码获取
		tLogTranFlow.setTermCode(iso.getField(FieldConstant.FIELD64_41));// 终端代码获取
		tLogTranFlow.setTranCode(Utils.getOrgTranCode(iso));//原交易码
		tLogTranFlow.setBatchNo(iso.getField(FieldConstant.FIELD64_61).substring(0, 6));// 原批次号获取
		tLogTranFlow.setTermSerialNo(iso.getField(FieldConstant.FIELD64_61).substring(6, 12));// 原终端流水号获取
		TLogTranFlow logTranFlow = tLogTranFlowService.getLogTranFlow(tLogTranFlow);
		//如果不存在，匹配是否为相关撤销类型。不是返回25
		if(logTranFlow == null){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A025,iso);
			log.info(String.format("[%s] revokeMatch：原交易不存在! ", iso.getSeq()));
			iso.setState(false);
			return iso;
		}else{
			//交易状态tran_flag是否为0，不是则返回25，原交易终端异常
			if(!StringUtils.equals("0",logTranFlow.getTranFlag())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A225,iso);
				log.info(String.format("[%s] revokeMatch：原交易非正常状态! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//撤销卡号与原交易匹配，不匹配返回14，撤销卡号与原交易不匹配
			if(!StringUtils.equals(iso.getFlow().getCardNo(),logTranFlow.getCardNo())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A014,iso);
				log.info(String.format("[%s] revokeMatch：撤销卡号与原交易不匹配! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//撤销交易日期只能当天撤销，与原交易日期匹配如果不相等则返回25,预授权撤销可以隔天撤销
			if(!StringUtils.equals(iso.getFlow().getTranDate(),logTranFlow.getTranDate())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A825,iso);
				log.info(String.format("[%s] revokeMatch：不能在隔天撤销! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}
			//撤销金额与原交易匹配，如果不等，就返回64
			if(Double.valueOf(iso.getField(FieldConstant.FIELD64_4))
					!= CommondUtils.bigDecimal(logTranFlow.getTranAmount().toString())){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A264,iso);
				log.info(String.format("[%s] revokeMatch：原始金额错误! ", iso.getSeq()));
				iso.setState(false);
				return iso;
			}else{
				iso.setState(true);
				iso.setOrgFlow(getValue(logTranFlow));
//				utField90(iso);
				return iso;
			}
		}
	}
	/**
	 * 获取数据库表里的值
	 * @param logTranFlow
	 * @return
	 * @throws Exception
	 */
	public FlowMessage getValue(TLogTranFlow logTranFlow) throws Exception{
		
		FlowMessage orgFlow = new FlowMessage();
		orgFlow.setSerialNo(logTranFlow.getSerialNo());
		orgFlow.setTranCode(logTranFlow.getTranCode());
		orgFlow.setTranDate(logTranFlow.getTranDate());
		orgFlow.setTranTime(logTranFlow.getTranTime());
		orgFlow.setTranType(logTranFlow.getTranType());
		orgFlow.setTranFlag(logTranFlow.getTranFlag());
		orgFlow.setMerchCode(logTranFlow.getMerchCode());
		orgFlow.setTermCode(logTranFlow.getTermCode());
		orgFlow.setBatchNo(logTranFlow.getBatchNo());
		orgFlow.setTermSerialNo(logTranFlow.getTermSerialNo());
		orgFlow.setOrigSerialTime(logTranFlow.getTranTime());
		orgFlow.setCupsSerialNo(logTranFlow.getCupsSerialNo());
		orgFlow.setCupsDate(logTranFlow.getCupsDate());
		orgFlow.setTranAmount(logTranFlow.getTranAmount());
		orgFlow.setAuthNo(logTranFlow.getAuthNo());
		orgFlow.setOrigSerialNo(logTranFlow.getOrigSerialNo());
		//解密卡号
		orgFlow.setCardNo(obtainObjectInformation.decryptionAccountNumber(logTranFlow.getCardNo()));
		orgFlow.setConditionMode(logTranFlow.getConditionMode());
		orgFlow.setReferNo(logTranFlow.getReferNo());
		orgFlow.setRemainAmount(logTranFlow.getRemainAmount());
		orgFlow.setRcvBankCode(logTranFlow.getRcvBankCode());
		orgFlow.setRcvBranchCode(logTranFlow.getRcvBranchCode());
		orgFlow.setExtsysRespCode(logTranFlow.getExtsysRespCode());
		orgFlow.setInputMode(logTranFlow.getInputMode());
		orgFlow.setCupsSerialNo(logTranFlow.getCupsSerialNo());
		return orgFlow;
	}
}

