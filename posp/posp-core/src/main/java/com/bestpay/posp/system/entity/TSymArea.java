package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class TSymArea {

	private String areaCode;
    private String areaName;
    private String parentArea;
    private String areaType;
    private String areaDesc;
    private String zip;
    private String tel;
    private String stat;
    private Long macroValue;

}
