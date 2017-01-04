package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by HR on 2016/5/9.
 */
@Setter
@Getter
public class TCfgRiskMoveInfo {
    private Long moveId;
    private String batchId;
    private String mctCode;
    private String posCode;
    private String handStat;
    private String moveFlag;
    private Date moveDate;
    private String mctName;
    private Long times;
    private String stationVal;
    private String handStaff;
    private String handWay;
    private String resultMsg;
    private Date beginDate;
    private Date endDate;
    private String believe;
    private String[] stationVals;
}
