package com.bestpay.posp.system.service.impl;

import com.bestpay.posp.system.dao.TLogSettleFlowStatisticsDao;
import com.bestpay.posp.system.entity.TLogSettleFlowStatistics;
import com.bestpay.posp.system.service.TLogSettleFlowStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("TLogSettleFlowStatisticsService")
public class TLogSettleFlowStatisticsServiceImpl implements
		TLogSettleFlowStatisticsService {
	@Autowired
	private TLogSettleFlowStatisticsDao tLogSettleFlowStatisticsDao;
	@Override
	public TLogSettleFlowStatistics getLogSettleFlowStatistics(
			TLogSettleFlowStatistics logSettleFlowStatistics) {
		// TODO Auto-generated method stub
		TLogSettleFlowStatistics data=tLogSettleFlowStatisticsDao.querySettleFlowStatistics(logSettleFlowStatistics);
		if (data == null){
			data = new TLogSettleFlowStatistics();
			data.setMerchCode(logSettleFlowStatistics.getMerchCode());
			data.setTermCode(logSettleFlowStatistics.getTermCode());
			data.setBatchNo(logSettleFlowStatistics.getBatchNo());
			data.setCreditCount(0);
			data.setCreditSum(0L);
			data.setDebitCount(0);
			data.setDebitSum(0L);
		}
		return data;
	}

	@Override
	public TLogSettleFlowStatistics getOffilceLogSettleFlowStatistics(
			TLogSettleFlowStatistics logSettleFlowStatistics) {
		// TODO Auto-generated method stub
		
		TLogSettleFlowStatistics data=tLogSettleFlowStatisticsDao.queryOffliceSettleFlowStatistics(logSettleFlowStatistics);
		if (data == null){
			data = new TLogSettleFlowStatistics();
			data.setMerchCode(logSettleFlowStatistics.getMerchCode());
			data.setTermCode(logSettleFlowStatistics.getTermCode());
			data.setBatchNo(logSettleFlowStatistics.getBatchNo());
			data.setCreditCount(0);
			data.setCreditSum(0L);
			data.setDebitCount(0);
			data.setDebitSum(0L);
		}
		return data;
	}

}
