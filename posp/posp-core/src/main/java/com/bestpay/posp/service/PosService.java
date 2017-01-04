package com.bestpay.posp.service;

import com.bestpay.posp.protocol.IsoMessage;

/**
 * @author yihaijun
 *
 */
public interface PosService {
	public IsoMessage invokeService(IsoMessage requestPosIso);
}
