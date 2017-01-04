package com.bestpay.posp.protocol;

import java.io.UnsupportedEncodingException;

import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.remoting.transport.client.EncryptionAPI;
import com.bestpay.posp.spring.PospApplicationContext;

public class UnipayCombineMac implements IMacCallback {

	@Override
	public String calculate(IMessage msg, PKT_DEF[] pktdef, byte[] buffer) {
		String strText = "";
		String strVal = "";
		String macValue = "";
		for (int index = 0; index < pktdef.length; index++) {
			switch (index) {
			case 0:
			case 2:
			case 3:
			case 4:
			case 7:
			case 11:
			case 18:
			case 25:
			case 28:
			case 32:
			case 33:
			case 38:
			case 39:
			case 41:
			case 42:
				strVal = msg.getField2(index);
				if (strVal != null) {
					strText += strVal;
					strText += " ";
				}
				break;
			case 90: 
				strVal = msg.getField2(index);
				if (strVal != null) {
					strText += strVal.substring(0, 20);
					strText += " ";
				}
				break;
			case 102:
			case 103:
				strVal = msg.getField2(index);
				if (strVal != null) {
					strText += strVal;
					strText += " ";
				}
				break;
			default:
				break;
			}
		}
		strText = strText.trim();
//		EncryptionAPI encryptionAPI = (EncryptionAPI) PospApplicationContext.getBean("EncryptionAPI");
		UnionAPI unionAPI = (UnionAPI)PospApplicationContext.getBean("UnionAPI");
		String macVal2 = unionAPI.unionGenerateChinaPayMac(strText);
		
		macVal2 = macVal2.substring(0, 8);
		try {
			macValue = HexCodec.hexEncode(macVal2.getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return macValue;
	}
}
