package com.bestpay.posp.test;

import java.io.UnsupportedEncodingException;

import com.bestpay.posp.protocol.IMacCallback;
import com.bestpay.posp.protocol.IMessage;
import com.bestpay.posp.protocol.PKT_DEF;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CombineMac implements IMacCallback {

	@Override
	public String calculate(IMessage msg, PKT_DEF[] pktdef, byte[] buffer) {
		String strText = "";
		String strVal = "";
		for (int index = 0; index < pktdef.length; index++) {
			switch (index) {
			case 0:
			case 3:
			case 4:
			case 25:
			case 37:
			case 39:
			case 41:
			case 42:
				strVal = msg.getField(index);
				if (strVal != null) {
					strText += strVal;
					strText += " ";
				}
				break;

			case 2:
				try {
					strVal = msg.getField(index);
					if (strVal != null) {

						strText += String.format("%02d",
								strVal.getBytes("GBK").length);
						strText += strVal;
						strText += " ";
					}

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		}
		log.debug("MAC_STR:[" + strText + "]");
		strText = "AAAABBBBCCCCDDDD";
		return strText;
	}
}
