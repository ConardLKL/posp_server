package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TStlVoucher {

	private Long voucherId;
    private String slogan;
    private String scale;
    private String mctCode;
    private String posCode;
    private String province;
    private String cityName;
    private String district;
    private Date createDate;
    private Date modDate;
    private String stat;
    private String memo;

}
