package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TMcmPosinfo {

	private Long posId;
    private String posCode;
    private String mctCode;
    private String sortMctcode;
    private String mctAlias;
    private String province;
    private String cityName;
    private String district;
    private String bussinessName;
    private String bussinessAddr;
    private String bussinessLink;
    private String telAreacode;
    private String linkPhone;
    private Date installDate;
    private Date removeDate;
    private String posState;
    private Double deposit;
    private String comnuType;
    private String comnuNum;
    private String postermType;
    private String posSn;
    private String pinSn;
    private String powSn;
    private String keySn;
    private String installProc;
    private Date modDate;
    private String modStaff;
    private String memo;
    private String siteType;
    private String procState;
    private String auditMemo;
    private Date stopDate;
    private String psamSn;
    private String batchNo;
    private Long isLogon;
    private Long alarmAmountTrans;
    private Long maxAmountTrans;
    private Long peakAmountPerday;
    private String stationVal1;
    private String stationVal2;
    private String stationVal3;

}
