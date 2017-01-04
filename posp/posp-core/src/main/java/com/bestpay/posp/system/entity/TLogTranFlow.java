package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TLogTranFlow {

	private String serialNo;
    private String tranDate;
    private String tranTime;
    private String tranCode;
    private String tranType;
    private String settleFlag;
    private String settleDate;
    private String settleMode;
    private String origSerialNo;
    private String origSerialDate;
    private String origSerialTime;
    private String origTranType;
    private String termSerialNo;
    private String termDate;
    private String termTime;
    private String batchNo;
    private String cupsSerialNo;
    private String cupsDate;
    private String cardNo;
    private String intoCard;
    private String addCardNo;
    private String cardType;
    private String cardBin;
    private Double tranAmount;
    private Double remainAmount;
    private Double otherAmount;
    private String tranFlag;
    private String referNo;
    private String authNo;
    private String authDate;
    private Double authAmount;
    private String rcvBankCode;
    private String rcvBranchCode;
    private String mcc;
    private String merchCode;
    private String termCode;
    private String termFlag;
    private String operNo;
    private String channelType;
    private Long channelCode;
    private String inputMode;
    private String conditionMode;
    private String ccyCode;
    private String respCode;
    private String extsysRespCode;
    private String extsysRespDesc;
    private String tranState;
    private String batchState;
    private String batchResult;
    private String termSettleDate;
    private String ip;
    private Date sendDate;
    private Date receDate;
    private String reserve;
    private String cardFlag;
    private String phoneNumber;
    private String calledNumber;
    private String tpdu;
    private String isMatch;
    private String bindNumber;
    private String baseStationType;
    private String baseStationValues;
    private String mctName;
    private String cardProperties;
    private String xRealIp;
    private String cardProducts;
    private String servAgent;
    private String deviceSerialNo;
    private String softwareVersionNo;
    private String detailRespCode;
    private String freePasswordSign;
}