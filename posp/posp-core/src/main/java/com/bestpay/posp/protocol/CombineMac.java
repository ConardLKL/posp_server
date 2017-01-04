package com.bestpay.posp.protocol;

import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.utils.recombin.HexBinary;
import org.springframework.stereotype.Component;

@Component("CombineMac")
public class CombineMac implements IMacCallback {

	@Override
	public String calculate(IMessage msg, PKT_DEF[] pktdef, byte[] buffer) {

		IsoMessage iso = (IsoMessage) msg;
		String data = null;
		try {
			data = msg.getMessageWithHex();
		} catch (Exception e) {
			e.printStackTrace();
			throw new EncryptoMachineException(String.format("EXCEPTION: GENERATE MAC ERROR."));
		}
		byte[] datasource = HexCodec.hexDecode(data.substring(0, data.length()-16));
		// 补满8或16字节的字节数组
		int len = 8;
		if(iso.isStateKeySign()) {
			len = 16;
		}
		byte[] temp = null;
		// 如果不满8或16字节，进行填充
		if (datasource.length%len != 0) {
			temp = new byte[datasource.length + (len- datasource.length%len)];
			System.arraycopy(datasource, 0, temp, 0, datasource.length);
		} else {
			temp = datasource;
		}
		// 每8或16字节进行异或
		byte[] tempResult = new byte[len];
		// 将首8或16个字节进行copy
		System.arraycopy(temp, 0, tempResult, 0,tempResult.length);
		for (int j=len;j<temp.length;j=j+len) {
			for(int k=0;k<len;k++) {
				//每字节进行异或
				tempResult[k] = (byte) (tempResult[k]^temp[j+k]);
			}
		}
		byte[] packetStr = HexBinary.encode(tempResult).getBytes();
		String macvalue = new String(packetStr);
//		EncryptionAPI encryptionAPI = (EncryptionAPI)PospApplicationContext.getBean("EncryptionAPI");
		UnionAPI unionAPI = (UnionAPI)PospApplicationContext.getBean("UnionAPI");
		String mac = unionAPI.unionGenerateMac(msg.getField(42)+msg.getField(41), macvalue).substring(0, 8);
		String macHex = HexBinary.encode(mac.getBytes());
		if (macHex.length() != 16) {
			throw new EncryptoMachineException(String.format(
					"EXCEPTION: GENERATE MAC ERROR. MACCHINE RETURN VALUE:[%s]",macHex));
		} 
		return macHex;
	}
	//第三方组成MAC使用原始32域
	public String calculate(IsoMessage msg) {
		String data = null;
		try {
			data = msg.getMessageWithHex();
		} catch (Exception e) {
			e.printStackTrace();
			throw new EncryptoMachineException(String.format("EXCEPTION: GENERATE MAC ERROR."));
		}
		byte[] datasource = HexCodec.hexDecode(data.substring(0, data.length()-16));
		// 补满8字节的字节数组
		byte[] temp = null;
		int len = 8;
		if(msg.isStateKeySign()) {
			len = 16;
		}
		// 如果不满8字节，进行填充
		if (datasource.length%len != 0) {
			temp = new byte[datasource.length + (len- datasource.length%len)];
			System.arraycopy(datasource, 0, temp, 0, datasource.length);
		} else {
			temp = datasource;
		}
		// 每8字节进行异或
		byte[] tempResult = new byte[len];
		// 将首8个字节进行copy
		System.arraycopy(temp, 0, tempResult, 0,tempResult.length);
		for (int j=len;j<temp.length;j=j+len) {
			for(int k=0;k<len;k++) {
				//每字节进行异或
				tempResult[k] = (byte) (tempResult[k]^temp[j+k]);
			}
		}
		byte[] packetStr = HexBinary.encode(tempResult).getBytes();
		String macvalue = new String(packetStr);
//		String macvalue = HexCodec.hexEncode(packetStr);
//		EncryptionAPI encryptionAPI = (EncryptionAPI)PospApplicationContext.getBean("EncryptionAPI");
		UnionAPI unionAPI = (UnionAPI)PospApplicationContext.getBean("UnionAPI");
		String mac = unionAPI.unionGenerateMac(msg.getFlow().getField32(), macvalue).substring(0, 8);
		String macHex = HexBinary.encode(mac.getBytes());
		if (macHex.length() != 16) {
			throw new EncryptoMachineException(String.format(
					"EXCEPTION: GENERATE MAC ERROR. MACCHINE RETURN VALUE:[%s]",macHex));
		} 
		return macHex;
	}
}
