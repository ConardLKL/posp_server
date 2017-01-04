package com.bestpay.posp.test;

import com.bestpay.posp.protocol.*;
import com.bestpay.posp.service.impl.BaseStationInfo;

public class CompareMac  implements IMacCallback {

	@Override
	public String calculate(IMessage msg, PKT_DEF[] pktdef, byte[] buffer) {
		throw new InvalidMacException("InvalidMacException");
		//return "";
	}
}
