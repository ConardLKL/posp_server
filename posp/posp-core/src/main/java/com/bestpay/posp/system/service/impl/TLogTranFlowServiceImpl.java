package com.bestpay.posp.system.service.impl;

import java.util.List;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.system.entity.TLogTranFlow;
import com.bestpay.posp.system.service.SysSerialNoService;
import com.bestpay.posp.system.service.TLogTranFlowService;
import com.bestpay.posp.system.service.TSymAppkeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.service.impl.Pub_UpdateTransactionRecord;
import com.bestpay.posp.system.dao.TLogTranFlowDao;
import com.bestpay.posp.system.dao.TMcmMerchantDao;
import com.bestpay.posp.system.dao.TStlBankCardBinDao;
import com.bestpay.posp.system.dao.TSymAreaDao;
import com.bestpay.posp.system.dao.TSysParaDao;
import com.bestpay.posp.system.service.TStlVoucherService;

@Component("TLogTranFlowService")
public class TLogTranFlowServiceImpl implements TLogTranFlowService {
	
	@Autowired 
	private TLogTranFlowDao tLogTranFlowDao;
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
	@Autowired
	private TStlVoucherService voucherService;
	@Autowired
	private TSymAppkeyService tSymAppkeyService;
	@Autowired
	private Pub_UpdateTransactionRecord pub_UpdateTransactionRecord;
	
	@Override
	public boolean insertLogTranFlow(TLogTranFlow tLogTranFlow) {
		int i = tLogTranFlowDao.insert(tLogTranFlow);
		if(i == 1){
			return true;
		}
		return false;
	}

	@Override
	public boolean isLogTranFlow(TLogTranFlow tLogTranFlow) {
		if(tLogTranFlowDao.findUnique(tLogTranFlow)!=null){
			return true;
		}
		return false;
	}

	@Override
	public TLogTranFlow getLogTranFlow(TLogTranFlow tLogTranFlow) {
		return tLogTranFlowDao.findUnique(tLogTranFlow);
	}
	
	@Override
	public List<TLogTranFlow> getLogTranFlows(TLogTranFlow tLogTranFlow) {
			return tLogTranFlowDao.find(tLogTranFlow);
	}
	@Override
	public boolean updateLogTranFlow(TLogTranFlow tLogTranFlow) {
		int i = tLogTranFlowDao.update(tLogTranFlow);
		if(i == 1){
			return true;
		}
		return false;
	}

	@Override
	public TLogTranFlow sumTranAmount(TLogTranFlow tLogTranFlow) {
		TLogTranFlow logTranFlow = tLogTranFlowDao.sumTranAmount(tLogTranFlow);
		if(logTranFlow == null){
			logTranFlow=new TLogTranFlow();
			logTranFlow.setTranAmount(0.00);	
		}
		return logTranFlow;
	}
	
	
	@Override
	public void saveTranFlow(IsoMessage iso) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void updateTranFlow(IsoMessage iso) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isExistTranFlow(String serialNo) {
		return false;
	}
	
	@Override
	public int updateBantchLogTranFlow(TLogTranFlow tLogTranFlow) {
		return tLogTranFlowDao.updateBatchResultTran(tLogTranFlow);
	}

	@Override
	public List<TLogTranFlow> getAllBatchNoSend(TLogTranFlow tLogTranFlow) {
		return tLogTranFlowDao.findAllBatchNoSend(tLogTranFlow);
	}

	
}
