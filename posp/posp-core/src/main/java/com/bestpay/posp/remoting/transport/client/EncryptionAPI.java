package com.bestpay.posp.remoting.transport.client;


import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.system.cache.ConfigCache;
import com.union.api.UnionAPI;

/**
 * 加密机参数加载
 * @author HR
 *
 */

public class EncryptionAPI extends UnionAPI {
	
	private ConfigCache configCache;
 
	public EncryptionAPI(ConfigCache configCache) throws Exception{
		//获取加密机IP
//		super(configCache.getParaValues(8001L, "000040"),//ip
//				Integer.parseInt(configCache.getParaValues(8001L, "000041")),//端口port
//				Integer.parseInt(configCache.getParaValues(8001L, "000043")),//timeOut延时
//				Integer.parseInt(configCache.getParaValues(8001L, "000044")),//longOrShortConn
//				configCache.getParaValues(8001L, "000042"));//gunionIDOfEsscAPI
		super(configCache.getParaValues(8001L, "union.ip"),//ip
				Integer.parseInt(configCache.getParaValues(8001L, "union.port")),//端口port
				Integer.parseInt(configCache.getParaValues(8001L, "union.timeOut")),//timeOut延时
				Integer.parseInt(configCache.getParaValues(8001L, "union.conn")),//longOrShortConn
				configCache.getParaValues(8001L, "union.id"));//gunionIDOfEsscAPI
		if(this.configCache == null){
			this.configCache = configCache;
		}else{
			throw new Exception(String.format("EXCEPTION: EncryptionAPI 加载参数失败. "));
		}
		
	}
	/**
	 * 调用edk解密密钥
	 * 
	 */
	@Override
	public String unionKeyDecryptDataBy531yizhifu(String name,String data){
		String fomatEdk = configCache.getParaValues(8001L, "000051");//POS.%s.edk
		String fullName = String.format(fomatEdk, name);
		return super.unionKeyDecryptDataBy531yizhifu(fullName, data);
	}
	/**
	 * 抵用edk加密密钥
	 */
	@Override
	public String unionKeyEncryptDataBy530(String name,String data){
		String fomatEdk = configCache.getParaValues(8001L, "000051");//POS.%s.edk
		String fullName = String.format(fomatEdk, name);
		return super.unionKeyEncryptDataBy530(fullName, data);
	}
	/**
	 * POS机zak密钥
	 */
	@Override
	public String UnionGenerateMac(String name, int lenOfMacData, String macData){
		String fomatZak = configCache.getParaValues(8001L, "000052");//POS.%s.zak
		String fullKeyName = String.format(fomatZak, name);
		return super.UnionGenerateMac(fullKeyName, lenOfMacData, macData);
	}
	/**
	 * POS机zak密钥
	 */
	public int UnionVerifyMac(String name, int lenOfMacData, String macData, String mac){
		String fomatZak = configCache.getParaValues(8001L, "000052");//POS.%s.zak
		String fullKeyName = String.format(fomatZak, name);
		return super.UnionVerifyMac(fullKeyName, lenOfMacData, macData, mac);
	}
	/**
	 * 银联zak密钥
	 * fullKeyName为定值
	 */
	public String UnionGenerateChinaPayMac(int lenOfMacData, String macData){
		String fullKeyName = configCache.getParaValues(8001L, "000053");//String.format("JG.%s.zak", "YZF000666");
		return super.UnionGenerateChinaPayMac(fullKeyName, lenOfMacData, macData);
	}
	/**
	 * 银联zak密钥
	 */
	public int UnionVerifyChinaPayMac(int lenOfMacData, String macData, String mac){
		String fullKeyName = configCache.getParaValues(8001L, "000053");//String.format("JG.%s.zak", "YZF000666");
		return super.UnionVerifyChinaPayMac(fullKeyName, lenOfMacData, macData, mac);
	}
	/**
	 * zpk密钥下载
	 */
	public String[] UnionGenerateKeyZpk(String name){
		String fomatZpk = configCache.getParaValues(8001L, "000054");//POS.%s.zpk
		String fullKeyName = String.format(fomatZpk, name);
		return super.UnionGenerateKey(fullKeyName);
	}
	/**
	 * POS.zak下载
	 * @param name
	 * @return
	 */
	public String[] UnionGenerateKeyZak(String name){
		String fomatZak = configCache.getParaValues(8001L, "000052");//POS.%s.zak
		String fullKeyName = String.format(fomatZak, name);
		return super.UnionGenerateKey(fullKeyName);
	}
	/**
	 * POS.edk下载
	 * @param name
	 * @return
	 */
	public String[] UnionGenerateKeyEdk(String name){
		String fomatEdk = configCache.getParaValues(8001L, "000051");//POS.%s.edk
		String fullKeyName = String.format(fomatEdk, name);
		return super.UnionGenerateKey(fullKeyName);
	}
	/**
	 * 银联zpk密钥
	 */
	public String UnionTranslatePin(String name,String pinBlock1, String accNo){
		String fomatZpkp = configCache.getParaValues(8001L, "000054");//POS.%s.zpk
		String fullKeyName1 = String.format(fomatZpkp, name);
		String fullKeyName2 = configCache.getParaValues(8001L, "000055");//String.format("JG.%s.zpk", "YZF000666")
		return super.UnionTranslatePin(fullKeyName1, fullKeyName2, pinBlock1, accNo);
	}
	
	/**
	 * 获取卡密码明文
	 * @param pinBlock
	 * @param accNo
     * @return
     */
	public String UnionDecryptPin(String pinBlock,String accNo){
		ConfigCache configCache = (ConfigCache) PospApplicationContext.getBean("ConfigCache");
		EncryptionAPI encryptionAPI = (EncryptionAPI)PospApplicationContext.getBean("EncryptionAPI");
		String fullKeyName = configCache.getParaValues(8001L, "000055");
		return encryptionAPI.UnionDecryptPin(fullKeyName,pinBlock, accNo);
	}
	
}
