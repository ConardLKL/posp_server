package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TInfoTermSign{

	private String termCode;
    private String merchCode;
    private String tmkTpk;
    private String tmkTdk;
    private String tmkTak;
    private String batchNo;
    private Date updateTime;
    private String signState;
    private String serialNo;
    private Long channelCode;

}
