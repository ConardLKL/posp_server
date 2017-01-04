package com.bestpay.posp.system.service;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TLogTranFlowBatch;

/**
 * 披上送记录流水接口
 * @author FENGLI
 *
 */
public interface TLogTranFlowBatchService extends BaseService<TLogTranFlowBatch, Long> {

	/**
	 * 记录新的流水
	 * @return
	 */
	boolean insertLogTranFlowBatch(TLogTranFlowBatch tLogTranFlowBatch);
	/**
	 * 查询流水是否存在
	 * true 流水存在,报错；false 流水不存在，可以进行下步操作
	 */
	boolean isLogTranFlowBatch(TLogTranFlowBatch tLogTranFlowBatch);
	/**
	 * 更新流水
	 */
	boolean updateLogTranFlowBatch(TLogTranFlowBatch tLogTranFlowBatch);
	
	/**
	 * 保存流水信息 
	 * @param iso
	 */
	public void saveTranFlow(IsoMessage iso);
	
	/**
	 * 更新流水
	 * @param iso
	 */
	public void updateTranFlow(IsoMessage iso);
	/**
	 *  查询最大序列数
	 * @param iso
	 */
	public String getMaxSeqNum(TLogTranFlowBatch tLogTranFlowBatch);
	
}
