package com.bestpay.cupsf.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by HR on 2016/5/17.
 */
@Setter
@Getter
public class TCfgPktDef {

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
