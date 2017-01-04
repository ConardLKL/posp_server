package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TMcmSmtcyl {

	private Long smtcylId;
    private String mctCode;
    private String periodType;
    private String periodVal;
    private Date modDate;
    private String modStaff;
    private String stat;
    private String memo;

}
