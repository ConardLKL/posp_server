package com.bestpay.posp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class DateUtils {
	
	public static final String YYYYMMDD = "yyyyMMdd";
	public static final String YYMMDD = "yyMMdd";
	public static final String MMddHHmmss = "MMddHHmmss";
	public static final String HHmmss = "HHmmss";
	public static final String MMDD = "MMdd";
	
	
	public static String paraDate(String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}
	
	public static String paraDate(Date date, String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	public static void main(String[] args) {
		log.debug(DateUtils.paraDate(HHmmss));
	}

}
