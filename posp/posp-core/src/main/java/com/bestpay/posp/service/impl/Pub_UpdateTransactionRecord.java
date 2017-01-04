package com.bestpay.posp.service.impl;

import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.system.entity.TLogTranFlowH;
import com.bestpay.posp.system.service.TLogTranFlowService;
import com.bestpay.posp.system.entity.TLogTranFlow;
import com.bestpay.posp.system.service.TLogTranFlowHService;
import com.bestpay.posp.system.service.TMcmPosinfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 更新流水
 * @author HR
 *
 */
@Slf4j
@Component("pub_UpdateTransactionRecord")
public class Pub_UpdateTransactionRecord {
	@Autowired
	private TLogTranFlowService tLogTranFlowService;
	@Autowired
	private TLogTranFlowHService tLogTranFlowHService;
	@Autowired
	public TMcmPosinfoService tMcmPosinfoService;

	//有原始交易并且银联成功返回后
	//更新当前流水以及原流水
	public IsoMessage updatePosSign(IsoMessage iso){
		updateFlowSign(iso);
		if(iso.isState() && iso.getRspCode().equals("00")){
			updateOrgFlowSign(iso);
		}
		return iso;
	}
	/**
	 * 更新当前流水
	 * @param iso
	 * @return
	 */
	//更新当前流水
	@Transactional(propagation=Propagation.REQUIRED)
	public IsoMessage updateFlowSign(IsoMessage iso) {
		try {
			iso.setState(false);
			Map<String, String> transformerTmpField = iso.getTransformerTmpField();
			String infoLogMessage = "交易";
			if(transformerTmpField != null
					&& StringUtils.isNotEmpty(transformerTmpField.get("infoLogMessage"))){
				infoLogMessage = transformerTmpField.get("infoLogMessage");
			}
			
			TLogTranFlow tLogTranFlow = new TLogTranFlow();
			String seq = iso.getSeq();
			tLogTranFlow.setSerialNo(iso.getFlow().getSerialNo());
			tLogTranFlow.setChannelCode(Long.valueOf(iso.getChannelCode()));
			tLogTranFlow.setCupsSerialNo(iso.getFlow().getCupsSerialNo());
			tLogTranFlow.setAuthNo(iso.getField(38));//授权码
			tLogTranFlow.setReceDate(new java.util.Date());//接收时间
			tLogTranFlow.setCardProducts(iso.getCardProducts());//卡产品
			tLogTranFlow.setDetailRespCode(iso.getFlow().getDetailRespCode());
			tLogTranFlow.setFreePasswordSign(iso.getFreePasswordSign());
			if(iso.getOrgFlow() != null){
				tLogTranFlow.setOrigSerialNo(iso.getOrgFlow().getReferNo());//原交易流水
				tLogTranFlow.setOrigSerialDate(iso.getOrgFlow().getTranDate());//原交易日期
				tLogTranFlow.setOrigSerialTime(iso.getOrgFlow().getTranTime());//原交易时间
				tLogTranFlow.setOrigTranType(iso.getOrgFlow().getTranType());//原交易类型
			}
			if(iso.getFlow().getTranCode().equals("0100030610")){
				tLogTranFlow.setAuthDate(iso.getFlow().getYYYYMMDD());
//				tLogTranFlow.setAuthNo(iso.getField(38));
				tLogTranFlow.setAuthAmount(Double.valueOf(iso.getField(4))/100);
			}
			boolean selSign = false;
			//返回成功
			if(StringUtils.equals("00", iso.getField(39))){
				//接收银联成功返回
				//为了区分冲正与原交易的终端流水号，所以改变冲正终端流水号第一位为9
				if(iso.getField(0).equals("0400")){
					tLogTranFlow.setTermSerialNo("9"+iso.getField(11).substring(1));
				}
				tLogTranFlow.setTranState("2");//交易状态
				tLogTranFlow.setRespCode(iso.getField(39));//交易响应吗
				tLogTranFlow.setExtsysRespCode(iso.getField(39));//后端系统返回码
				tLogTranFlow.setExtsysRespDesc("交易成功");//后端系统返回说明
				infoLogMessage +="成功";
			}else{
				//接收银联失败返回
				if(StringUtils.equals(iso.getField(0), "0400") || StringUtils.equals(iso.getField(0), "0410")){
					tLogTranFlow.setTermSerialNo("9"+iso.getField(11).substring(1));
				}
				tLogTranFlow.setTranState("1");//交易状态
				tLogTranFlow.setRespCode(iso.getPospCode());//交易响应吗
				if(!StringUtils.equals(iso.getChannelCode(), SysConstant.CAPITAL_POOL_5001)){
					tLogTranFlow.setExtsysRespCode(iso.getRspCode());//后端系统返回码
					tLogTranFlow.setExtsysRespDesc(iso.getRspMsg());
				}
				infoLogMessage +="失败";
			}
			try {
				tLogTranFlow.setReserve(infoLogMessage);
				selSign = tLogTranFlowService.updateLogTranFlow(tLogTranFlow);
				log.info(String.format("[%s] 更新"+infoLogMessage+"流水操作成功! ", seq));
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			if(!selSign){
				iso.setState(false);
				iso.setChannelCode(SysConstant.CAPITAL_POOL_5001);
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_B296,iso);
				log.info(String.format("[%s] error：更新"+infoLogMessage+"当前流水操作失败! ", seq));
			}else{
				iso.setState(true);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
//			e.printStackTrace();
		}
		return iso;
	}
	/**
	 * 银联无返回时更新流水
	 * @param iso
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public IsoMessage updatNullSign(IsoMessage iso) {
		iso.setState(false);
		Map<String, String> transformerTmpField = iso.getTransformerTmpField();
		String infoLogMessage = "交易";
		if(transformerTmpField != null
				&& StringUtils.isNotEmpty(transformerTmpField.get("infoLogMessage"))){
			infoLogMessage = transformerTmpField.get("infoLogMessage");
		}
		TLogTranFlow tLogTranFlow = new TLogTranFlow();
		String seq = iso.getSeq();
		tLogTranFlow.setSerialNo(iso.getFlow().getSerialNo());
		tLogTranFlow.setChannelCode(Long.valueOf(iso.getChannelCode()));
		boolean selSign = false;
		try {
			//接收银联失败返回
			if(iso.getField(0).equals("0400")){
				tLogTranFlow.setTermSerialNo("9"+iso.getField(11).substring(1));
			}
			tLogTranFlow.setTranState("1");//交易状态
			tLogTranFlow.setReceDate(new java.util.Date());//接收时间
			tLogTranFlow.setRespCode(iso.getPospCode());//交易响应吗
			tLogTranFlow.setFreePasswordSign(iso.getFreePasswordSign());
			if(iso.getOrgFlow() != null){
				tLogTranFlow.setOrigSerialNo(iso.getOrgFlow().getReferNo());//原交易流水
				tLogTranFlow.setOrigSerialDate(iso.getOrgFlow().getTranDate());
				tLogTranFlow.setOrigSerialTime(iso.getOrgFlow().getTranTime());
				tLogTranFlow.setOrigTranType(iso.getOrgFlow().getTranType());
			}
			tLogTranFlow.setReserve(infoLogMessage+"失败");
			selSign = tLogTranFlowService.updateLogTranFlow(tLogTranFlow);
			log.info(String.format("[%s] 更新"+infoLogMessage+"失败流水操作成功! ", seq));
			if(!selSign){
				iso.setState(false);
				iso.setChannelCode(SysConstant.CAPITAL_POOL_5001);
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_B396,iso);
				log.info(String.format("[%s] error：更新"+infoLogMessage+"流水操作失败! ", seq));
			}else{
				iso.setState(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return iso;
	}
	/**
	 * 更新原流水表
	 * @param iso
	 * @return
	 */
	//更新原流水表
	@Transactional(propagation=Propagation.REQUIRED)
	public IsoMessage updateOrgFlowSign(IsoMessage iso) {
		String seq = iso.getSeq();
		String orgSerialNo = null;
		Map<String, String> transformerTmpField = iso.getTransformerTmpField();
		String infoLogMessage = "交易";
		if(transformerTmpField != null
				&& StringUtils.isNotEmpty(transformerTmpField.get("infoLogMessage"))){
			infoLogMessage = transformerTmpField.get("infoLogMessage");
		}
		String infoMessage = infoLogMessage.substring(0, infoLogMessage.length()-2)+"已"+infoLogMessage.substring(infoLogMessage.length()-2);
		/**
		 * 更新原流水
		 */
		TLogTranFlow tLogTranFlow = new TLogTranFlow();
		tLogTranFlow.setSerialNo(iso.getOrgFlow().getSerialNo());
		TLogTranFlowH tLogTranFlowH = new TLogTranFlowH();
		tLogTranFlowH.setSerialNo(iso.getOrgFlow().getSerialNo());
		boolean selSign = false;
		tLogTranFlow.setReserve(infoMessage);
		tLogTranFlowH.setReserve(infoMessage);
		//联机退货
		if(iso.getTranCode().equals(PosConstant.TRANS_TYPE_0220200025)){
			Double s = Double.valueOf(new java.text.DecimalFormat("0.00").format(iso.getOrgFlow().getRemainAmount()-Double.valueOf(iso.getField(4))/100));
			tLogTranFlow.setTranFlag("9");//交易标志
			tLogTranFlow.setRemainAmount(s);
			tLogTranFlow.setReserve("已退货");
			tLogTranFlowH.setTranFlag("9");//交易标志
			tLogTranFlowH.setRemainAmount(s);
			tLogTranFlowH.setReserve("已退货");
		}else if(iso.getField(0).substring(0, 2).equals("04")){
			tLogTranFlow.setTranFlag("2");//冲正
			tLogTranFlowH.setTranFlag("2");//冲正
		}else if(iso.getTranCode().equals(PosConstant.TRANS_TYPE_0200000620) 
				|| iso.getTranCode().equals(PosConstant.TRANS_TYPE_0220000624)){
			tLogTranFlow.setTranFlag("4");//预授权完成
			tLogTranFlowH.setTranFlag("4");//预授权完成
		}else {
			tLogTranFlow.setTranFlag("1");//撤销
			tLogTranFlowH.setTranFlag("1");//撤销
		}
		try {
			selSign = tLogTranFlowService.updateLogTranFlow(tLogTranFlow);
			if(!selSign){
				selSign = tLogTranFlowHService.updateLogTranFlowH(tLogTranFlowH);
				TLogTranFlowH tranFlowH = tLogTranFlowHService.getLogTranFlowH(tLogTranFlowH);
				orgSerialNo = tranFlowH.getOrigSerialDate().substring(2, 8)+tranFlowH.getOrigSerialNo();
			}else{
				TLogTranFlow tranFlow = tLogTranFlowService.getLogTranFlow(tLogTranFlow);
				orgSerialNo = tranFlow.getOrigSerialDate().substring(2, 8)+tranFlow.getOrigSerialNo();
			}
		} catch (Exception e) {
			log.error(String.format("[%s]" + e.getMessage(), seq));
		}
		/**
		 * 更新原始交易流水
		 */
		TLogTranFlow logTranFlow = new TLogTranFlow();
		logTranFlow.setSerialNo(orgSerialNo);
		TLogTranFlowH logTranFlowH = new TLogTranFlowH();
		logTranFlowH.setSerialNo(orgSerialNo);
		//预授权完成撤销
		if(iso.getTranCode().equals(PosConstant.TRANS_TYPE_0200200621)){
			logTranFlow.setTranFlag("0");
			logTranFlow.setReserve("预授权成功");
			logTranFlowH.setTranFlag("0");
			logTranFlowH.setReserve("预授权成功");
			try {
				selSign = tLogTranFlowService.updateLogTranFlow(logTranFlow);
				if(!selSign){
					selSign = tLogTranFlowHService.updateLogTranFlowH(logTranFlowH);
				}
			} catch (Exception e) {
				log.error(String.format("[%s]" + e.getMessage(), seq));
			}
		}
		//消费撤销冲正，预授权完成撤销冲正，现金充值撤销冲正
		if(StringUtils.equals(iso.getTranCode(), PosConstant.TRANS_TYPE_0400200023)
				|| StringUtils.equals(iso.getTranCode(), PosConstant.TRANS_TYPE_0400200621)
				|| StringUtils.equals(iso.getTranCode(), PosConstant.TRANS_TYPE_0400179151)
				|| StringUtils.equals(iso.getTranCode(), PosConstant.TRANS_TYPE_0400200611)
				|| StringUtils.equals(iso.getTranCode(), PosConstant.TRANS_TYPE_0400000620)){
			logTranFlow.setTranFlag("0");
			logTranFlow.setReserve(infoLogMessage.substring(0, infoLogMessage.length()-4)+"成功");
			logTranFlowH.setTranFlag("0");
			logTranFlowH.setReserve(infoLogMessage.substring(0, infoLogMessage.length()-4)+"成功");
			try {
				selSign = tLogTranFlowService.updateLogTranFlow(logTranFlow);
				if(!selSign){
					selSign = tLogTranFlowHService.updateLogTranFlowH(logTranFlowH);
				}
			} catch (Exception e) {
				log.error(String.format("[%s]" + e.getMessage(), seq));
			}
		}
		if(!selSign){
			iso.setState(false);
			iso.setChannelCode(SysConstant.CAPITAL_POOL_5001);
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_B496,iso);
			log.info(String.format("[%s] error：更新"+infoLogMessage+"流水操作失败! ", seq));
		}else{
			iso.setState(true);
		}
		return iso;
	}
	/**
	 * 更新管理类交易流水以及公共检查失败更新流水
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public IsoMessage updatePublicFailure(IsoMessage iso){
		if(!StringUtils.equals(iso.getTranCode(), "0320203")
				&& !StringUtils.equals(iso.getTranCode(), "0320205")
				&& !PosConstant.TRANS_TYPE_SLIP.contains(iso.getTranCode())){
			TLogTranFlow tLogTranFlow = new TLogTranFlow();
			boolean selSign = false;
			String seq = iso.getSeq();
			tLogTranFlow.setChannelCode(Long.valueOf(iso.getChannelCode()));
			tLogTranFlow.setSerialNo(iso.getFlow().getSerialNo());
			tLogTranFlow.setFreePasswordSign(iso.getFreePasswordSign());
			if(StringUtils.isNotEmpty(iso.getPospCode())){
				tLogTranFlow.setRespCode(iso.getPospCode());//交易响应吗
			}else{
				tLogTranFlow.setRespCode(iso.getField(39));//交易响应吗
			}
			try {
				selSign = tLogTranFlowService.updateLogTranFlow(tLogTranFlow);
				if(log.isDebugEnabled()){
					log.debug(String.format("[%s] 交易成功! ", seq));
					log.debug(String.format("[%s] 更新流水操作成功! ", seq));
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			if(!selSign){
				iso.setChannelCode(SysConstant.CAPITAL_POOL_5001);
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_B596,iso);
				log.info(String.format("[%s] error：更新流水操作失败! ",seq));
				return iso;
			}
		}
		return iso;
	}
}