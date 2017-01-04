package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PospMessage{

	/**
	 * mpos
	 */
	private String mpos;
	/**
	 * mobilPhone
	 */
	private String mobilPhone;
	/**
	 * tudu
	 */
	private byte[] tpdu;
	/**
	 * 主叫号码ani
	 */
	private String ani;
	/**
	 * 被叫号码dnis
	 */
	private String dnis;
	/**
	 * 报文头header
	 */
	private byte[] header;
	/**
	 * 报文正文
	 */
	private byte[] body;
	/**
	 * 类型
	 */
	private byte[] type;

}
