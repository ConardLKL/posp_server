package com.bestpay.posp.protocol;


/*
 * @author   PengGuoYi  
 * @version  1.0  2014/06/13 
 */
public class PKT_DEF {
	PacketDefineType type;
	PacketDefineFormat format;
	PacketDefineSpec spec = PacketDefineSpec.NULL_VALUE;
	int length = -1;
	String memo;

	public void setType(PacketDefineType type) {
		this.type = type;
	}

	public PacketDefineType getType() {
		return this.type;
	}

	public void setFormat(PacketDefineFormat format) {
		this.format = format;
	}

	public PacketDefineFormat getFormat() {
		return this.format;
	}

	public void setSpec(PacketDefineSpec spec) {
		this.spec = spec;
	}

	public PacketDefineSpec getSpec() {
		return this.spec;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return this.length;
	}
	
	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getMemo() {
		return this.memo;
	}

}
