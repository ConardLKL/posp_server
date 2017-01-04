package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TMcmPostranspriv;

/**
 * 终端交易控制表
 * @author HR
 *
 */
public interface TMcmPostransprivService extends BaseService<TMcmPostranspriv, Long> {
	 /**
	  * 获取终端交易控制表信息
	  * @param tCfgTermTranControl
	  * @return
	  */
	public TMcmPostranspriv getMcmPostranspriv(TMcmPostranspriv tMcmPostranspriv);
}
