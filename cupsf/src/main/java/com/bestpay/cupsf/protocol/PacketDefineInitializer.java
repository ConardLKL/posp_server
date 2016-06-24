package com.bestpay.cupsf.protocol;

import com.bestpay.cupsf.entity.CupsfBuffer;
import com.bestpay.cupsf.entity.PKT_DEF;
import com.bestpay.cupsf.entity.TCfgPktDef;
import com.bestpay.cupsf.utils.TextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HR on 2016/5/17.
 */
public final class PacketDefineInitializer {
	
	private Map<String, PKT_DEF[]> hashMapPktDef = new HashMap<String, PKT_DEF[]>();

	public PacketDefineInitializer() {
	}

	private synchronized void init() throws Exception {
		List<TCfgPktDef> pktDefs = TextUtil.readTxtFile();
		Map<String, List<TCfgPktDef>> pktDefMap = new HashMap<String, List<TCfgPktDef>>();
		pktDefMap.put("6001", pktDefs);
		for (Map.Entry<String, List<TCfgPktDef>> pktItem : pktDefMap.entrySet()) {
			String service = pktItem.getKey();
			PKT_DEF[] pktdef = new PKT_DEF[pktItem.getValue().size()];
			for (TCfgPktDef pospPktDef : pktItem.getValue()) {
				
				int bit = pospPktDef.getBit() != null ? Integer.valueOf(pospPktDef.getBit().toString()) : 0;
				String type = pospPktDef.getType();
				String format = pospPktDef.getFormat();
				String spec = pospPktDef.getSpec();
				int length =  pospPktDef.getLength() != null ? Integer.valueOf(pospPktDef.getLength().toString()) : 0;
				
				pktdef[bit] = new PKT_DEF();
				pktdef[bit].setSpec(PacketDefineSpec.NULL_VALUE);
				pktdef[bit].setLength(length);
				
				if (type != null) {
					if (type.equals("ASC")) {
						pktdef[bit].setType(PacketDefineType.TYP_ASC);
					}
					else if (type.equals("ASC2")) {
						pktdef[bit].setType(PacketDefineType.TYP_ASC2);
					}
					else if (type.equals("BIN")) {
						pktdef[bit].setType(PacketDefineType.TYP_BIN);
					}
					else if (type.equals("BIT")) {
						pktdef[bit].setType(PacketDefineType.TYP_BIT);
					}
					else if (type.equals("BCD")) {
						pktdef[bit].setType(PacketDefineType.TYP_BCD);
					}
				}

				if (format != null) {
					if (format.equals("FIXED"))
						pktdef[bit].setFormat(PacketDefineFormat.FMT_FIXED);
					else if (format.equals("LVAR"))
						pktdef[bit].setFormat(PacketDefineFormat.FMT_LVAR);
					else if (format.equals("LLVAR"))
						pktdef[bit].setFormat(PacketDefineFormat.FMT_LLVAR);
					else if (format.equals("LLLVAR"))
						pktdef[bit].setFormat(PacketDefineFormat.FMT_LLLVAR);
				}

				if (spec != null) {
					if (spec.equals("L_BLANK"))
						pktdef[bit].setSpec(PacketDefineSpec.L_BLANK);
					else if (spec.equals("R_BLANK"))
						pktdef[bit].setSpec(PacketDefineSpec.R_BLANK);
					else if (spec.equals("L_ZERO"))
						pktdef[bit].setSpec(PacketDefineSpec.L_ZERO);
					else if (spec.equals("R_ZERO"))
						pktdef[bit].setSpec(PacketDefineSpec.R_ZERO);
					else
						pktdef[bit].setSpec(PacketDefineSpec.NULL_VALUE);
				}
				
			}
			hashMapPktDef.put(service, pktdef);
		}
	}

	public void findPacketDefine() throws Exception{
		init();
		CupsfBuffer.pkt_def = hashMapPktDef.get("6001");
	}
}
