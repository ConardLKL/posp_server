package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TMcmCheckacct {

	private Long checkacctId;
    private String mctCode;
    private String fileFormat;
    private String revType;
    private String revEmail;
    private String ftpIp;
    private String ftpPort;
    private String ftpUse;
    private String ftpPwd;
    private Date modDate;
    private String modStaff;
    private String stat;
    private String memo;
    private String ftpAddr;
    private String lever;

}
