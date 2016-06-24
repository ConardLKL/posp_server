package cn.com.bestpay.posp.system.service;

import cn.com.bestpay.posp.system.entity.TSysPara;
/**
 * 系统参数控制表
 * @author YZH
 *
 */
public interface TSysParaService extends BaseService<TSysPara,Long>{
	/**
	 * 获取系统参数控制信息
	 * @param TSysPara
	 * @return
	 */
	public TSysPara getPospSysPara(TSysPara tSysPara);
	
	/**
	 * 获取POSP交易流水号
	 * @return
	 */
	public String getSerialNo();
	
}
