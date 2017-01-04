package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TStlPosBlackList {
	private String termCode;
	private String productModel;
	private String serialNumber;
	private String merchantCode;
	private String merchantName;
	private String addr;
	private String remark;
	private String inputTeller;
	private String inputDate;
	private String inputTime;
	private String upDateTeller;
	private String upDateDate;
	private String upDateTime;
	private String stat;
	private String inputDateTime;
	private String updateDateTime;

}
