package com.bestpay.posp.utils.recombin;

import com.bestpay.posp.utils.StringToAscii;

import java.nio.ByteBuffer;

public class TLVEncoder {
	
	private static String CHARSET = "GBK";
	
	//TODO 暂且处理最多两个字节的情况
	public static byte[] encodeLen(int len) throws Exception{
		byte[] lenBuf;
		if(len < 0){
			throw new Exception("len value is:["+len+"] error!");
		}else if(len <= 127){
			lenBuf = HexBinary.decode(String.format("%0"+2+"x", len));
		}else{
			//TODO
			ByteBuffer buf = ByteBuffer.allocate(2);
			buf.put((byte)0x81);
			buf.put(intToHexToBCD(2,len));
			
			lenBuf = buf.array();
		}
		
		return lenBuf;
	}
	
	private static byte[] intToHexToBCD(int len,int value){
		return HexBinary.decode(String.format("%0"+len+"x", value));
	}
	
	/**
	 * 编码一个TLV域
	 * @param tag  标签（标签的16进制ASCII形式）
	 * @param value 值（如果压缩时需要填充，右补0）
	 * @param bcd   是否对该域的值进行压缩
	 * @return
	 * @throws Exception
	 */
	public static TLV genTLV(String tag, String value,boolean bcd) throws Exception{
		value = value.trim();
		//TODO 是否对标签补齐 怎么补齐？？
		if(tag.getBytes(CHARSET).length % 2 != 0){
			throw new Exception("tag is:["+tag+"] error!");
		}
		int valueLen = value.getBytes(CHARSET).length;
		if(bcd){
			if((valueLen % 2) == 0){
				valueLen = valueLen/2;
			}else{
				valueLen = valueLen/2 + 1;
				value += "0";
			}
		}
		byte[] tagBuf = HexBinary.decode(tag);
		byte[] lenBuf = encodeLen(valueLen);
		byte[] valueBuf = null;
		if(bcd){
			valueBuf = HexBinary.decode(value);
		}else{
			valueBuf = value.getBytes(CHARSET);
		}
		return new TLV(tagBuf, lenBuf, valueBuf);
	}

	/**
	 * 编码一个TLV域
	 * @param tag  标签（标签的16进制ASCII形式）
	 * @param value 值
	 * @return
	 * @throws Exception
	 */
	public static TLV genTLV(String tag, String value) throws Exception{
		value = value.trim();
		//TODO 是否对标签补齐 怎么补齐？？
		if(tag.getBytes(CHARSET).length % 2 != 0){
			throw new Exception("tag is:["+tag+"] error!");
		}
		int valueLen = value.getBytes(CHARSET).length;
		byte[] tagBuf = HexBinary.decode(StringToAscii.parseAscii(tag));
		byte[] lenBuf = HexBinary.decode(StringToAscii.parseAscii(String.format("%03x", valueLen)));
		byte[] valueBuf = HexBinary.decode(StringToAscii.parseAscii(value));
		return new TLV(tagBuf, lenBuf, valueBuf);
	}
	/**
	 * 编码一个TLV域
	 * @param tag  标签（标签的16进制ASCII形式）
	 * @param valueBuf 二进制值
	 * @return
	 * @throws Exception
	 */
	public static TLV genTLV(String tag,byte[] valueBuf) throws Exception{
		if(tag.getBytes(CHARSET).length % 2 != 0){
			throw new Exception("tag is:["+tag+"] error!");
		}
		int valueLen = valueBuf.length;
		
		byte[] tagBuf = HexBinary.decode(tag);
		byte[] lenBuf = encodeLen(valueLen);
		return new TLV(tagBuf, lenBuf, valueBuf);
	}
	
	/**
	 * 编码一个TLV域
	 * @param tag  标签
	 * @param valueBuf 二进制值
	 * @return
	 * @throws Exception
	 */
	public static TLV genTLV(byte tag,byte[] valueBuf) throws Exception{
		int valueLen = valueBuf.length;
		byte[] lenBuf = encodeLen(valueLen);
		byte[] tagBuf = new byte[]{tag};
		return new TLV(tagBuf, lenBuf, valueBuf);
	}
	
	public static TLV gen86TLV(byte tag,byte[] valueBuf) throws Exception{
		int valueLen = valueBuf.length;
		//TODO
		byte[] lenBuf = encodeLen(valueLen + 5);
		byte[] tagBuf = new byte[]{tag};
		return new TLV(tagBuf, lenBuf, valueBuf);
	}

}
