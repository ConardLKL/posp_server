package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Setter
@Getter
public class TInfoTermPara {

	private String paraId;
    private String aid;
    private String appSelectId;
    private String appVersion;
    private String tacDefault;
    private String tacOnline;
    private String tacDeny;
    private String termMinLimit;
    private String biasRandValue;
    private String biasRandMaxTargetPer;
    private String randomTargetPer;
    private String defDdol;
    private String termOnlinePinCap;
    private String termEcashLimit;
    private String contactlessOfflineLimit;
    private String contactlessLimit;
    private String cvmLimit;
    private Date updateTime;

}
