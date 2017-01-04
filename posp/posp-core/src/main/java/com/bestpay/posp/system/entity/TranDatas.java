package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 传递数据对象
 * 
 * @author yzh
 * 
 * @time 2015-11-30 上午8:51:51
 */
@Setter
@Getter
public class TranDatas {

	private Map<String, String> data;
	private String client;


}
