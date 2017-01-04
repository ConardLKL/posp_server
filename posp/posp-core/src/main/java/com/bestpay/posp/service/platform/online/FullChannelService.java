package com.bestpay.posp.service.platform.online;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.bestpay.posp.protocol.IsoMessage;

public interface FullChannelService {

	public Map<String, String> setFormDate(IsoMessage iso) throws UnsupportedEncodingException, IOException ;
	public Map<String, String> service(IsoMessage iso) throws UnsupportedEncodingException, IOException; 
}
