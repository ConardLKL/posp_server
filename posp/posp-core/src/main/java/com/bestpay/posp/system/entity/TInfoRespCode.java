package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class TInfoRespCode {

	private String pospCode;
	private String pospDesc;
    private String respCode;
    private String respDesc;
	private String extsysRespCode;
    private Long channelCode;
    private String extsysRespDesc;


	public void print(String seq){
  		log.info(String.format("[%s] EXTSYS_RESPOND->CODE:[%d] DESC:[%s]"),seq,this.extsysRespCode,this.extsysRespDesc);
  	}
 
}
