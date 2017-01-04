package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Setter
@Getter
public class TInfoNonSecretPara {

	private String nonContactSwitch;
    private String quickRepaitTime;
    private String quickRecordTime;
    private String qpsNonSecretAmount;
    private String qpsNonSecretLabel;
    private String binALabel;
    private String binBLabel;
    private String cdcvmLabel;
    private String nonSignLimit;
    private String nonSignLabel;
    private Date updateTime;

}
