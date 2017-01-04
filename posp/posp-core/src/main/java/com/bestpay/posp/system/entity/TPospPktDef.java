package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class TPospPktDef {

	private Long pktId;
    private String channelCode;
    private String msgType;
    private Long bit;
    private String type;
    private String format;
    private String spec;
    private Long length;
    private String stat;

}
