package com.bestpay.posp.system.service;

import java.util.List;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TLogTranFlow;

/**
 * 流水表实现类
 * @author YZH
 *
 */
public interface TLogTranFlowService extends BaseService<TLogTranFlow,Long>{
	/**
	 * 记录新的流水
	 * @return
	 */
	boolean insertLogTranFlow(TLogTranFlow tLogTranFlow);
	/**
	 * 查询流水是否存在
	 * true 流水存在,报错；false 流水不存在，可以进行下步操作
	 */
	boolean isLogTranFlow(TLogTranFlow tLogTranFlow);
	/**
	 * 更新流水
	 */
	boolean updateLogTranFlow(TLogTranFlow tLogTranFlow);
	/**
	 * 计算累积金额
	 * @param tLogTranFlow
	 * @return
	 */
	public TLogTranFlow sumTranAmount(TLogTranFlow tLogTranFlow);
	
	/**
	 * 获取流水信息
	 * @param tLogTranFlow
	 * @return
	 */
	public TLogTranFlow getLogTranFlow(TLogTranFlow tLogTranFlow);
	/**
	 * 获取部分流水信息
	 * @param tLogTranFlow
	 * @return
	 */
	public List<TLogTranFlow> getLogTranFlows(TLogTranFlow tLogTranFlow);
	
	
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
	 * 判断流水是否存在
	 * @param serialNo
	 * @return
	 */
	boolean isExistTranFlow(String serialNo);
	
	/**
	 * 更新流水
	 */
	int updateBantchLogTranFlow(TLogTranFlow tLogTranFlow);
	/**
	 * 查询POSP平台有POS没有上送的交易
	 * @return
	 */
	 List<TLogTranFlow> getAllBatchNoSend(TLogTranFlow tLogTranFlow); 
 
}
