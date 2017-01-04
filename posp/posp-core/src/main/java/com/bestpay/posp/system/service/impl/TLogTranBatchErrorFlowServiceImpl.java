package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.system.dao.TLogTranBatchErrorFlowDao;
import com.bestpay.posp.system.service.TLogTranBatchErrorFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.system.entity.TLogTranBatchErrorFlow;

@Component("TLogTranBatchErrorFlowService")
public class TLogTranBatchErrorFlowServiceImpl implements
		TLogTranBatchErrorFlowService {
	@Autowired 
	private TLogTranBatchErrorFlowDao tLogTranBatchErrorFlowDao;
	@Override
	public boolean insertLogTranBatchErrorFlow(
			TLogTranBatchErrorFlow tLogTranBatchErrorFlow) {
		// TODO Auto-generated method stub
		int i=tLogTranBatchErrorFlowDao.insert(tLogTranBatchErrorFlow);
		if(i == 1){
			return true;
		}
		return false;
	}

	@Override
	public boolean isLogTranBatchErrorFlow(
			TLogTranBatchErrorFlow tLogTranBatchErrorFlow) {
		// TODO Auto-generated method stub
		TLogTranBatchErrorFlow entity = tLogTranBatchErrorFlowDao.findUnique(tLogTranBatchErrorFlow);
		if(entity!=null)
			return false;
		else{
			return true;
		}
	}

	@Override
	public boolean updateLogTranBatchErrorFlow(
			TLogTranBatchErrorFlow tLogTranBatchErrorFlow) {
		// TODO Auto-generated method stub
		int i=tLogTranBatchErrorFlowDao.update(tLogTranBatchErrorFlow);
		if(i == 1){
			return true;
		}
		return false;
	}

	@Override
	public void saveLogTranBatchErrorFlow(IsoMessage iso) {
		// TODO Auto-generated method stub


	}

	@Override
	public void updateLogTranBatchErrorFlow(IsoMessage iso) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getMaxSeqErrorNum(TLogTranBatchErrorFlow tLogTranBatchErrorFlow) {
		// TODO Auto-generated method stub
		
		return tLogTranBatchErrorFlowDao.findMaxSeqNumError(tLogTranBatchErrorFlow);
	}

}
