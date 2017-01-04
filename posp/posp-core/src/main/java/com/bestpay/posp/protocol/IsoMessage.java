package com.bestpay.posp.protocol;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.system.entity.SalesSlip;
import lombok.extern.slf4j.Slf4j;
import com.bestpay.posp.protocol.util.StrUtil;
import com.bestpay.posp.system.entity.PospMessage;

/*
 * @author   PengGuoYi  
 * @version  1.0  2014/06/13 
 */
@Slf4j
public class IsoMessage implements IMessage, Cloneable {

	/*********************** IsoMessage 域转化属性 ***********************************/
	private boolean markCalcMac = false;
	private boolean compress = false;
	private String encoding = "GBK";
	private String ISO8859;
	private Map<Integer, String> hashMapField = new HashMap<Integer, String>();

	PKT_DEF[] pktdef;
	IMacCallback iCompareMacCallBack = null;
	IMacCallback iCombineMacCallBack = null;
	//private String msg8583Hex = "";
	private byte[] byteMSG = null;

	private Map<String, String> transformerTmpField = new HashMap<String, String>();

	/*********************** 交易流程公共参数 ***********************************/
	/**
	 * 当前流水对象
	 */
	private FlowMessage flow;
	/**
	 * 原交易流水对象
	 */
	private FlowMessage orgFlow;
	/**
	 * 报文头
	 */
	private PospMessage pospMessage;
	/**
	 * 终端IP
	 */
	private String xRealIp;
	/**
	 * 本机IP
	 */
	private String ip;
	/**
	 * 卡产品
	 */
	private String cardProducts;
	private String channelCode;
	private String tranCode;
	private String seq = "";

	/**
	 * 检查状态
	 */
	private boolean state;

	/**
	 * 应答码
	 */
	private String rspCode;
	/**
	 * POSP错误码
	 */
	private String pospCode;
	/**
	 * 应答码对应信息
	 */
	private String rspMsg;
	/**
	 * 平台标志
	 */
	private boolean platform;
	/**
	 * 免验密码网络标志
	 */
	private String freePasswordSign;
	/**
	 * 国密标志
	 */
	private boolean stateKeySign;
	
	/**
	 * POS终端绑定号码
	 */
	private String BindNumber;
	/**
	 * 主叫号码与系统登记号码是否一致
	 */
	private boolean isMatch;
	/**
	 * 48域
	 * 退货时 用field_48来识别是哪一个流水表用，“1”为流水表，否则为历史流水表
	 * @return
	 */
	private String field48;
	/**
	 * 签购单域
	 */
	private SalesSlip salesSlip;
	public IsoMessage clone() {
		IsoMessage cl = null;
		try {
			cl = (IsoMessage) super.clone();
			cl.setMessageDefine(this.getMessageDefine());
			cl.setMessage(this.getMessage());
			cl.setSeq(this.getSeq());
			cl.setPospCode(this.getPospCode());
			cl.setRspCode(this.getRspCode());
			cl.setChannelCode(this.getChannelCode());
			cl.getTransformerTmpField().putAll(this.getTransformerTmpField());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cl;
	}
	public void clone(IsoMessage iso){
		try {
			iso.setSeq(this.getSeq());
			iso.setChannelCode(this.getChannelCode());
			iso.setPospMessage(this.getPospMessage());
			iso.setFlow(this.getFlow());
			iso.setPospCode(this.getPospCode());
			iso.setRspCode(this.getRspCode());
			iso.setRspMsg(this.getRspMsg());
			iso.setTranCode(this.getTranCode());
			iso.setPlatform(this.isPlatform());
			iso.setCardProducts(this.getCardProducts());
			iso.setHashMapField(this.getHashMapField());
			iso.setFreePasswordSign(this.getFreePasswordSign());
			iso.getTransformerTmpField().putAll(this.getTransformerTmpField());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getSeq() {
		return seq;
	}

	public void setCompareMac(IMacCallback iCallBack) {
		iCompareMacCallBack = iCallBack;
	}

	public void setCombineMac(IMacCallback iCallBack) {
		iCombineMacCallBack = iCallBack;
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
	@Override
	public void setMessageDefine(PKT_DEF[] def) throws Exception {
		if (def == null)
			throw new NullPointerException(
					"FORMAT_EXCEPTION: ISO8583数据定义配置不能为null。");
		if (!((def.length == (64 + 1)) || (def.length == (128 + 1))))
			throw new Exception(
					"FORMAT_EXCEPTION: ISO8583数据定义异常。PKT_DEF数组长度必须为(64 + 1)或(128 + 1)。");
		pktdef = def;
	}

	@Override
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
	@Override
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
	@Override
	public void setMessageWithHex(String message) throws Exception {
		int pos = 0;
		int len;
		String msg_type = null;
		String bitstr = null;
		String strVal = null;
		String strLen = null;
		if (pktdef == null)
			throw new NullPointerException("FORMAT_EXCEPTION: 未设置ISO8583数据包定义！");

		//msg8583Hex = message;

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
			switch (pktdef[x].getFormat()) {
			case FMT_FIXED:
				strVal = message
						.substring(pos, pos + pktdef[x].getLength() * 2);
				msg_type = new String(HexCodec.hexDecode(strVal), encoding);
				pos += pktdef[x].getLength() * 2;
//				log.debug("FMT_FIXED = FMT_FIXED " + msg_type);
				break;
			default:
				throw new Exception("FORMAT_EXCEPTION: 位图消息类型定义错误！");
			}
			break;
			
		case TYP_ASC2:
			switch (pktdef[x].getFormat()) {
			case FMT_FIXED:
				strVal = message
						.substring(pos, pos + pktdef[x].getLength() * 2);
				msg_type = new String(HexCodec.hexDecode(strVal), encoding);
				pos += pktdef[x].getLength() * 2;
//				log.debug("FMT_FIXED = FMT_FIXED " + msg_type);
				break;
			default:
				throw new Exception("FORMAT_EXCEPTION: 位图消息类型定义错误！");
			}
			break;
			
		default:
			break;
		}
//		log.debug("msg_type = " + msg_type);

		if (pktdef.length == 64 + 1) {
			bitstr = message.substring(pos, pos + 16);
			pos = pos + 16;
		} else if (pktdef.length == 128 + 1) {
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
//					log.debug(" =========== > bitidx = " + bitidx + "  " +pktdef[bitidx].getType() );
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
								if(pktdef[bitidx].getSpec() == PacketDefineSpec.L_ZERO)
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
//							log.debug(" =========== > strVal = " + bitidx + "  " + strVal);
							break;

						case FMT_LVAR:
						case FMT_LLVAR:
							strLen = message.substring(pos, pos + 2);
							pos += 2;
							len = Integer.parseInt(strLen);
							if (len % 2 == 0) {
								// 偶数
								strVal = message.substring(pos, pos + len);
								pos += len;
							} else {
								// 奇数
								strVal = message.substring(pos, pos + len);
								pos += len;
								pos += 1;
							}
							break;

						case FMT_LLLVAR:
							strLen = message.substring(pos, pos + 4);
//							log.debug(" =========== > bitidx = " + bitidx + "  " + strLen);
							pos += 4;
							len = Integer.parseInt(strLen);
							if (len % 2 == 0) {
								// 偶数
								strVal = message.substring(pos, pos + len);
								pos += len;
							} else {
								// 奇数
								strVal = message.substring(pos, pos + len);
								pos += len;
								pos += 1;
							}
//							log.debug(" =========== > strVal = " + bitidx + "  " + strVal);
//							strLen = message.substring(pos, pos + 4);
//							log.debug(" =========== > bitidx = " + bitidx + "  " + strLen);
//							pos += 4;
//							len = Integer.parseInt(strLen);
//							if (pktdef[bitidx].getLength() % 2 == 0) {
//								// 偶数
//								strVal = message.substring(pos, pos + len);
//								pos += len;
//							} else {
//								// 奇数
//								strVal = message.substring(pos, pos + len);
//								pos += len;
//								pos += 1;
//							}
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
							if (len % 2 == 0) {
								// 偶数
								strVal = message.substring(pos, pos + len);
								pos += len;
							} else {
								// 奇数
								strVal = message.substring(pos, pos + len);
								pos += len;
								pos += 1;
							}
							break;

						case FMT_LLLVAR:
							strLen = message.substring(pos, pos + 4);
							pos += 4;
							len = Integer.parseInt(strLen);
							if (pktdef[bitidx].getLength() % 2 == 0) {
								// 偶数
								strVal = message.substring(pos, pos + len);
								pos += len;
							} else {
								// 奇数
								strVal = message.substring(pos, pos + len);
								pos += len;
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
//							log.debug(" =========== > strVal = " + bitidx + "  " + strVal);

							break;

						case FMT_LVAR:
							if (compress == true) {
								strLen = message.substring(pos, pos + 2);
								pos += 2;
								len = Integer.parseInt(strLen) * 2;
								strVal = message.substring(pos, pos + len);
								strVal = new String(HexCodec.hexDecode(strVal),
										encoding);
								pos += len;
							} else {
								strLen = message.substring(pos, pos + 2);
								pos += 2;
								len = Integer.parseInt(new String(HexCodec
										.hexDecode(strLen), encoding)) * 2;
								strVal = message.substring(pos, pos + len);
								strVal = new String(HexCodec.hexDecode(strVal),
										encoding);
								pos += len;
							}
							break;

						case FMT_LLVAR:
							if (compress == true) {
								strLen = message.substring(pos, pos + 2);
								pos += 2;
								len = Integer.parseInt(strLen) * 2;
								strVal = message.substring(pos, pos + len);
								strVal = new String(HexCodec.hexDecode(strVal),
										encoding);
								pos += len;
							} else {
								strLen = message.substring(pos, pos + 4);
								pos += 4;
								len = Integer.parseInt(new String(HexCodec
										.hexDecode(strLen), encoding)) * 2;
								strVal = message.substring(pos, pos + len);
								strVal = new String(HexCodec.hexDecode(strVal),
										encoding);
								pos += len;
							}
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
								// 去掉空格或者零
								strVal = StrUtil.rightTrim(strVal, "0");
								break;
							default:
								break;
							}
//							log.debug(" =========== > strVal = " + bitidx + "  " + strVal);

							break;

						case FMT_LVAR:
							if (compress == true) {
								strLen = message.substring(pos, pos + 2);
								pos += 2;
								len = Integer.parseInt(strLen) * 2;
								strVal = message.substring(pos, pos + len);
								pos += len;
							} else {
								strLen = message.substring(pos, pos + 2);
								pos += 2;
								len = Integer.parseInt(new String(HexCodec
										.hexDecode(strLen), encoding)) * 2;
								strVal = message.substring(pos, pos + len);
								strVal = new String(HexCodec.hexDecode(strVal),
										encoding);
								pos += len;
							}
							break;

						case FMT_LLVAR:
							if (compress == true) {
								strLen = message.substring(pos, pos + 2);
								pos += 2;
								len = Integer.parseInt(strLen) * 2;
								strVal = message.substring(pos, pos + len);
								pos += len;
							} else {
								strLen = message.substring(pos, pos + 4);
								pos += 4;
								len = Integer.parseInt(new String(HexCodec
										.hexDecode(strLen), encoding)) * 2;
								strVal = message.substring(pos, pos + len);
								strVal = new String(HexCodec.hexDecode(strVal),
										encoding);
								pos += len;
							}
							break;

						case FMT_LLLVAR:
							if (compress == true) {
								strLen = message.substring(pos, pos + 4);
								pos += 4;
								len = Integer.parseInt(strLen) * 2;
								strVal = message.substring(pos, pos + len);
								pos += len;
							} else {
								strLen = message.substring(pos, pos + 6);
								pos += 6;
								len = Integer.parseInt(new String(HexCodec
										.hexDecode(strLen), encoding)) * 2;
								strVal = message.substring(pos, pos + len);
								pos += len;
							}
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

	public void compareMac() throws Exception {
		if (iCompareMacCallBack != null) {
			iCompareMacCallBack.calculate(this, pktdef,
					byteMSG);
		}
	}

	/**
	 * getMessage 获取8583包的数据包内容，格式为GBK的字节流。
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	@Override
	public byte[] getMessage() throws Exception {
		String message = getMessageWithHex();
		if (iCombineMacCallBack != null && markCalcMac == false) {

			String mac_str = iCombineMacCallBack.calculate(this, pktdef, null);
			if (mac_str == null)
				throw new Exception("MAC ERROR");

			if (pktdef.length == 64 + 1)
				setField(64, mac_str);
			else if (pktdef.length == 128 + 1)
				setField(128, mac_str);

			message = getMessageWithHex();
			markCalcMac = true;
			
		}
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
				if(pktdef[index].getSpec() == PacketDefineSpec.L_ZERO){
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
		int len;
		if(index == 63 && ISO8859 != null) {
			len = str.getBytes(ISO8859).length;
		}else {
			len = str.getBytes(encoding).length;
		}

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
		if(index == 63 && ISO8859 != null) {
			message += HexCodec.hexEncode(str.getBytes(ISO8859));
		}else {
			message += HexCodec.hexEncode(str.getBytes(encoding));
		}
		return message;
	}
	
	
	/**
	 * MergeStr 根据每个域的定义，合并该域的字符串，一般用于ASC2 格式
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	private String MergeASC2Str(PacketDefineFormat format,
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
				if (len % 2 != 0)
					throw new Exception("FORMAT_EXCEPTION: 第[" + index
							+ "]域数据异常。INFO=>LLLVAR["
							+ pktdef[index].getLength() + "] DATA:[" + str
							+ "](" + len + ")");
				
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
			if (len % 2 != 0)
				throw new Exception("FORMAT_EXCEPTION: 第[" + index
						+ "]域数据异常。INFO=>LLLVAR["
						+ pktdef[index].getLength() + "] DATA:[" + str
						+ "](" + len + ")");
			len = len / 2;
			
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
			if (len % 2 != 0)
				throw new Exception("FORMAT_EXCEPTION: 第[" + index
						+ "]域数据异常。INFO=>LLLVAR["
						+ pktdef[index].getLength() + "] DATA:[" + str
						+ "](" + len + ")");
			len = len / 2;
			
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
			if (len % 2 != 0)
				throw new Exception("FORMAT_EXCEPTION: 第[" + index
						+ "]域数据异常。INFO=>LLLVAR["
						+ pktdef[index].getLength() + "] DATA:[" + str
						+ "](" + len + ")");
			len = len / 2;
			
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


		message += str;
		
		return message;
	}


	/**
	 * getMessageWithHex 获取8583包的数据包内容。格式为16进制的字符串。
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	@Override
	public String getMessageWithHex() throws Exception {
		String message = "";
		String tailflag = "0";
		String str = null;

		int bit[] = { 0x80, 0x40, 0x20, 0x10, 0x8, 0x4, 0x2, 0x1 };
		byte[] mapbit = null;
		if (pktdef.length == 64 + 1)
			mapbit = new byte[8];
		else if (pktdef.length == 128 + 1)
			mapbit = new byte[16];

		int bitval = 0;
		if (pktdef.length == 128 + 1)
			bitval = bitval | 0x80;

		for (int index = 0, byte_idx = 0; index < pktdef.length; index++) {
			if (index == 1) {
				if (pktdef.length == 128 + 1)
					message += "ABCDEFGHIJKLMNOPABCDEFGHIJKLMNOP";
				else
					message += "ABCDEFGHIJKLMNOP";
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
				
			case TYP_ASC2:
				message += MergeASC2Str(pktdef[index].getFormat(),
						pktdef[index].getSpec(), str, index);
				break;
			default:
				break;
			}
		}

		if (pktdef.length == 64 + 1)
			message = message.replaceFirst("ABCDEFGHIJKLMNOP",
					HexCodec.hexEncode(mapbit));
		else if (pktdef.length == 128 + 1)
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
		// TODO Auto-generated method stub
		String text = "";
		int len = 0;

		try {
			len = message.getBytes(encoding).length;
			// 如果格式后的长度比msg长度还短就直接返回原msg
			if (fmt_len <= len)
				return message;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
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
	@Override
	public String getField(int index) {
		// TODO Auto-generated method stub
		return hashMapField.get(index);
	}

	@Override
	public String getField2(int index) {
		// TODO Auto-generated method stub
		String strText = "";
		String strVal = "";
//		String macValue = "";
		try {
			switch (pktdef[index].getFormat()) {
			case FMT_LVAR:
				strVal = hashMapField.get(index);
				if (strVal != null) {
					strText += String.format("%d",
							strVal.getBytes("GBK").length);
					strText += strVal;
				}
				break;
			case FMT_LLVAR:
				strVal = hashMapField.get(index);
				if (strVal != null) {
					strText += String.format("%02d",
							strVal.getBytes("GBK").length);
					strText += strVal;
				}
				break;
			case FMT_LLLVAR:
				strVal = hashMapField.get(index);
				if (strVal != null) {
					strText += String.format("%03d",
							strVal.getBytes("GBK").length);
					strText += strVal;
				}
				break;
			default:
				strText = hashMapField.get(index);
				break;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			log.error(String.format("[%s] %s", seq, e.getMessage()));
			e.printStackTrace();
		}
		return strText;
	}

	/**
	 * setField 设置某域的值。
	 * 
	 * @param index 第几域
	 * @param value 该域对应的值
	 * @throws Exception
	 *             if an error occurred
	 */
	@Override
	public void setField(int index, String value) throws Exception {
		// TODO Auto-generated method stub
		if (index >= pktdef.length || pktdef[index].length == 0)
			throw new Exception("FORMAT_EXCEPTION: 第[" + index + "]域数据未定义。");
		markCalcMac = false;
		hashMapField.put(index, value);
	}

	/**
	 * printMessage 打印8583包内所有域的内容，敏感字段将会用字符#替换输出
	 * 
	 * @param id 一般为交易流水标识
	 * @throws Exception
	 *             if an error occurred
	 */
	@Override
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
				}else if(index == 14 || index == 35
						|| index == 36  || index == 52
						|| index == 55 || index == 62
						|| index == 102 || index == 103){
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

		/*
		val = hashMapField.get(55);
		if (val != null) {
			IsoICCardParser ic = new IsoICCardParser();
			try {
				ic.setBuffer(val);
				for (Entry<String, String> entry : ic.hashMapTag.entrySet()) {

					if (entry.getKey().equals("9F1E")
							|| entry.getKey().equals("9F74")) {
						try {
							log.debug(String.format(
									"[%s]  [%-4s]: <%s> {%s}",
									id,
									entry.getKey(),
									entry.getValue(),
									new String(HexCodec.hexDecode(entry
											.getValue()), "GBK")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							log.error(String.format("[%s] %s", seq,
									e.getMessage()));
						}
					} else
						log.debug(String.format("[%s]  [%-4s]: <%s>", id,
								entry.getKey(), entry.getValue()));
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				log.error(String.format("[%s] %s", seq, e1.getMessage()));
			}
		}
		*/
	}

	/**
	 * printMessage 打印8583包内所有域的内容，敏感字段将会用字符#替换输出
	 * 
	 * @throws Exception
	 *             if an error occurred
	 */
	@Override
	public void printMessage() {
		String vt = null;
		String val = null;
		log.info(String.format(
				"[%s]  *******************************************", seq));
		for (int index = 0; index < pktdef.length; index++) {
			val = hashMapField.get(index);
			if (val != null) {
				if (log.isInfoEnabled()) {
					if(index == 2 ){
						vt = val.substring(0,6);
						for(int n = 7 ; n < val.length() - 4 ; n ++)
							vt += "*";
						vt += val.substring(val.length() - 4);
						
						log.info(String.format("[%s]  [%2d]: <%s>", seq, index,
								vt));
					}else if(index == 14 || index == 35
							|| index == 36  || index == 52
							|| index == 55 || index == 62
							|| index == 102 || index == 103){
						vt = "";
						for(int n = 0 ; n < val.length()  ; n ++)
							vt += "*";
						log.info(String.format("[%s]  [%2d]: <%s>", seq, index,
								vt));
					}
					else 
					log.info(String.format("[%s]  [%2d]: <%s>", seq, index,
							val));
				}
			}
		}
		log.info(String.format(
				"[%s]  *******************************************", seq));

		/*
		val = hashMapField.get(55);
		if (val != null) {
			IsoICCardParser ic = new IsoICCardParser();
			try {
				ic.setBuffer(val);
				for (Entry<String, String> entry : ic.hashMapTag.entrySet()) {

					if (entry.getKey().equals("9F1E")
							|| entry.getKey().equals("9F74")) {
						try {
							log.debug(String.format(
									"[%s]  [%-4s]: <%s> {%s}",
									seq,
									entry.getKey(),
									entry.getValue(),
									new String(HexCodec.hexDecode(entry
											.getValue()), "GBK")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							log.error(String.format("[%s] %s", seq,
									e.getMessage()));
						}
					} else {
						if (log.isDebugEnabled()) {
							log.debug(String.format("[%s]  [%-4s]: <%s>", seq,
									entry.getKey(), entry.getValue()));
						}
					}
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				log.error(String.format("[%s] %s", seq, e1.getMessage()));
			}
		}
		*/
	}

	public FlowMessage getFlow() {
		return flow;
	}

	public void setFlow(FlowMessage flow) {
		this.flow = flow;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getTranCode() {
		return tranCode;
	}

	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}

	public FlowMessage getOrgFlow() {
		return orgFlow;
	}

	public void setOrgFlow(FlowMessage orgFlow) {
		this.orgFlow = orgFlow;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String getRspCode() {
		return rspCode;
	}

	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}

	public Map<Integer, String> getHashMapField() {
		return hashMapField;
	}

	public void setHashMapField(Map<Integer, String> hashMapField) {
		this.hashMapField = hashMapField;
	}
	/**
	 * @return the transformerTmpField
	 */
	public Map<String, String> getTransformerTmpField() {
		return transformerTmpField;
	}

	/**
	 * @param transformerTmpField
	 *            the transformerTmpField to set
	 */
	public void setTransformerTmpField(Map<String, String> transformerTmpField) {
		this.transformerTmpField = transformerTmpField;
	}

	public String getField48() {
		return field48;
	}

	public void setField48(String field48) {
		this.field48 = field48;
	}

	public String getRspMsg() {
		return rspMsg;
	}

	public void setRspMsg(String rspMsg) {
		this.rspMsg = rspMsg;
	}

	public boolean isMatch() {
		return isMatch;
	}

	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}

	public String getPospCode() {
		return pospCode;
	}

	public void setPospCode(String pospCode) {
		this.pospCode = pospCode;
	}

	public String getBindNumber() {
		return BindNumber;
	}

	public void setBindNumber(String bindNumber) {
		BindNumber = bindNumber;
	}

	public SalesSlip getSalesSlip() {
		return salesSlip;
	}

	public void setSalesSlip(SalesSlip salesSlip) {
		this.salesSlip = salesSlip;
	}

	public PospMessage getPospMessage() {
		return pospMessage;
	}

	public void setPospMessage(PospMessage pospMessage) {
		this.pospMessage = pospMessage;
	}

	public boolean isPlatform() {
		return platform;
	}

	public void setPlatform(boolean platform) {
		this.platform = platform;
	}
	public String getXRealIp() {
		return xRealIp;
	}
	public void setXRealIp(String xRealIp) {
		this.xRealIp = xRealIp;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getCardProducts() {
		return cardProducts;
	}
	public void setCardProducts(String cardProducts) {
		this.cardProducts = cardProducts;
	}

	public String getFreePasswordSign() {
		return freePasswordSign;
	}

	public void setFreePasswordSign(String freePasswordSign) {
		this.freePasswordSign = freePasswordSign;
	}

	public boolean isStateKeySign() {
		return stateKeySign;
	}

	public void setStateKeySign(boolean stateKeySign) {
		this.stateKeySign = stateKeySign;
	}

	public String getISO8859() {
		return ISO8859;
	}

	public void setISO8859(String ISO8859) {
		this.ISO8859 = ISO8859;
	}
}
