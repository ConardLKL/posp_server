package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TSymAppkey {

	private String keyId;
    private String appEncrypt;
    private Date createDate;


}
