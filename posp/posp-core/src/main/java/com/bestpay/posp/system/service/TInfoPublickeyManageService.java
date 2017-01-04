package com.bestpay.posp.system.service;

import java.util.List;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TInfoPublickeyManage;

/**
 * 终端公钥下载信息表
 * @author YZH
 *
 */
public interface TInfoPublickeyManageService extends BaseService<TInfoPublickeyManage, Long> {
	/**
	 * 获取终端公钥下载信息表
	 * @param tInfoPublickeyManage
	 * @return
	 */
	public List<TInfoPublickeyManage> getInfoPublickeyManage(TInfoPublickeyManage tInfoPublickeyManage);
	
	/**
	 * 查询终端公钥下载信息
	 * @param tInfoPublickeyManage
	 * @return
	 */
	public TInfoPublickeyManage findInfoPublickeyManage(TInfoPublickeyManage tInfoPublickeyManage);

}
