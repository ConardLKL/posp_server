package com.bestpay.posp.protocol;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * 各种加密、解密算法
 * */
public class DesedeUtils {

	private static final String ENCODING = "GBK";
	
	//3Des算法
	private static final String Algorithm = "DESede";
	
	public static String str2HexStr(byte[] bs) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString();
	}

	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static int toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
	
	public static String data256PreEncrypt(String data,int dateLength){
		
		if(data.length() < dateLength){
			String head = String.valueOf(data.length()); 
			int hl = 4 - head.length();
			String resultStr = data;
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < hl; i++){
				sb.append("F");
			}
			
			for(int i = 0; i < dateLength - data.length(); i ++){
				resultStr += "X";
			}
			
			return sb.toString() + head + resultStr;
		}else{
			return "FFFF" + data;
		}
	}
	
	/**
	 * 获得实际字符串的值
	 * */
	public static String pre256DataToReal(String data){
		String headStr = data.substring(0,4).replace("F", "");
		 if("".equals(headStr)){
			 return data.substring(4, data.length());
		 }else{
			 int dataLength = Integer.valueOf(headStr);
			 return data.substring(4, 4 + dataLength);
		 }
	}
	
	/**
	 * 传入进来的key不够指定位，则自动补充到指定位长度；若大于指定位长度，则只取前指定位字符
	 * */
	public static String processKey(String key,int dateLength){
		//刚刚好dateLength位
		if(key.length() == dateLength){
			return key;
		}
		//小于dateLength位
		if(key.length() < dateLength){
			int length = dateLength - key.length();
			StringBuffer sb = new StringBuffer();
			sb.append(key);
			for(int i = 0; i < length; i++){
				sb.append("X");
			}
			return sb.toString();
		}else{
			//key长度大于dateLength为，则取前dateLength个字符作为key
			return key.substring(0, dateLength);
		}
	}

	/**
	 * 3DES加密算法
	 * @author qinjun 2015-06-08
	 * @param key 加密密钥
	 * @param data 加密数据
	 * @return 返回加密后字符串
	 * */
	public static String encrypt(String key,String data){
		//校验秘钥和加密数据
		if(key == null || data == null){
			return null;
		}
		
		try {
			byte[] byteKey = processKey(key, 24).getBytes(ENCODING);
			byte[] byteData = data.getBytes(ENCODING);
			
			SecretKey deskey = new SecretKeySpec(byteKey, Algorithm);
			Cipher cipher = Cipher.getInstance(Algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, deskey);
			return str2HexStr(cipher.doFinal(byteData));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 3DES解密算法
	 * @author qinjun 2015-06-08
	 * @param key 解密秘钥
	 * @param 解密字符串
	 * */
	public static String decrypt(String key,String enData){
		if(key == null || enData == null){
			return null;
		}
		
		try {
			byte[] byteKey = processKey(key, 24).getBytes(ENCODING);
			byte[] byteData = hexStringToByte(enData);
			
			SecretKey deskey = new SecretKeySpec(byteKey, Algorithm);
			Cipher cipher = Cipher.getInstance(Algorithm);
			cipher.init(Cipher.DECRYPT_MODE, deskey);
			return new String(cipher.doFinal(byteData));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 加密后长度大于256位字符
	 * @author qinjun 2015-06-08
	 * @param key 加密密钥
	 * @param data 加密数据
	 * */
	public static String encrypt256(String key, String data){
		String data256 = data256PreEncrypt(data, 120);
		return encrypt(key, data256);
	}
	
	/**
	 * 解密encrypt256数据
	 * @author qinjun 2015-06-08
	 * @param key 解密密钥
	 * @param data 解密数据
	 * @return 解密后字符串
	 * */
	public static String decrypt256(String key, String enData){
		String data = decrypt(key, enData);
		return pre256DataToReal(data);
	}
}
