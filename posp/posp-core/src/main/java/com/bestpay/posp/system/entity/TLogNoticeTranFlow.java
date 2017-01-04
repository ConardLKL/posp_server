package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class TLogNoticeTranFlow {

	private String serialNo;
    private String tranDate;
    private String tranTime;
    private String tranCode;
    private String tranType;
    private String origSerialNo;
    private String origTranDate;
    private String origTranTime;
    private String origTranCode;
    private String origTranType;
    private String merchCode;
    private String termCode;
    private String batchNo;
    private String termSerialNo;
    private String termDate;
    private String termTime;
    private String origTermSerialNo;
    private String origTermDate;
    private String origTermTime;
    private String cardNo;
    private Double tranAmount;
    private String writeFlag;
    private String respCode;
    private String returnCode;
    private String returnDesc;
    private Long maxRetryTimes;
    private Long interval;
    private Long retryTimes;
    private String succssFlag;
    private String lastRetryTime;
    private Long pbAsciiLen;
    private String pbAsciiStr;

}
