package cn.com.bestpay.posp.protocol.util;

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
