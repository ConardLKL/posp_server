package com.bestpay.posp.remoting.transport.client;

import com.bestpay.posp.protocol.IsoMessage;

public interface ClientCall {

	public IsoMessage call(IsoMessage in, int timeout) throws Exception;
}
