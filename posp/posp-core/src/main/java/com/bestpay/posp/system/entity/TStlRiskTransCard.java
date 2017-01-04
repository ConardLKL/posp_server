package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TStlRiskTransCard {

	private Long transCardId;
    private String cardType;
    private Long alarmAmountTrans;
    private Long maxAmountTrans;
    private Long peakAmountPerday;
    private Date modDate;
    private String modStaff;
    private String freeLimit;

}
