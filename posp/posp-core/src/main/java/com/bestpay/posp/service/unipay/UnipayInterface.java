package com.bestpay.posp.service.unipay;

import com.bestpay.posp.protocol.IsoMessage;

public interface UnipayInterface {
	
	/**
	 * 设置发送银联报文
	 * @param request
	 * @return
	 */
	public IsoMessage createUpiso(IsoMessage request) throws Exception;

	/**
	 * 往银联发送报文
	 * @param request
	 * @return
	 */
	public IsoMessage call(IsoMessage request);
}
