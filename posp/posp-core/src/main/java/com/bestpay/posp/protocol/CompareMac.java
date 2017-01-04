package com.bestpay.posp.protocol;

import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.utils.recombin.HexBinary;
import org.springframework.stereotype.Component;

@Component("CompareMac")
public class CompareMac implements IMacCallback {
	
	/**
	 * 生成MAC BLOCK 由于POS校验MAC使用分段异或结果的ASC码作为元数据。
	 * 因此先计算8字节的标准MAB，然后转换成16字节的asc码返回。 数据不足补充00
	 * 国密为16字节
	 * @param datasource
	 * @return
	 */
	public byte[] generateMAB(boolean sign,byte[] datasource) {
		// 补满8或16字节的字节数组
		byte[] temp = null;
		int len = 8;
		if(sign) {
			len = 16;
		}
		// 如果不满8或16字节，进行填充
		if (datasource.length % len != 0) {
			temp = new byte[datasource.length + (len - datasource.length % len)];
			System.arraycopy(datasource, 0, temp, 0, datasource.length);
		} else {
			temp = datasource;
		}
		// 每8或16字节进行异或
		byte[] tempResult = new byte[len];
		// 将首8或16个字节进行copy
		System.arraycopy(temp, 0, tempResult, 0, tempResult.length);
		for (int j = len; j < temp.length; j = j + len) {
			for (int k = 0; k < len; k++) {
				// 每字节进行异或
				tempResult[k] = (byte) (tempResult[k] ^ temp[j + k]);
			}
		}
		String result = HexBinary.encode(tempResult);
		return result.getBytes();
	}

	private int MACdata(IsoMessage iso, byte[] buffer) {
//		EncryptionAPI encryptionAPI = (EncryptionAPI)PospApplicationContext.getBean("EncryptionAPI");
		UnionAPI unionAPI = (UnionAPI)PospApplicationContext.getBean("UnionAPI");
		int macValue = 0;
		byte [] byteMacStr = new byte[buffer.length - 8];
		System.arraycopy(buffer, 0, byteMacStr, 0, byteMacStr.length);
//		String b = new String(generateMAB(iso.isStateKeySign(),byteMacStr));
		String b = HexCodec.hexEncode(generateMAB(iso.isStateKeySign(),byteMacStr));
		String encode = new String(HexBinary.decode(iso.getField(64)));
		if(!iso.isPlatform()){
			macValue = unionAPI.unionVerifyMac(iso.getField(32), b, encode);
		}else{
			macValue = unionAPI.unionVerifyMac(iso.getField(42) + iso.getField(41), b, encode);
		}
		return macValue;
	}

	@Override
	public String calculate(IMessage msg, PKT_DEF[] pktdef, byte[] buffer) {

		String macValue = null;
		IsoMessage iso = (IsoMessage) msg;
		String messageType = iso.getField(0);
		/**
		 * 管理类交易检查mac
		 */
		if ("0320".equals(messageType)
				|| "0500".equals(messageType)// 管理类
				|| "0620".equals(messageType) || "0800".equals(messageType)
				|| "0820".equals(messageType)) {
			return "";
		}
		int macData = MACdata(iso, buffer);
		if (macData != 0) {
			String errorCode;
			if(macData < 0 ){
				errorCode = String.valueOf(macData).substring(1);
			}else{
				errorCode = String.valueOf(macData);
			}
			throw new EncryptoMachineException(errorCode);
		} else{
			macValue = iso.getField(64);
		}
		return macValue;
	}

}
