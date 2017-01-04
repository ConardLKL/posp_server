package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class TCfgTranControl{

	private String tranCode;
    private String tranType;
    private String channelNo;
    private String tranName;
    private String openFlag;
    private String operDate;
    private String operId;
    private String remark;

}
