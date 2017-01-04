package com.bestpay.posp.utils;

import java.io.UnsupportedEncodingException;

public class ByteUtils {
	
	
	/**
	 * 处理字符串字节码， 只获取固定长度， 如果字符串不超过长度，原值返回
	 * @param str
	 * @param len
	 * @param encoding
	 * @return
	 */
	public static String subStringByteArrayLen(String str, int len, String encoding){
		try {
			byte[] sba = str.getBytes(encoding);
			
			if(sba.length > len){
				byte[] b = new byte[len];
				for (int i = 0; i < len; i++) {
					b[i] = sba[i];
				}
				return new String(b, encoding);
			}
			return str;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
