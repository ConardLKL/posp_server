package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TLogSettleFlowStatistics {
	//商户编号
	    private String merchCode;
	   //终端编号
	    private String termCode;
	    //批次号
	    private String batchNo;
	    //借机总金额
	    private Long debitSum;
	    //借记总次数
	    private Integer debitCount;
	    //贷记总金额
	    private Long creditSum;
	    //贷记总次数
	    private Integer creditCount;


}
