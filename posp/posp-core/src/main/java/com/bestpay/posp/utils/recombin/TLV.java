package com.bestpay.posp.utils.recombin;

import java.nio.ByteBuffer;

/**
 * 
 * 标识一个TLV(Tag,Length,Value)域
 *
 */
public class TLV {

	private byte[] tag;
	
	private byte[] len;

	private byte[] value;

	/**
	 * TLV域的二进制Buffer，包含TAG、LENGTH和VALUE三者的值
	 */
	private byte[] data;   
	
	public TLV(byte[] tag, byte[] len, byte[] value) {
		this.tag = tag;
		this.len = len;
		this.value = value;
		setData();
	}

	public void setTag(byte[] tag) {
		this.tag = tag;
	}

	public void setLen(byte[] len) {
		this.len = len;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public byte[] toBinary() {
		return data;
	}


	public byte[] getTag(){
		return this.tag;
	}
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getLen() {
		return len;
	}

	public byte[] getValue() {
		return value;
	}

	private void setData() {
		ByteBuffer tempData = ByteBuffer.allocate(tag.length + len.length
				+ value.length);
		tempData.put(tag);
		tempData.put(len);
		tempData.put(value);
		this.data = tempData.array();
	}

}
