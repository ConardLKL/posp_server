package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by HR on 2016/5/9.
 */
@Setter
@Getter
public class TCfgRiskWhiteList {
    private Long whiteId;
    private Long infoId;
    private String servAgent;
    private String stationVal;
    private Date createDate;
    private String stat;
}
