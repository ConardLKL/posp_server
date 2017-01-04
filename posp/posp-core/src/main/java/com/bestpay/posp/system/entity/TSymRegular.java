package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TSymRegular {

	private String keyTip;
    private String appEncrypt;
    private String dbEncrypt;
    private String dbEncrypt2;
    private String orgSeq;
    private Date crateDate;
    private Date modDate;
    private String modStaff;

}
