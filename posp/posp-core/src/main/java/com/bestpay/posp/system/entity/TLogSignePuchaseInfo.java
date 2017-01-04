package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class TLogSignePuchaseInfo {

	private String merchCode;
	private String termCode;
	private String batchNo;
	private String termSerialNo;
	private String cardNo;
	private String tranDate;
	private String referNo;
	private String settleDate;
	private String tranCode;
	private Double tranAmount;
	private String merchName;
	private String tranType;
	private String operatorNum;
	private String rcvBranchCode;
	private String rcvBankCode;
	private String authNo;
	private Double tipAmount;
	private String cardOrganization;
	private String tranCcy;
	private String phoneNum;
	private String appLabelTerm;
	private String appNameTerm;
	private String appLabel;
	private String appName;
	private String appIdent;
	private String appCrypt;
	private String cardBalance;
	private String addCardNo;
	private String unpdtNum;
	private String aip;
	private String termVerResults;
	private String tranStatus;
	private String atc;
	private String issueAppdata;
	private String origDocumtNo;
	private String origBatchNo;
	private String origReferNo;
	private String origTranDate;
	private String origAuthNo;
	private String origTermCode;
	private String printNo;
	private Object eleSignatInfo;

}
