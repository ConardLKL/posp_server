package com.bestpay.posp.sfiptest;

import java.io.File;
import java.math.BigDecimal;

import com.bestpay.posp.protocol.PacketDefineFormat;
import com.bestpay.posp.protocol.PacketDefineType;
import com.bestpay.posp.protocol.PKT_DEF;
import com.bestpay.posp.protocol.PacketDefineSpec;

import com.regaltec.nma.collector.common.ExcelUtil;

public class LoadPktDefFromExcel {
	public PKT_DEF[] load() {
		String configFilePath = "../config/T_POSP_PKT_DEF.xls";
		File configFile = new File(configFilePath);
		if (!configFile.exists()) {
			configFilePath = "src/main/resources/T_POSP_PKT_DEF.xls";
		}
		String[][] t_posp_pkt_def = ExcelUtil.readExcelToTable(configFilePath,
				"SQL Results");
		// PKT_ID CHANNEL_CODE SERVICE MSG_TYPE BIT TYPE FORMAT SPEC LENGTH STAT
		int colindex_MSG_TYPE = 4;
		int colindex_BIT = 5;
		int colindex_TYPE = 6;
		int colindex_FORMAT = 7;
		int colindex_LENGTH = 9;
		PKT_DEF[] pktdef = new PKT_DEF[64 + 1];
		for (int i = 1; i < t_posp_pkt_def.length; i++) {
			String msg_type = t_posp_pkt_def[i][colindex_MSG_TYPE];
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
			int length = BigDecimal.valueOf(
					Double.parseDouble(strLength.trim())).intValue();
			pktdef[bit] = new PKT_DEF();
			pktdef[bit].setSpec(PacketDefineSpec.NULL_VALUE);
			pktdef[bit].setLength(length);

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
		return pktdef;
	}
}
