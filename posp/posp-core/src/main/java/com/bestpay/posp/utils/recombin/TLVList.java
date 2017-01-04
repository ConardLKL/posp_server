package com.bestpay.posp.utils.recombin;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 用于存放一组TLV
 *
 */
public class TLVList {
	
	private List<TLV> list = new ArrayList<TLV>();
	
	
	public void add(TLV tlv){
		list.add(tlv);
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public byte[] toBinary() throws Exception{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for(int i=0; i<list.size(); i++){
			TLV tlv = list.get(i);
			out.write(tlv.toBinary());
		}
		return out.toByteArray();
	}
	
	/**
	 * 根据tag获取tlv
	 */
	public TLV get(String tag) {
		for (TLV tlv:list) {
			if (HexBinary.encode(tlv.getTag()).equals(tag)) {
				return tlv;
			}
		}
		return null;
	}

}
