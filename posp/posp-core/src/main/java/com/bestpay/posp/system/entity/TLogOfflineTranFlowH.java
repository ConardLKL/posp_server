package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class TLogOfflineTranFlowH {

	private String termCode;
    private String batchNo;
    private String termSerialNo;
    private String termDate;
    private String termTime;
    private String tranDate;
    private String serialNo;
    private String tranType;
    private String settleFlag;
    private String settleDate;
    private String cardNo;
    private String cardType;
    private Double tranAmount;
    private Double remainAmount;
    private Double amtAfterWithdraw;
    private String authRespCode;
    private String authDate;
    private String rcvBankCode;
    private String rcvBranchCode;
    private String issBankCode;
    private String issInstCode;
    private String agtInstCode;
    private String mcc;
    private String merchCode;
    private String agtSerialNo;
    private String channelType;
    private String inputMode;
    private String cardSeqno;
    private String termReadab;
    private String icCondition;
    private String respCode;
    private String ccyCode;
    private String expDate;
    private String termFlag;
    private String tranFlag;
    private String referNo;
    private String batchState;
    private String batchResult;
    private String state;
    private String result;
    private String termSettleDate;
    private String termSettleFtime;
    private String ip;
    private String retreferno;
    private String reserve;
    private String cardFlag;

}
