package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class TCfgBranchTranControl{

	private String tranCode;
    private String branchNo;
    private String channelNo;
    private String openFlag;
    private String operId;
    private String operDate;
    private String remark;

}
