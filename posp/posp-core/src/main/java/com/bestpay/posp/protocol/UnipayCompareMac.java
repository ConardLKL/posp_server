package com.bestpay.posp.protocol;

import lombok.extern.slf4j.Slf4j;
import com.bestpay.posp.remoting.transport.client.EncryptionAPI;
import com.bestpay.posp.spring.PospApplicationContext;

@Slf4j
public class UnipayCompareMac  implements IMacCallback {

	@Override
	public String calculate(IMessage msg, PKT_DEF[] pktdef, byte[] buffer)  {
		String strText = "";
		String strVal = "";
		String macValue = "";
		int i = -1;
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
			case 90:
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
		
		log.debug("MAC_STR:[" + strText + "]");
		
		EncryptionAPI encryptionAPI = (EncryptionAPI)PospApplicationContext.getBean("EncryptionAPI");
		i = encryptionAPI.UnionVerifyChinaPayMac(strText.length(), strText, msg.getField(128));
		
		if(i >= 0){
			macValue = msg.getField(128);
		}else{
			throw new EncryptoMachineException(String.format("EXCEPTION: INVALID_MAC. Calc:[%s] TERM:[%s]",macValue,msg.getField(128)));	
		}
		
//		macValue = "AAAABBBBCCCCDDDD";
		return macValue;
	}

}
