package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TMcmMaintain {

	private Long maintainId;
    private String maintainComp;
    private String mtnLinkman;
    private String mtnLinkphone;
    private Date modDate;
    private String modStaff;
    private String stat;
    private String memo;
    private String posCode;

}
