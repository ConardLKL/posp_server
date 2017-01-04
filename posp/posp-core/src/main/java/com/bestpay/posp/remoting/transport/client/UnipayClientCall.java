package com.bestpay.posp.remoting.transport.client;

import com.bestpay.dubbo.CupsfService;
import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.PKT_DEF;
import com.bestpay.posp.protocol.UnipayCombineMac;
import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.system.cache.ConfigCache;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

@Slf4j
public class UnipayClientCall implements ClientCall{

	private String seq = "";
	private int readTimeout = 0;
	private int writTimeout = 0;
//	private String HOST ="172.25.132.29";
	private String HOST ="";
	private int PORT = 1688;
	private PKT_DEF[] unipayPktDef;
	
	private int headerLength = 46; // 头长度
	private int headerFlagAndVersion = 02; // 头标识和版本号
	// private int totalMessageLength = 0; // 整个报文长度
	// private int packetMessageLength = 0; // 包体报文长度
	private String destinationID = "00010000"; // 目的ID
	private String sourceID = "48370000"; // 源ID
//	private String reservedforUse = null; // 保留使用
	private int batchNumber = 0; // 批次号
	private String transactionInfo = "00000000"; // 交易信息
	// private String userInfo = null; // 用户信息
	private String rejectCode = "00000"; // 拒绝码
	private byte[] reservedforUse = new byte[]{0x00,0x00,0x00};
	
	public UnipayClientCall(PKT_DEF[] unipayPktDef){
		ConfigCache configCache = (ConfigCache)PospApplicationContext.getBean("ConfigCache");
		this.unipayPktDef = unipayPktDef;
		this.HOST = configCache.getParaValues(SysConstant.CL7001, "100010");
//		this.HOST = "127.0.0.1";
//		this.PORT = Integer.parseInt(configCache.getParaValues(SysConstant.CL7001, "100011"));
		this.PORT = 1689;
		this.readTimeout = Integer.parseInt(configCache.getParaValues(SysConstant.CL7001, "100012"));
		this.writTimeout = Integer.parseInt(configCache.getParaValues(SysConstant.CL7001, "100013"));
	}
	
	public UnipayClientCall(int readTimeout, int writTimeout, String HOST, int PORT, PKT_DEF[] unipayPktDef){
		this.readTimeout = readTimeout;
		this.writTimeout = writTimeout;
		this.HOST = HOST;
		this.PORT = PORT;
		this.unipayPktDef = unipayPktDef;
	}
	
	public IsoMessage call(IsoMessage in, int timeout) throws Exception {
		
		if(unipayPktDef == null){
			//没有设置银联报文定义
			return in;
		}
		
		this.seq = in.getSeq();
		this.readTimeout = timeout;
		if(!in.getTranCode().equals("0620951"))
			in.setCombineMac(new UnipayCombineMac());
		in.printMessage(in.getSeq());
		byte[] unipay8583 = this.send(in.getMessage());
		in.setISO8859(null);
		if(unipay8583 != null){
			IsoMessage out = in.clone();
			out.setMessageDefine(unipayPktDef);
			out.setMessage(unipay8583);
			out.setChannelCode(SysConstant.CAPITAL_POOL_6001);
			return out; 
		}
		return null;
	}
	
	private byte[] send(byte[] out8583) throws Exception {
		CupsfService cupsfService = (CupsfService)PospApplicationContext.getBean("cupsfService");
		int outMsgLen = out8583.length;
		byte[] outHeader = getHeaderBytebuf(outMsgLen);
		byte[] packet    = new byte[outMsgLen+50];
		setByteContext(packet, outHeader, 50, 0);
		setByteContext(packet, out8583, outMsgLen, 50);
		byte[] unipay8583 = null;
		try {
			unipay8583 = cupsfService.send(packet);
		}catch (Exception e){
			log.error("dubbo error:"+e.getMessage());
		}
		return unipay8583;
	}

	public static byte[] getByteContext(byte[] a, int len, int s) {  
		byte[] b = new byte[len];
		for (int i = 0; i < len; i++) {
			 b[i] = a[s+i];
		}
		return b;
	} 
	 
	public static void setByteContext(byte[] a, byte[] b, int len, int s) {  
		for(int i=0; i < len; i++){
			a[s+i] = b[i];
		}
	}
	
	
	public static void setByteContext(byte[] a, byte b, int i) {  
		a[i] = b;
	}
	
	
	private byte[] getHeaderBytebuf(int packet_body_length) throws UnsupportedEncodingException{
		
		byte[] header = new byte[50];
		
		StringBuffer strText = new StringBuffer();
		
		String totalLength = String.format("%04d", packet_body_length + 46);
		
		setByteContext(header, totalLength.getBytes("GBK"), 4, 0);
//		strText.append(totalLength);
		//Field1 头长度（Header Length）1
		byte f1 = (byte)headerLength;
		setByteContext(header, f1, 4);
		
		// Field2 头标识和版本号（Header Flag and Version）1
		strText.append(headerFlagAndVersion);
		byte f2 = (byte)headerFlagAndVersion;
		setByteContext(header, f2, 5);
		
		//Field3 整个报文长度（Total Message Length）4
		strText.append(totalLength);
		setByteContext(header, totalLength.getBytes("GBK"), 4, 6);
		
//		Field4 目的ID（Destination ID）11
		strText.append(String.format("%-11s", destinationID));
		setByteContext(header, String.format("%-11s", destinationID).getBytes("GBK"), 11, 10);
		
		
//		Field5 源ID（Source ID） 11
		strText.append(String.format("%-11s", sourceID));
		setByteContext(header, String.format("%-11s", sourceID).getBytes("GBK"), 11, 21);
		
//		Field6 保留使用（Reserved for Use） 3
		strText.append("000");
		
		setByteContext(header, reservedforUse, 3, 32);
		
//		Field7 批次号（Batch Number） 1
		strText.append(batchNumber);
		byte f7 = (byte)(batchNumber >> 8);
		setByteContext(header, f7, 35);
		
//		Field8 交易信息（Transaction Information） 8
		strText.append(String.format("%8s", transactionInfo));
		setByteContext(header, String.format("%8s", transactionInfo).getBytes("GBK"), 8, 36);
		
//		Field9 用户信息（User Information） 1
		strText.append("0");
		byte f9 = (byte)(0 >> 8);
		setByteContext(header, f9, 44);
		
//		Field10 拒绝码（Reject Code） 5
		strText.append(String.format("%5s", rejectCode));
		setByteContext(header, String.format("%5s", rejectCode).getBytes("GBK"), 5, 45);
		return header;
	}
	
}
