package com.bestpay.posp.utils.crypto.essc.communicate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.log4j.Logger;

import com.bestpay.posp.utils.crypto.essc.api.Config;
import com.bestpay.posp.utils.crypto.essc.api.HSMException;

public class CommWithEsscSvr {
	public String hsmip;
	public int hsmport;
	public byte[] bytereturnPackage;
	public String gunionIDOfEsscAPI;
	public int timeOut;
	public int longOrShortConn;

	private static ConnectPool connPool = null;
	private static Object lock = new Object();
	private static CommWithEsscSvr instance = null;
	private static Logger logger = Logger.getLogger("CommWithEsscSvr.class");

	public static synchronized CommWithEsscSvr getInstance(String ip, int port,
			int timeOut, String gunionIDOfEsscAPI) {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new CommWithEsscSvr(ip, port, timeOut,
							gunionIDOfEsscAPI);
				}

			}
		}
		return instance;
	}

	public CommWithEsscSvr(String ip, int port, int timeOut,
			String gunionIDOfEsscAPI) {
		hsmip = ip;
		hsmport = port;
		bytereturnPackage = null;
		this.timeOut = timeOut;
		this.gunionIDOfEsscAPI = gunionIDOfEsscAPI;
		if (connPool == null) {
			synchronized (lock) {
				if (connPool == null)
					connPool = ConnectPool.getInstance(hsmip, hsmport, 30);
			}
		}
	}

	public int UnionBitCommWithEsscSvrShortConn(String serviceCode,
			byte[] reqStr, int lenOfReqStr) {
		int ret = 0;
		int allReqLen = 0;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		int len;
		len = lenOfReqStr + 6;
		Integer h1, h2;
		byte[] outbytes = null;
		// 组织请求报文
		byte head[] = { 0, 0 };

		// 组织请求报文
		h1 = new Integer(len / 256);
		h2 = new Integer(len % 256);
		head[0] = h1.byteValue();
		head[1] = h2.byteValue();

		try {
			bout.write(head);
			bout.write(gunionIDOfEsscAPI.getBytes());
			bout.write(serviceCode.getBytes());
			bout.write("1".getBytes());
			bout.write(reqStr, 0, lenOfReqStr);
		} catch (IOException e) {
			logger.debug("发送请求报文时出错!");
			return -5505;
		}

		allReqLen = Config.ALL_LEN_LEN + 6 + lenOfReqStr;

		// 开始与HSM机交互
		SocketIO socket = new SocketIO();
		try {
		      if (!socket.connectHSM(hsmip, hsmport, timeOut)) {
		        socket.allClose();
		        logger.error("socket连接超时（可能IP端口出错）!");
		        return -5518;
		      }
		    } catch (Exception e) {
		      logger.error("socket异常" + e);
		      return -5518;
		    }
		outbytes = socket.BytesHsmCmd(bout.toByteArray(), allReqLen);
		socket.allClose();

		// check outbytes
		
		if (outbytes == null) {
		      logger.error("服务器返回了长度为0的响应!");
		      return -5508;
		 }
		
		// errCode
		String errCode = new String(outbytes, Config.ERRCODE_GEGIN,
				Config.ERRCODE_LEN);
		if (new Integer(errCode).intValue() < 0) {
			return new Integer(errCode).intValue();
		}

		int allheadlen = 2 + Config.API_ID_LEN + Config.SVR_CODE_LEN
				+ Config.RESPFLAG_LEN + Config.ERRCODE_LEN;
		// len check
		if (outbytes.length < allheadlen){
			logger.debug("响应报文长度不对!");
			return -5504;
		}

		// allLen
		int allLen = outbytes.length;
		// responseFlag
		char responseFlag = new String(outbytes, Config.RESPFLAG_BEGIN,
				Config.RESPFLAG_LEN).charAt(0);
		if (responseFlag != '0'){
			logger.debug("响应报文格式不对!");
			return -5504;
		}
		// apiId
		String apiID = new String(outbytes, Config.API_ID_GEGIN,
				Config.API_ID_LEN);

		// serviceCode
		String svrCode = new String(outbytes, Config.SVR_CODE_BEGIN,
				Config.SVR_CODE_LEN);
		if (!apiID.equals(gunionIDOfEsscAPI) || !svrCode.equals(serviceCode)){
			logger.debug("响应报文格式不对!");
			return -5504;
		}

		bytereturnPackage = new byte[outbytes.length - allheadlen];
		System.arraycopy(outbytes, allheadlen, bytereturnPackage, 0, allLen
				- allheadlen);
		ret = allLen - allheadlen;
		return ret;
	}

	public int UnionBitCommWithEsscSvrLongConn(String serviceCode,
			byte[] reqStr, int lenOfReqStr) {
		int ret = 0;
		int allReqLen = 0;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		int len;
		len = lenOfReqStr + 6;
		Integer h1, h2;
		byte[] outbytes = null;
		// 组织请求报文
		byte head[] = { 0, 0 };

		// 组织请求报文
		h1 = new Integer(len / 256);
		h2 = new Integer(len % 256);
		head[0] = h1.byteValue();
		head[1] = h2.byteValue();

		try {
			bout.write(head);
			bout.write(gunionIDOfEsscAPI.getBytes());
			bout.write(serviceCode.getBytes());
			bout.write("1".getBytes());
		} catch (IOException e) {
			logger.debug("发送请求报文时出错!");
			return -5505;
		}

		bout.write(reqStr, 0, lenOfReqStr);
		allReqLen = Config.ALL_LEN_LEN + 6 + lenOfReqStr;

		// 开始与HSM机交互
		SocketIO socket = null;
		// log.debug(bout.toString());
		try {
			socket = connPool.getConnect();
			if (socket == null)
				return -1;
			if (socket.IsConnected() == false) {
				if (!socket.connectHSM(hsmip, hsmport, timeOut)) {
					logger.debug("socket连接超时（可能IP端口出错）!");
					return -5518;
				}
			}
			outbytes = socket.BytesHsmCmd(bout.toByteArray(), allReqLen);

			if (outbytes == null || outbytes.length < 12) {
				socket.allClose();
				socket = new SocketIO();
				if (!socket.connectHSM(hsmip, hsmport, timeOut)) {
					logger.debug("socket连接超时（可能IP端口出错）!");
					return -5518;
				}
				outbytes = socket.BytesHsmCmd(bout.toByteArray(), allReqLen);
			}

		} catch (SocketException e) {
			socket.allClose();
		} catch (HSMException e) {
			socket.allClose();
		} catch (Exception e) {
			socket.allClose();
		} finally {
			connPool.putConnect(socket);
		}

		// check outbytes
		if (outbytes == null) {
		      logger.error("服务器返回了长度为0的响应!");
		      return -5508;
		}
		
		// errCode
		String errCode = new String(outbytes, Config.ERRCODE_GEGIN,
				Config.ERRCODE_LEN);
		if (new Integer(errCode).intValue() < 0) {
			return new Integer(errCode).intValue();
		}

		int allheadlen = 2 + Config.API_ID_LEN + Config.SVR_CODE_LEN
				+ Config.RESPFLAG_LEN + Config.ERRCODE_LEN;
		// len check
		if (outbytes.length < allheadlen)
			return -5504;

		// allLen
		int allLen = outbytes.length;
		// responseFlag
		char responseFlag = new String(outbytes, Config.RESPFLAG_BEGIN,
				Config.RESPFLAG_LEN).charAt(0);
		if (responseFlag != '0')
			return -5504;
		// apiId
		String apiID = new String(outbytes, Config.API_ID_GEGIN,
				Config.API_ID_LEN);

		// serviceCode
		String svrCode = new String(outbytes, Config.SVR_CODE_BEGIN,
				Config.SVR_CODE_LEN);
		if (!apiID.equals(gunionIDOfEsscAPI) || !svrCode.equals(serviceCode))
			return -5504;

		bytereturnPackage = new byte[outbytes.length - allheadlen];
		System.arraycopy(outbytes, allheadlen, bytereturnPackage, 0, allLen
				- allheadlen);
		ret = allLen - allheadlen;
		return ret;
	}

}
