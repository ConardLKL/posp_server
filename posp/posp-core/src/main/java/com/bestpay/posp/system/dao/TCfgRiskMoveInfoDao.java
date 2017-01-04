package com.bestpay.posp.system.dao;

import com.bestpay.posp.system.base.BaseDao;
import com.bestpay.posp.system.entity.TCfgRiskMoveInfo;

import java.util.List;

public interface TCfgRiskMoveInfoDao extends BaseDao<TCfgRiskMoveInfo, Long> {

    public List<TCfgRiskMoveInfo> findByStationVals(TCfgRiskMoveInfo tCfgRiskMoveInfo);

}
