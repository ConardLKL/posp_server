package com.bestpay.posp.service.unipay;

import com.bestpay.posp.protocol.IsoMessage;

public interface MessageTranslationInterface {
	
	/**
	 * 终端报文转成银联报文
	 * @param iso
	 * @return
	 */
	public IsoMessage posToUnion(IsoMessage iso) throws Exception;

	/**
	 * 银联报文转成终端报文
	 * @param iso
	 * @return
	 */
	public IsoMessage unionToPos(IsoMessage iso) throws Exception;
}
