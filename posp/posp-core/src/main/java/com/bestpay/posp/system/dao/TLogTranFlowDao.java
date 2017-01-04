package com.bestpay.posp.system.dao;

import java.util.List;

import com.bestpay.posp.system.base.BaseDao;
import com.bestpay.posp.system.entity.TLogTranFlow;

public interface TLogTranFlowDao extends BaseDao<TLogTranFlow, Long> {

	/**
	 * 更新交易表批结算状态
	 * @return
	 */
	public int updateBatchResultTran (TLogTranFlow logTranFlow);
	/**
	 * 查询POSP平台有POS没有上送的交易
	 * @return
	 */
	public List<TLogTranFlow> findAllBatchNoSend(TLogTranFlow logTranFlow);
	/**
	 * 计算日累积金额
	 * @param logTranFlow
	 * @return
	 */
	public TLogTranFlow sumTranAmount (TLogTranFlow logTranFlow);
}
