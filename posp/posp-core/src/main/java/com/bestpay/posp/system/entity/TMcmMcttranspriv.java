package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TMcmMcttranspriv {

	private Long mctprivId;
    private String mctCode;
    private String transId;
    private String transIdName;
    private String transCode;
    private String processCode;
    private String channelCode;
    private String confType;
    private String createStaff;
    private Date createDate;
    private String modStaff;
    private Date modDate;
    private String stat;

}
