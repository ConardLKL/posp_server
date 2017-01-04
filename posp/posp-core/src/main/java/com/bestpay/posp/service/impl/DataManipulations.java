package com.bestpay.posp.service.impl;

import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.system.entity.TCfgRiskMoveInfo;
import com.bestpay.posp.system.entity.TLogDubiousTranFlow;
import com.bestpay.posp.system.entity.TLogTranFlow;
import com.bestpay.posp.system.service.TCfgRiskMoveInfoService;
import com.bestpay.posp.system.service.TLogDubiousTranFlowService;
import com.bestpay.posp.system.service.TLogTranFlowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component("DataManipulations")
public class DataManipulations {

	@Autowired
	private TLogTranFlowService tLogTranFlowService;
	@Autowired
	private TLogDubiousTranFlowService tLogDubiousTranFlowService;
	@Autowired
	private TCfgRiskMoveInfoService tCfgRiskMoveInfoService;
	@Autowired
	private InitialData initialData;
	/**
	 * 初始化流水数据
	 * @param iso
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void initialFlow(IsoMessage iso) throws Exception{
		boolean selSign = false;
		TLogTranFlow tLogTranFlow = initialData.dataInitial(iso);
		//IC卡联机交易明细和电子化凭条交易不记录流水
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0320203,iso.getTranCode())
				|| StringUtils.equals(PosConstant.TRANS_TYPE_0320205,iso.getTranCode())
				|| PosConstant.TRANS_TYPE_SLIP.contains(iso.getTranCode())){
			iso.setState(true);
			return ;
		}
		selSign = tLogTranFlowService.insertLogTranFlow(tLogTranFlow);
		if(!selSign){
			iso.setState(false);
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_B196, iso);
		}else{
			iso.setState(true);
		}
	}
	/**
	 * 交易失败计入可疑表
	 * @param iso
     */
	@Transactional(propagation=Propagation.REQUIRED)
	public void insertDubious(IsoMessage iso){
		try{
			//IC卡联机交易明细和电子化凭条交易不记录流水
			if(!StringUtils.equals(PosConstant.TRANS_TYPE_0320203,iso.getTranCode()) 
					&& !StringUtils.equals(PosConstant.TRANS_TYPE_0320205,iso.getTranCode())
					&& !PosConstant.TRANS_TYPE_SLIP.contains(iso.getTranCode())){
				Map<String, String> transformerTmpField = iso.getTransformerTmpField();
				String infoLogMessage = transformerTmpField.get("infoLogMessage");
		
				TLogDubiousTranFlow tLogDubiousTranFlow = new TLogDubiousTranFlow();
				tLogDubiousTranFlow.setSerialNo(iso.getFlow().getSerialNo());// 流水号
				tLogDubiousTranFlow.setTranDate(iso.getFlow().getTranDate());// 交易日期
				tLogDubiousTranFlow.setTranTime(iso.getFlow().getTranTime());// 交易时间
				tLogDubiousTranFlow.setTermSerialNo(iso.getFlow().getTermSerialNo());//终端流水号
				tLogDubiousTranFlow.setReferNo(iso.getFlow().getReferNo());//系统参考号
				tLogDubiousTranFlow.setTranType(iso.getFlow().getTranType());// 交易类型
				tLogDubiousTranFlow.setMerchCode(iso.getFlow().getMerchCode());// 商户编号
				tLogDubiousTranFlow.setTermCode(iso.getFlow().getTermCode());// 终端编号
				tLogDubiousTranFlow.setCardNo(iso.getFlow().getCardNo());// 卡号
				tLogDubiousTranFlow.setCardBin(iso.getFlow().getCardBin());//卡bin
				tLogDubiousTranFlow.setChannelCode(Long.valueOf(iso.getChannelCode()));//渠道代码
				tLogDubiousTranFlow.setRespCode(iso.getPospCode());// 交易响应吗
				tLogDubiousTranFlow.setTranCode(iso.getTranCode());//交易码
				if(!StringUtils.equals(iso.getChannelCode(), SysConstant.CAPITAL_POOL_5001)){
					tLogDubiousTranFlow.setExtsysRespCode(iso.getRspCode());//后端系统返回码
					tLogDubiousTranFlow.setExtsysRespDesc(iso.getRspMsg());//返回错误信息
				}
				tLogDubiousTranFlow.setTranAmount(iso.getFlow().getTranAmount());//金额
				tLogDubiousTranFlow.setTranType(iso.getFlow().getTranType());//交易类型
				tLogDubiousTranFlow.setBatchNo(iso.getFlow().getBatchNo());//批次号
				tLogDubiousTranFlow.setReserve(infoLogMessage);
				tLogDubiousTranFlow.setRcvBankCode(iso.getFlow().getRcvBankCode());//银联区域码
				tLogDubiousTranFlow.setBaseStationType(iso.getFlow().getBaseStationType());//基站类型
				tLogDubiousTranFlow.setBaseStationValues(iso.getFlow().getBaseStationValues());//基站信息值
				tLogDubiousTranFlow.setCardProperties(iso.getFlow().getCardProperties());//卡属性
				tLogDubiousTranFlow.setXRealIp(iso.getXRealIp());//终端IP
				tLogDubiousTranFlow.setCardProducts(iso.getCardProducts());//卡产品
				tLogDubiousTranFlow.setDetailRespCode(iso.getFlow().getDetailRespCode());
				tLogDubiousTranFlow.setFreePasswordSign(iso.getFreePasswordSign());
				tLogDubiousTranFlowService.insertLogDubiousTranFlow(tLogDubiousTranFlow);
			}
		}catch(Exception e){
			log.error(e.getMessage());
			log.error(String.format("[%s] 插入可疑流水表失败!", iso.getSeq()));
		}
	}

	/**
	 * 生成告警信息
	 * @param iso
	 * @param stationVal
     */
	@Transactional(propagation=Propagation.REQUIRED)
	public void insertMoveInfo(IsoMessage iso,String stationVal){
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateString = formatter.format(new Date());
			Date date = formatter.parse(dateString);
			TCfgRiskMoveInfo tCfgRiskMoveInfo = new TCfgRiskMoveInfo();
			tCfgRiskMoveInfo.setMctCode(iso.getFlow().getMerchCode());// 商户编号
			tCfgRiskMoveInfo.setPosCode(iso.getFlow().getTermCode());// 终端编号
			tCfgRiskMoveInfo.setHandStat("WAIT");//处理状态为 WAIT 待处理
			tCfgRiskMoveInfo.setMoveDate(date);//发生时间
			tCfgRiskMoveInfo.setMctName(iso.getFlow().getMctName());//商户名称
			tCfgRiskMoveInfo.setTimes(Long.valueOf(1));//次数
			tCfgRiskMoveInfo.setStationVal(stationVal);//基站信息
			tCfgRiskMoveInfoService.insertRiskMoveInfo(tCfgRiskMoveInfo);
		}catch(Exception e){
			log.error(e.getMessage());
			log.error(String.format("[%s] 生成告警信息失败!"+e.getMessage(), iso.getSeq()));
		}
	}
}
