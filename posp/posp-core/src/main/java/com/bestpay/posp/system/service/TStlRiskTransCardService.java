package com.bestpay.posp.system.service;

import com.bestpay.posp.system.base.BaseService;
import com.bestpay.posp.system.entity.TStlRiskTransCard;

/**
 * 流水表实现类
 * @author YZH
 *
 */
public interface TStlRiskTransCardService extends BaseService<TStlRiskTransCard,Long> {

	public TStlRiskTransCard findUnique(TStlRiskTransCard tStlRiskTransCard);
}
