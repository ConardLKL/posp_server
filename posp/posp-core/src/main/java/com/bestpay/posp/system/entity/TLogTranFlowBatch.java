package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class TLogTranFlowBatch {

	private String serialNo;
    private String tranDate;
    private String batchNo;
    private String seqNum;
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
    private String cupsSerialNo;
    private String cupsDate;
    private String cardNo;
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
    private String inputMode;
    private String conditionMode;
    private String ccyCode;
    private String respCode;
    private String tranState;
    private String batchState;
    private String batchResult;
    private String termSettleDate;
    private String ip;
    private String reserve;
    private String cardFlag;

}
