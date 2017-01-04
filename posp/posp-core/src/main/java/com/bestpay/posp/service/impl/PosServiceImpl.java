/**
 * 
 */
package com.bestpay.posp.service.impl;

import java.io.File;
import java.math.BigDecimal;

import com.bestpay.posp.protocol.PacketDefineFormat;
import com.bestpay.posp.protocol.PacketDefineType;
import com.bestpay.posp.protocol.PKT_DEF;
import com.bestpay.posp.protocol.PacketDefineSpec;
import com.bestpay.posp.service.PosService1;

import com.regaltec.nma.collector.common.ExcelUtil;

/**
 * @author yihaijun
 * 
 */
public class PosServiceImpl implements PosService1 {
	static PKT_DEF[] pktdef = null;

	public byte[] call(byte[] request) {
//		if(log.isDebugEnabled())
//		{
//			log.debug("request:" + HexCodec.hexEncode(request));
//		}
//		
//		byte[] msg_type = new byte[4];
//		msg_type[0] =  request[2 + 0];
//		msg_type[1] =  request[2 + 1];
//		msg_type[2] =  request[2 + 2];
//		msg_type[3] =  request[2 + 3];
//
//		
//		byte [] req = new byte[request.length - 2];
//		for(int n = 2 ; n < request.length ; n ++)
//		{
//			req[n-2] = request[n];
//		}
//		//System.arraycopy(req, 0, request, 2, request.length - 2);
//		log.debug("req:" + HexCodec.hexEncode(req));
//
//		PKT_DEF[] pkt_def = PacketDefineInitializer.getInstance()
//				.findPacketDefine(new String(msg_type));
//		if (true) {
//			try{
//				IsoMessage iso = new IsoMessage();
//				iso.setMessageDefine(pkt_def);
//
//				iso.setMessage(req);
//				iso.printMessage("0000000000000000");			
//				
//			}catch(Exception e)
//			{
//				e.printStackTrace();
//			}			
//	
//			
//		} else {
//			log.debug("error , please do something here");
//		}

		
		return request;
	}

	public byte[] callmock(byte[] request) {
		return test_128();
	}

	public static void loadDataTmp() {
		String configFilePath = "../config/T_POSP_PKT_DEF.xls";
		File configFile = new File(configFilePath);
		if (!configFile.exists()) {
			configFilePath = "src/main/resources/T_POSP_PKT_DEF.xls";
		}
		configFile = new File(configFilePath);
		if (!configFile.exists()) {
			PospApplicationContextServices.copyResourceFile2SfipPath("T_POSP_PKT_DEF.xls", "T_POSP_PKT_DEF.xls");
		}
		
		String[][] t_posp_pkt_def = ExcelUtil.readExcelToTable(configFilePath,
				"SQL Results");
		// PKT_ID CHANNEL_CODE SERVICE MSG_TYPE BIT TYPE FORMAT SPEC LENGTH STAT
//		int colindex_MSG_TYPE = 4;
		int colindex_BIT = 5;
		int colindex_TYPE = 6;
		int colindex_FORMAT = 7;
		int colindex_LENGTH = 9;
		pktdef = new PKT_DEF[64 + 1];
		for (int i = 1; i < t_posp_pkt_def.length; i++) {
//			String msg_type = t_posp_pkt_def[i][colindex_MSG_TYPE];
			String strBit = t_posp_pkt_def[i][colindex_BIT];
			if(strBit.isEmpty()){
				continue;
			}
			int bit = BigDecimal.valueOf(Double.parseDouble(strBit.trim()))
					.intValue();
			String type = t_posp_pkt_def[i][colindex_TYPE];
			String format = t_posp_pkt_def[i][colindex_FORMAT];
			String strLength = t_posp_pkt_def[i][colindex_LENGTH];
			if(strLength.isEmpty()){
				continue;
			}
			int length = BigDecimal.valueOf(Double.parseDouble(strLength.trim())).intValue();
			pktdef[bit] = new PKT_DEF();
			pktdef[bit].setSpec(PacketDefineSpec.NULL_VALUE);
			pktdef[bit].setLength(Integer.valueOf(length));

			if (type.equals("ASC"))
				pktdef[bit].setType(PacketDefineType.TYP_ASC);
			else if (type.equals("BIN"))
				pktdef[bit].setType(PacketDefineType.TYP_BIN);
			else if (type.equals("BIT"))
				pktdef[bit].setType(PacketDefineType.TYP_BIT);
			else if (type.equals("BCD"))
				pktdef[bit].setType(PacketDefineType.TYP_BCD);
			if (format.equals("FIXED"))
				pktdef[bit].setFormat(PacketDefineFormat.FMT_FIXED);
			else if (format.equals("LVAR"))
				pktdef[bit].setFormat(PacketDefineFormat.FMT_LVAR);
			else if (format.equals("LLVAR"))
				pktdef[bit].setFormat(PacketDefineFormat.FMT_LLVAR);
			else if (format.equals("LLLVAR"))
				pktdef[bit].setFormat(PacketDefineFormat.FMT_LLLVAR);
		}

	}

	static public byte[] test_128() {
//		IsoMessage msg = new IsoMessage();
//		try {
//			PKT_DEF[] pkt_def = PacketDefineInitializer.getInstance()
//					.findPacketDefine("0210");
//			msg.setMessageDefine(pkt_def);
//			msg.setField(0, "0200");
//			// msg.setField(1, "87654321");
//			msg.setField(2, "622621542365212450");
//			msg.setField(3, "000000");
//			msg.setField(4, "000000000001");
//			//msg.setField(7, "2014052709");
//			msg.setField(11, "000000");
//			msg.setField(12, "092736");
//			msg.setField(13, "1243");
//			// msg.setField(14, "0609");//
//			msg.setField(15, "0527");
//			msg.setField(22, "813");
//			msg.setField(32, "");
//			msg.setField(37, "201405270932");
//			msg.setField(41, "88200001");
//			msg.setField(42, "000000000006059");
//			//msg.setField(48, "0221000");
//			//msg.setField(52, "0000000000000000");
//			msg.setField(63, "00000000");
//			//msg.setField(100, "625810");
//			//msg.setField(104, "P");
//			//msg.setField(107, "999999999999");
//			//msg.setField(128, "1234567890ABCDEF");
//			msg.getMessageWithHex();
//			log.debug(msg.getMessageWithHex());
//
//			IsoMessage msg2 = new IsoMessage();
//			msg2.setMessageDefine(pkt_def);
//			msg2.setMessageWithHex(msg.getMessageWithHex());
//
//			return msg2.getMessage();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return "".getBytes();
	}

}
