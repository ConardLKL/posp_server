package com.bestpay.posp.utils.recombin;

import java.nio.ByteBuffer;

import org.springframework.stereotype.Component;
@Component
public class TLVDecoder {
	
	private static byte BASE_BYTE = 0X1f;
	
	public TLVList decode(ByteBuffer buf){
		TLVList list = new TLVList();
		while(buf.position() < buf.limit()){
			byte[] tag = decodeTag(buf);
			byte[] len = decodeLen(buf);
			int valueLen = hexBufferToInt(len);
			byte[] value = decodeValue(buf, valueLen);
			TLV tlv = new TLV(tag, len, value);
			list.add(tlv);
		}
		return list;
	}
	
	
	public static int hexBufferToInt(byte[] buf){
		return Integer.parseInt(HexBinary.encode(buf),16);
	}
	
	public static byte[] decodeValue(ByteBuffer buf,int valueLen){
		byte[] value = new byte[valueLen];
		buf.get(value);
		return value;
	}
	
	/**
	 * 解析一个TAG值
	 * 
	 *  tag标签的属性为bit，由16进制表示，占1～2个字节长度。
	 *  若tag标签的第一个字节（注：字节排序方向为从
	 *	左往右数，第一个字节即为最左边的字节。bit排序规则同理。）的后五个bit为“11111”，则说明该
	 *	tag占两个字节，；否则占一个字节。
	 *  例如，“9F33”为一个占用两个字节的tag标签。而“95”为一个占用一个字节的tag标签。
	 * @param buf   
	 * @return
	 */
	public static byte[] decodeTag(ByteBuffer buf){
		byte[] tag;
		byte bt = buf.get();
		buf.position(buf.position() - 1);
		if(((bt & BASE_BYTE) ^ BASE_BYTE) == 0){
			tag = new byte[2];
			buf.get(tag);
		}else{
			tag = new byte[1];
			buf.get(tag);
		}
		return tag;
	}
	
	/**
	 * 解析长度属性
	 * 当 L 字段最左边字节的最左bit 位（即bit8）为0，表示该L 字段占一个字节，它的后续
	 *  7 个bit 位（即bit7～bit1）表示子域取值的长度，采用二进制数表示子域取值长度的十
	 *  进制数。例如，某个域取值占3 个字节，那么其子域取值长度表示为“00000011”。
	 * 
	 * 当 L 字段最左边字节的最左bit 位（即bit8）为1，表示该L 字段不止占一个字节，那么
	 * 它到底占几个字节由该最左字节的后续7 个bit 位（即bit7～bit1）的十进制取值表示。例
	 * 如，若最左字节为10000010，表示L 字段除该字节外，后面还有两个字节。
	 * 例如，若L 字段为“1000 0001 1111 1111”，表示该子域取值占255 个字节
	 * @param buf
	 * @return
	 */
	public static byte[] decodeLen(ByteBuffer buf){
		byte[] len ;
		byte bt = buf.get();
		if(((bt >> 7) & 1) == 0){
			len = new byte[1];
			buf.position(buf.position() - 1);
			buf.get(len);
		}else{
			int ln = bt & 0X7f;
			len = new byte[ln];
			buf.get(len);
		}
		
		return len;
	}
	

}
