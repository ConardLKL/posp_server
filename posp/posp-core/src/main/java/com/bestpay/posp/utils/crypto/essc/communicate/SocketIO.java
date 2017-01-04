package com.bestpay.posp.utils.crypto.essc.communicate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import lombok.extern.slf4j.Slf4j;

import org.apache.log4j.Logger;
@Slf4j
public class SocketIO {
	private Socket h;
	private BufferedOutputStream byteos;
	private BufferedInputStream bytein;

	public boolean ok = false;
	public String conerrmsg = null;
	private static Logger logger = Logger.getLogger("SocketIO.class");
	
	public SocketIO() {

	}
	public SocketIO(int iserial) {
		h = null;
		byteos = null;
		bytein = null;
		ok = false;
		conerrmsg = null;
	}
	protected boolean IsConnected()
	{
		 return this.ok;
	}
	
	public boolean connectHSM(String ip, int port,int timeout) throws Exception {
		try {
			h = new Socket();
			h.connect(new InetSocketAddress(ip, port), timeout*1000);
			h.setSoLinger(true, 0);
			h.setSoTimeout(100000);

			byteos = new BufferedOutputStream(h.getOutputStream());
			bytein = new BufferedInputStream(h.getInputStream());
			ok = true;
		} catch (SocketException e) {
			ok = false;
			logger.debug("socket连接出错！");
		} catch (Exception e) {
			ok = false;
		} finally {
			if (!ok) {
				allClose();
				return false;
			}
		}
		return true;
	}

	public byte[] BytesHsmCmd(byte[] in, int alllen)  {
		try {
			SendToHSM(in, alllen);
			log.debug("Send to hsm::"+new String(in,"GBK"));
		} catch (Exception e) {
			logger.debug("发送请求报文时出错!");
			return "-5524".getBytes();
		}
		return ReceFromHSM();
	}

	private void SendToHSM(byte[] in, int alllen) throws Exception {
		byteos.write(in, 0, alllen);
		byteos.flush();
	}

	private byte[] ReceFromHSM()  {
		byte[] outbyte = null;
		int count=0;
		try {
			int offset=0;
			outbyte=new byte[4096];
			count=bytein.read(outbyte, offset, 4096);
		} catch (Exception e) {
			logger.debug("接收响应报文时出错!");
			return "-5525".getBytes();
		}
		byte[] retbytes = new byte[count];
		System.arraycopy(outbyte, 0, retbytes, 0, count);
		return retbytes;
	}

	public void allClose() {
		try {
			if (bytein != null) {
				bytein.close();
				bytein = null;
			}
			if (byteos != null) {
				byteos.close();
				byteos = null;
			}
			if (h != null) {
				h.close();
				h = null;
			}
		} catch (Exception e) {
			;
		}
	}
	public void IOClose() {
		try {
			if (bytein != null) {
				bytein.close();
				bytein = null;
			}
			if (byteos != null) {
				byteos.close();
				byteos = null;
			}
		} catch (Exception e) {
			;
		}
	}
}