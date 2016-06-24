package com.bestpay.cupsf.utils;
/**
 * Created by HR on 2016/5/17.
 */
public final class StrUtil {
	public static String leftTrim(String str, String ch) {
		if (str == null || str.equals("")) {
			return str;
		} else {
			return str.replaceAll(String.format("^[%s]+",ch), "");
		}
	}

	public static String rightTrim(String str, String ch) {
		if (str == null || str.equals("")) {
			return str;
		} else {
			return str.replaceAll(String.format("[%s]+$",ch), "");
		}
	}
}
