/**
 * 
 */
package com.bestpay.posp.service;

import java.io.Serializable;


/**
 * @author yihaijun
 *
 */
@lombok.Data
public class PosRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8387569702060433760L;
	
	private String testCmd;
	private String testRequest;
}
