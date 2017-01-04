package com.bestpay.posp.protocol;


import com.bestpay.posp.service.exception.TrackDecryptionException;
import com.bestpay.posp.spring.PospApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 对密文35-36域
 * 解密为明文放回原域
 * @author HR
 *
 */
@Slf4j
@Component
public class TrackDecryption {
	
	/**
	 * 如果35/36存在则调用解密，用明文替换之前密文
	 * @param iso
	 * @throws Exception
	 */
	public static void decryptTrack(IsoMessage iso) throws Exception{
		if(StringUtils.isNotEmpty(iso.getField(35))){
			String field35 = decryptionData(iso,iso.getField(35));
			compareTrack(iso.getField(2),field35);
			iso.setField(35, field35);
			setAccountNumber(iso);
//			log.info("35域："+iso.getField(35));
		}
		if(StringUtils.isNotEmpty(iso.getField(36))){
			String field36 = decryptionData(iso,iso.getField(36));
//			compareTrack(iso.getField(2),field36.substring(2));
			iso.setField(36, field36);
			setAccountNumber(iso);
		}
	}
	/**
	 * 解密
	 * @param iso
	 * @param fieldData
	 * @return
	 */
	private static String decryptionData(IsoMessage iso,String fieldData) throws Exception{
		if(iso.isPlatform()){
			fieldData = assembledData(iso.isStateKeySign(),fieldData,iso.getField(42)+iso.getField(41));
		}else{
			fieldData = assembledData(iso.isStateKeySign(),fieldData,iso.getField(32));
		}
		return fieldData;
	}
	/**
	 * 组装数据
	 * @param sign 是否为国密
	 * @param trackData
	 * @param fullName
	 * @return
     * @throws Exception
     */
	private static String assembledData(boolean sign,String trackData, String fullName) throws Exception{
		//由于磁道数据长度奇偶不同，解密的数据位置也不同
		//国密和国际加密长度不同
		if(sign){
			if (trackData.length() % 2 != 0) {
				trackData = trackData.substring(0, trackData.length() - 33) +
						decodeTrack(sign, fullName, trackData.substring(trackData.length() - 33, trackData.length() - 1))
						+ trackData.substring(trackData.length() - 1);
			} else {
				trackData = trackData.substring(0, trackData.length() - 34) +
						decodeTrack(sign, fullName, trackData.substring(trackData.length() - 34, trackData.length() - 2))
						+ trackData.substring(trackData.length() - 2);
			}
		}else {
			if (trackData.length() % 2 != 0) {
				trackData = trackData.substring(0, trackData.length() - 17) +
						decodeTrack(sign, fullName, trackData.substring(trackData.length() - 17, trackData.length() - 1))
						+ trackData.substring(trackData.length() - 1);
			} else {
				trackData = trackData.substring(0, trackData.length() - 18) +
						decodeTrack(sign, fullName, trackData.substring(trackData.length() - 18, trackData.length() - 2))
						+ trackData.substring(trackData.length() - 2);
			}
		}
		//由于兼容性问题，若没有等号，则把“D”或“d”改为“=”
		if(trackData.indexOf("=") == -1){
			int index = trackData.toLowerCase().indexOf("d");
			trackData = trackData.substring(0,index) + "=" + trackData.substring(index+1);
		}
		return trackData;
	}
	/**
	 * 验证解密结果
	 * @param cardNo
	 * @param trackData
	 * @throws Exception
	 */
	private static void compareTrack(String cardNo,String trackData) throws Exception{
		if(cardNo != null && !StringUtils.equals(cardNo, trackData.substring(0, trackData.indexOf("=")))){
			throw new Exception("卡号与磁道不一致");
		}
	}
	/**
	 * 获取主账号
	 * 如果不送第二域
	 * @param iso
	 * @throws Exception
	 */
	private static void setAccountNumber(IsoMessage iso) {
		try{
			if(StringUtils.isEmpty(iso.getField(2)) && StringUtils.isNotEmpty(iso.getField(35))){
				int loop = iso.getField(35).indexOf("=") != -1 ?  
						iso.getField(35).indexOf("=") : iso.getField(35).toLowerCase().indexOf("d") ;
				iso.setField(2, iso.getField(35).substring(0, loop));
			}else if(StringUtils.isEmpty(iso.getField(2)) && StringUtils.isNotEmpty(iso.getField(36))){
				int loop = iso.getField(36).indexOf("=") != -1 ?  
						iso.getField(36).indexOf("=") : iso.getField(36).toLowerCase().indexOf("d") ;
				iso.setField(2, iso.getField(36).substring(2, loop));
			}
		}catch(Exception e){
			log.error(e.getMessage());
		}
	}

	/**
	 * 使用unionKeyDecryptDataBy531密文
	 * @param fullName 密钥名称
	 * @param encodeStr 密文
	 * @return
	 */
	private static String decodeTrack(boolean sign,String fullName,String encodeStr) throws Exception{
//		EncryptionAPI encryptionAPI = (EncryptionAPI)PospApplicationContext.getBean("EncryptionAPI");
		UnionAPI unionAPI = (UnionAPI)PospApplicationContext.getBean("UnionAPI");
		String res = unionAPI.unionKeyDecryptDataBy531yizhifu(sign,fullName,encodeStr);
		if(StringUtils.equals(res.substring(0,1),"-")){
			throw new TrackDecryptionException("磁道解密失败！");
		}
		return res;
	}
}
