package com.bestpay.posp.utils.crypto.essc.api;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import com.bestpay.posp.utils.crypto.essc.communicate.CommWithEsscSvr;
import lombok.extern.slf4j.Slf4j;

import org.apache.log4j.Logger;

@Slf4j
public class UnionAPI {

	public String gunionIDOfEsscAPI;

	private static String esscIp;
	private static int esscPort;
	private static int timeOut;
	private int longOrShortConn = 0; // 0:短连接;非0:长连接.
	private static Logger logger = Logger.getLogger("UnionAPI.class");

	public UnionAPI(String ip, int port, int timeOut, int longOrShortConn,
			String gunionIDOfEsscAPI) {
		esscIp = ip;
		esscPort = port;
		UnionAPI.timeOut = timeOut;
		this.gunionIDOfEsscAPI = gunionIDOfEsscAPI;
		this.setLongOrShortConn(longOrShortConn);
	}

	/*public UnionAPI(String ip, int port, int timeOut, String gunionIDOfEsscAPI) {
		esscIp = ip;
		esscPort = port;
		UnionAPI.timeOut = timeOut;
		this.gunionIDOfEsscAPI = gunionIDOfEsscAPI;
		this.setLongOrShortConn(longOrShortConn);
	}*/

	public int getLongOrShortConn() {
		return longOrShortConn;
	}

	public void setLongOrShortConn(int longOrShortConn) {
		this.longOrShortConn = longOrShortConn;
	}

	// 将一个ESSC报文域以字节的形式打入到包中, 返回打入到包中的数据以字节形式.
	private byte[] UnionBitPutFldIntoStr(int fldTag, byte[] value,
			int lenOfValue) {
		if (lenOfValue < 0 || value.length == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501".getBytes();
		}
		if (fldTag < 0 || fldTag > 999 || lenOfValue > Config.BUF_LEN) {
			logger.debug("调用API接口传入参数错误");
			return "-5501".getBytes();
		}
		byte[] tmpBuf = bytePlusByte(
				(printCStyle(Config.AREA_NAME_LEN, fldTag) + printCStyle(
						Config.AREA_LEN_LEN, lenOfValue)).getBytes(), value);
		return tmpBuf;
	}

	// 把字节数组req插到字节数组res的后面,
	private byte[] bytePlusByte(byte[] res, byte[] req) {
		byte[] tmpres = new byte[res.length + req.length];
		int i = 0, j = 0;
		for (i = 0; i < res.length; i++)
			tmpres[i] = res[i];
		for (j = 0; j < req.length; j++)
			tmpres[i + j] = req[j];
		return tmpres;
	}

	// 把一个数字转换成长度为len的字符串
	private String printCStyle(int len, int value) {
		StringBuffer sb = new StringBuffer(len);
		int padlen = len - String.valueOf(value).length();
		for (int i = padlen; i > 0; i--)
			sb.append('0');
		sb.append(value);
		return sb.toString();
	}

	// 从一个ESSC报文中读取一个域
	private byte[] UnionReadSpecFldFromBytes(byte[] databytes, int len,
			int fldTag) {
		String headStr = new String(databytes, 0, Config.AREA_COUNT_LEN);
		int fldNum = getLenOfFld(headStr, 0, Config.AREA_COUNT_LEN);
		if (fldNum <= 0) {
			logger.debug("响应报文域数目错！");
			return "-5504".getBytes();
		}
		int offset = Config.AREA_COUNT_LEN;
		for (int index = 0; index < fldNum; index++) {
			int reaHeadLen = Config.AREA_NAME_LEN + Config.AREA_LEN_LEN;
			if (offset + reaHeadLen > len) {
				return "-5504".getBytes();
			}
			int tmpFldTag = getLenOfFld(new String(databytes, offset,
					Config.AREA_NAME_LEN), 0, Config.AREA_NAME_LEN);
			offset += Config.AREA_NAME_LEN;
			int fldLen = getLenOfFld(new String(databytes, offset,
					Config.AREA_LEN_LEN), 0, Config.AREA_LEN_LEN);
			offset += Config.AREA_LEN_LEN;
			if (offset + fldLen > len) {
				logger.debug("域长度错!");
				return "-5504".getBytes();
			}
			if (tmpFldTag == fldTag) {
				if (fldLen < 0) {
					logger.debug("域长度错!");
					return "-5504".getBytes();
				}
				byte[] tmpData = new byte[fldLen];
				System.arraycopy(databytes, offset, tmpData, 0, fldLen);
				return tmpData;
			}
			offset += fldLen;
		}
		return null;

	}

	// 从字符串s中读取从begin开始的len位长度的字符串.
	private int getLenOfFld(String s, int bengin, int len) {
		byte[] result = s.getBytes();
		String ks = new String(result, bengin, len);
		Integer ts = new Integer(ks);
		return ts.intValue();
	}

	public String UnionEncryptPlainData(String zekName, String data,
			int dataLen, String encFlag, String iv) {
		if (zekName == null || zekName.length() == 0 || data == null
				|| data.length() == 0 ) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[data.getBytes().length + 200];
		int offset = 0;

		// area count is 2 or 4
		if (encFlag.contentEquals("1")) {
			System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		} else {
			System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		}
		offset += 3;
		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zekName.getBytes(), zekName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area 081
		byte[] area081 = UnionBitPutFldIntoStr(fldTag.conEsscFldPlainData,
				data.getBytes(), data.getBytes().length);
		System.arraycopy(area081, 0, reqbuf, offset, area081.length);
		offset += area081.length;

		// area 002
		if (encFlag.contentEquals("1")) {
			// area 214
			byte[] area214 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldAlgorithmMode, encFlag.getBytes(),
					encFlag.getBytes().length);
			System.arraycopy(area214, 0, reqbuf, offset, area214.length);
			offset += area214.length;

			// area 213
			byte[] area213 = UnionBitPutFldIntoStr(fldTag.conEsscFldIV,
					iv.getBytes(), iv.getBytes().length);
			System.arraycopy(area213, 0, reqbuf, offset, area213.length);
			offset += area213.length;
		}

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("516",
					reqbuf, offset);
		} else {

			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("516",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldCiperData), "GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
		} else {
			return len + "";
		}

	}

	public String UnionDecryptEncData(String zekName, String encData,
			String encFlag, String iv) {
		if (zekName == null || zekName.length() == 0 || encData == null
				|| encData.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[encData.getBytes().length + 200];
		int offset = 0;
		if (encFlag.contentEquals("1")) {
			System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		} else {
			System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		}
		offset += 3;
		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area area001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zekName.getBytes(), zekName.length());
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area area082
		byte[] area082 = UnionBitPutFldIntoStr(fldTag.conEsscFldCiperData,
				encData.getBytes(), encData.getBytes().length);
		System.arraycopy(area082, 0, reqbuf, offset, area082.length);
		offset += area082.length;

		if (encFlag.contentEquals("1")) {
			// area 214
			byte[] area214 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldAlgorithmMode, encFlag.getBytes(),
					encFlag.getBytes().length);
			System.arraycopy(area214, 0, reqbuf, offset, area214.length);
			offset += area214.length;

			// area 213
			byte[] area213 = UnionBitPutFldIntoStr(fldTag.conEsscFldIV,
					iv.getBytes(), iv.getBytes().length);
			System.arraycopy(area213, 0, reqbuf, offset, area213.length);
			offset += area213.length;
		}
		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("517",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("517",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldData), "GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	// 密码机指令
	public String UnionDirectHsmCmd(String hsmCmdReq, int lenOfHsmCmdReq) {
		byte[] reqbuf = new byte[hsmCmdReq.length() + 100];
		int offset = 0;
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;
		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1
		byte[] area072 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldDirectHsmCmdReq, hsmCmdReq.getBytes(),
				hsmCmdReq.length());

		System.arraycopy(area072, 0, reqbuf, offset, area072.length);
		offset += area072.length;
		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("100",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("100",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldDirectHsmCmdRes), "GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
		} else {
			return len + "";
		}
	}

	// ===================== CVV 函数 =====================
	/*
	 * 产生VISA卡校验值CVV 输入参数 period,有效期 serviceCode,服务码 accNo,卡号 cvkName,CVK密钥全名
	 * 
	 * 返回值 cvv
	 */
	public String UnionGenerateCVV(String cvkName, String period,
			String serviceCode, String accNo) {
		if ((period == null || period.length() == 0)
				|| (accNo == null || accNo.length() == 0)
				|| (serviceCode == null || serviceCode.length() == 0)
				|| (cvkName == null || cvkName.length() == 0)) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;

		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 072
		byte[] area072 = UnionBitPutFldIntoStr(fldTag.conEsscFldCardPeriod,
				period.getBytes(), period.getBytes().length);
		System.arraycopy(area072, 0, reqbuf, offset, area072.length);
		offset += area072.length;

		// area2 073
		byte[] area073 = UnionBitPutFldIntoStr(fldTag.conEsscFldServiceID,
				serviceCode.getBytes(), serviceCode.length());
		System.arraycopy(area073, 0, reqbuf, offset, area073.length);
		offset += area073.length;

		// area3 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				cvkName.getBytes(), cvkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("501",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("501",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldVisaCVV), "GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
		} else {
			return len + "";
		}
	}

	/*
	 * 验证VISA卡校验值CVV 输入参数 period,有效期 serviceCode,服务码 accNo,卡号 cvkName,CVK密钥全名
	 * cvv 输出参数 无 返回值 <0,是错误码。0 成功
	 */
	public int UnionVerifyCVV(String cvkName, String period,
			String serviceCode, String accNo, String cvv) {
		if ((period == null || period.length() == 0)
				|| (accNo == null || accNo.length() == 0)
				|| (cvv == null || cvv.length() == 0)
				|| (serviceCode == null || serviceCode.length() == 0)
				|| (cvkName == null || cvkName.length() == 0)) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;

		// area count is 5
		System.arraycopy("005".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 072
		byte[] area072 = UnionBitPutFldIntoStr(fldTag.conEsscFldCardPeriod,
				period.getBytes(), period.getBytes().length);
		System.arraycopy(area072, 0, reqbuf, offset, area072.length);
		offset += area072.length;

		// area2 073
		byte[] area073 = UnionBitPutFldIntoStr(fldTag.conEsscFldServiceID,
				serviceCode.getBytes(), serviceCode.length());
		System.arraycopy(area073, 0, reqbuf, offset, area073.length);
		offset += area073.length;

		// area3 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				cvkName.getBytes(), cvkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		// area5 071
		byte[] area071 = UnionBitPutFldIntoStr(fldTag.conEsscFldVisaCVV,
				cvv.getBytes(), cvv.getBytes().length);
		System.arraycopy(area071, 0, reqbuf, offset, area071.length);
		offset += area071.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("502",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("502",
					reqbuf, offset);
		}
		return len;
	}

	// ===================== 密钥管理 函数 =====================

	// 读取库中的密钥，并使用同一属主的ZMK加密输出
	/*
	 * 输入参数： fullKeyName，分别为密钥全名， 返回值： keyValue,checkValue,密钥密文（ZMK加密），校验值（加密全0）
	 */
	public String[] UnionReadKey(String fullKeyName) {
		String[] results = { "", "" };
		if (fullKeyName == null || fullKeyName.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			results[0] = "-5501";
			return results;
		}
		byte[] reqbuf = new byte[100];
		int offset = 0;

		// area count is 1
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("282",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("282",
					reqbuf, offset);
		}

		if (len >= 0) {
			try {
				results[0] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyValue), "GBK");
				results[1] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyCheckValue), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		} else {
			results[0] = len + "";
		}
		return results;
	}

	// 生成指定的密钥替换库中的密钥，并使用同一属主的ZMK加密输出
	/*
	 * 输入参数： fullKeyName，分别为密钥全名， 输出参数：
	 * keyValue,checkValue,密钥密文（ZMK加密），校验值（加密全0） 无 返回值：<0，是错误码，否则成功
	 */
	public String[] UnionGenerateKey(String fullKeyName) {
		String[] results = { "", "" };
		if (fullKeyName == null || fullKeyName.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			results[0] = "-5501";
			return results;
		}
		byte[] reqbuf = new byte[100];
		int offset = 0;

		// area count is 1
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("281",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("281",
					reqbuf, offset);
		}

		if (len >= 0) {
			try {
				results[0] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyValue), "GBK");
				results[1] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyCheckValue), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		} else {
			results[0] = len + "";
		}
		return results;
	}

	// 将指定的密钥使用指定的ZMK加密输出UnionReadKeyBySpecZmk
	/*
	 * 输入参数：fullKeyName：要读取的密钥名称 zmkName：ZMK密钥名称 输出参数：
	 * keyValue,checkValue,密钥密文（ZMK加密），校验值（加密全0） 无 返回值：<0，是错误码，否则成功
	 */
	public String[] UnionReadKeyBySpecZmk(String fullKeyName, String zmkName) {
		String[] results = { "", "" };
		if ((fullKeyName == null || fullKeyName.length() == 0)
				|| (zmkName == null || zmkName.length() == 0)) {
			logger.debug("调用API接口传入参数错误");
			results[0] = "-5501";
			return results;
		}
		byte[] reqbuf = new byte[100];
		int offset = 0;

		// area count is 2
		System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 011
		byte[] area011 = UnionBitPutFldIntoStr(fldTag.conEsscFldZMKName,
				zmkName.getBytes(), zmkName.getBytes().length);
		System.arraycopy(area011, 0, reqbuf, offset, area011.length);
		offset += area011.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("288",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("288",
					reqbuf, offset);
		}
		if (len >= 0) {
			try {
				results[0] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyValue), "GBK");
				results[1] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyCheckValue), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		} else {
			results[0] = len + "";
		}
		return results;
	}

	// 生成ZMK加密的密钥
	// 这个函数与UnionGenerateKey的区别在于，后者生成的密钥替换了库中的密钥
	// 而本函数只生成一个密钥值，并不替换库中的任何密钥
	/*
	 * 输入参数： zmkName，keyLenFlag， 输出参数： keyValue,checkValue,密钥密文（ZMK加密），校验值（加密全0）
	 * 无 返回值：<0，是错误码，否则成功
	 */
	public String[] UnionGenerateKeyByZMK(String zmkName, int keyLenFlag) {
		String[] results = { "", "" };
		if (zmkName == null || zmkName.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			results[0] = "-5501";
			return results;
		}
		String keyLenBuf = "";
		if (keyLenFlag == 0)
			keyLenBuf = "64";
		else if (keyLenFlag == 1)
			keyLenBuf = "128";
		else if (keyLenFlag == 2)
			keyLenBuf = "192";
		else {
			logger.debug("调用API接口传入参数错误");
			results[0] = "-5501";
			return results;
		}
		byte[] reqbuf = new byte[100];
		int offset = 0;

		// area count is 2
		System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldZMKName,
				zmkName.getBytes(), zmkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 203
		byte[] area203 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyLenFlag,
				keyLenBuf.getBytes(), keyLenBuf.getBytes().length);
		System.arraycopy(area203, 0, reqbuf, offset, area203.length);
		offset += area203.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("284",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("284",
					reqbuf, offset);
		}
		if (len >= 0) {
			try {
				results[0] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyValue), "GBK");
				results[1] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyCheckValue), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		} else {
			results[0] = len + "";
		}
		return results;
	}

	// 生成指定的密钥替换库中的密钥
	// 与UnionGenerateKey的区别在于：后者使用同一属主的ZMK加密输出密钥
	/*
	 * 输入参数： fullKeyName，分别为密钥全名， 输出参数： 无 返回值：<0，是错误码，否则成功
	 */
	public int UnionGenerateKeyMerely(String fullKeyName) {
		if (fullKeyName == null || fullKeyName.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[100];
		int offset = 0;

		// area count is 1
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("285",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("285",
					reqbuf, offset);
		}
		return len;
	}

	// 将ZMK加密的密钥存储到库中
	/*
	 * 输入参数： fullKeyName,keyValue,checkValue,分别为密钥全名，密钥密文（ZMK加密），校验值（加密全0） 输出参数：
	 * 无 返回值：<0，是错误码，否则成功
	 */
	public int UnionStoreKey(String fullKeyName, String keyValue,
			String checkValue) {
		if (fullKeyName == null || fullKeyName.length() == 0
				|| keyValue == null || keyValue.length() == 0
				|| checkValue == null || checkValue.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;

		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 061
		byte[] area061 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyValue,
				keyValue.getBytes(), keyValue.getBytes().length);
		System.arraycopy(area061, 0, reqbuf, offset, area061.length);
		offset += area061.length;

		// area3 051
		byte[] area051 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyCheckValue,
				checkValue.getBytes(), checkValue.getBytes().length);
		System.arraycopy(area051, 0, reqbuf, offset, area051.length);
		offset += area051.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("283",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("283",
					reqbuf, offset);
		}
		return len;
	}

	// ===================== MAC 函数 =====================

	// 产生报文MAC
	/*
	 * 这个函数,使用指定密钥生成MAC,算法是x9.9/x9.19 输入参数：fullKeyName,lenOfMacData,macData
	 * fullKeyName：密钥全名,字符串(ASCII),长度变长(不大于40),以'\0'结束
	 * lenOfMacData：MAC数据的长度,以十进制表示, <=500 macData：MAC数据,长度变长, 字符串, 以'\0'结束
	 * 返回值：mac mac：MAC值,长度16字节字符串(ASCII), 以'\0'结束
	 */
	public String UnionGenerateMac(String fullKeyName, int lenOfMacData,
			String macData) {
		if (fullKeyName == null || fullKeyName.length() == 0 || macData == null
				|| macData.length() == 0 || lenOfMacData <= 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[macData.length() + 100];
		int offset = 0;

		// area count is 2
		System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 021
		byte[] area021 = UnionBitPutFldIntoStr(fldTag.conEsscFldMacData,
				macData.getBytes(), macData.getBytes().length);
		System.arraycopy(area021, 0, reqbuf, offset, area021.length);
		offset += area021.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("302",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("302",
					reqbuf, offset);
		}
		if (len >= 0) {
			String mac = "";
			try {
				mac = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldMac), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return mac;
		} else {
			return len + "";
		}
	}

	public String UnionGenerateMac(String fullKeyName, int lenOfMacData,
			byte[] indate) {

		if (fullKeyName == null || fullKeyName.length() == 0 || indate == null
				|| indate.length == 0 || lenOfMacData <= 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[indate.length + 100];
		int offset = 0;

		// area count is 2
		System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 021
		byte[] area021 = UnionBitPutFldIntoStr(fldTag.conEsscFldMacData,
				indate, indate.length);
		System.arraycopy(area021, 0, reqbuf, offset, area021.length);
		offset += area021.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("302",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("302",
					reqbuf, offset);
		}
		if (len >= 0) {
			String mac = "";
			try {
				mac = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldMac), "GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return mac;
		} else {
			return len + "";
		}
	}

	// 验证报文MAC
	/*
	 * 这个函数,使用指定密钥验证MAC,算法是x9.9/x9.19 输入参数：fullKeyName,lenOfMacData,macData
	 * fullKeyName：密钥全名,字符串(ASCII),长度变长(不大于40),以'\0'结束
	 * lenOfMacData：MAC数据的长度,以十进制表示, <=500 macData：MAC数据,长度变长, 字符串, 以'\0'结束
	 * mac：MAC值,(ASCII), 以'\0'结束 输出参数：无 返回值：<0,是错误码。0 成功。
	 */
	public int UnionVerifyMac(String fullKeyName, int lenOfMacData,
			String macData, String mac) {
		if (fullKeyName == null || fullKeyName.length() == 0 || macData == null
				|| macData.length() == 0 || lenOfMacData <= 0 || mac == null
				|| mac.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[macData.getBytes().length + 100];
		int offset = 0;

		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 021
		byte[] area021 = UnionBitPutFldIntoStr(fldTag.conEsscFldMacData,
				macData.getBytes(), macData.getBytes().length);
		System.arraycopy(area021, 0, reqbuf, offset, area021.length);
		offset += area021.length;

		// area3 022
		byte[] area022 = UnionBitPutFldIntoStr(fldTag.conEsscFldMac,
				mac.getBytes(), mac.getBytes().length);
		System.arraycopy(area022, 0, reqbuf, offset, area022.length);
		offset += area022.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("332",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("332",
					reqbuf, offset);
		}
		return len;
	}

	public int UnionVerifyMac(String fullKeyName, int lenOfMacData,
			byte[] inMacData, byte[] inMac) {

		if (fullKeyName == null || fullKeyName.length() == 0
				|| inMacData == null || inMacData.length == 0
				|| lenOfMacData <= 0 || inMac == null || inMac.length == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[inMacData.length + 100];
		int offset = 0;

		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 021
		byte[] area021 = UnionBitPutFldIntoStr(fldTag.conEsscFldMacData,
				inMacData, inMacData.length);
		System.arraycopy(area021, 0, reqbuf, offset, area021.length);
		offset += area021.length;

		// area3 022
		byte[] area022 = UnionBitPutFldIntoStr(fldTag.conEsscFldMac, inMac,
				inMac.length);
		System.arraycopy(area022, 0, reqbuf, offset, area022.length);
		offset += area022.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("332",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("332",
					reqbuf, offset);
		}
		return len;
	}

	// 产生报文MAC
	/*
	 * 这个函数,使用指定密钥生成MAC,算法是中国银联标准 输入参数：fullKeyName,lenOfMacData,macData
	 * fullKeyName：密钥全名,字符串(ASCII),长度变长(不大于40),以'\0'结束
	 * lenOfMacData：MAC数据的长度,以十进制表示, <=500 macData：MAC数据,长度变长, 字符串, 以'\0'结束
	 * 返回值：mac mac：MAC值,长度16字节字符串(ASCII), 以'\0'结束
	 */
	public String UnionGenerateChinaPayMac(String fullKeyName,
			int lenOfMacData, String macData) {
		if (fullKeyName == null || fullKeyName.length() == 0 || macData == null
				|| macData.length() == 0 || lenOfMacData <= 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[macData.getBytes().length + 100];
		int offset = 0;

		// area count is 2
		System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 021
		byte[] area021 = UnionBitPutFldIntoStr(fldTag.conEsscFldMacData,
				macData.getBytes(), macData.getBytes().length);
		System.arraycopy(area021, 0, reqbuf, offset, area021.length);
		offset += area021.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("335",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("335",
					reqbuf, offset);
		}
		if (len >= 0) {
			String mac = "";
			try {
				mac = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldMac), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return mac;
		} else {
			return len + "";
		}
	}

	// 验证报文MAC
	/*
	 * 这个函数,使用指定密钥验证MAC,算法是中国银联标准 输入参数：fullKeyName,lenOfMacData,macData
	 * fullKeyName：密钥全名,字符串(ASCII),长度变长(不大于40),以'\0'结束
	 * lenOfMacData：MAC数据的长度,以十进制表示, <=500 macData：MAC数据,长度变长, 字符串, 以'\0'结束
	 * mac：MAC值,(ASCII), 以'\0'结束 输出参数：无 返回值：<0,是错误码。0 成功。
	 */
	public int UnionVerifyChinaPayMac(String fullKeyName, int lenOfMacData,
			String macData, String mac) {
		if (fullKeyName == null || fullKeyName.length() == 0 || macData == null
				|| macData.length() == 0 || lenOfMacData <= 0 || mac == null
				|| mac.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[macData.getBytes().length + 100];
		int offset = 0;

		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 021
		byte[] area021 = UnionBitPutFldIntoStr(fldTag.conEsscFldMacData,
				macData.getBytes(), macData.getBytes().length);
		System.arraycopy(area021, 0, reqbuf, offset, area021.length);
		offset += area021.length;

		// area3 022
		byte[] area022 = UnionBitPutFldIntoStr(fldTag.conEsscFldMac,
				mac.getBytes(), mac.getBytes().length);
		System.arraycopy(area022, 0, reqbuf, offset, area022.length);
		offset += area022.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("336",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("336",
					reqbuf, offset);
		}
		return len;
	}

	// 产生报文MAC
	/*
	 * 这个函数,使用指定ZMK加密的密钥生成MAC,算法是中国银联标准 输入参数：zmkName,lenOfMacData,macData
	 * zmkName：ZMK密钥全名,字符串(ASCII),长度变长(不大于40),以'\0'结束 keyByZmk: 是ZMK加密的密钥
	 * lenOfMacData：MAC数据的长度,以十进制表示, <=500 macData：MAC数据,长度变长, 字符串, 以'\0'结束
	 * 返回值：mac mac：MAC值,长度16字节字符串(ASCII), 以'\0'结束
	 */
	public String UnionGenerateChinaPayMacUsingKeyByZmk(String zmkName,
			String keyByZmk, int lenOfMacData, String macData) {
		if (zmkName == null || zmkName.length() == 0 || macData == null
				|| macData.length() == 0 || lenOfMacData <= 0
				|| keyByZmk == null || keyByZmk.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[macData.getBytes().length + 100];
		int offset = 0;
		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldZMKName,
				zmkName.getBytes(), zmkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 061
		byte[] area061 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyValue,
				keyByZmk.getBytes(), keyByZmk.getBytes().length);
		System.arraycopy(area061, 0, reqbuf, offset, area061.length);
		offset += area061.length;

		// area3 021
		byte[] area021 = UnionBitPutFldIntoStr(fldTag.conEsscFldMacData,
				macData.getBytes(), macData.getBytes().length);
		System.arraycopy(area021, 0, reqbuf, offset, area021.length);
		offset += area021.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("337",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("337",
					reqbuf, offset);
		}
		if (len >= 0) {
			String mac = "";
			try {
				mac = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldMac), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return mac;
		} else {
			return len + "";
		}
	}

	// ===================== PIN 函数 =====================
	// 将一个ZPK加密的PIN密文转换为另一个ZPK加密的PIN密文
	// 加密算法为Ansi x9.8/x9.18
	/*
	 * 输入参数：fullKeyName1,fullKeyName2,pinBlock1,accNo fullKeyName1：源密钥全名
	 * fullKeyName2：目的密钥全名
	 * pinBlock1：源密钥加密的PIN密文(PIN格式为ANSI9。8),长度16字节字符串(ASCII), 以'\0'结束。
	 * accNo：源账号/卡号,长度13-19字节字符串(ASCII),
	 * 以'\0'结束;卡号为实际卡号;账号和卡号填16个ASCII'0',表示无账号运算。 输出参数：pinBlock2,
	 * pinBlock2：目的成员行机构号加密的PIN密文(PIN格式为ANSI9.8), 长度16字节字符串(ASCII), 以'\0'结束
	 * 返回值：<0,是错误码。0 成功。
	 */
	public String UnionTranslatePin(String fullKeyName1, String fullKeyName2,
			String pinBlock1, String accNo) {
		if (fullKeyName1 == null || fullKeyName1.length() == 0
				|| fullKeyName2 == null || fullKeyName2.length() == 0
				|| pinBlock1 == null || pinBlock1.length() == 0
				|| accNo == null || accNo.length() == 0 || pinBlock1 == null
				|| pinBlock1.length() != 16) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldFirstWKName,
				fullKeyName1.getBytes(), fullKeyName1.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 002
		byte[] area002 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				fullKeyName2.getBytes(), fullKeyName2.getBytes().length);
		System.arraycopy(area002, 0, reqbuf, offset, area002.length);
		offset += area002.length;

		// area3 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK1, pinBlock1.getBytes(),
				pinBlock1.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;
		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("301",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("301",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByZPK2), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}

	}

	// 使用指定的密钥加密的PIN明文，算法为ansi x9.8/x9.18
	/*
	 * 这个函数,将用户的PIN明文加密成PIN密文。 输入参数： fullKeyName,clearPin,accNo fullKeyName:
	 * 密钥的名称 clearPin：PIN明文,长度6字节字符串(ASCII), 以'\0'结束。
	 * accNo：账号/卡号,长度13-19字节字符串(ASCII),
	 * 以'\0'结束;卡号为实际卡号;账号和卡号填16个ASCII'0',表示无账号运算。 返回值：pinBlock
	 * pinBlock：上送中心加密的PIN密文(PIN格式为ANSI9.8), 长度16字节字符串(ASCII), 以'\0'结束
	 */
	public String UnionEncryptPin(String fullKeyName, String clearPin,
			String accNo) {
		if (fullKeyName == null || fullKeyName.length() == 0
				|| clearPin == null || clearPin.length() == 0 || accNo == null
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 031
		byte[] area031 = UnionBitPutFldIntoStr(fldTag.conEsscFldPlainPin,
				clearPin.getBytes(), clearPin.getBytes().length);
		System.arraycopy(area031, 0, reqbuf, offset, area031.length);
		offset += area031.length;

		// area3 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("433",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("433",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByZPK), "GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/*
	 * 由一个ZPK加密的PIN导出一个由PVK生成的PVV303 从一个ZPK加密的PIN，导出一个由PVK加密生成的PVV。
	 * ZPK加密PIN采用的加密标准为ANSIX9.8。 PVK生成PVV（PIN Verification Value），采用的加密标准为Visa
	 * Method。 输入参数： zpkName,ZPK密钥名称 pinBlock，ZPK加密的PIN pvkName,PVK密钥名称 accNo,账号
	 * 返回值： pvv
	 */
	public String UnionDerivePVVFromPinByZpk(String zpkName, String pvkName,
			String pinBlock, String accNo) {
		if (zpkName == null || zpkName.length() == 0 || pvkName == null
				|| pvkName.length() == 0 || pinBlock == null
				|| pinBlock.length() == 0 || accNo == null
				|| accNo.length() == 0)
			return "-9001";

		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldFirstWKName,
				zpkName.getBytes(), zpkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 002
		byte[] area002 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				pvkName.getBytes(), pvkName.getBytes().length);
		System.arraycopy(area002, 0, reqbuf, offset, area002.length);
		offset += area002.length;

		// area3 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pinBlock.getBytes(),
				pinBlock.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("303",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("303",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldVisaPVV), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/*
	 * 根据一个ZPK加密的PIN密文和PVK生成的PVV验证PIN 从一个ZPK加密的PIN，导出一个由PVK加密生成的PVV。
	 * ZPK加密PIN采用的加密标准为ANSIX9.8。 PVK生成PVV（PIN Verification Value），采用的加密标准为Visa
	 * Method。 输入参数： zpkName,ZPK密钥名称 pinBlock，ZPK加密的PIN pvkName,PVK密钥名称 accNo,账号
	 * pvv,VisaPVV 输出参数： 无 返回值：<0,是错误码。0 成功。
	 */
	public int UnionVerifyPVVAndPinByZpk(String zpkName, String pvkName,
			String pinBlock, String accNo, String pvv) {
		if (zpkName == null || zpkName.length() == 0 || pvkName == null
				|| pvkName.length() == 0 || pinBlock == null
				|| pinBlock.length() == 0 || accNo == null
				|| accNo.length() == 0 || pvv == null || pvv.length() == 0)
			return -9001;

		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 5
		System.arraycopy("005".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldFirstWKName,
				zpkName.getBytes(), zpkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 002
		byte[] area002 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				pvkName.getBytes(), pvkName.getBytes().length);
		System.arraycopy(area002, 0, reqbuf, offset, area002.length);
		offset += area002.length;

		// area3 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pinBlock.getBytes(),
				pinBlock.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		// area5 036
		byte[] area036 = UnionBitPutFldIntoStr(fldTag.conEsscFldVisaPVV,
				pvv.getBytes(), pvv.getBytes().length);
		System.arraycopy(area036, 0, reqbuf, offset, area036.length);
		offset += area036.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("304",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("304",
					reqbuf, offset);
		}
		return len;
	}

	/*
	 * 将一个ZPK加密的PIN转换为由LMK加密 从一个ZPK加密的PIN，导出一个由PVK加密生成的PVV。
	 * ZPK加密PIN采用的加密标准为ANSIX9.8。 输入参数： zpkName,ZPK密钥名称 pinBlock，ZPK加密的PIN
	 * accNo,账号 输出参数： pinByLmk,LMK0203加密的PIN 返回值：<0,是错误码。0 成功。
	 */
	public String UnionDerivePinByLmkFromPinByZpk(String zpkName,
			String pinBlock, String accNo) {
		if (zpkName == null || zpkName.length() == 0 || pinBlock == null
				|| pinBlock.length() == 0 || accNo == null
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zpkName.getBytes(), zpkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pinBlock.getBytes(),
				pinBlock.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area3 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("306",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("306",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByLMK0203), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	
	/*将一个TPK加密的PIN转换为由LMK加密307  同306
	 * 将一个ZPK加密的PIN转换为由LMK加密 从一个ZPK加密的PIN，导出一个由PVK加密生成的PVV。
	 * ZPK加密PIN采用的加密标准为ANSIX9.8。 输入参数： zpkName,ZPK密钥名称 pinBlock，ZPK加密的PIN
	 * accNo,账号 输出参数： pinByLmk,LMK0203加密的PIN 返回值：<0,是错误码。0 成功。
	 */
	public String UnionDerivePinByLmkFromPinByZpk2(String zpkName,
			String pinBlock, String accNo) {
		if (zpkName == null || zpkName.length() == 0 || pinBlock == null
				|| pinBlock.length() == 0 || accNo == null
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zpkName.getBytes(), zpkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pinBlock.getBytes(),
				pinBlock.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area3 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("307",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("307",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByLMK0203), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}
	// 将一个ZPK加密的PIN密文转换为另一个ZPK加密的PIN密文
	// 与UnionTranslatePin的区别在于，这个函数使用了两个账号，即源目账号不同
	// 加密算法为Ansi x9.8/x9.18
	/*
	 * 输入参数：fullKeyName1,fullKeyName2,pinBlock1,accNo1 fullKeyName1：源密钥全名
	 * fullKeyName2：目的密钥全名
	 * pinBlock1：源密钥加密的PIN密文(PIN格式为ANSI9。8),长度16字节字符串(ASCII), 以'\0'结束。
	 * accNo1：源账号/卡号,长度13-19字节字符串(ASCII),
	 * 以'\0'结束;卡号为实际卡号;账号和卡号填16个ASCII'0',表示无账号运算。
	 * accNo2：目的账号/卡号,长度13-19字节字符串(ASCII),
	 * 以'\0'结束;卡号为实际卡号;账号和卡号填16个ASCII'0',表示无账号运算。 返回值：pinBlock2,
	 * pinBlock2：目的成员行机构号加密的PIN密文(PIN格式为ANSI9.8), 长度16字节字符串(ASCII), 以'\0'结束
	 */
	public String UnionTranslatePinWith2AccNo(String fullKeyName1,
			String fullKeyName2, String pinBlock1, String accNo1, String accNo2) {
		if (fullKeyName1 == null || fullKeyName1.length() == 0
				|| fullKeyName2 == null || fullKeyName2.length() == 0
				|| pinBlock1 == null || pinBlock1.length() == 0
				|| accNo1 == null || accNo1.length() == 0 || accNo2 == null
				|| accNo2.length() == 0 || pinBlock1 == null
				|| pinBlock1.length() != 16) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 5
		System.arraycopy("005".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldFirstWKName,
				fullKeyName1.getBytes(), fullKeyName1.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 002
		byte[] area002 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				fullKeyName2.getBytes(), fullKeyName2.getBytes().length);
		System.arraycopy(area002, 0, reqbuf, offset, area002.length);
		offset += area002.length;

		// area3 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK1, pinBlock1.getBytes(),
				pinBlock1.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo1,
				accNo1.getBytes(), accNo1.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		// area5 042
		byte[] area042 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo2,
				accNo2.getBytes(), accNo2.getBytes().length);
		System.arraycopy(area042, 0, reqbuf, offset, area042.length);
		offset += area042.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("310",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("310",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByZPK2), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	// 随机产生一个PIN 401，获得LMK加密的密文
	/*
	 * 输入参数： pinLen,PIN长度 accNo：账号/卡号,长度13-19字节字符串(ASCII),
	 * 以'\0'结束;卡号为实际卡号;账号和卡号填16个ASCII'0',表示无账号运算。 输出参数： pinByLmk,LMK0203加密的密文
	 * 返回值：<0,是错误码。0 成功。
	 */
	public String UnionGeneratePinRandomly(int pinLen, String accNo) {
		if (accNo == null || accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 2
		System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		String bufPinLen = String.valueOf(pinLen);
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldForPinLength,
				bufPinLen.getBytes(), bufPinLen.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("401",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("401",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByLMK0203), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/*
	 * 由ZPK加密的PIN导出一个PIN的Offset 从一个ZPK加密的PIN，导出一个由PVK加密生成的PinOffset。
	 * ZPK加密PIN采用的加密标准为ANSIX9.8。 用IBM方式产生一个PIN的PIN Offset。 输入参数： zpkName,ZPK密钥名称
	 * pinBlock，ZPK加密的PIN pvkName,PVK密钥名称 accNo,账号 返回值： pinOffset
	 */
	public String UnionDerivePinOffsetFromPinByZpk(String zpkName,
			String pvkName, String pinBlock, String accNo) {
		if (zpkName == null || zpkName.length() == 0 || pvkName == null
				|| pvkName.length() == 0 || pinBlock.length() == 0
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldFirstWKName,
				zpkName.getBytes(), zpkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 002
		byte[] area002 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				pvkName.getBytes(), pvkName.getBytes().length);
		System.arraycopy(area002, 0, reqbuf, offset, area002.length);
		offset += area002.length;

		// area3 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pinBlock.getBytes(),
				pinBlock.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("404",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("404",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldIBMPinOffset), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/*
	 * 通过ZPK加密的PIN和PVK生成的IBMPINOFFSET验证密码 ZPK加密PIN采用的加密标准为ANSIX9.8。
	 * PVK生成PinOffset（PIN Verification Value），采用的加密标准为Visa Method。 输入参数：
	 * zpkName,ZPK密钥名称 pinBlock，ZPK加密的PIN pvkName,PVK密钥名称 accNo,账号
	 * pinOffset,IBM算法 输出参数： 无 返回值：<0,是错误码。0 成功。
	 */
	public int UnionVerifyPinOffsetAndPinByZpk(String zpkName, String pvkName,
			String pinBlock, String accNo, String pinOffset) {
		if (zpkName == null || zpkName.length() == 0 || pvkName == null
				|| pvkName.length() == 0 || pinBlock == null
				|| pinBlock.length() == 0 || accNo == null
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 5
		System.arraycopy("005".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldFirstWKName,
				zpkName.getBytes(), zpkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 002
		byte[] area002 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				pvkName.getBytes(), pvkName.getBytes().length);
		System.arraycopy(area002, 0, reqbuf, offset, area002.length);
		offset += area002.length;

		// area3 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pinBlock.getBytes(),
				pinBlock.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		// area5 037
		byte[] area037 = UnionBitPutFldIntoStr(fldTag.conEsscFldIBMPinOffset,
				pinOffset.getBytes(), pinOffset.getBytes().length);
		System.arraycopy(area037, 0, reqbuf, offset, area037.length);
		offset += area037.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("461",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("461",
					reqbuf, offset);
		}
		return len;
	}

	// 使用LMK0203加密的PIN明文
	/*
	 * 这个函数,将用户的PIN明文加密成PIN密文。 输入参数： pinCyperLen,PIN密文长度
	 * clearPin：PIN明文,长度6字节字符串(ASCII), 以'\0'结束。 accNo：账号/卡号,长度13-19字节字符串(ASCII),
	 * 以'\0'结束;卡号为实际卡号;账号和卡号填16个ASCII'0',表示无账号运算。 输出参数： pinByLmk：LMK加密的PIN密文
	 * 返回值：<0,是错误码。0 成功。
	 */
	public String UnionEncryptPinByLmk(int pinCiyperLen, String clearPin,
			String accNo) {
		if (clearPin == null || clearPin.length() == 0 || accNo == null
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 205
		String tmpBuf = String.valueOf(pinCiyperLen);
		byte[] area205 = UnionBitPutFldIntoStr(fldTag.conEsscFldForPinLength,
				tmpBuf.getBytes(), tmpBuf.getBytes().length);
		System.arraycopy(area205, 0, reqbuf, offset, area205.length);
		offset += area205.length;

		// area2 031
		byte[] area031 = UnionBitPutFldIntoStr(fldTag.conEsscFldPlainPin,
				clearPin.getBytes(), clearPin.getBytes().length);
		System.arraycopy(area031, 0, reqbuf, offset, area031.length);
		offset += area031.length;

		// area3 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("431",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("431",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByLMK0203), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}

	}

	/*
	 * 通过ZPK加密的PIN和LMK加密的PIN验证密码 ZPK加密PIN采用的加密标准为ANSIX9.8。 输入参数： zpkName,ZPK密钥名称
	 * pinBlock，ZPK加密的PIN accNo,账号 pinByLmk,IBM算法 输出参数： 无 返回值：<0,是错误码。0 成功。
	 */
	public int UnionVerifyPinByLmkAndPinByZpk(String zpkName, String pinBlock,
			String accNo, String pinByLmk) {
		if (zpkName == null || zpkName.length() == 0 || pinBlock == null
				|| pinBlock.length() == 0 || accNo == null
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zpkName.getBytes(), zpkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pinBlock.getBytes(),
				pinBlock.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area3 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		// area4 035
		byte[] area035 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByLMK0203, pinByLmk.getBytes(),
				pinByLmk.getBytes().length);
		System.arraycopy(area035, 0, reqbuf, offset, area035.length);
		offset += area035.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("465",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("465",
					reqbuf, offset);
		}
		return len;
	}

	/*
	 * 使用指定应用的私钥进行签名 输入参数： idOfApp,应用编号 lenOfData,签名数据的长度 data，签名数据
	 * sizeOfSign，接收签名的数据缓冲的大小 flag，数据填充方式 返回值： sign，签名
	 */
	public String UnionGenerateSignature(String idOfApp, String flag,
			int lenOfData, String data) {
		if (idOfApp == null || idOfApp.length() == 0 || data == null
				|| data.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[data.getBytes().length + 100];
		int offset = 0;
		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 207
		byte[] area207 = UnionBitPutFldIntoStr(fldTag.conEsscFldIDOfApp,
				idOfApp.getBytes(), idOfApp.length());
		System.arraycopy(area207, 0, reqbuf, offset, area207.length);
		offset += area207.length;

		// area2 091
		byte[] area091 = UnionBitPutFldIntoStr(fldTag.conEsscFldSignData,
				data.getBytes(), data.getBytes().length);
		System.arraycopy(area091, 0, reqbuf, offset, area091.length);
		offset += area091.length;

		// area3 093
		byte[] area093 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldSignDataPadFlag, flag.getBytes(),
				flag.length());
		System.arraycopy(area093, 0, reqbuf, offset, area093.length);
		offset += area093.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("134",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("134",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldSign), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/*
	 * 使用指定公钥验证 输入参数： keyName,公钥名称 lenOfData,签名数据的长度 data，签名数据 lenOfSign，签名长度
	 * flag，数据填充方式 sign，签名 输出参数： 无 返回值：<0,是错误码。否则是签名验证成功
	 */
	// 由于函数的变动该函数已被UnionNewVerifySignature()覆盖, 所以没有进行测试.请调用时先进行测试.
	public int UnionVerifySignature(String keyName, String flag, int lenOfData,
			String data, String sign, int lenOfSign) {
		if (keyName == null || keyName.length() == 0 || data == null
				|| data.length() == 0 || lenOfSign <= 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[data.getBytes().length + sign.length() + 100];
		int offset = 0;
		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				keyName.getBytes(), keyName.length());
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 091
		byte[] area091 = UnionBitPutFldIntoStr(fldTag.conEsscFldSignData,
				data.getBytes(), data.length());
		System.arraycopy(area091, 0, reqbuf, offset, area091.length);
		offset += area091.length;

		// area3 092
		byte[] area092 = UnionBitPutFldIntoStr(fldTag.conEsscFldSign,
				sign.getBytes(), sign.length());
		System.arraycopy(area092, 0, reqbuf, offset, area092.length);
		offset += area092.length;

		// area4 093
		byte[] area093 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldSignDataPadFlag, flag.getBytes(),
				flag.length());
		System.arraycopy(area093, 0, reqbuf, offset, area093.length);
		offset += area093.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("133",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("133",
					reqbuf, offset);
		}
		return len;
	}

	/*
	 * 新增这两个函数因为：对于使用37指令的密码服务平台，参数flag为1位； 对于使用EW指令的密码服务平台，参数flag为2位。
	 * 新增函数是因为考虑到兼容以前用到的版本，所以不修改原来的函数。
	 */
	/*
	 * 使用指定应用的私钥进行签名 输入参数： idOfApp,应用编号 lenOfData,签名数据的长度 data，签名数据
	 * sizeOfSign，接收签名的数据缓冲的大小 flag，数据填充方式
	 * hashID，HASH算法标志：01，SHA-1，02，MD5，03，ISO 1011802，04，NoHash 输出参数： sign，签名
	 * 返回值：<0,是错误码。否则是签名的长度
	 */
	public String UnionNewGenerateSignature(String idOfApp, String flag,
			String hashID, int lenOfData, String data) {
		if (idOfApp == null || idOfApp.length() == 0 || data == null
				|| data.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[data.getBytes().length + 200];
		int offset = 0;
		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 207
		byte[] area207 = UnionBitPutFldIntoStr(fldTag.conEsscFldIDOfApp,
				idOfApp.getBytes(), idOfApp.length());
		System.arraycopy(area207, 0, reqbuf, offset, area207.length);
		offset += area207.length;

		// area2 091
		byte[] area091 = UnionBitPutFldIntoStr(fldTag.conEsscFldSignData,
				data.getBytes(), data.getBytes().length);
		System.arraycopy(area091, 0, reqbuf, offset, area091.length);
		offset += area091.length;

		// area3 093
		byte[] area093 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldSignDataPadFlag, flag.getBytes(),
				flag.length());
		System.arraycopy(area093, 0, reqbuf, offset, area093.length);
		offset += area093.length;

		// area4 214
		byte[] area214 = UnionBitPutFldIntoStr(fldTag.conEsscFldAlgorithmMode,
				hashID.getBytes(), hashID.length());
		System.arraycopy(area214, 0, reqbuf, offset, area214.length);
		offset += area214.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("134",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("134",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldSign), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}

	}

	/*
	 * 使用指定公钥验证 输入参数： keyName,公钥名称 lenOfData,签名数据的长度 data，签名数据 lenOfSign，签名长度
	 * flag，数据填充方式 hashID，HASH算法标志：01，SHA-1，02，MD5，03，ISO 1011802，04，NoHash
	 * sign，签名 输出参数： 无 返回值：<0,是错误码。否则是签名验证成功
	 */
	public int UnionNewVerifySignature(String keyName, String flag,
			String hashID, int lenOfData, String data, String sign,
			int lenOfSign) {
		if (keyName == null || keyName.length() == 0 || data == null
				|| data.length() == 0 || lenOfSign <= 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[data.getBytes().length + 200];
		int offset = 0;
		// area count is 5
		System.arraycopy("005".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				keyName.getBytes(), keyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 091
		byte[] area091 = UnionBitPutFldIntoStr(fldTag.conEsscFldSignData,
				data.getBytes(), data.getBytes().length);
		System.arraycopy(area091, 0, reqbuf, offset, area091.length);
		offset += area091.length;

		// area3 093
		byte[] area093 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldSignDataPadFlag, flag.getBytes(),
				flag.getBytes().length);
		System.arraycopy(area093, 0, reqbuf, offset, area093.length);
		offset += area093.length;

		// area4 214
		byte[] area214 = UnionBitPutFldIntoStr(fldTag.conEsscFldAlgorithmMode,
				hashID.getBytes(), hashID.getBytes().length);
		System.arraycopy(area214, 0, reqbuf, offset, area214.length);
		offset += area214.length;

		// area5 092
		UnionStr us = new UnionStr();
		byte[] signBcd = us.aschex_to_bcdhex(sign);
		byte[] area092 = UnionBitPutFldIntoStr(fldTag.conEsscFldSign, signBcd,
				signBcd.length);
		System.arraycopy(area092, 0, reqbuf, offset, area092.length);
		offset += area092.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("133",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("133",
					reqbuf, offset);
		}
		return len;
	}

	// 将ZMK加密的密钥存储到库中
	/*
	 * 输入参数： fullKeyName,keyValue,checkValue,分别为密钥全名，密钥密文（ZMK加密），校验值（加密全0）
	 * zmkName,是ZMK的名称 输出参数： 无 返回值：<0，是错误码，否则成功
	 */
	public int UnionStoreKeyBySpecZmk(String fullKeyName, String zmkName,
			String keyValue, String checkValue) {
		if (fullKeyName == null || fullKeyName.length() == 0
				|| keyValue == null || keyValue.length() == 0
				|| checkValue == null || checkValue.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[200];
		int offset = 0;
		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 011
		byte[] area011 = UnionBitPutFldIntoStr(fldTag.conEsscFldZMKName,
				zmkName.getBytes(), zmkName.getBytes().length);
		System.arraycopy(area011, 0, reqbuf, offset, area011.length);
		offset += area011.length;

		// area3 061
		byte[] area061 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyValue,
				keyValue.getBytes(), keyValue.getBytes().length);
		System.arraycopy(area061, 0, reqbuf, offset, area061.length);
		offset += area061.length;

		// area4 051
		byte[] area051 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyCheckValue,
				checkValue.getBytes(), checkValue.getBytes().length);
		System.arraycopy(area051, 0, reqbuf, offset, area051.length);
		offset += area051.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("286",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("286",
					reqbuf, offset);
		}
		return len;
	}

	// ============================== 数据加解密服务 ==============================
	/*
	 * 函数名称UnionEncryptData 功能使用指定的密钥加密数据 输入参数fullKeyName：加密密钥的名称，密钥用于加密数据
	 * clearDataLen：明文数据的长度 clearData：明文数据 initIV：初始化向量，可选；若不需要，填“NULL”
	 * arithmeticFlag：算法标志，可选，“0”，ECB，“1”，CBC； 若不需要，填“NULL”。 返回值：
	 * cryptograph：加密得到的密文数据，密文数据对应的明文是： 4字节明文长度+明文+补位‘0’
	 * 
	 * 密钥 1．fullKeyName：加密密钥，加密明文数据生成密文 服务代码 703
	 */
	public String UnionEncryptData(String fullKeyName, int clearDataLen,
			String clearData, String initIV, String arithmeticFlag) {
		if (fullKeyName == null || fullKeyName.length() == 0
				|| clearData == null || clearData.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[clearData.getBytes().length + 200];
		int offset = 0;
		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 081
		byte[] area081 = UnionBitPutFldIntoStr(fldTag.conEsscFldPlainData,
				clearData.getBytes(), clearData.getBytes().length);
		System.arraycopy(area081, 0, reqbuf, offset, area081.length);
		offset += area081.length;

		// area3 213
		byte[] area213 = UnionBitPutFldIntoStr(fldTag.conEsscFldIV,
				initIV.getBytes(), initIV.length());
		System.arraycopy(area213, 0, reqbuf, offset, area213.length);
		offset += area213.length;

		// area4 214
		byte[] area214 = UnionBitPutFldIntoStr(fldTag.conEsscFldAlgorithmMode,
				arithmeticFlag.getBytes(), arithmeticFlag.length());
		System.arraycopy(area214, 0, reqbuf, offset, area214.length);
		offset += area214.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("703",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("703",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldCiperData), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			//UnionStr us = new UnionStr();
			//return us.bcdhex_to_aschex(res.getBytes());
			return res;
		} else {
			return len + "";
		}
	}

	/*
	 * 函数名称 UnionDecryptData 功能 使用指定的密钥解密数据 输入参数 fullKeyName：解密密钥的名称，密钥用于解密数据
	 * cryptographLen：密文数据的长度 cryptograph：待解密的密文数据，密文数据对应的明文是： 4字节明文长度+明文+补位‘0’
	 * initIV：初始化向量，可选；若不需要，填“NULL” arithmeticFlag：算法标志，可选，“0”，ECB，“1”，CBC；
	 * 若不需要，填“NULL”。 返回值 clearData：解密出来的明文数据 密钥 1．fullKeyName：解密密钥，解密密文数据得到明文
	 * 服务代码 704
	 */
	public String UnionDecryptData(String fullKeyName, int cryptographLen,
			String cryptograph, String initIV, String arithmeticFlag) {
		if (fullKeyName == null || fullKeyName.length() == 0
				|| cryptograph == null || cryptograph.length() == 0
				|| initIV == null || initIV.length() == 0
				|| arithmeticFlag == null || arithmeticFlag.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[cryptograph.getBytes().length + 200];
		int offset = 0;
		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 082
		byte[] area082 = UnionBitPutFldIntoStr(fldTag.conEsscFldCiperData,
				cryptograph.getBytes(), cryptograph.getBytes().length);
		System.arraycopy(area082, 0, reqbuf, offset, area082.length);
		offset += area082.length;

		// area3 213
		byte[] area213 = UnionBitPutFldIntoStr(fldTag.conEsscFldIV,
				initIV.getBytes(), initIV.length());
		System.arraycopy(area213, 0, reqbuf, offset, area213.length);
		offset += area213.length;

		// area4 214
		byte[] area214 = UnionBitPutFldIntoStr(fldTag.conEsscFldAlgorithmMode,
				arithmeticFlag.getBytes(), arithmeticFlag.length());
		System.arraycopy(area214, 0, reqbuf, offset, area214.length);
		offset += area214.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("704",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("704",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldPlainData), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			//UnionStr us = new UnionStr();
			//return us.bcdhex_to_aschex(res.getBytes());
			return res;
		} else {
			return len + "";
		}
	}

	// 对字符串Hash计算摘要 520
	/*
	 * 输入参数： method,hashdata,datalen,分别为hash算法（01,02,03,04)，数据，数据长度
	 * 
	 * 输出参数： hashresult 返回值：<0，是错误码，否则成功
	 */
	public String UnionHash(String method, String hashdata, int datalen) {
		if (method == null || method.length() == 0 || hashdata == null
				|| hashdata.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[hashdata.getBytes().length + 100];
		int offset = 0;
		// area count is 2
		System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 214
		byte[] area214 = UnionBitPutFldIntoStr(fldTag.conEsscFldAlgorithmMode,
				method.getBytes(), method.length());
		System.arraycopy(area214, 0, reqbuf, offset, area214.length);
		offset += area214.length;

		// area2 083
		byte[] area083 = UnionBitPutFldIntoStr(fldTag.conEsscFldHashData,
				hashdata.getBytes(), hashdata.getBytes().length);
		System.arraycopy(area083, 0, reqbuf, offset, area083.length);
		offset += area083.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("520",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("520",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldHashDegist), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			UnionStr us = new UnionStr();
			return us.bcdhex_to_aschex(res.getBytes());
		} else {
			return len + "";
		}
	}

	/*
	 * 函数名称 UnionDecryptPin 功能 使用指定的ZPK密钥解密PIN密文，算法为ANSI X9.8/X9.18，这个函数，
	 * 将用户的PIN密文解密成PIN明文 输入参数 fullKeyName：ZPK密钥名称
	 * pinBlock：待解密的PIN密文，长度16字节字符串(ASCII)，以'\0'结束
	 * accNo：账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；
	 * 卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算 返回值
	 * clearPin：解密后的PIN明文，ASCII字符串，以'\0'结束。
	 * 
	 * 密钥 1．fullKeyName：ZPK密钥，解密PIN密文pinBlock 服务代码 434
	 */
	public String UnionDecryptPin(String fullKeyName, String pinBlock,
			String accNo) {

		if ((fullKeyName == null || fullKeyName.length() == 0)
				|| (accNo == null || accNo.length() == 0)
				|| (pinBlock == null || pinBlock.length() == 0)) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}

		byte[] reqbuf = new byte[pinBlock.getBytes().length + 100];
		int offset = 0;
		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pinBlock.getBytes(),
				pinBlock.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area3 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.length());
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("434",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("434",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldPlainPin), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/*
	 * 功能 把PINOFFSET转换为专用算法（FINSE算法）加密的密文 输入参数 pinOffset: PVK加密的PIN的偏移量 accNo:账号
	 * fullKeyNameByPVK: PVK密钥名称 输出参数 pinBlock: 由A算法生成的6位pin密文 返回值
	 * <0：函数执行失败，值为失败的错误码 >0： 函数执行成功
	 */
	public String unionTranslatePinOffSetWithFINSE(String pinOffset,
			String accNo, String fullKeyNameByPVK) {
		if ((pinOffset == null || pinOffset.length() == 0)
				|| (accNo == null || accNo.length() == 0)
				|| (fullKeyNameByPVK == null || fullKeyNameByPVK.length() == 0)) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		if (12 != pinOffset.length()) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		if (12 > accNo.length()) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		if (12 < accNo.length())
			accNo = accNo.substring(accNo.length() - 13, accNo.length() - 1);

		byte[] reqbuf = new byte[pinOffset.getBytes().length + 100];
		int offset = 0;
		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 036
		byte[] area036 = UnionBitPutFldIntoStr(fldTag.conEsscFldVisaPVV,
				pinOffset.getBytes(), pinOffset.length());
		System.arraycopy(area036, 0, reqbuf, offset, area036.length);
		offset += area036.length;

		// area2 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.length());
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		// area3 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyNameByPVK.getBytes(), fullKeyNameByPVK.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("520",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("520",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByZPK), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/*
	 * 功能 把PINBLOCK转换为专用算法（FINSE算法）加密的密文 输入参数 OriPinBlock: ZPK加密的PIN密文 fullName:
	 * 网银系统加密PIN的ZPK密钥名称 accNo: 账号 输出参数 pinBlock: 由A算法生成的6位pin密文 返回值 <0：
	 * 函数执行失败，值为失败的错误码 >0： 函数执行成功
	 */
	/*public String unionTranslatePinBlockWithFINSE(String OriPinBlock,
			String fullKeyNameByZPK, String accNo) {
		if ((OriPinBlock == null || OriPinBlock.length() == 0)
				|| (fullKeyNameByZPK == null || fullKeyNameByZPK.length() == 0)
				|| (accNo == null || accNo.length() == 0)) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		if (12 > accNo.length()) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		if (12 < accNo.length())
			accNo = accNo.substring(accNo.length() - 13, accNo.length() - 1);

		byte[] reqbuf = new byte[OriPinBlock.getBytes().length + 100];
		int offset = 0;
		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, OriPinBlock.getBytes(),
				OriPinBlock.length());
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area2 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyNameByZPK.getBytes(), fullKeyNameByZPK.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area3 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.length());
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("521",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("521",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByZPK), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}
*/
	
	public int  UnionAddOneNodeKey(String keyName,int lenOfZMK,int lenOfZPK,int lenOfZAK){
		if (keyName == null || keyName.length() == 0 ) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}

		byte[] reqbuf = new byte[200];
		int offset = 0;

		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				keyName.getBytes(), keyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 071
		byte[] area071 = UnionBitPutFldIntoStr(fldTag.conEsscFldVisaCVV,
				(lenOfZMK+"").getBytes(), (lenOfZMK+"").getBytes().length);
		System.arraycopy(area071, 0, reqbuf, offset, area071.length);
		offset += area071.length;

		// area3 072
		byte[] area072 = UnionBitPutFldIntoStr(fldTag.conEsscFldCardPeriod,
				(lenOfZPK+"").getBytes(), (lenOfZPK+"").getBytes().length);
		System.arraycopy(area072, 0, reqbuf, offset, area072.length);
		offset += area072.length;

		// area4 073
		byte[] area073 = UnionBitPutFldIntoStr(fldTag.conEsscFldServiceID,
				(lenOfZAK+"").getBytes(), (lenOfZAK+"").getBytes().length);
		System.arraycopy(area073, 0, reqbuf, offset, area073.length);
		offset += area073.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("402",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("402",
					reqbuf, offset);
		}
		return len;
	
	}
	// ===================== RSA密钥对产生函数 =====================
	// 随机产生一对RSA密钥对

	/*
	 * 输入参数：使用指定应用的私钥进行签名 输入参数： idOfApp,应用编号 keyLenth,RSA密钥对长度）
	 * 生成指定索引的RSA密钥对，私钥存在密码机中和应用编号关联。公钥以如下名称存储在公钥库中：GrpXXX.vkIndeYY.pk。
	 * 其中XXX是密码机组号，YY是私钥在密码机中的存储索引。 密码机组号是执行本命令的密码机组。
	 * 
	 * 输出参数： 无 返回值：<0,是错误码。0 成功。
	 */
	public String UnionGenerateRSA(String idOfApp) {
		if (idOfApp == null || idOfApp.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}

		byte[] reqbuf = new byte[idOfApp.length() + 100];
		int offset = 0;
		// area count is 1
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 207
		byte[] area207 = UnionBitPutFldIntoStr(fldTag.conEsscFldIDOfApp,
				idOfApp.getBytes(), idOfApp.length());
		System.arraycopy(area207, 0, reqbuf, offset, area207.length);
		offset += area207.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("212",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("212",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyValue), "GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}

	}

	// add by hzh in 2011.9.14 增加一函数，去除字符串末尾的0x00

	public static String TrimBitZero(String buf) {
		String str = null;
		byte[] bt = { 0x00 };
		int i = 0;
		String btstr = new String(bt);

		if (buf == null || buf.length() == 0)
			return "";
		try {
			for (i = buf.length() - 1; i >= 0; i--) {
				if (buf.substring(i, i + 1).equals(btstr) != true)
					break;
			}
			if (i < 0)
				return "";
		} catch (Exception e) {
			return "";
		}
		return buf.substring(0, i + 1);
	}

	// add end

	// add by hzh in 2012.3.15 增加一函数，去除字符串末尾的填充字符
	public static String AddFillFromBuf(String buf, String fill) {
		int i = 0;
		int len = 0;
		byte[] bt = { 0x00 };
		String tstr = "";

		if (buf == null || buf.length() == 0)
			return "";

		if (fill == null || fill.length() == 0 || fill.getBytes().length != 1)
			fill = new String(bt);

		len = buf.getBytes().length;
		if (len % 8 == 0)
			return buf;
		int lenfill = 8 - len % 8;

		for (i = 0; i < lenfill; i++) {
			tstr += fill;
		}

		buf = buf + tstr;

		return buf;
	}

	public static String TrimFillFromBuf(String buf, String fill) {
		int i = 0;
		int j = 0;
		byte[] bt = { 0x00 };

		if (buf == null || buf.length() == 0)
			return "";

		if (fill == null || fill.length() == 0 || fill.getBytes().length != 1)
			fill = new String(bt);

		try {
			for (i = buf.length() - 1; i >= 0; i--, j++) {
				if (j >= 7)
					break;
				if (buf.substring(i, i + 1).equals(fill) != true)
					break;
			}
			if (i < 0)
				return "";
		} catch (Exception e) {
			return "";
		}
		return buf.substring(0, i + 1);
	}

	// add end

	// ===================== 加密数据接口函数 =====================
	// 用指定的一把zek加密数据
	/*
	 * 输入参数：zekName, data, encFlag, iv zekName： 加密密钥全名 data： 数据 encFlag：
	 * 算法标识：“0”，ECB，“1”，CBC；若不需要，填“NULL” iv： 初始化向量 fill: 补位字符 返回值： 加密后的数据（扩展后的）。
	 */
	public String UnionEncryptPlainDataAddFill(String zekName, String data,
			String encFlag, String iv, String fill) {
		if (zekName == null || zekName.length() == 0 || data == null
				|| data.length() == 0
				|| (encFlag.contentEquals("1") && iv.length() == 0)) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[data.getBytes().length + 200];
		int offset = 0;
		// area count is 2 or 4
		if (encFlag.contentEquals("1")) {
			System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		} else {
			System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		}
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zekName.getBytes(), zekName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		data = AddFillFromBuf(data, fill);

		// area2 081
		byte[] area081 = UnionBitPutFldIntoStr(fldTag.conEsscFldPlainData,
				data.getBytes(), data.getBytes().length);
		System.arraycopy(area081, 0, reqbuf, offset, area081.length);
		offset += area081.length;

		if (encFlag.contentEquals("1")) {
			// area3 214
			byte[] area214 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldAlgorithmMode, encFlag.getBytes(),
					encFlag.getBytes().length);
			System.arraycopy(area214, 0, reqbuf, offset, area214.length);
			offset += area214.length;

			// area4 213
			byte[] area213 = UnionBitPutFldIntoStr(fldTag.conEsscFldIV,
					iv.getBytes(), iv.getBytes().length);
			System.arraycopy(area213, 0, reqbuf, offset, area213.length);
			offset += area213.length;
		}

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("516",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("516",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldCiperData), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	// ===================== 加密数据接口函数 =====================
	// 用指定的一把zek解密数据
	/*
	 * 输入参数：zekName, encData, encFlag, iv zekName： 加密密钥全名 encData： 密文数据 encFlag：
	 * 算法标识：“0”，ECB，“1”，CBC；若不需要，填“NULL” iv： 初始化向量 fill 应去除的补位字符 返回值： 解密数据
	 */
	public String UnionDecryptEncDataDelFill(String zekName, String encData,
			String encFlag, String iv, String fill) {

		byte[] result;
		if (zekName == null || zekName.length() == 0 || encData == null
				|| encData.length() == 0
				|| (encFlag.contentEquals("1") && iv.length() == 0)) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}

		byte[] reqbuf = new byte[encData.getBytes().length + 200];
		int offset = 0;
		// area count is 2 or 4
		if (encFlag.contentEquals("1")) {
			System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		} else {
			System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		}
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zekName.getBytes(), zekName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 082
		byte[] area082 = UnionBitPutFldIntoStr(fldTag.conEsscFldCiperData,
				encData.getBytes(), encData.getBytes().length);
		System.arraycopy(area082, 0, reqbuf, offset, area082.length);
		offset += area082.length;

		if (encFlag.contentEquals("1")) {
			// area3 214
			byte[] area214 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldAlgorithmMode, encFlag.getBytes(),
					encFlag.getBytes().length);
			System.arraycopy(area214, 0, reqbuf, offset, area214.length);
			offset += area214.length;

			// area4 213
			byte[] area213 = UnionBitPutFldIntoStr(fldTag.conEsscFldIV,
					iv.getBytes(), iv.getBytes().length);
			System.arraycopy(area213, 0, reqbuf, offset, area213.length);
			offset += area213.length;
		}

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("517",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("517",
					reqbuf, offset);
		}
		if (len >= 0) {
			String data = "";
			try {
				data = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldPlainData), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			UnionStr us = new UnionStr();
			result = us.aschex_to_bcdhex(data);
			try {
				data = new String(result, "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			data = TrimFillFromBuf(data, fill);
			return data;
		} else {
			return len + "";
		}

	}

	public boolean UnionVerifyARQCUsingDerivedKey(String fullName,
			int keyVersion, String pan, String processGene, String data,
			String ARQC, int iccType) {
		String strKeyVersion = String.valueOf(keyVersion);
		String strIccType = String.valueOf(iccType);

		DecimalFormat df = new DecimalFormat("0000");
		String buf = new String();
		buf += "007";
		// MK-AC密钥名称
		buf += "001" + df.format(fullName.length()) + fullName;
		// 密钥版本
		buf += "410" + df.format(strKeyVersion.length()) + strKeyVersion;
		// 卡号
		buf += "041" + df.format(pan.length()) + pan;
		// 离散过程因子
		if (iccType == 0)
			buf += "303" + df.format(processGene.length()) + processGene;
		else if (iccType == 1)
			buf += "303" + "0004" + "0000";
		else {
			log.debug("IC卡类型错误");
			return false;
		}

		// 离散数据
		buf += "304" + df.format(data.length()) + data;
		// ARQC
		buf += "313" + df.format(ARQC.length()) + ARQC;
		// IC卡类型
		buf += "214" + df.format(strIccType.length()) + strIccType;

		int reqLen = buf.length();
		byte head[] = { 0, 0 };
		head[0] = Integer.valueOf(reqLen / 256).byteValue();
		head[1] = Integer.valueOf(reqLen % 256).byteValue();

		int offset = 0;
		// offset=reqLen-4-2;
		offset = reqLen;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("525",
					buf.getBytes(), offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("525",
					buf.getBytes(), offset);
		}
		if (len >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public String UnionGenerateARPCUsingDerivedKey(String fullName,
			int keyVersion, String pan, String processGene, String data,
			String ARQC, String ARC, int iccType) {
		String strKeyVersion = String.valueOf(keyVersion);
		String strIccType = String.valueOf(iccType);

		DecimalFormat df = new DecimalFormat("0000");
		String buf = new String();
		buf += "008";
		// MK-AC密钥名称
		buf += "001" + df.format(fullName.length()) + fullName;
		// 密钥版本
		buf += "410" + df.format(strKeyVersion.length()) + strKeyVersion;
		// 卡号
		buf += "041" + df.format(pan.length()) + pan;
		// 离散过程因子
		if (iccType == 0)
			buf += "303" + df.format(processGene.length()) + processGene;
		else if (iccType == 1)
			buf += "303" + "0004" + "0000";
		else {
			logger.debug("调用API接口传入参数错误，IC卡类型错误");
			return "-5501";
		}
		// 离散数据
		buf += "304" + df.format(data.length()) + data;
		// ARQC
		buf += "313" + df.format(ARQC.length()) + ARQC;
		// 授权响应码
		buf += "315" + df.format(ARC.length()) + ARC;
		// IC卡类型
		buf += "214" + df.format(strIccType.length()) + strIccType;

		int reqLen = buf.length();
		byte head[] = { 0, 0 };
		head[0] = Integer.valueOf(reqLen / 256).byteValue();
		head[1] = Integer.valueOf(reqLen % 256).byteValue();

		int offset = 0;
		offset = reqLen;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("526",
					buf.getBytes(), offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("526",
					buf.getBytes(), offset);
		}

		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len, 314), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	public String UnionGenerateARPCUsingDerivedKey2(String fullName,
			int keyVersion, String pan, String processGene, String ARQC,
			String ARC, int iccType) {
		String strKeyVersion = String.valueOf(keyVersion);
		String strIccType = String.valueOf(iccType);

		DecimalFormat df = new DecimalFormat("0000");
		String buf = new String();
		buf += /* appID + "5291" + */"007";
		// MK-AC密钥名称
		buf += "001" + df.format(fullName.length()) + fullName;
		// 密钥版本
		buf += "410" + df.format(strKeyVersion.length()) + strKeyVersion;
		// 卡号
		buf += "041" + df.format(pan.length()) + pan;
		// 离散过程因子
		if (iccType == 0)
			buf += "303" + df.format(processGene.length()) + processGene;
		else if (iccType == 1)
			buf += "303" + "0004" + "0000";
		else {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}

		// ARQC
		buf += "313" + df.format(ARQC.length()) + ARQC;
		// 授权响应码
		buf += "315" + df.format(ARC.length()) + ARC;
		// IC卡类型
		buf += "214" + df.format(strIccType.length()) + strIccType;

		int reqLen = buf.length();
		byte head[] = { 0, 0 };
		head[0] = Integer.valueOf(reqLen / 256).byteValue();
		head[1] = Integer.valueOf(reqLen % 256).byteValue();

		int offset = 0;
		offset = reqLen;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("529",
					buf.getBytes(), offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("529",
					buf.getBytes(), offset);
		}

		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len, 314), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	public String UnionGenerateMacUsingDerivedKey(String fullName,
			int keyVersion, String pan, String processGene, String data) {
		if (fullName == null || fullName.length() == 0 || pan == null
				|| pan.length() == 0 || processGene == null
				|| processGene.length() == 0 || data == null
				|| data.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[data.getBytes().length + 200];
		int offset = 0;

		// area count is 5
		System.arraycopy("005".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullName.getBytes(), fullName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 410
		byte[] area410 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyVersion,
				(keyVersion + "").getBytes(), (keyVersion + "").length());
		System.arraycopy(area410, 0, reqbuf, offset, area410.length);
		offset += area410.length;

		// area3 426
		byte[] area426 = UnionBitPutFldIntoStr(fldTag.conEsscDisperseData,
				pan.getBytes(), pan.getBytes().length);
		System.arraycopy(area426, 0, reqbuf, offset, area426.length);
		offset += area426.length;

		// area4 303
		byte[] area303 = UnionBitPutFldIntoStr(fldTag.conEsscFldRandNum,
				processGene.getBytes(), processGene.getBytes().length);
		System.arraycopy(area303, 0, reqbuf, offset, area303.length);
		offset += area303.length;

		// area5 081
		byte[] area081 = UnionBitPutFldIntoStr(fldTag.conEsscFldPlainData,
				data.getBytes(), data.getBytes().length);
		System.arraycopy(area081, 0, reqbuf, offset, area081.length);
		offset += area081.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("528",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("528",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldMac), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			UnionStr us = new UnionStr();
			return us.bcdhex_to_aschex(result.getBytes());
		} else {
			return len + "";
		}
	}

	public String UnionEncryptDataUsingDerivedKey(int encryptMode,
			String fullName, int keyVersion, String pan, String processGene,
			String data) {
		if (fullName == null || fullName.length() == 0 || pan == null
				|| pan.length() == 0 || processGene == null
				|| processGene.length() == 0 || data == null
				|| data.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[data.getBytes().length + 200];
		int offset = 0;

		// area count is 6
		System.arraycopy("006".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullName.getBytes(), fullName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 410
		byte[] area410 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyVersion,
				(keyVersion + "").getBytes(), (keyVersion + "").length());
		System.arraycopy(area410, 0, reqbuf, offset, area410.length);
		offset += area410.length;

		// area3 214
		byte[] area214 = UnionBitPutFldIntoStr(fldTag.conEsscFldAlgorithmMode,
				(encryptMode + "").getBytes(), (encryptMode + "").length());
		System.arraycopy(area214, 0, reqbuf, offset, area214.length);
		offset += area214.length;

		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				pan.getBytes(), pan.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		// area5 303
		byte[] area303 = UnionBitPutFldIntoStr(fldTag.conEsscFldRandNum,
				processGene.getBytes(), processGene.getBytes().length);
		System.arraycopy(area303, 0, reqbuf, offset, area303.length);
		offset += area303.length;

		// area6 081
		byte[] area081 = UnionBitPutFldIntoStr(fldTag.conEsscFldPlainData,
				data.getBytes(), data.getBytes().length);
		System.arraycopy(area081, 0, reqbuf, offset, area081.length);
		offset += area081.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("527",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("527",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldCiperData), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			UnionStr us = new UnionStr();
			return us.bcdhex_to_aschex(result.getBytes());
		} else {
			return len + "";
		}
	}

	// ============================== 脱机PIN加密-ZPK加密X9.8格式密文PIN输入
	// ==============================
	// 脱机PIN加密-PVK加密Pinoffset输入
	/*
	 * 输入参数： mksmc ： mk-smc密钥名称 mksmcVersion: mk-smc密钥版本号 encryptMode：加密模式（0 =
	 * CBC, 1 = ECB） panNum： 离散卡片密钥使用的帐号或者帐号序列号 Atc： ATC zpk： zpk 密钥全称
	 * zpkVersion: zpk密钥版本号 pinCryptData： PIN密文 account：账号 linkOffPin：脱机PIN模式
	 * 
	 * 
	 * 
	 * 输出参数： cryptograph： 密文PIN数据
	 * 
	 * 返回值： >=0 成功，密文数据的长度 <0 失败，返回错误码
	 */

	public String UnionEncryptLinkOffPinByZPK(String mksmc, int mksmcVersion,
			String encryptMode, String methodID, String panNum, String Atc,
			String zpk, String pinCryptData, String account, String linkOffPin) {
		if (mksmc == null || mksmc.length() == 0 || mksmcVersion < 0
				|| encryptMode == null || encryptMode.length() == 0
				|| methodID == null || methodID.length() == 0 || panNum == null
				|| panNum.length() == 0 || Atc == null || Atc.length() == 0
				|| zpk == null || zpk.length() == 0 || pinCryptData == null
				|| pinCryptData.length() == 0 || account == null
				|| account.length() == 0 || linkOffPin == null
				|| linkOffPin.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[pinCryptData.getBytes().length + 200];
		int offset = 0;

		// area count is 10
		System.arraycopy("010".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				mksmc.getBytes(), mksmc.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 410
		byte[] area410 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyVersion,
				(mksmcVersion + "").getBytes(),
				(mksmcVersion + "").getBytes().length);
		System.arraycopy(area410, 0, reqbuf, offset, area410.length);
		offset += area410.length;

		// area3 214
		byte[] area214 = UnionBitPutFldIntoStr(fldTag.conEsscFldAlgorithmMode,
				encryptMode.getBytes(), encryptMode.getBytes().length);
		System.arraycopy(area214, 0, reqbuf, offset, area214.length);
		offset += area214.length;

		// area4 201
		byte[] area201 = UnionBitPutFldIntoStr(fldTag.conEsscFldHsmGrpID,
				methodID.getBytes(), methodID.getBytes().length);
		System.arraycopy(area201, 0, reqbuf, offset, area201.length);
		offset += area201.length;

		// area5 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				panNum.getBytes(), panNum.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		// area6 315
		byte[] area315 = UnionBitPutFldIntoStr(fldTag.conEsscFldARC,
				Atc.getBytes(), Atc.getBytes().length);
		System.arraycopy(area315, 0, reqbuf, offset, area315.length);
		offset += area315.length;

		// area7 002
		byte[] area002 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				zpk.getBytes(), zpk.getBytes().length);
		System.arraycopy(area002, 0, reqbuf, offset, area002.length);
		offset += area002.length;

		// area8 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pinCryptData.getBytes(),
				pinCryptData.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area9 042
		byte[] area042 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo2,
				account.getBytes(), account.getBytes().length);
		System.arraycopy(area042, 0, reqbuf, offset, area042.length);
		offset += area042.length;

		// area10 221
		byte[] area221 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyTypeFlag,
				linkOffPin.getBytes(), linkOffPin.getBytes().length);
		System.arraycopy(area221, 0, reqbuf, offset, area221.length);
		offset += area221.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("556",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("556",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldCiperData), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			/*System.arraycopy(result.getBytes(), 0, cryptograph, 0,
					getLength(result));*/
			return result;
		} else {
			return len+"";
		}
	}

	public int getLength(String packageBufFld) {
		String str = null;
		try {
			str = new String(packageBufFld.getBytes("GBK"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		return str.length();
	}

	/*
	 * 函数名称 UnionConvertEncryptWithKeyVersion 功能 转加密数据，带密钥版本号 输入参数
	 * oriFullKeyName：源密钥的名称，密钥用于解密数据 oriKeyVersion： 源密钥密钥类型 oriDataLen： 密文数据长度
	 * oriData： 源密文数据 oriArithmeticFlag: 源加密算法,源算法标志，可选： "00”=DES_ECB
	 * “01”=DES_ECB_LP “02”=DES_ECB_P “10”=DES_CBC “11”=DES_CBC_LP
	 * “12”=DES_CBC_P “20”=M/Chip4（CBC模式，强制填充X80） “21”=VISA/PBOC（带长度指引的ECB）；
	 * 若不需要，填“NULL” initIV：初始化向量，可选；若不需要，填“NULL” destFullKeyName: 目的加密密钥全名
	 * destKeyVersion: 目的加密密钥类型 destArithmeticFlag: 目的算法标志，可选： “00”= DES_ECB
	 * “02”= DES_ECB_P “0” 若不需要，填“NULL” 返回值 destData： 转加密后的数据 密钥 服务代码 539
	 */
	public String UnionConvertEncryptWithKeyVersion(String oriFullKeyName,
			String oriKeyVersion, int oriDataLen, String oriData,
			String oriArithmeticFlag, String initIV, String destFullKeyName,
			String destKeyVersion, String destArithmeticFlag) {
		if (oriFullKeyName == null || oriFullKeyName.length() == 0
				|| oriData == null || oriData.length() == 0
				|| oriArithmeticFlag == null || oriArithmeticFlag.length() == 0
				|| destFullKeyName == null || destFullKeyName.length() == 0
				|| destKeyVersion == null || destKeyVersion.length() == 0
				|| destArithmeticFlag == null
				|| destArithmeticFlag.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[oriData.getBytes().length + 200];
		int offset = 0;

		// area count is 7 or 8
		if (oriArithmeticFlag.equals("10") || oriArithmeticFlag.equals("11")
				|| oriArithmeticFlag.equals("12")
				|| oriArithmeticFlag.equals("20")) {
			System.arraycopy("008".getBytes(), 0, reqbuf, offset, 3);
		} else {
			System.arraycopy("007".getBytes(), 0, reqbuf, offset, 3);
		}
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 221
		byte[] area221 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyTypeFlag,
				oriKeyVersion.getBytes(), oriKeyVersion.getBytes().length);
		System.arraycopy(area221, 0, reqbuf, offset, area221.length);
		offset += area221.length;

		// area2 081
		byte[] area081 = UnionBitPutFldIntoStr(fldTag.conEsscFldData,
				oriData.getBytes(), oriData.getBytes().length);
		System.arraycopy(area081, 0, reqbuf, offset, area081.length);
		offset += area081.length;

		// area3 214
		byte[] area214 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldAlgorithm01Mode, oriArithmeticFlag.getBytes(),
				oriArithmeticFlag.getBytes().length);
		System.arraycopy(area214, 0, reqbuf, offset, area214.length);
		offset += area214.length;

		if (oriArithmeticFlag.equals("10") || oriArithmeticFlag.equals("11")
				|| oriArithmeticFlag.equals("12")
				|| oriArithmeticFlag.equals("20")) {
			// area4 213
			byte[] area213 = UnionBitPutFldIntoStr(fldTag.conEsscFldIV,
					initIV.getBytes(), initIV.getBytes().length);
			System.arraycopy(area213, 0, reqbuf, offset, area213.length);
			offset += area213.length;
		}

		// area5 203
		byte[] area203 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyLenFlag,
				destKeyVersion.getBytes(), destKeyVersion.getBytes().length);
		System.arraycopy(area203, 0, reqbuf, offset, area203.length);
		offset += area203.length;

		// area6 215
		byte[] area215 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldAlgorithm02Mode,
				destArithmeticFlag.getBytes(),
				destArithmeticFlag.getBytes().length);
		System.arraycopy(area215, 0, reqbuf, offset, area215.length);
		offset += area215.length;

		UnionStr ua = new UnionStr();

		// area7 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldFirstWKName,
				oriFullKeyName.getBytes(), oriFullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area8 002
		byte[] area002 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				destFullKeyName.getBytes(), destFullKeyName.getBytes().length);
		System.arraycopy(area002, 0, reqbuf, offset, area002.length);
		offset += area002.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("539",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("539",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldData), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			UnionStr us = new UnionStr();
			return us.bcdhex_to_aschex(result.getBytes());
		} else {
			return len + "";
		}

	}

	/*
	 * 函数名称 UnionDerivePinByLmkFromPinOffset 功能
	 * 由PVK加密的IBMPinOffset导出一个LMK加密的PIN，用IBM方式产生一个 PIN的PIN Offset 输入参数
	 * pvkName：PVK密钥名称 pinOffset：PVK生成的PIN Offset，左对齐，右补‘F’
	 * accNo：账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算
	 * pinLength：最小PIN长度，通常为4
	 * 
	 * 返回值 pinByLmk：LMK0203对加密的PIN密文 密钥 1．pvkName：PVK密钥，加密PIN生成pinOffset
	 * 2．LMK0203对：存储在加密机中 服务代码 402
	 */
	public String UnionDerivePinByLmkFromPinOffset(String pvkName,
			String pinOffset, String accNo, String pinLength) {
		if (pvkName == null || pvkName.length() == 0 || pinOffset == null
				|| pinOffset.length() == 0 || accNo == null
				|| accNo.length() == 0 || pinLength == null
				|| pinLength.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}

		byte[] reqbuf = new byte[pinOffset.getBytes().length + 200];
		int offset = 0;

		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				pvkName.getBytes(), pvkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 037
		byte[] area037 = UnionBitPutFldIntoStr(fldTag.conEsscFldIBMPinOffset,
				pinOffset.getBytes(), pinOffset.getBytes().length);
		System.arraycopy(area037, 0, reqbuf, offset, area037.length);
		offset += area037.length;

		// area3 205
		byte[] area205 = UnionBitPutFldIntoStr(fldTag.conEsscFldForPinLength,
				pinLength.getBytes(), pinLength.getBytes().length);
		System.arraycopy(area205, 0, reqbuf, offset, area205.length);
		offset += area205.length;

		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("402",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("402",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByLMK0203), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return result;
		} else {
			return len + "";
		}
	}

	/*
	 * 函数名称 UnionDerivePinBlockFromPinByLmk 功能 将一个LMK0203对加密的PIN转换为由ZPK加密
	 * ZPK加密PIN采用的加密标准为ANSI X9.8/X9.18 输入参数 zpkName：ZPK密钥名称
	 * pinByLmk：LMK0203对加密的PIN accNo：账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；
	 * 卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算 返回值 pinBlock：ZPK加密的PIN 密钥
	 * 1．zpkName：ZPK密钥，加密PIN生成pinBlock 2．LMK0203对：存储在密码机中 服务代码 308
	 */
	public String UnionDerivePinBlockFromPinByLmk(String zpkName,
			String pinByLmk, String accNo) {
		if (zpkName == null || zpkName.length() == 0 || pinByLmk == null
				|| pinByLmk.length() == 0 || accNo == null
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}

		byte[] reqbuf = new byte[pinByLmk.getBytes().length + 200];
		int offset = 0;

		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zpkName.getBytes(), zpkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 035
		byte[] area035 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByLMK0203, pinByLmk.getBytes(),
				pinByLmk.getBytes().length);
		System.arraycopy(area035, 0, reqbuf, offset, area035.length);
		offset += area035.length;

		// area3 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("308",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("308",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByZPK), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return result;
		} else {
			return len + "";
		}
	}

	// add by liangqf 2014-01-23
	/**
	 * 从远程申请下载指定密钥的当前密钥值（291）
	 * 
	 * @param fullKeyName
	 *            密钥名称
	 * @return 成功 >=0 失败<0
	 */
	public int UnionApplyCurrentKey(String fullKeyName) {
		if (fullKeyName == null || fullKeyName.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}

		byte[] reqbuf = new byte[fullKeyName.getBytes().length + 200];
		int offset = 0;

		// area count is 1
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("291",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("291",
					reqbuf, offset);
		}
		return len;
	}

	/**
	 * 292 从远程申请下载指定密钥的新密钥值，即：申请远程平台生成对应密钥的新值，并将新值下载到本地，替换本地密钥的值。
	 * 
	 * @param fullKeyName
	 *            要下载的密钥名称
	 * @return 成功 >=0 失败<0
	 */
	public int UnionApplyNewKey(String fullKeyName) {
		if (fullKeyName == null || fullKeyName.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}

		byte[] reqbuf = new byte[fullKeyName.getBytes().length + 200];
		int offset = 0;

		// area count is 1
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("292",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("292",
					reqbuf, offset);
		}
		return len;
	}

	/**
	 * 293 将本地平台的指定密钥的当前值，分发到远程平台；远程平台会使用分发的该值，替换库中对应密钥的值
	 * 
	 * @param fullKeyName
	 *            要分发的密钥名称
	 * @return 成功 >=0 失败<0
	 */
	public int UnionDeployCurrentKey(String fullKeyName) {
		if (fullKeyName == null || fullKeyName.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}

		byte[] reqbuf = new byte[fullKeyName.getBytes().length + 200];
		int offset = 0;

		// area count is 1
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("293",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("293",
					reqbuf, offset);
		}
		return len;
	}

	/**
	 * 294 在本地平台生成指定密钥的新值并替换库中的密钥，然后将新值分发到远程平台；远程平台会使用分发的该值，替换库中对应密钥的值。
	 * 
	 * @param fullKeyName
	 *            要分发的密钥名称
	 * @return 成功 >=0 失败<0
	 */
	public int UnionDeployNewKey(String fullKeyName) {
		if (fullKeyName == null || fullKeyName.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}

		byte[] reqbuf = new byte[fullKeyName.getBytes().length + 200];
		int offset = 0;

		// area count is 1
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("294",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("294",
					reqbuf, offset);
		}
		return len;
	}

	/**
	 * 使用指定的密码机组生成一对RSA密钥对（211）
	 * 
	 * @param vkIndex
	 *            私钥索引号，即私钥在密码机中的存储索引，范围在‘00’－‘19’之间。
	 * @param lenOfRsaPair
	 *            RSA密钥对的长度，其值可以为‘512’、‘1024’、‘2048’、‘4096’和‘8192’
	 * @return 公开密钥
	 */
	public String UnionGenerateRsaPairAtHsmGrp(String vkIndex, int lenOfRsaPair) {
		if ((vkIndex == null || vkIndex.length() == 0)) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;

		// area count is 2
		System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 210
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyIndex,
				vkIndex.getBytes(), vkIndex.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 208
		byte[] area2 = UnionBitPutFldIntoStr(fldTag.conEsscFldLengthOfKey,
				(lenOfRsaPair + "").getBytes(), (lenOfRsaPair + "").length());
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("211",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("211",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyValue), "GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
		} else {
			return len + "";
		}
	}

	/**
	 * 生成PKCS10应用的RSA密钥对
	 * 
	 * @param idOfApp
	 *            应用编号
	 * @param keyLenFlag
	 *            密钥长度标识，其值可以为‘512’、‘1024’、‘2048’和‘0’。长度标识为‘0’表示不产生密钥，
	 *            而是取存在文件里的公钥输出
	 * @return
	 */
	public String UnionGenerateRsaPairForPKCS10(String idOfApp, int keyLenFlag) {
		if ((idOfApp == null || idOfApp.length() == 0)) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;

		// area count is 2
		System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 072
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldCardPeriod,
				idOfApp.getBytes(), idOfApp.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 208
		byte[] area2 = UnionBitPutFldIntoStr(fldTag.conEsscFldLengthOfKey,
				(keyLenFlag + "").getBytes(), (keyLenFlag + "").length());
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("512",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("512",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyValue), "GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
		} else {
			return len + "";
		}
	}

	/**
	 * 读取密钥库中存储的公钥，并输出
	 * 
	 * @param fullKeyName
	 *            公钥的名称
	 * @return 公开密钥
	 */
	public String UnionReadPK(String fullKeyName) {
		if (fullKeyName == null || fullKeyName.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}

		byte[] reqbuf = new byte[fullKeyName.getBytes().length + 200];
		int offset = 0;

		// area count is 1
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("511",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("511",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyValue), "GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
		} else {
			return len + "";
		}
	}

	// 将一个ZPK加密的PIN密文转换为另一个ZPK加密的PIN密文(同301)
	// 加密算法为Ansi x9.8/x9.18
	/*
	 * 输入参数：fullKeyName1,fullKeyName2,pinBlock1,accNo fullKeyName1：源密钥全名
	 * fullKeyName2：目的密钥全名
	 * pinBlock1：源密钥加密的PIN密文(PIN格式为ANSI9。8),长度16字节字符串(ASCII), 以'\0'结束。
	 * accNo：源账号/卡号,长度13-19字节字符串(ASCII),
	 * 以'\0'结束;卡号为实际卡号;账号和卡号填16个ASCII'0',表示无账号运算。 输出参数：pinBlock2,
	 * pinBlock2：目的成员行机构号加密的PIN密文(PIN格式为ANSI9.8), 长度16字节字符串(ASCII), 以'\0'结束
	 * 返回值：<0,是错误码。0 成功。
	 */
	public String UnionTranslateAnotherPin(String fullKeyName1,
			String fullKeyName2, String pinBlock1, String accNo) {
		if (fullKeyName1 == null || fullKeyName1.length() == 0
				|| fullKeyName2 == null || fullKeyName2.length() == 0
				|| pinBlock1 == null || pinBlock1.length() == 0
				|| accNo == null || accNo.length() == 0 || pinBlock1 == null
				|| pinBlock1.length() != 16) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldFirstWKName,
				fullKeyName1.getBytes(), fullKeyName1.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 002
		byte[] area002 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				fullKeyName2.getBytes(), fullKeyName2.getBytes().length);
		System.arraycopy(area002, 0, reqbuf, offset, area002.length);
		offset += area002.length;

		// area3 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK1, pinBlock1.getBytes(),
				pinBlock1.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;
		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("305",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("305",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByZPK2), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}

	}

	/**
	 * 源帐号加密转换为目的帐号加密并同时产生Pinoffset 309
	 * 由源帐号生成的PINBLOCK转换为由目的帐号生成的PINBLOCK，并受指定的密钥加密保护，同时产生目的帐号的PINOFFSET
	 * 
	 * @param pvkKeyName
	 *            ZPK名称
	 * @param zpkKeyName
	 *            PVK名称
	 * @param pin
	 *            PIN密文
	 * @param srcAcco
	 *            源帐号
	 * @param desAcco
	 *            目的帐号
	 * @return ZPK由目的帐号加密的PIN密文 使用目的帐号产生的 PIN Offset，左对齐，右补‘F’
	 */
	public String[] UnionTranslatePinAndGenerateOffset(String zpkKeyName,String pvkKeyName,
		String pin, String srcAcco, String desAcco) {
		String result[] = { "", "" };
		if (pvkKeyName == null || pvkKeyName.length() == 0
				|| zpkKeyName == null || zpkKeyName.length() == 0
				|| pin == null || pin.length() == 0 || srcAcco == null
				|| srcAcco.length() == 0 || desAcco == null
				|| desAcco.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			result[0] = "-5501";
			return result;
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;

		// area count is 5
		System.arraycopy("005".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zpkKeyName.getBytes(), zpkKeyName.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 002
		byte[] area2 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				pvkKeyName.getBytes(), pvkKeyName.length());
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		// area3 033
		byte[] area3 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pin.getBytes(),
				pin.getBytes().length);
		System.arraycopy(area3, 0, reqbuf, offset, area3.length);
		offset += area3.length;

		// area4 041
		byte[] area4 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo1,
				srcAcco.getBytes(), srcAcco.getBytes().length);
		System.arraycopy(area4, 0, reqbuf, offset, area4.length);
		offset += area4.length;

		// area5 042
		byte[] area5 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo2,
				desAcco.getBytes(), desAcco.getBytes().length);
		System.arraycopy(area5, 0, reqbuf, offset, area5.length);
		offset += area5.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("309",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("309",
					reqbuf, offset);
		}

		if (len >= 0) {
			try {
				result[0] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByZPK), "GBK");
				result[1] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldIBMPinOffset), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		} else {
			result[0] = len + "";
		}
		return result;
	}

	/**
	 * 将一个ZPK加密的PIN密文转换为另一个ZPK加密的PIN密文。 510
	 * 这个函数支持两个账号、两种算法，即转换前后使用的账号可以不同，转换前后使用的加密算法可以不同。
	 * 
	 * @param fullKeyName1
	 *            源ZPK密钥名称
	 * @param fullKeyName2
	 *            目的ZPK密钥名称
	 * @param pinBlock1
	 *            源ZPK密钥加密的PIN密文，长度16字节字符串(ASCII)，以'\0'结束。
	 * @param accNo1
	 *            源账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，
	 *            表示无账号运算
	 * @param accNo2
	 *            目的账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，
	 *            表示无账号运算
	 * @param arithmeticMode1
	 *            源加密算法，取值为01、02、03、04、05，算法具体说明参见密码机指令集。
	 * @param arithmeticMode2
	 *            目的加密算法，取值为01、02、03、04、05，算法具体说明参见密码机指令集。
	 * @return 成功 ：PIN密文 失败：小于0的错误码
	 */
	public String UnionTranslatePinWith2AccNo2Arith(String fullKeyName1,
			String fullKeyName2, String pinBlock1, String accNo1,
			String accNo2, String arithmeticMode1, String arithmeticMode2) {
		if (fullKeyName1 == null || fullKeyName1.length() == 0
				|| fullKeyName2 == null || fullKeyName2.length() == 0
				|| accNo1 == null || accNo1.length() == 0 || accNo2 == null
				|| accNo2.length() == 0 || arithmeticMode1 == null
				|| arithmeticMode1.length() == 0 || arithmeticMode2 == null
				|| arithmeticMode2.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[2048];
		int offset = 0;

		// area count is 7
		System.arraycopy("007".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldFirstWKName,
				fullKeyName1.getBytes(), fullKeyName1.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 002
		byte[] area002 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				fullKeyName2.getBytes(), fullKeyName2.getBytes().length);
		System.arraycopy(area002, 0, reqbuf, offset, area002.length);
		offset += area002.length;

		// area3 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pinBlock1.getBytes(),
				pinBlock1.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo1,
				accNo1.getBytes(), accNo1.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		// area5 042
		byte[] area042 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo2,
				accNo2.getBytes(), accNo2.getBytes().length);
		System.arraycopy(area042, 0, reqbuf, offset, area042.length);
		offset += area042.length;

		// area6 214
		byte[] area214 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldAlgorithm01Mode, arithmeticMode1.getBytes(),
				arithmeticMode1.getBytes().length);
		System.arraycopy(area214, 0, reqbuf, offset, area214.length);
		offset += area214.length;

		// area7 215
		byte[] area215 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldAlgorithm02Mode, arithmeticMode2.getBytes(),
				arithmeticMode2.getBytes().length);
		System.arraycopy(area215, 0, reqbuf, offset, area215.length);
		offset += area215.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("510",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("510",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByZPK2), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			UnionStr us = new UnionStr();
			return us.bcdhex_to_aschex(result.getBytes());
		} else {
			return len + "";
		}

	}

	/**
	 * 将一个LMK0203对加密的PIN转换为由ZPK加密 ZPK加密PIN采用的加密标准为pboc 2.0
	 * 
	 * @param zpkName
	 *            ZPK密钥名称
	 * @param pinByLmk
	 *            LMK0203对加密的PIN
	 * @param accNo
	 *            账号/卡号，长度12字节字符串
	 * @return pinBlock：ZPK加密的PIN
	 */
	public String UnionDerivePinBlockFromPinByLmk2(String zpkName,
			String pinByLmk, String accNo) {
		if (zpkName == null || zpkName.length() == 0 || pinByLmk == null
				|| pinByLmk.length() == 0 || accNo == null
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;

		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zpkName.getBytes(), zpkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 035
		byte[] area2 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByLMK0203, pinByLmk.getBytes(),
				pinByLmk.getBytes().length);
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		// area3 041
		byte[] area3 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area3, 0, reqbuf, offset, area3.length);
		offset += area3.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("318",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("318",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByZPK1), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/**
	 * 使用指定的密钥验证MAC，若验证通过，则用另一把密钥使用相同的MAC数据产生MAC，算法是ANSI X9.9/X9.19
	 * 
	 * @param zakName1
	 *            ZAK密钥名称，用于验证MAC
	 * @param zakName2
	 *            ZAK密钥名称，用于产生MAC
	 * @param macData
	 *            MAC数据，长度变长，字符串，以‘\0’结束
	 * @param macBeVerifie
	 *            待验证的MAC值，长度16字节字符串(ASCII)
	 * @return newMac：用密钥fullKeyName2产生的MAC值，长度16字节字符串(ASCII)
	 */
	public String UnionTranslateMac(String zakName1, String zakName2,
			String macData, String macBeVerified) {
		if (zakName1 == null || zakName1.length() == 0 || zakName2 == null
				|| zakName2.length() == 0 || macData == null
				|| macData.length() == 0 || macBeVerified == null
				|| macBeVerified.length() == 0)
			return "-9001";

		byte[] reqbuf = new byte[2048];
		int offset = 0;
		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldFirstWKName,
				zakName1.getBytes(), zakName1.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 002
		byte[] area2 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				zakName2.getBytes(), zakName2.getBytes().length);
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		// area3 021
		byte[] area3 = UnionBitPutFldIntoStr(fldTag.conEsscFldMacData,
				macData.getBytes(), macData.getBytes().length);
		System.arraycopy(area3, 0, reqbuf, offset, area3.length);
		offset += area3.length;

		// area4 022
		byte[] area4 = UnionBitPutFldIntoStr(fldTag.conEsscFldMac,
				macBeVerified.getBytes(), macBeVerified.getBytes().length);
		System.arraycopy(area4, 0, reqbuf, offset, area4.length);
		offset += area4.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("333",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("333",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldMac), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/**
	 * 使用指定ZMK加密的密钥生成MAC，算法是ANSI X9.9/X9.19
	 * 
	 * @param zmkName
	 *            ZMK密钥名称
	 * @param keyByZmk
	 *            ZMK加密的密钥密文
	 * @param macData
	 *            MAC数据
	 * @return
	 */
	public String UnionGenerateMacUsingKeyByZmk(String zmkName,
			String keyByZmk, String macData) {
		if (zmkName == null || zmkName.length() == 0 || keyByZmk == null
				|| keyByZmk.length() == 0 || keyByZmk == null
				|| keyByZmk.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[2048];
		int offset = 0;

		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 011
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldZMKName,
				zmkName.getBytes(), zmkName.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 061
		byte[] area2 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyValue,
				keyByZmk.getBytes(), keyByZmk.getBytes().length);
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		// area3 021
		byte[] area3 = UnionBitPutFldIntoStr(fldTag.conEsscFldMacData,
				macData.getBytes(), macData.getBytes().length);
		System.arraycopy(area3, 0, reqbuf, offset, area3.length);
		offset += area3.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("334",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("334",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldMac), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/**
	 * 由一个LMK对加密的PIN导出一个由PVK生成的PVV
	 * 
	 * @param pvkName
	 *            PVK密钥名称
	 * @param pinByLmk
	 *            LMK0203对加密的PIN
	 * @param accNo
	 *            账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，
	 *            表示无账号运算
	 * @return
	 */
	public String UnionDerivePVVFromPinByLmk(String pvkName, String pinByLmk,
			String accNo) {
		if (pvkName == null || pvkName.length() == 0 || pinByLmk == null
				|| pinByLmk.length() == 0 || accNo == null
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[2048];
		int offset = 0;

		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				pvkName.getBytes(), pvkName.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 035
		byte[] area2 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByLMK0203, pinByLmk.getBytes(),
				pinByLmk.getBytes().length);
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		// area3 041
		byte[] area3 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area3, 0, reqbuf, offset, area3.length);
		offset += area3.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("403",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("403",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldVisaPVV), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/**
	 * 由LMK加密的PIN密文导出PVK加密的PIN Offset，PVK加密产生PIN Offset的算法为IBM3642标准算法
	 * 
	 * @param pvkName
	 *            PVK密钥名称
	 * @param pinByLmk
	 *            LMK0203对加密的PIN密文
	 * @param accNo
	 *            ：账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，
	 *            表示无账号运算
	 * @param pinLength
	 *            最小PIN长度，通常为4
	 * @return PVK生成的PIN Offset，左对齐，右补‘F’
	 */
	public String UnionDerivePinOffsetFromPinByLmk(String pvkName,
			String pinByLmk, String accNo, int pinLength) {
		if (pvkName == null || pvkName.length() == 0 || pinByLmk == null
				|| pinByLmk.length() == 0 || accNo == null
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[2048];
		int offset = 0;

		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				pvkName.getBytes(), pvkName.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 035
		byte[] area2 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByLMK0203, pinByLmk.getBytes(),
				pinByLmk.getBytes().length);
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		// area3 041
		byte[] area3 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area3, 0, reqbuf, offset, area3.length);
		offset += area3.length;

		// area4 205
		byte[] area4 = UnionBitPutFldIntoStr(fldTag.conEsscFldForPinLength,
				(pinLength + "").getBytes(), (pinLength + "").getBytes().length);
		System.arraycopy(area4, 0, reqbuf, offset, area4.length);
		offset += area4.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("513",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("513",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldIBMPinOffset), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/**
	 * 将一PVK加密的PIN Offset转换为另一PVK加密，算法为IBM3642标准算法。
	 * 
	 * @param pvkName1
	 *            源PVK密钥名称
	 * @param pvkName2
	 *            目的PVK密钥名称
	 * @param pinOffset1
	 *            待转换的PINOffset，源PVK加密
	 * @param accNo
	 *            账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，
	 *            表示无账号运算
	 * @param pinLength
	 *            最小PIN长度，通常为4
	 * @return pinOffset2：转换后的PIN Offset，目的PVK加密，左对齐，右补‘F’
	 */
	public String UnionTranslatePinOffset(String pvkName1, String pvkName2,
			String pinOffset1, String accNo, int pinLength) {
		if (pvkName1 == null || pvkName1.length() == 0 || pvkName2 == null
				|| pvkName2.length() == 0 || pinOffset1 == null
				|| pinOffset1.length() == 0 || accNo == null
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[2048];
		int offset = 0;

		// area count is 5
		System.arraycopy("005".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				pvkName1.getBytes(), pvkName1.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 002
		byte[] area2 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				pvkName2.getBytes(), pvkName2.getBytes().length);
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		// area3 037
		byte[] area3 = UnionBitPutFldIntoStr(fldTag.conEsscFldIBMPinOffset,
				pinOffset1.getBytes(), pinOffset1.getBytes().length);
		System.arraycopy(area3, 0, reqbuf, offset, area3.length);
		offset += area3.length;

		// area4 041
		byte[] area4 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area4, 0, reqbuf, offset, area4.length);
		offset += area4.length;

		// area5 205
		byte[] area5 = UnionBitPutFldIntoStr(fldTag.conEsscFldForPinLength,
				(pinLength + "").getBytes(), (pinLength + "").getBytes().length);
		System.arraycopy(area5, 0, reqbuf, offset, area5.length);
		offset += area5.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("514",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("514",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldIBMPinOffset), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/**
	 * 将一PVK加密的PIN Offset转换为另一PVK加密，源帐号和目的帐号不同，原始用户PIN明文保持不变，算法为IBM3642标准算法。
	 * 
	 * @param pvkName1
	 *            源PVK密钥名称
	 * @param pvkName2
	 *            目的PVK密钥名称
	 * @param pinOffset1
	 *            待转换的PINOffset，源PVK加密
	 * @param accNo1
	 *            源账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，
	 *            表示无账号运算
	 * @param accNo2
	 *            目的账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，
	 *            表示无账号运算
	 * @param pinLength
	 *            最小PIN长度，通常为4
	 * @return pinOffset2：转换后的PIN Offset，目的PVK加密，左对齐，右补‘F’
	 */
	public String UnionTranslatePinOffsetWith2AccNo(String pvkName1,
			String pvkName2, String pinOffset1, String accNo1, String accNo2,
			int pinLength) {
		if (pvkName1 == null || pvkName1.length() == 0 || pvkName2 == null
				|| pvkName2.length() == 0 || pinOffset1 == null
				|| pinOffset1.length() == 0 || accNo1 == null
				|| accNo1.length() == 0 || accNo2 == null
				|| accNo2.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[2048];
		int offset = 0;

		// area count is 6
		System.arraycopy("006".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				pvkName1.getBytes(), pvkName1.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 002
		byte[] area2 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				pvkName2.getBytes(), pvkName2.getBytes().length);
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		// area3 037
		byte[] area3 = UnionBitPutFldIntoStr(fldTag.conEsscFldIBMPinOffset,
				pinOffset1.getBytes(), pinOffset1.getBytes().length);
		System.arraycopy(area3, 0, reqbuf, offset, area3.length);
		offset += area3.length;

		// area4 041
		byte[] area4 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo1.getBytes(), accNo1.getBytes().length);
		System.arraycopy(area4, 0, reqbuf, offset, area4.length);
		offset += area4.length;

		// area5 042
		byte[] area5 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo2,
				accNo2.getBytes(), accNo2.getBytes().length);
		System.arraycopy(area5, 0, reqbuf, offset, area5.length);
		offset += area5.length;

		// area6 205
		byte[] area6 = UnionBitPutFldIntoStr(fldTag.conEsscFldForPinLength,
				(pinLength + "").getBytes(), (pinLength + "").getBytes().length);
		System.arraycopy(area6, 0, reqbuf, offset, area6.length);
		offset += area6.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("515",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("515",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldIBMPinOffset), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/**
	 * 使用LMK0203对解密PIN密文，这个函数，将用户的PIN密文解密成PIN明文
	 * 
	 * @param pinByLmk
	 *            LMK0203对加密的PIN密文
	 * @param accNo
	 *            账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，
	 *            表示无账号运算
	 * @return clearPin：PIN明文，ASCII字符串，以'\0'结束
	 */
	public String UnionDecryptPinByLmk(String pinByLmk, String accNo) {
		if ((pinByLmk == null || pinByLmk.length() == 0)
				|| (accNo == null || accNo.length() == 0)) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[100];
		int offset = 0;

		// area count is 2
		System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 035
		byte[] area1 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByLMK0203, pinByLmk.getBytes(),
				pinByLmk.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 041
		byte[] area2 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("432",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("432",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldPlainPin), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/*
	 * 与461服务相同 通过ZPK加密的PIN和PVK生成的IBMPINOFFSET验证密码 ZPK加密PIN采用的加密标准为ANSIX9.8。
	 * PVK生成PinOffset（PIN Verification Value），采用的加密标准为Visa Method。 输入参数：
	 * zpkName,ZPK密钥名称 pinBlock，ZPK加密的PIN pvkName,PVK密钥名称 accNo,账号
	 * pinOffset,IBM算法 输出参数： 无 返回值：<0,是错误码。0 成功。
	 */
	public int UnionVerifyPinOffsetAndPinByZpk2(String zpkName, String pvkName,
			String pinBlock, String accNo, String pinOffset) {
		if (zpkName == null || zpkName.length() == 0 || pvkName == null
				|| pvkName.length() == 0 || pinBlock == null
				|| pinBlock.length() == 0 || accNo == null
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 5
		System.arraycopy("005".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldFirstWKName,
				zpkName.getBytes(), zpkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 002
		byte[] area002 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				pvkName.getBytes(), pvkName.getBytes().length);
		System.arraycopy(area002, 0, reqbuf, offset, area002.length);
		offset += area002.length;

		// area3 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pinBlock.getBytes(),
				pinBlock.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		// area5 037
		byte[] area037 = UnionBitPutFldIntoStr(fldTag.conEsscFldIBMPinOffset,
				pinOffset.getBytes(), pinOffset.getBytes().length);
		System.arraycopy(area037, 0, reqbuf, offset, area037.length);
		offset += area037.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("462",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("462",
					reqbuf, offset);
		}
		return len;
	}

	/**
	 * 通过ZPK加密的PIN和PVK生成的PVV（VISA方式）验证PIN 463
	 * 
	 * @param zpkName
	 * @param pvkName
	 * @param pinBlock
	 *            ZPK加密的PIN
	 * @param accNo
	 *            账号
	 * @param pvv
	 *            终端PIN的4位VISA PVV
	 * @return
	 */
	public int UnionVerifyPinByZpkAndPvk(String zpkName, String pvkName,
			String pinBlock, String accNo, String pvv) {
		if (zpkName == null || zpkName.length() == 0 || pvkName == null
				|| pvkName.length() == 0 || pinBlock == null
				|| pinBlock.length() == 0 || accNo == null
				|| accNo.length() == 0 || pvv == null || pvv.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 5
		System.arraycopy("005".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldFirstWKName,
				zpkName.getBytes(), zpkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 002
		byte[] area002 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				pvkName.getBytes(), pvkName.getBytes().length);
		System.arraycopy(area002, 0, reqbuf, offset, area002.length);
		offset += area002.length;

		// area3 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pinBlock.getBytes(),
				pinBlock.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		// area5 036
		byte[] area036 = UnionBitPutFldIntoStr(fldTag.conEsscFldVisaPVV,
				pvv.getBytes(), pvv.getBytes().length);
		System.arraycopy(area036, 0, reqbuf, offset, area036.length);
		offset += area036.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("463",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("463",
					reqbuf, offset);
		}
		return len;
	}

	/**
	 * 和463一样 通过ZPK加密的PIN和PVK生成的PVV（VISA方式）验证PIN 464
	 * 
	 * @param zpkName
	 * @param pvkName
	 * @param pinBlock
	 *            ZPK加密的PIN
	 * @param accNo
	 *            账号
	 * @param pvv
	 *            终端PIN的4位VISA PVV
	 * @return
	 */
	public int UnionVerifyPinByZpkAndPvk2(String zpkName, String pvkName,
			String pinBlock, String accNo, String pvv) {
		if (zpkName == null || zpkName.length() == 0 || pvkName == null
				|| pvkName.length() == 0 || pinBlock == null
				|| pinBlock.length() == 0 || accNo == null
				|| accNo.length() == 0 || pvv == null || pvv.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 5
		System.arraycopy("005".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldFirstWKName,
				zpkName.getBytes(), zpkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 002
		byte[] area002 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
				pvkName.getBytes(), pvkName.getBytes().length);
		System.arraycopy(area002, 0, reqbuf, offset, area002.length);
		offset += area002.length;

		// area3 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pinBlock.getBytes(),
				pinBlock.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		// area5 036
		byte[] area036 = UnionBitPutFldIntoStr(fldTag.conEsscFldVisaPVV,
				pvv.getBytes(), pvv.getBytes().length);
		System.arraycopy(area036, 0, reqbuf, offset, area036.length);
		offset += area036.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("463",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("463",
					reqbuf, offset);
		}
		return len;
	}

	/*
	 * 同465 通过ZPK加密的PIN和LMK加密的PIN验证密码 ZPK加密PIN采用的加密标准为ANSIX9.8。 输入参数：
	 * zpkName,ZPK密钥名称 pinBlock，ZPK加密的PIN accNo,账号 pinByLmk,IBM算法 输出参数： 无
	 * 返回值：<0,是错误码。0 成功。
	 */
	public int UnionVerifyPinByLmkAndPinByZpk2(String zpkName, String pinBlock,
			String accNo, String pinByLmk) {
		if (zpkName == null || zpkName.length() == 0 || pinBlock == null
				|| pinBlock.length() == 0 || accNo == null
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;
		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zpkName.getBytes(), zpkName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 033
		byte[] area033 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByZPK, pinBlock.getBytes(),
				pinBlock.getBytes().length);
		System.arraycopy(area033, 0, reqbuf, offset, area033.length);
		offset += area033.length;

		// area3 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		// area4 035
		byte[] area035 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldEncryptedPinByLMK0203, pinByLmk.getBytes(),
				pinByLmk.getBytes().length);
		System.arraycopy(area035, 0, reqbuf, offset, area035.length);
		offset += area035.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("466",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("466",
					reqbuf, offset);
		}
		return len;
	}

	/**
	 * 使用指定的PKCS10应用的密钥签名534
	 * 
	 * @param idOfApp
	 *            应用编号
	 * @param flag
	 *            数据补位方式 ‘0’：补0，‘1’，PKCS#11 可选项，不设置该项，缺省采用‘1’
	 * @param hashID
	 *            HASH算法标识 可选：如果采用RACAL标准指令密码机指令EY：01，SHA-1，02，MD5，03，ISO
	 *            1011802，04，NoHash 如果采用SJL06
	 *            38指令：‘0’：MD5，‘1’：SHA-1，不采用hash,不要送该域
	 * @param data
	 *            签名数据
	 * @return
	 */
	public String UnionGenerateRsaSignatureForPKCS10(String idOfApp,
			String flag, String hashID, String data) {
		if (idOfApp == null || idOfApp.length() == 0 || data == null
				|| data.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[data.length() + 1024];
		int offset = 0;

		// area count is 3 or 4
		if (hashID == null) {
			System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		} else {
			System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		}
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 207
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldIDOfApp,
				idOfApp.getBytes(), idOfApp.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 093
		if (flag == null) {
			byte[] area2 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldSignDataPadFlag, "1".getBytes(),
					"1".getBytes().length);
			System.arraycopy(area2, 0, reqbuf, offset, area2.length);
			offset += area2.length;
		} else {
			byte[] area2 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldSignDataPadFlag, flag.getBytes(),
					flag.getBytes().length);
			System.arraycopy(area2, 0, reqbuf, offset, area2.length);
			offset += area2.length;
		}

		// area3 214
		if (hashID != null) {
			byte[] area3 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldAlgorithmMode, hashID.getBytes(),
					hashID.getBytes().length);
			System.arraycopy(area3, 0, reqbuf, offset, area3.length);
			offset += area3.length;
		}

		// area4 091
		byte[] area4 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				data.getBytes(), data.getBytes().length);
		System.arraycopy(area4, 0, reqbuf, offset, area4.length);
		offset += area4.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("534",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("534",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldSign), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/**
	 * 使用输入的公钥验证签名135
	 * 
	 * @param pkValue
	 *            公开密钥值
	 * @param flag
	 *            数据补位方式 可选，‘0’：补0，‘1’，PKCS#11，缺省是PKCS#11方式
	 * @param hashID
	 *            HASH算法标识 可选：如果采用RACAL标准指令密码机指令EY：01，SHA-1，02，MD5，03，ISO
	 *            1011802，04，NoHash 如果采用SJL06
	 *            38指令：‘0’：MD5，‘1’：SHA-1，不采用hash,不要送该域
	 * @param data
	 *            签名数据
	 * @param sign
	 *            签名
	 * @return
	 */
	public int UnionVerifySignatureUsingInputPK(String pkValue, String flag,
			String hashID, String data, String sign) {
		if (pkValue == null || pkValue.length() == 0 || data == null
				|| data.length() == 0 || sign == null || sign.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[data.length() + 1024];
		int offset = 0;

		// area count is 4 or 5
		if (hashID == null) {
			System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		} else {
			System.arraycopy("005".getBytes(), 0, reqbuf, offset, 3);
		}
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 061
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyValue,
				pkValue.getBytes(), pkValue.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 093
		if (flag == null) {
			byte[] area2 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldSignDataPadFlag, "1".getBytes(),
					"1".getBytes().length);
			System.arraycopy(area2, 0, reqbuf, offset, area2.length);
			offset += area2.length;
		} else {
			byte[] area2 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldSignDataPadFlag, flag.getBytes(),
					flag.getBytes().length);
			System.arraycopy(area2, 0, reqbuf, offset, area2.length);
			offset += area2.length;
		}

		// area3 214
		if (hashID != null) {
			byte[] area3 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldAlgorithmMode, hashID.getBytes(),
					hashID.getBytes().length);
			System.arraycopy(area3, 0, reqbuf, offset, area3.length);
			offset += area3.length;
		}

		// area4 091
		byte[] area4 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				data.getBytes(), data.getBytes().length);
		System.arraycopy(area4, 0, reqbuf, offset, area4.length);
		offset += area4.length;

		// area5 092
		byte[] area5 = UnionBitPutFldIntoStr(fldTag.conEsscFldSign,
				sign.getBytes(), sign.getBytes().length);
		System.arraycopy(area5, 0, reqbuf, offset, area5.length);
		offset += area5.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("135",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("135",
					reqbuf, offset);
		}
		return len;
	}

	/**
	 * 将指定应用的PK加密的PIN转换为ZPK加密
	 * 
	 * @param idOfApp
	 *            应用编号
	 * @param pinByPK
	 *            PK加密的PIN
	 * @param zpkName
	 *            ZPK密钥名称
	 * @param accNo
	 *            账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，
	 *            表示无账号运算
	 * @return ZPK加密的PIN
	 */
	public String UnionDerivePinBlockFromPinByPK(String idOfApp,
			String pinByPK, String zpkName, String accNo) {
		if (idOfApp == null || idOfApp.length() == 0 || pinByPK == null
				|| pinByPK.length() == 0 || zpkName == null
				|| zpkName.length() == 0 || accNo == null
				|| accNo.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[2048];
		int offset = 0;

		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 207
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldIDOfApp,
				idOfApp.getBytes(), idOfApp.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 038
		byte[] area2 = UnionBitPutFldIntoStr(fldTag.conEsscFldPinByRsaPK,
				pinByPK.getBytes(), pinByPK.getBytes().length);
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		// area3 001
		byte[] area3 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area3, 0, reqbuf, offset, area3.length);
		offset += area3.length;

		// area4 041
		byte[] area4 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				accNo.getBytes(), accNo.getBytes().length);
		System.arraycopy(area4, 0, reqbuf, offset, area4.length);
		offset += area4.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("170",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("170",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldEncryptedPinByZPK), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/**
	 * 私钥解密
	 * 
	 * @param pkIndex
	 *            私钥索引
	 * @param pkPadFlag
	 *            公钥填充方式
	 * @param cipherDataLen
	 *            加密数据长度
	 * @param cipherData
	 *            加密数据
	 * @return 解密数据
	 */
	public String UnionDecDataByPK(int pkIndex, int pkPadFlag,
			int cipherDataLen, String cipherData) {
		if (cipherData == null || cipherData.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[2048];
		int offset = 0;

		// area count is 4
		System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 210
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyIndex,
				printCStyle(2, pkIndex).getBytes(), printCStyle(2, pkIndex)
						.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 093
		byte[] area2 = UnionBitPutFldIntoStr(fldTag.conEsscFldSignDataPadFlag,
				(pkPadFlag + "").getBytes(), (pkPadFlag + "").getBytes().length);
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		// area3 205
		byte[] area3 = UnionBitPutFldIntoStr(fldTag.conEsscFldForPinLength,
				(cipherDataLen + "").getBytes(),
				(cipherDataLen + "").getBytes().length);
		System.arraycopy(area3, 0, reqbuf, offset, area3.length);
		offset += area3.length;

		// area4 082
		byte[] area4 = UnionBitPutFldIntoStr(fldTag.conEsscFldCiperData,
				cipherData.getBytes(), cipherData.getBytes().length);
		System.arraycopy(area4, 0, reqbuf, offset, area4.length);
		offset += area4.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("535",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("535",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldPlainData), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len + "";
		}
	}

	/*
	 * 同133 使用指定公钥验证 输入参数： keyName,公钥名称 lenOfData,签名数据的长度 data，签名数据
	 * lenOfSign，签名长度 flag，数据填充方式  hashID：HASH算法标志，如果采用RACAL标准指令密码机指令EY：01，SHA-1，02，MD5，03，ISO 1011802，04，NoHash；如果采用SJL06 38指令：‘0’：MD5，‘1’：SHA-1；如果不需要做HASH，为空值，即“NULL”。sign，签名 输出参数： 无 返回值：<0,是错误码。否则是签名验证成功
	 */
	// 由于函数的变动该函数已被UnionNewVerifySignature()覆盖, 所以没有进行测试.请调用时先进行测试.
	public int UnionVerifySignature2(String keyName, String flag,String hashID,
			int lenOfData, String data, String sign, int lenOfSign) {
		if (keyName == null || keyName.length() == 0 || data == null
				|| data.length() == 0 || lenOfSign <= 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[data.getBytes().length + sign.length() + 100];
		int offset = 0;
		// area count is 5
		System.arraycopy("005".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				keyName.getBytes(), keyName.length());
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 214
		byte[] area214 = UnionBitPutFldIntoStr(fldTag.conEsscFldAlgorithmMode,
				hashID.getBytes(), hashID.length());
		System.arraycopy(area214, 0, reqbuf, offset, area214.length);
		offset += area214.length;
		
		// area3 091
		byte[] area091 = UnionBitPutFldIntoStr(fldTag.conEsscFldSignData,
				data.getBytes(), data.length());
		System.arraycopy(area091, 0, reqbuf, offset, area091.length);
		offset += area091.length;

		// area4 092
		byte[] area092 = UnionBitPutFldIntoStr(fldTag.conEsscFldSign,
				sign.getBytes(), sign.length());
		System.arraycopy(area092, 0, reqbuf, offset, area092.length);
		offset += area092.length;

		// area5 093
		byte[] area093 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldSignDataPadFlag, flag.getBytes(),
				flag.length());
		System.arraycopy(area093, 0, reqbuf, offset, area093.length);
		offset += area093.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("604",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("604",
					reqbuf, offset);
		}
		return len;
	}

	/**
	 * 解密密文数据，支持光大30所加密机（同517）601
	 * 
	 * @param zekName
	 * @param encData
	 * @param encFlag
	 * @param iv
	 * @return
	 */
	public String UnionDecryptEncData2(String zekName, String encData,
			String encFlag, String iv) {
		if (zekName == null || zekName.length() == 0 || encData == null
				|| encData.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[encData.getBytes().length + 200];
		int offset = 0;
		if (encFlag.contentEquals("1")) {
			System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		} else {
			System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		}
		offset += 3;
		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area area001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zekName.getBytes(), zekName.length());
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area area082
		byte[] area082 = UnionBitPutFldIntoStr(fldTag.conEsscFldCiperData,
				encData.getBytes(), encData.getBytes().length);
		System.arraycopy(area082, 0, reqbuf, offset, area082.length);
		offset += area082.length;

		if (encFlag.contentEquals("1")) {
			// area 214
			byte[] area214 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldAlgorithmMode, encFlag.getBytes(),
					encFlag.getBytes().length);
			System.arraycopy(area214, 0, reqbuf, offset, area214.length);
			offset += area214.length;

			// area 213
			byte[] area213 = UnionBitPutFldIntoStr(fldTag.conEsscFldIV,
					iv.getBytes(), iv.getBytes().length);
			System.arraycopy(area213, 0, reqbuf, offset, area213.length);
			offset += area213.length;
		}
		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("601",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("601",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldData), "GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	/**
	 * 解密密文数据，不支持光大30所加密机（同601） 602
	 * 
	 * @param zekName
	 * @param encData
	 * @param encFlag
	 * @param iv
	 * @return
	 */
	public String UnionDecryptEncData3(String zekName, String encData,
			String encFlag, String iv) {
		if (zekName == null || zekName.length() == 0 || encData == null
				|| encData.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[encData.getBytes().length + 200];
		int offset = 0;
		if (encFlag.contentEquals("1")) {
			System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		} else {
			System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		}
		offset += 3;
		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area area001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zekName.getBytes(), zekName.length());
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area area082
		byte[] area082 = UnionBitPutFldIntoStr(fldTag.conEsscFldCiperData,
				encData.getBytes(), encData.getBytes().length);
		System.arraycopy(area082, 0, reqbuf, offset, area082.length);
		offset += area082.length;

		if (encFlag.contentEquals("1")) {
			// area 214
			byte[] area214 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldAlgorithmMode, encFlag.getBytes(),
					encFlag.getBytes().length);
			System.arraycopy(area214, 0, reqbuf, offset, area214.length);
			offset += area214.length;

			// area 213
			byte[] area213 = UnionBitPutFldIntoStr(fldTag.conEsscFldIV,
					iv.getBytes(), iv.getBytes().length);
			System.arraycopy(area213, 0, reqbuf, offset, area213.length);
			offset += area213.length;
		}
		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("601",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("601",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldData), "GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	/**
	 * 使用指定的密钥加密数据（同50指令）701
	 * 
	 * @param zekName
	 * @param encData
	 * @param encFlag
	 * @param iv
	 * @return
	 */
	public String UnionEncryptDataWithSpecifiedKey(String fullKeyName, String clearData, String initIV,
			String arithmeticFlag) {
		if (fullKeyName == null || fullKeyName.length() == 0
				|| clearData == null || clearData.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[clearData.getBytes().length + 200];
		int offset = 0;
		// area count is 3 or 4
		if(initIV!=null){
			System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		}else{
			System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		}
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 081
		byte[] area081 = UnionBitPutFldIntoStr(fldTag.conEsscFldPlainData,
				clearData.getBytes(), clearData.getBytes().length);
		System.arraycopy(area081, 0, reqbuf, offset, area081.length);
		offset += area081.length;

		// area3 213
		if(initIV!=null){
		byte[] area213 = UnionBitPutFldIntoStr(fldTag.conEsscFldIV,
				initIV.getBytes(), initIV.length());
		System.arraycopy(area213, 0, reqbuf, offset, area213.length);
		offset += area213.length;
		}

		// area4 214
		byte[] area214 = UnionBitPutFldIntoStr(fldTag.conEsscFldAlgorithmMode,
				arithmeticFlag.getBytes(), arithmeticFlag.length());
		System.arraycopy(area214, 0, reqbuf, offset, area214.length);
		offset += area214.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("701",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("701",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldCiperData), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			UnionStr us = new UnionStr();
			return us.bcdhex_to_aschex(res.getBytes());
		} else {
			return len + "";
		}
	}
	
	public String  UnionGenerateRandom(int length){
		byte[] reqbuf = new byte[200];
		int offset = 0;
		// area count is 1
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				(length+"").getBytes(), (length+"").getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;


		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("542",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("542",
					reqbuf, offset);
		}
		if (len >= 0) {
			String res = "";
			try {
				res = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyLenFlag), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return res;
		} else {
			return len+"";
		}
		
	}
	/**
	 * 脱机PIN加密-明文输入(PBOC2.0) 555
	 * @param keyName 密钥名称
	 * @param keyVersion 密钥版本号
	 * @param encFlag 加密模式：0=CBC(MASTERCARD) 1=ECB(VISA/PBOC)
	 * @param acco 离散卡片密钥使用的帐号或者帐号序列号，该数据的填充由应用完成
	 * @param ATC ATC
	 * @param pinMode 脱机PIN模式
	 * @param pinClearData PIN 明文
	 * @return 加密结果 
	 */ 
	public String UnionOfflinePinEncryptionClearDataInput(String keyName,String keyVersion,String encFlag,String acco,String ATC,String pinMode,String pinClearData){
		if (keyName == null || keyName.length() == 0
				|| keyVersion == null || keyVersion.length() == 0
				|| encFlag == null || encFlag.length() == 0 || acco == null
				|| acco.length() == 0 || ATC == null
				|| ATC.length() == 0 || pinMode == null
				|| pinMode.length() == 0||pinClearData==null||pinClearData.length()==0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[2048];
		int offset = 0;

		// area count is 7
		System.arraycopy("007".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldFirstWKName,
				keyName.getBytes(), keyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 410
		byte[] area2 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyVersion,
				keyVersion.getBytes(), keyVersion.getBytes().length);
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		// area3 214
		byte[] area214 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldAlgorithmMode, encFlag.getBytes(),
				encFlag.getBytes().length);
		System.arraycopy(area214, 0, reqbuf, offset, area214.length);
		offset += area214.length;

		// area4 041
		byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo1,
				acco.getBytes(), acco.getBytes().length);
		System.arraycopy(area041, 0, reqbuf, offset, area041.length);
		offset += area041.length;

		// area5 315
		byte[] area315 = UnionBitPutFldIntoStr(fldTag.conEsscFldARC,
				ATC.getBytes(), ATC.getBytes().length);
		System.arraycopy(area315, 0, reqbuf, offset, area315.length);
		offset += area315.length;

		// area6 221
		byte[] area221 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldKeyTypeFlag, pinMode.getBytes(),
				pinMode.getBytes().length);
		System.arraycopy(area221, 0, reqbuf, offset, area221.length);
		offset += area221.length;

		// area7 081
		byte[] area081 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldPlainData, pinClearData.getBytes(),
				pinClearData.getBytes().length);
		System.arraycopy(area081, 0, reqbuf, offset, area081.length);
		offset += area081.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("555",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("555",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldCiperData), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return result;
		} else {
			return len + "";
		}

	}
	//557
	/**
	 * 对Pinoffset方式输入PIN，使用卡片加密过程密钥进行加密计算(PBOC2.0) 557
	 * @param fullKeyName 密钥名称
	 * @param version 密钥版本号
	 * @param arithmeticMode1 加密模式
	 * @param pan pan序列号
	 * @param atc ATC
	 * @param pvkName pvk密钥名称
	 * @param IBMoffset offset
	 * @param checklength 检查长度
	 * @param accNo 账号 
	 * @param Contable 十进制转换表
	 * @param pinCheck pin校验数据
	 * @param arithmeticMode2 脱机PIN模式
	 * @return
	 */
	public String UnionEncryptUseCardencryptProcess(String fullKeyName,String version,String arithmeticMode1,String pan,
			String atc, String pvkName,String IBMoffset,int checklength,String accNo,String Contable,String pinCheck,String linkOffPin){
			if (fullKeyName == null || fullKeyName.length() == 0 || version == null || version.length() == 0 
					|| arithmeticMode1 == null || arithmeticMode1.length() == 0
					|| pan == null || pan.length() == 0 || atc == null
					|| atc.length() == 0 || pvkName == null || pvkName.length() == 0
					|| IBMoffset == null || IBMoffset.length() == 0 || accNo == null
					|| accNo.length() == 0 || Contable == null
					|| Contable.length() == 0 || pinCheck == null
					|| pinCheck.length() == 0|| linkOffPin == null
					|| linkOffPin.length() == 0) {
				logger.debug("调用API接口传入参数错误");
				return "-5501";
			}
			byte[] reqbuf = new byte[4096];
			int offset = 0;

			// area count is 12
			System.arraycopy("012".getBytes(), 0, reqbuf, offset, 3);
			offset += 3;

			EsscFldTagDef fldTag = new EsscFldTagDef();
			// area1 001
			byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
					fullKeyName.getBytes(), fullKeyName.getBytes().length);
			System.arraycopy(area001, 0, reqbuf, offset, area001.length);
			offset += area001.length;

			// area2 410
			byte[] area410 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyVersion,
					version .getBytes(),
				version.getBytes().length);
			System.arraycopy(area410, 0, reqbuf, offset, area410.length);
			offset += area410.length;

			// area3 214
			byte[] area214 = UnionBitPutFldIntoStr(fldTag.conEsscFldAlgorithmMode,
					arithmeticMode1.getBytes(), arithmeticMode1.getBytes().length);
			System.arraycopy(area214, 0, reqbuf, offset, area214.length);
			offset += area214.length;

			// area4 041
			byte[] area201 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
					pan.getBytes(), pan.getBytes().length);
			System.arraycopy(area201, 0, reqbuf, offset, area201.length);
			offset += area201.length;

			// area5 051
			byte[] area041 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyCheckValue,
					atc.getBytes(), atc.getBytes().length);
			System.arraycopy(area041, 0, reqbuf, offset, area041.length);
			offset += area041.length;

			// area6 002
			byte[] area315 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
					pvkName.getBytes(), pvkName.getBytes().length);
			System.arraycopy(area315, 0, reqbuf, offset, area315.length);
			offset += area315.length;

			// area7 002
			byte[] area002 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondWKName,
					pvkName.getBytes(), pvkName.getBytes().length);
			System.arraycopy(area002, 0, reqbuf, offset, area002.length);
			offset += area002.length;

			// area8 032
			byte[] area033 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldPinOffset, IBMoffset.getBytes(),
					IBMoffset.getBytes().length);
			System.arraycopy(area033, 0, reqbuf, offset, area033.length);
			offset += area033.length;

			// area9 205
			byte[] area205 = UnionBitPutFldIntoStr(fldTag.conEsscFldForPinLength,
					(checklength+"").getBytes(), (checklength+"").getBytes().length);
			System.arraycopy(area205, 0, reqbuf, offset, area205.length);
			offset += area205.length;

			// area10 042
			byte[] area042 = UnionBitPutFldIntoStr(fldTag.conEsscFldForPinLength,
					accNo.getBytes(),accNo.getBytes().length);
			System.arraycopy(area042, 0, reqbuf, offset, area042.length);
			offset += area042.length;
			
			// area10 221
			byte[] area211 = UnionBitPutFldIntoStr(fldTag.conEsscFldHsmIPAddrList,
					Contable.getBytes(), Contable.getBytes().length);
			System.arraycopy(area211, 0, reqbuf, offset, area211.length);
			offset += area211.length;

			// area11 051
			byte[] area051 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyCheckValue,
			pinCheck.getBytes(), pinCheck.getBytes().length);
			System.arraycopy(area051, 0, reqbuf, offset, area051.length);
			offset += area051.length;
			
			// area12 221
			byte[] area221 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyTypeFlag,
					linkOffPin.getBytes(), linkOffPin.getBytes().length);
			System.arraycopy(area221, 0, reqbuf, offset, area221.length);
			offset += area221.length;
			
			CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
					timeOut, gunionIDOfEsscAPI);
			int len = 0;
			if (longOrShortConn == 0) {
				len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("557",
						reqbuf, offset);
			} else {
				len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("557",
						reqbuf, offset);
			}
			String result = "";
			if (len >= 0) {
				try {
					result = new String(UnionReadSpecFldFromBytes(
							commWithEsscSvr.bytereturnPackage, len,
							fldTag.conEsscFldCiperData), "GBK");
				} catch (UnsupportedEncodingException e) {

					e.printStackTrace();
				}
				return result;
			} else {
				return len+"";
			}
	}
	/**
	 * 查询平台资源的状态
	 * @param resourceNum 资源号
	 * @return 备注信息
	 */
	public String  UnionQueryPlatformResourceState(String resourceNum){
		byte[] reqbuf = new byte[100];
		int offset = 0;
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;
		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1
		byte[] area997 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldResID, resourceNum.getBytes(),
				resourceNum.length());
		System.arraycopy(area997, 0, reqbuf, offset, area997.length);
		offset += area997.length;
		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("559",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("559",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldRemark), "GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
		} else {
			return len + "";
		}
	}
	/**
	 * Zmk加密的zek和zak转换为lmk对加密 支持光大30所加密机  600
	 * @param zmkName
	 * @param zek
	 * @param zak
	 * @return
	 */
	public String[] UnionConvertZmkEncryptZekAndZakToLmkEncrypt(String zmkName ,String zek,String zak){
		String[] results = { "", "" };
		if (zmkName == null || zmkName.length() == 0||zek == null || zek.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			results[0] = "-5501";
			return results;
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;

		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 011
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldZMKName,
				zmkName.getBytes(), zmkName.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 061
		byte[] area2 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyValue,
				zek.getBytes(), zek.getBytes().length);
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		// area3 062
		byte[] area3 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondKeyCheckValue,
				zak.getBytes(), zak.getBytes().length);
		System.arraycopy(area3, 0, reqbuf, offset, area3.length);
		offset += area3.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("600",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("600",
					reqbuf, offset);
		}
		if (len >= 0) {
			try {
				results[0] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyValue), "GBK");
				results[1] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldSecondKeyCheckValue), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		} else {
			results[0] = len + "";
		}
		return results;
	}
	
	public String[] UnionGenerateZmkEncryptionKey(String zmkName ,String keyLengthFlag,String keyType){
		String[] results = { "", "","" };
		if (zmkName == null || zmkName.length() == 0||keyLengthFlag == null || keyLengthFlag.length() == 0||keyType == null || keyType.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			results[0] = "-5501";
			return results;
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;

		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 011
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldZMKName,
				zmkName.getBytes(), zmkName.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 203
		byte[] area2 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyLenFlag,
				keyLengthFlag.getBytes(), keyLengthFlag.getBytes().length);
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		// area3 221
		byte[] area3 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyTypeFlag,
				keyType.getBytes(), keyType.getBytes().length);
		System.arraycopy(area3, 0, reqbuf, offset, area3.length);
		offset += area3.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("605",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("605",
					reqbuf, offset);
		}
		if (len >= 0) {
			try {
				results[0] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyCheckValue), "GBK");
				results[1] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyValue), "GBK");
				results[2] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldSecondKeyCheckValue), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		} else {
			results[0] = len + "";
		}
		return results;
	}
	/**
	 * 根据密钥名称，删除密码服务平台中该该密钥定义
	 * @param keyName
	 * @return
	 */
	public int  UnionDeleteKeyDefinitionByKeyName(String keyName){
		byte[] reqbuf = new byte[100];
		int offset = 0;
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;
		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1 001
		byte[] area1 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldKeyName, keyName.getBytes(),
				keyName.length());
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;
		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("522",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("522",
					reqbuf, offset);
		}
		return len;
	}
	
	/**
	 * 将LMK加密的密钥密文值，写入密钥全名所对应的密钥值中（重新生成密钥值）523
	 * @param fullKeyName 密钥全名
	 * @param keyValue 密钥密文值
	 * @param checkValue 密钥校验值
	 * @return
	 */
	public int UnionWrittenLmkEncryptCipherToKeyValue(String fullKeyName, String keyValue,
			String checkValue) {
		if (fullKeyName == null || fullKeyName.length() == 0
				|| keyValue == null || keyValue.length() == 0
				|| checkValue == null || checkValue.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return -5501;
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;

		// area count is 3
		System.arraycopy("003".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area2 061
		byte[] area061 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyValue,
				keyValue.getBytes(), keyValue.getBytes().length);
		System.arraycopy(area061, 0, reqbuf, offset, area061.length);
		offset += area061.length;

		// area3 051
		byte[] area051 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyCheckValue,
				checkValue.getBytes(), checkValue.getBytes().length);
		System.arraycopy(area051, 0, reqbuf, offset, area051.length);
		offset += area051.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("523",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("523",
					reqbuf, offset);
		}
		return len;
	}
	/**
	 * 根据密钥全名，读取密码服务平台中存储的该密钥的密文值 524
	 * @param fullKeyName 密钥全名
	 * @return 密钥值 和校验值
	 */
	public String[] UnionReadTheCipherKeyByKeyName(String fullKeyName){
		String[] results = { "", "" };
		if (fullKeyName == null || fullKeyName.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			results[0] = "-5501";
			return results;
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;

		// area count is 1
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 001
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				fullKeyName.getBytes(), fullKeyName.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;


		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("524",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("524",
					reqbuf, offset);
		}
		if (len >= 0) {
			try {
				results[0] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyValue), "GBK");
				results[1] = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldKeyCheckValue), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
		} else {
			results[0] = len + "";
		}
		return results;
	}
	/**
	 * 生成CVV 287
	 * @param acco
	 * @return
	 */
	public String UnionGenerateCVV(String magnetic){
		if (magnetic == null || magnetic.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;

		// area count is 1
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 041
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldAccNo,
				magnetic.getBytes(), magnetic.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;


		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("287",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("287",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldVisaCVV), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return result;
		} else {
			return len + "";
		}
	}
	/**
	 * 计算MAC 直接传入ZAK密钥值 603
	 * @param macData Mac数据
	 * @param zak Zak密钥值
	 * @return 计算出的MAC
	 */ 
	public String UnionCalculateMacDirectIncomingZak(String macData,String zak){
		if (macData == null || macData.length() == 0 || zak == null || zak.length() == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[1024];
		int offset = 0;

		// area count is 2
		System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;

		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area1 021
		byte[] area1 = UnionBitPutFldIntoStr(fldTag.conEsscFldMacData,
				macData.getBytes(), macData.getBytes().length);
		System.arraycopy(area1, 0, reqbuf, offset, area1.length);
		offset += area1.length;

		// area2 062
		byte[] area2 = UnionBitPutFldIntoStr(fldTag.conEsscFldSecondKeyCheckValue,
				zak.getBytes(), zak.getBytes().length);
		System.arraycopy(area2, 0, reqbuf, offset, area2.length);
		offset += area2.length;

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("603",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("603",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldMac), "GBK");
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}
			return result;
		} else {
			return len + "";
		}
	}
	
	public String UnionEncryptPlainDataWithByte(String zekName, byte[] data,
			String encFlag, String iv) {
		if (zekName == null || zekName.length() == 0 || data == null
				|| data.length == 0 ) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[data.length + 200];
		int offset = 0;

		// area count is 2 or 4
		if (encFlag.contentEquals("1")) {
			System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		} else {
			System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		}
		offset += 3;
		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area 001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zekName.getBytes(), zekName.getBytes().length);
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area 081
		byte[] area081 = UnionBitPutFldIntoStr(fldTag.conEsscFldPlainData,
				data, data.length);
		System.arraycopy(area081, 0, reqbuf, offset, area081.length);
		offset += area081.length;

		// area 002
		if (encFlag.contentEquals("1")) {
			// area 214
			byte[] area214 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldAlgorithmMode, encFlag.getBytes(),
					encFlag.getBytes().length);
			System.arraycopy(area214, 0, reqbuf, offset, area214.length);
			offset += area214.length;

			// area 213
			byte[] area213 = UnionBitPutFldIntoStr(fldTag.conEsscFldIV,
					iv.getBytes(), iv.getBytes().length);
			System.arraycopy(area213, 0, reqbuf, offset, area213.length);
			offset += area213.length;
		}

		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("516",
					reqbuf, offset);
		} else {

			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("516",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldCiperData), "GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
		} else {
			return len + "";
		}

	}
	public String UnionDecryptEncDataWithByte(String zekName, byte[] encData,
			String encFlag, String iv)  {
		if (zekName == null || zekName.length() == 0 || encData == null
				|| encData.length == 0) {
			logger.debug("调用API接口传入参数错误");
			return "-5501";
		}
		byte[] reqbuf = new byte[encData.length + 200];
		int offset = 0;
		if (encFlag.contentEquals("1")) {
			System.arraycopy("004".getBytes(), 0, reqbuf, offset, 3);
		} else {
			System.arraycopy("002".getBytes(), 0, reqbuf, offset, 3);
		}
		offset += 3;
		EsscFldTagDef fldTag = new EsscFldTagDef();
		// area area001
		byte[] area001 = UnionBitPutFldIntoStr(fldTag.conEsscFldKeyName,
				zekName.getBytes(), zekName.length());
		System.arraycopy(area001, 0, reqbuf, offset, area001.length);
		offset += area001.length;

		// area area082
		byte[] area082 = UnionBitPutFldIntoStr(fldTag.conEsscFldCiperData,
				encData, encData.length);
		System.arraycopy(area082, 0, reqbuf, offset, area082.length);
		offset += area082.length;

		if (encFlag.contentEquals("1")) {
			// area 214
			byte[] area214 = UnionBitPutFldIntoStr(
					fldTag.conEsscFldAlgorithmMode, encFlag.getBytes(),
					encFlag.getBytes().length);
			System.arraycopy(area214, 0, reqbuf, offset, area214.length);
			offset += area214.length;

			// area 213
			byte[] area213 = UnionBitPutFldIntoStr(fldTag.conEsscFldIV,
					iv.getBytes(), iv.getBytes().length);
			System.arraycopy(area213, 0, reqbuf, offset, area213.length);
			offset += area213.length;
		}
		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("517",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("517",
					reqbuf, offset);
		}
		String result = "";
		if (len >= 0) {
			try {
				UnionStr us = new UnionStr();
				result = us.bcdhex_to_aschex(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldData));
				log.debug("517 UnionDecryptEncDataWithByte:: clearData=="+result);
				
				result = new String(UnionReadSpecFldFromBytes(
						commWithEsscSvr.bytereturnPackage, len,
						fldTag.conEsscFldData), "GBK");
			
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return result;

	}
	/**
	 * 测试密码服务平台是否能正常提供服务 000
	 * @param testData 测试数据
	 * @return
	 */
	public int UnionTestEssc(String testData){
		byte[] reqbuf = new byte[testData.length()+10];
		int offset = 0;
		System.arraycopy("001".getBytes(), 0, reqbuf, offset, 3);
		offset += 3;
		EsscFldTagDef fldTag = new EsscFldTagDef();

		// area1
		byte[] area072 = UnionBitPutFldIntoStr(
				fldTag.conEsscFldDirectHsmCmdReq, testData.getBytes(),
				testData.length());

		System.arraycopy(area072, 0, reqbuf, offset, area072.length);
		offset += area072.length;
		CommWithEsscSvr commWithEsscSvr = new CommWithEsscSvr(esscIp, esscPort,
				timeOut, gunionIDOfEsscAPI);
		int len = 0;
		if (longOrShortConn == 0) {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrShortConn("000",
					reqbuf, offset);
		} else {
			len = commWithEsscSvr.UnionBitCommWithEsscSvrLongConn("000",
					reqbuf, offset);
		}
		return len;
	}
}
