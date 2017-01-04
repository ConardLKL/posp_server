package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TMcmSubacctinfo {

	private Long acctId;
    private Long mctId;
    private String mctCode;
    private String revacctName;
    private String revacctBank;
    private String revAcct;
    private String linkMan;
    private String linkPhone;
    private String email;
    private Date modDate;
    private String modStaff;
    private String stat;
    private String memo;
    private String bankCode;
    private String bankArea;
    private String subCode;

}
