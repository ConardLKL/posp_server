package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TInfoNonSecretPara;

import java.util.List;

/**
 * 非接业务参数下载信息表
 * @author HR
 *
 */
public interface TInfoNonSecretParaService extends BaseService<TInfoNonSecretPara, Long> {

	/**
	 * 获取接业务参数下载信息表
	 * @return
	 */
	public List<TInfoNonSecretPara> getInfoNonSecretPara();

	/**
	 * 查询非接业务参数下载信息
	 * @param tInfoNonSecretPara
	 * @return
	 */
	public TInfoNonSecretPara findInfoNonSecretPara(TInfoNonSecretPara tInfoNonSecretPara);

}
