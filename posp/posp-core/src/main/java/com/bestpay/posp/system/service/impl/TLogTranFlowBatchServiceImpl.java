package com.bestpay.posp.system.service.impl;

import java.util.Date;

import com.bestpay.posp.constant.FieldConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.system.dao.*;
import com.bestpay.posp.system.entity.*;
import com.bestpay.posp.utils.DateUtils;
import com.bestpay.posp.system.service.SysSerialNoService;
import com.bestpay.posp.system.service.TLogTranFlowBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.constant.SysConstant;

@Component("TLogTranFlowBatchService")
public class TLogTranFlowBatchServiceImpl implements TLogTranFlowBatchService {
	@Autowired
	private TLogTranFlowBatchDao tLogTranFlowBatchDao;
	@Autowired
	private TStlBankCardBinDao tStlBankCardBinDao;
	@Autowired
	private TMcmMerchantDao tMcmMerchantDao;
	@Autowired
	private TSysParaDao tSysParaDao;
	@Autowired
	private TSymAreaDao tSymAreaDao;
	@Autowired
	private SysSerialNoService sysSerialNoService;
	
	@Override
	public boolean insertLogTranFlowBatch(TLogTranFlowBatch tLogTranFlowBatch) {
		int i = tLogTranFlowBatchDao.insert(tLogTranFlowBatch);
		if(i == 1){
			return true;
		}
		return false;
	}

	@Override
	public boolean isLogTranFlowBatch(TLogTranFlowBatch tLogTranFlowBatch) {
		if(tLogTranFlowBatchDao.findUnique(tLogTranFlowBatch)!=null){
			return true;
		}
		return false;
	}

	@Override
	public boolean updateLogTranFlowBatch(TLogTranFlowBatch tLogTranFlowBatch) {
		int i = tLogTranFlowBatchDao.update(tLogTranFlowBatch);
		if(i == 1){
			return true;
		}
		return false;
	}
	
	@Override
	public void saveTranFlow(IsoMessage iso){
		String serialNo = sysSerialNoService.querySerialNo();
		iso.setSeq(serialNo);
		Date tranDate = new Date();
		
		TLogTranFlowBatch entity = new TLogTranFlowBatch();
		entity.setSerialNo(serialNo);
		entity.setTranDate(DateUtils.paraDate(tranDate,DateUtils.YYYYMMDD));
		entity.setTranTime(DateUtils.paraDate(tranDate,DateUtils.HHmmss));
		
		entity.setTranCode(iso.getTranCode());
//		entity.setTranCode(TranCode(iso.getField(FieldConstant.FIELD64_60)));
		
		if(iso.getField(FieldConstant.FIELD64_2) != null)
			entity.setCardNo(iso.getField(FieldConstant.FIELD64_2));
		
		if(iso.getField(FieldConstant.FIELD64_4) != null)
			entity.setTranAmount(Double.valueOf(iso.getField(FieldConstant.FIELD64_4))/100);
		
		/**
		 * 60.1域
		 */
		if(iso.getField(FieldConstant.FIELD64_60) != null)
			entity.setTranType(iso.getField(FieldConstant.FIELD64_60).substring(0,2));
		
		if(iso.getField(FieldConstant.FIELD64_11) != null)
			entity.setTermSerialNo(iso.getField(FieldConstant.FIELD64_11));
		else 
			entity.setTermSerialNo(iso.getField(FieldConstant.FIELD64_37));
		
		entity.setReferNo(serialNo.substring(6, 18));
		entity.setTermCode(iso.getField(FieldConstant.FIELD64_41));
		entity.setMerchCode(iso.getField(FieldConstant.FIELD64_42));
		entity.setCcyCode(iso.getField(FieldConstant.FIELD64_49));
		entity.setIp(SysConstant.SYS_IP);
		
		//默认值
		entity.setBatchState("0");
		entity.setTranFlag("0");
		entity.setTranState("0");
		entity.setBatchResult("0");
		
		/**
		 * 获取卡BIN 信息
		 */
		if(entity.getCardNo() != null && !entity.getCardNo().equals("")){
			TStlBankCardBin cardBin = tStlBankCardBinDao.findByCardNo(entity.getCardNo());
			if(cardBin != null){
				entity.setCardBin(cardBin.getAccountPrefix());
				entity.setCardType(cardBin.getAccountType().toString());
			}
		}
		
		/**
		 * 获取受理机构表信息
		 */
		if(entity.getMerchCode() != null && !entity.getMerchCode().equals("")){
			TMcmMerchant merchant = tMcmMerchantDao.findByMctCode(entity.getMerchCode());
			TSysPara para20 = tSysParaDao.findByParaKey("000020");
			TSysPara para21 = tSysParaDao.findByParaKey("000021");
			TSymArea branch =tSymAreaDao.findByAreaCode(merchant.getAreaCode());
			entity.setRcvBankCode(para20.getParaValues()+branch.getMacroValue());
			entity.setRcvBranchCode(para21.getParaValues());
			
		}
		tLogTranFlowBatchDao.insert(entity);
	}
	
	@Override
	public void updateTranFlow(IsoMessage iso){
		
	}
	/**
	 * 获取最大序列数
	 */
	@Override
	public String getMaxSeqNum(TLogTranFlowBatch tLogTranFlowBatch) {
		return tLogTranFlowBatchDao.findMaxSeqNum(tLogTranFlowBatch);
	}
	
	
}
