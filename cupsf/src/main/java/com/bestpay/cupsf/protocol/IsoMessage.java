package com.bestpay.cupsf.protocol;

import com.bestpay.cupsf.entity.PKT_DEF;
import com.bestpay.cupsf.utils.HexCodec;
import com.bestpay.cupsf.utils.StrUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import static com.bestpay.cupsf.protocol.PacketDefineSpec.*;

/**
 * Created by HR on 2016/5/17.
 */
@Setter
@Getter
@Slf4j
public class IsoMessage implements Cloneable {

	/*********************** IsoMessage 域转化属性 ***********************************/
//	private boolean markCalcMac = false;
	private boolean compress = false;
	private String encoding = "GBK";
	private Map<Integer, String> hashMapField = new HashMap<Integer, String>();

	PKT_DEF[] pktdef;
	private byte[] byteMSG = null;
	private ChannelHandlerContext channelHandlerContext;
	private Map<String, String> transformerTmpField = new HashMap<String, String>();

	/*********************** 交易流程公共参数 ***********************************/

	public IsoMessage clone() {
		IsoMessage cl = null;
		try {
			cl = (IsoMessage) super.clone();
			cl.setMessageDefine(this.getMessageDefine());
			cl.setMessage(this.getMessage());
			cl.getTransformerTmpField().putAll(this.getTransformerTmpField());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cl;
	}

	/**
	 * setLengthValueCompress 设置TYP_ASC类型字段为 LVAR LLVAR LLLVAR等格式的长度位是否BCD压缩表示
	 * 
	 * @param compress true : LVAR LLVAR 长度位为一字节，LLLVAR 长度为为两个字节 BCD码表示
	 *        false : LVAR LLVAR LLLVAR 长度位为1、2、3 字节，ASC码表示
	 */
	public void setLengthValueCompress(boolean compress) {
		this.compress = compress;
	}

	/**
	 * setMessageDefine 设置8583的数据定义
	 * 
	 * @param def 8583数据定义的记录数组，必须长度 必须为 65（64 + 1） 或者 129（128 + 1）
	 * @throws Exception
	 *             if an error occurred
	 */
	public void setMessageDefine(PKT_DEF[] def) throws Exception {
		if (def == null)
			throw new NullPointerException(
					"FORMAT_EXCEPTION: ISO8583数据定义配置不能为null。");
		if (!(def.length == (128 + 1)))
			throw new Exception(
					"FORMAT_EXCEPTION: ISO8583数据定义异常。PKT_DEF数组长度必须为(64 + 1)或(128 + 1)。");
		pktdef = def;
	}

	public PKT_DEF[] getMessageDefine() {
		return pktdef;
	}

	/**
	 * setMessage 设置8583的数据,用户解析数据包
	 * 
	 * @param data 数据包内容。格式为GBK的字节流
	 * @throws Exception
	 *             if an error occurred
	 */
	public void setMessage(byte[] data) throws Exception {
		byteMSG = data;
		setMessageWithHex(HexCodec.hexEncode(data));
	}

	/**
	 * setMessageWithHex 设置8583的数据,用户解析数据包（格式为16进制字符串）
	 * 
	 * @param message 数据包内容。格式为16进制字符串。
	 * @throws Exception
	 *             if an error occurred
	 */
	public void setMessageWithHex(String message) throws Exception {
		int pos = 0;
		int len;
		String msg_type = null;
		String bitstr = null;
		String strVal = null;
		String strLen = null;
		if (pktdef == null)
			throw new NullPointerException("FORMAT_EXCEPTION: 未设置ISO8583数据包定义！");

		int start = pktdef[0].getLength();
		msg_type = message.substring(0, start);

		int x = 0;
		pos = 0;
		switch (pktdef[x].getType()) {
		case TYP_BCD:
		case TYP_BIT:
			switch (pktdef[x].getFormat()) {
			case FMT_FIXED:
				msg_type = message.substring(pos, pos + pktdef[x].getLength());
				pos += pktdef[x].getLength();
				break;

			default:
				throw new Exception("FORMAT_EXCEPTION: 位图消息类型定义错误！");

			}
			break;

		case TYP_ASC:
		case TYP_ASC2:
			switch (pktdef[x].getFormat()) {
			case FMT_FIXED:
				strVal = message
						.substring(pos, pos + pktdef[x].getLength() * 2);
				msg_type = new String(HexCodec.hexDecode(strVal), encoding);
				pos += pktdef[x].getLength() * 2;
				break;
			default:
				throw new Exception("FORMAT_EXCEPTION: 位图消息类型定义错误！");
			}
			break;
			
		default:
			break;
		}

		if (pktdef.length == 128 + 1) {
			bitstr = message.substring(pos, pos + 32);
			pos = pos + 32;
		}

		int bit_fmt[] = { 0x80, 0x40, 0x20, 0x10, 0x8, 0x4, 0x2, 0x1 };
		byte[] bitmap = HexCodec.hexDecode(bitstr);
		hashMapField.clear();
		hashMapField.put(0, msg_type);
		hashMapField.put(1, bitstr);
		for (int byidx = 1, index = 0, bitidx = 1; byidx < pktdef.length; byidx += 8) {
			
			int val = bitmap[index] & 0xFF;
			for (int idx = 0; idx < 8; idx++) {
				int flag = (val & bit_fmt[idx]) & 0xFF;
				if (flag > 0 && bitidx > 1) {
					switch (pktdef[bitidx].getType()) {
					case TYP_BCD: {
						switch (pktdef[bitidx].getFormat()) {
						case FMT_FIXED:
							if (pktdef[bitidx].getLength() % 2 == 0) {
								// 偶数
								strVal = message.substring(pos, pos
										+ pktdef[bitidx].getLength());
								pos += pktdef[bitidx].getLength();
							} else {
								// 奇数
								if(pktdef[bitidx].getSpec() == L_ZERO)
								{
									pos += 1;
									strVal = message.substring(pos, pos
											+ pktdef[bitidx].getLength());
									pos += pktdef[bitidx].getLength();
								}else{
									//default : R_ZERO
									strVal = message.substring(pos, pos
											+ pktdef[bitidx].getLength());
									pos += pktdef[bitidx].getLength();
									pos += 1;
									
								}
							}
							break;

						case FMT_LVAR:
						case FMT_LLVAR:
							strLen = message.substring(pos, pos + 2);
							pos += 2;
							len = Integer.parseInt(strLen);
							strVal = message.substring(pos, pos + len);
							pos += len;
							if (len % 2 != 0) {
								pos += 1;
							}
							break;

						case FMT_LLLVAR:
							strLen = message.substring(pos, pos + 4);
							pos += 4;
							len = Integer.parseInt(strLen);
							strVal = message.substring(pos, pos + len);
							pos += len;
							if (len % 2 != 0) {
								pos += 1;
							}
							break;
						default:
							break;
						}
						break;
					}

					case TYP_BIT:
					case TYP_BIN: {
						switch (pktdef[bitidx].getFormat()) {
						case FMT_FIXED:
							strVal = message.substring(pos, pos
									+ pktdef[bitidx].getLength() * 2);
							pos += pktdef[bitidx].getLength() * 2;
							break;

						case FMT_LVAR:
						case FMT_LLVAR:
							strLen = message.substring(pos, pos + 2);
							pos += 2;
							len = Integer.parseInt(strLen);
							strVal = message.substring(pos, pos + len);
							pos += len;
							if (len % 2 != 0) {
								pos += 1;
							}
							break;

						case FMT_LLLVAR:
							strLen = message.substring(pos, pos + 4);
							pos += 4;
							len = Integer.parseInt(strLen);
							strVal = message.substring(pos, pos + len);
							pos += len;
							if (pktdef[bitidx].getLength() % 2 != 0) {
								pos += 1;
							}
							break;
						default:
							break;
						}
						break;
					}
					case TYP_ASC: {
						switch (pktdef[bitidx].getFormat()) {
						case FMT_FIXED:
							len = pktdef[bitidx].getLength() * 2;
							strVal = message.substring(pos, pos + len);
							strVal = new String(HexCodec.hexDecode(strVal),
									encoding);
							pos += len;
							switch (pktdef[bitidx].getSpec()) {
								case L_BLANK:
									strVal = StrUtil.leftTrim(strVal, " ");
									break;
								case R_BLANK:
									strVal = StrUtil.rightTrim(strVal, " ");
									break;
								case L_ZERO:
									strVal = StrUtil.leftTrim(strVal, "0");
									break;
								case R_ZERO:
									// 去掉空格或者零
									strVal = StrUtil.rightTrim(strVal, "0");
									break;
								default:
									break;
							}

							break;

						case FMT_LVAR:
							strLen = message.substring(pos, pos + 2);
							pos += 2;
							if (compress == true) {
								len = Integer.parseInt(strLen) * 2;
							} else {
								len = Integer.parseInt(new String(HexCodec
										.hexDecode(strLen), encoding)) * 2;
							}
							strVal = message.substring(pos, pos + len);
							strVal = new String(HexCodec.hexDecode(strVal),
									encoding);
							pos += len;
							break;

						case FMT_LLVAR:
							if (compress == true) {
								strLen = message.substring(pos, pos + 2);
								pos += 2;
								len = Integer.parseInt(strLen) * 2;
							} else {
								strLen = message.substring(pos, pos + 4);
								pos += 4;

								len = Integer.parseInt(new String(HexCodec
										.hexDecode(strLen), encoding)) * 2;
							}
							strVal = message.substring(pos, pos + len);
							strVal = new String(HexCodec.hexDecode(strVal),
									encoding);
							pos += len;
							break;

						case FMT_LLLVAR:
							if (compress == true) {
								strLen = message.substring(pos, pos + 4);
								pos += 4;
								len = Integer.parseInt(strLen) * 2;
								strVal = message.substring(pos, pos + len);
								strVal = new String(
										HexCodec.hexDecode(strVal),
										encoding);
								pos += len;
								if(len % 2 == 1)
									pos += 1;

							} else {
								strLen = message.substring(pos, pos + 6);
								pos += 6;
								len = Integer.parseInt(new String(HexCodec
										.hexDecode(strLen), encoding)) * 2;
								strVal = message.substring(pos, pos + len);
								strVal = new String(
										HexCodec.hexDecode(strVal),
										encoding);
								pos += len;
							}
							break;
						default:
							break;
						}
						break;
					}
						case TYP_ASC2:{
							switch (pktdef[bitidx].getFormat()) {
								case FMT_FIXED:
									len = pktdef[bitidx].getLength() * 2;
									strVal = message.substring(pos, pos + len);
									strVal = new String(HexCodec.hexDecode(strVal),
											encoding);
									pos += len;
									switch (pktdef[bitidx].getSpec()) {
										case L_BLANK:
											strVal = StrUtil.leftTrim(strVal, " ");
											break;
										case R_BLANK:
											strVal = StrUtil.rightTrim(strVal, " ");
											break;
										case L_ZERO:
											strVal = StrUtil.leftTrim(strVal, "0");
											break;
										case R_ZERO:
											strVal = StrUtil.rightTrim(strVal, "0");
											break;
										default:
											break;
									}
									break;

								case FMT_LVAR:
									strLen = message.substring(pos, pos + 2);
									pos += 2;
									if (compress == true) {
										len = Integer.parseInt(strLen) * 2;
										strVal = message.substring(pos, pos + len);
									} else {
										len = Integer.parseInt(new String(HexCodec
												.hexDecode(strLen), encoding)) * 2;
										strVal = message.substring(pos, pos + len);
										strVal = new String(HexCodec.hexDecode(strVal),
												encoding);
									}
									pos += len;
									break;

								case FMT_LLVAR:
									if (compress == true) {
										strLen = message.substring(pos, pos + 2);
										pos += 2;
										len = Integer.parseInt(strLen) * 2;
										strVal = message.substring(pos, pos + len);
									} else {
										strLen = message.substring(pos, pos + 4);
										pos += 4;
										len = Integer.parseInt(new String(HexCodec
												.hexDecode(strLen), encoding)) * 2;
										strVal = message.substring(pos, pos + len);
										strVal = new String(HexCodec.hexDecode(strVal),
												encoding);
									}
									pos += len;
									break;

								case FMT_LLLVAR:
									if (compress == true) {
										strLen = message.substring(pos, pos + 4);
										pos += 4;
										len = Integer.parseInt(strLen) * 2;
										strVal = message.substring(pos, pos + len);
									} else {
										strLen = message.substring(pos, pos + 6);
										pos += 6;
										len = Integer.parseInt(new String(HexCodec
												.hexDecode(strLen), encoding)) * 2;
										strVal = message.substring(pos, pos + len);
									}
									pos += len;
									break;
								default:
									break;
							}
							break;
						}
					default:
						break;
					}

					hashMapField.put(bitidx, strVal);
				}
				strVal = null;
				bitidx++;
			}
			index++;
		}
	}

	/**
	 * getMessage 获取8583包的数据包内容，格式为GBK的字节流。
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	public byte[] getMessage() throws Exception {
		String message = getMessageWithHex();
//		if (markCalcMac == false) {
//			message = getMessageWithHex();
//			markCalcMac = true;
//		}
		return HexCodec.hexDecode(message);
	}

	/**
	 * MergeStr 根据每个域的定义，合并该域的字符串，一般用于BCD/BIN 格式
	 * 
	 * @param str 该域的字符串（十六进制形式）。
	 * @throws Exception
	 *             if an error occurred
	 */
	private String MergeStr(PacketDefineFormat format, String str, int index,
			String tailflag) throws Exception {
		String message = "";
		int len = str.getBytes(encoding).length;
		switch (format) {
		case FMT_FIXED:
			switch (pktdef[index].getType()) {
			case TYP_BIN:
			case TYP_BIT:
				if (len != pktdef[index].getLength() * 2)
					throw new Exception("FORMAT_EXCEPTION: 第[" + index
							+ "]域数据异常。INFO=>FIXED[" + pktdef[index].getLength()
							+ "] DATA:[" + str + "](" + len + ")");
				break;
			case TYP_BCD:
			default:
				if (len != pktdef[index].getLength())
					throw new Exception("FORMAT_EXCEPTION: 第[" + index
							+ "]域数据异常。INFO=>FIXED[" + pktdef[index].getLength()
							+ "] DATA:[" + str + "](" + len + ")");
				break;
			}

			break;

		case FMT_LVAR:
			if (len > pktdef[index].getLength())
				throw new Exception("FORMAT_EXCEPTION: 第[" + index
						+ "]域数据异常。INFO=>LVAR[" + pktdef[index].getLength()
						+ "] DATA:[" + str + "](" + len + ")");
			message += String.format("%02d", str.length());
			break;

		case FMT_LLVAR:
			if (len > pktdef[index].getLength())
				throw new Exception("FORMAT_EXCEPTION: 第[" + index
						+ "]域数据异常。INFO=>LLVAR[" + pktdef[index].getLength()
						+ "] DATA:[" + str + "](" + len + ")");

			message += String.format("%02d", str.length());
			break;

		case FMT_LLLVAR:
			if (len > pktdef[index].getLength())
				throw new Exception("FORMAT_EXCEPTION: 第[" + index
						+ "]域数据异常。INFO=>LLLVAR[" + pktdef[index].getLength()
						+ "] DATA:[" + str + "](" + len + ")");
			message += String.format("%04d", str.length());
			break;
		default:
			throw new Exception("FORMAT_EXCEPTION: 第[" + index
					+ "]域数据异常。INFO=>unknow format[" + pktdef[index].getLength()
					+ "] DATA:[" + str + "](" + len + ")");
		}

		if (len % 2 == 0) {
			message += str;
		} else {
			
			switch (pktdef[index].getType()) {
			case TYP_BIN:
			case TYP_BIT:
			case TYP_BCD:
				if(pktdef[index].getSpec() == L_ZERO){
					message += tailflag;
					message += str;					
				}
				else{
					message += str;
					message += tailflag;
				}
				break;
			default:
				message += str;
				message += tailflag;
				break;
			}
		}
		return message;
	}

	/**
	 * MergeStr 根据每个域的定义，合并该域的字符串，一般用于ASC 格式
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	private String MergeASCStr(PacketDefineFormat format,
			PacketDefineSpec spec, String str, int index) throws Exception {
		String message = "";
		int len = str.getBytes(encoding).length;

		switch (format) {
		case FMT_FIXED:
			switch (spec) {
			case L_BLANK:
			case R_BLANK:
			case L_ZERO:
			case R_ZERO:
				if (len > pktdef[index].getLength())
					throw new Exception("FORMAT_EXCEPTION: 第[" + index
							+ "]域数据异常。INFO=>FIXED[" + pktdef[index].getLength()
							+ "] DATA:[" + str + "](" + len + ")");

				str = FormatString(str, pktdef[index].getLength(), spec);
				break;

			default:
				if (len != pktdef[index].getLength())
					throw new Exception("FORMAT_EXCEPTION: 第[" + index
							+ "]域数据异常。INFO=>FIXED[" + pktdef[index].getLength()
							+ "] DATA:[" + str + "](" + len + ")");
				break;

			}
			break;

		case FMT_LVAR:
			if (len > pktdef[index].getLength())
				throw new Exception("FORMAT_EXCEPTION: 第[" + index
						+ "]域数据异常。INFO=>LVAR[" + pktdef[index].getLength()
						+ "] DATA:[" + str + "](" + len + ")");
			if (compress == true) {
				message += String.format("%02d", len);
			} else {
				message += HexCodec.hexEncode(String.format("%d", len)
						.getBytes(encoding));
			}
			break;

		case FMT_LLVAR:
			if (len > pktdef[index].getLength())
				throw new Exception("FORMAT_EXCEPTION: 第[" + index
						+ "]域数据异常。INFO=>LLVAR[" + pktdef[index].getLength()
						+ "] DATA:[" + str + "](" + len + ")");
			if (compress == true) {
				message += String.format("%02d", len);
			} else {
				message += HexCodec.hexEncode(String.format("%02d", len)
						.getBytes(encoding));
			}
			break;

		case FMT_LLLVAR:
			if (len > pktdef[index].getLength())
				throw new Exception("FORMAT_EXCEPTION: 第[" + index
						+ "]域数据异常。INFO=>LLLVAR[" + pktdef[index].getLength()
						+ "] DATA:[" + str + "](" + len + ")");
			if (compress == true) {
				message += String.format("%04d", len);
			} else {
				message += HexCodec.hexEncode(String.format("%03d", len)
						.getBytes(encoding));
			}
			break;
		default:
			throw new Exception("FORMAT_EXCEPTION: 第[" + index
					+ "]域数据异常。INFO=>unknow format[" + pktdef[index].getLength()
					+ "] DATA:[" + str + "](" + len + ")");
		}

		message += HexCodec.hexEncode(str.getBytes(encoding));
		return message;
	}

	/**
	 * getMessageWithHex 获取8583包的数据包内容。格式为16进制的字符串。
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	public String getMessageWithHex() throws Exception {
		String message = "";
		String tailflag = "0";
		String str = null;

		int bit[] = { 0x80, 0x40, 0x20, 0x10, 0x8, 0x4, 0x2, 0x1 };
		byte[] mapbit = null;
		int bitval = 0;
		if (pktdef.length == 128 + 1) {
			mapbit = new byte[16];
			bitval = bitval | 0x80;
		}

		for (int index = 0, byte_idx = 0; index < pktdef.length; index++) {
			if (index == 1) {
				message += "ABCDEFGHIJKLMNOPABCDEFGHIJKLMNOP";
				continue;
			}

			str = hashMapField.get(index);
			if (str == null) {
				if (index % 8 == 0) {
					mapbit[byte_idx++] = (byte) bitval;
					bitval = 0;
				}
				continue;
			}

			if (index > 1) {
				if (pktdef[index].length == 0)
					throw new Exception("FORMAT_EXCEPTION: 第[" + index
							+ "]域数据未定义。");
				else {
					bitval = bitval | bit[(index - 1) % 8];
					if (index % 8 == 0) {
						mapbit[byte_idx++] = (byte) bitval;
						bitval = 0;
					}
				}
			}

			switch (pktdef[index].getType()) {
			case TYP_BIT:
			case TYP_BCD:
			case TYP_BIN:
				message += MergeStr(pktdef[index].getFormat(), str, index,
						tailflag);
				break;
			case TYP_ASC:
				message += MergeASCStr(pktdef[index].getFormat(),
						pktdef[index].getSpec(), str, index);
				break;
			default:
				break;
			}
		}

		if (pktdef.length == 128 + 1)
			message = message.replaceFirst("ABCDEFGHIJKLMNOPABCDEFGHIJKLMNOP",
					HexCodec.hexEncode(mapbit));

		return message;
	}

	/**
	 * FormatString 格式化字符串。
	 * 
	 * @param message 需要格式化字符串
	 * @param fmt_len 格式后的长度(GBK格式的长度)
	 * @param spec L_BLANK: 左补空格 R_BLANK: 右补空格 L_ZERO: 左补字符零 R_ZERO: 右补字符零
	 */
	private String FormatString(String message, int fmt_len,
			PacketDefineSpec spec) {
		String text = "";
		int len = 0;

		try {
			len = message.getBytes(encoding).length;
			// 如果格式后的长度比msg长度还短就直接返回原msg
			if (fmt_len <= len)
				return message;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return message;
		}

		switch (spec) {
		case L_BLANK:
			for (int i = 0; i < fmt_len - len; i++) {
				text = " " + text;
			}
			text = text + message;
			break;

		case R_BLANK:
			for (int i = 0; i < fmt_len - len; i++) {
				text = " " + text;
			}
			text = message + text;
			break;

		case L_ZERO:
			for (int i = 0; i < fmt_len - len; i++) {
				text = "0" + text;
			}
			text = text + message;
			break;

		case R_ZERO:
			for (int i = 0; i < fmt_len - len; i++) {
				text = "0" + text;
			}
			text = message + text;
			break;
		default:
			return message;
		}
		return text;
	}

	/**
	 * getField 获取某域的值。
	 * 
	 * @param index 第几域
	 * @throws Exception
	 *             if an error occurred
	 */
	public String getField(int index) {
		return hashMapField.get(index);
	}

	/**
	 * setField 设置某域的值。
	 * 
	 * @param index 第几域
	 * @param value 该域对应的值
	 * @throws Exception
	 *             if an error occurred
	 */
	public void setField(int index, String value) throws Exception {
		if (index >= pktdef.length || pktdef[index].length == 0)
			throw new Exception("FORMAT_EXCEPTION: 第[" + index + "]域数据未定义。");
//		markCalcMac = false;
		hashMapField.put(index, value);
	}

	/**
	 * printMessage 打印8583包内所有域的内容，敏感字段将会用字符#替换输出
	 * 
	 * @param id 一般为交易流水标识
	 * @throws Exception
	 *             if an error occurred
	 */
	public void printMessage(String id) {
		String vt = null;
		String val = null;
		if (!log.isInfoEnabled()) {
			return;
		}
		log.info(String.format(
				"[%s]  *******************************************", id));
		for (int index = 0; index < pktdef.length; index++) {
			val = hashMapField.get(index);
			if (val != null){
				if(index == 2 ){
					vt = val.substring(0,6);
					for(int n = 7 ; n < val.length() - 4 ; n ++)
						vt += "*";
					vt += val.substring(val.length() - 4);
					log.info(String.format("[%s]  [%2d]: <%s>", id, index, vt));
				}else if(index == 14 || index == 35 || index == 36  || index == 52
						|| index == 55 || index == 102 || index == 103  ){
					vt = "";
					for(int n = 0 ; n < val.length()  ; n ++)
						vt += "*";
					log.info(String.format("[%s]  [%2d]: <%s>", id, index, vt));
				}
				else 
					log.info(String.format("[%s]  [%2d]: <%s>", id, index, val));
			}
		}
		log.info(String.format(
				"[%s]  *******************************************", id));

	}
}
