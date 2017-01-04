package com.bestpay.posp.protocol;

import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.remoting.transport.client.EncryptionAPI;
import com.bestpay.posp.system.cache.ConfigCache;
import com.bestpay.posp.system.entity.TSymAppkey;
import com.bestpay.posp.system.entity.TSymDbkey;
import com.bestpay.posp.system.service.TSymAppkeyService;
import com.bestpay.posp.system.service.TSymDbkeyService;
import com.bestpay.posp.utils.StringToAscii;
import com.bestpay.posp.utils.recombin.HexBinary;

/**
 * 存储解密后明文的密钥
 * @author HR
 *
 */
public class DecipherAppKey {

	private ConfigCache configCache;
//	private EncryptionAPI encryptionAPI;
	private UnionAPI unionAPI;
	private TSymDbkeyService tSymDbkeyService;
	private TSymAppkeyService tSymAppkeyService;
	
	private static String APPKEY;
	
	public DecipherAppKey(){
		
	}
	
	public DecipherAppKey(ConfigCache configCache, UnionAPI unionAPI, TSymDbkeyService tSymDbkeyService, TSymAppkeyService tSymAppkeyService) throws Exception{
		this.configCache = configCache;
		this.unionAPI = unionAPI;
		this.tSymDbkeyService = tSymDbkeyService;
		this.tSymAppkeyService = tSymAppkeyService;
		initAppKey();
	}
	
	private void initAppKey() {
		String TE_BB456_EDK = configCache.getParaValues(SysConstant.CL8001, "000056");
		TSymDbkey tSymDbkey = tSymDbkeyService.queryByKeyId(1000L);
		TSymAppkey tSymAppkey = tSymAppkeyService.queryByKeyId(1000L);
		String dbKetText = unionKeyDecryptDataBy531(TE_BB456_EDK,tSymDbkey.getDbEncrypt());
		APPKEY = DesedeUtils.decrypt256(dbKetText,tSymAppkey.getAppEncrypt());
	}
	 

	public static String getAPPKey() {
		return APPKEY;
	}
	
	
	/**
	 * 将使用加密key和使用key加密过的数据进行解密
	 * 
	 * @return 解密后的数据
	 * @author qinjun 2014-01-15
	 * */
	public String unionKeyDecryptDataBy531(String encryptKey,
			String encryptedData) {
		if (encryptedData == null || "".equals(encryptedData.trim())) {
			return null;
		}
//		String tempStr = this.unionAPI.unionKeyDecryptDataBy531(encryptKey,
//				encryptedData);
		String tempStr = unionAPI.unionKeyDecryptDataBy531(encryptKey,
				encryptedData);
		// 处理解密后的数据并返回
		String headStr = tempStr.substring(0,4).replace("F", "");
		 if("".equals(headStr)){
			 return tempStr.substring(4, tempStr.length());
		 }else{
			 int dataLength = Integer.valueOf(headStr);
			 return tempStr.substring(4, 4 + dataLength);
		 }
		
	}
 
}
