package com.bestpay.posp.utils;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class CommondUtils {
	
	/**
	 * 获取本地IP地址
	 * @return
	 */
	public static String localHostIP(){
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return  addr.getHostAddress().toString();
	}

	/**
	 * 转换数据
	 * @param data 转换的数据
	 * @param leftOrRight 左移或者右移
	 * @param len 移位长度
     * @return
     */
	public static double bigDecimal(String data,boolean leftOrRight, int len){
		BigDecimal decimal = new BigDecimal(data);
		double result;
		if(leftOrRight){
			result = decimal.movePointLeft(len).doubleValue();
		}else{
			result = decimal.movePointRight(len).doubleValue();
		}
		return result;
	}

	/**
	 * 转换数据(默认右移两位)
	 * @param data 转换的数据
	 * @return
     */
	public static double bigDecimal(String data){
		return bigDecimal(data, false, 2);
	}

	public static double multiply(double data1,double data2){
		return new BigDecimal(data1).multiply(new BigDecimal(Double.toString(data2))).doubleValue();
	}

	
	public static void main(String[] args) {
		String s = "00000500.00";
		String s1 = "0000057500";
		double d = bigDecimal(s) * (new BigDecimal("1.15").doubleValue());
		double d1 = new BigDecimal(bigDecimal(s)).multiply(new BigDecimal("1.15")).doubleValue();
		System.out.println(d);
		System.out.println(d1);
		if(bigDecimal(s1) == d){
			System.out.println("true");
		}
	}
}
