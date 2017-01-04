package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TMcmMerchant {

	private Long mctId;
    private String mctCode;
    private String mctName;
    private String parentMctcode;
    private String areaCode;
    private String mctCategory;
    private String mctType;
    private String province;
    private String cityName;
    private String district;
    private Date effDate;
    private Date expDate;
    private String prtnAddr;
    private String aqrBank;
    private Double rate;
    private Double uplmtAmount;
    private Date modDate;
    private String modStaff;
    private String stat;
    private String memo;
    private Double costRate;
    private Double costUplmtamount;
    private String aqrBankacctname;
    private String aqrBankacct;
    private String actType;
    private String aqrOrg;
    private String mctLevel;
    private String subAccount;
    private String isSettlement;
    private Date stlPoint;
    private String stlType;
    private String stlSplitlmonth;
    private String subFileformat;
    private String merchantComp;
    private String accountpayee;
    private Date payeeDate;
    private String isProvince;
    private String transConntype;
    private Long alarmAmountTrans;
    private Long maxAmountTrans;
    private Long peakAmountPerday;
    private String joinWay;
    private String servAgent;
    private String minFree;
}
