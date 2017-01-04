package com.bestpay.posp.system.service;

import java.util.List;

import com.bestpay.posp.system.entity.TInfoTermPara;
import com.bestpay.posp.system.base.BaseService;

/**
 * 终端参数下载信息表
 * @author YZH
 *
 */
public interface TInfoTermParaService extends BaseService<TInfoTermPara, Long> {
	/**
	 * 获取终端参数下载信息表
	 * @param TInfoTermPara
	 * @return
	 */
	public List<TInfoTermPara> getPospTermPara();
	/**
	 * 查询终端参数下载信息
	 * @return
	 */
	public TInfoTermPara findPospTermPara(TInfoTermPara tInfoTermPara);

}
