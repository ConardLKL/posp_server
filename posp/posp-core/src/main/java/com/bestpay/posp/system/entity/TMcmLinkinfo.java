package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TMcmLinkinfo {

	private Long linkId;
    private String mctCode;
    private String linkType;
    private String areaCode;
    private String province;
    private String cityName;
    private String district;
    private String department;
    private String linkMan;
    private String linkPost;
    private String mobNbr;
    private String telNbr;
    private String phsNbr;
    private String qq;
    private String msn;
    private String email;
    private String address;
    private String zipCode;
    private Date modDate;
    private String modStaff;
    private String stat;
    private String memo;

}
