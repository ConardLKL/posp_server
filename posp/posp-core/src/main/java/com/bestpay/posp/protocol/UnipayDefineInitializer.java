package com.bestpay.posp.protocol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.system.entity.TCfgPktDef;
import com.bestpay.posp.system.service.TCfgPktDefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UnipayDefineInitializer implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8034858650569234065L;

	@Autowired
	private TCfgPktDefService tCfgPktDefService;
	
	private Map<String, PKT_DEF[]> hashMapPktDef = new HashMap<String, PKT_DEF[]>();
	
	private UnipayDefineInitializer() {}

	public synchronized void init() throws Exception {
		TCfgPktDef def = new TCfgPktDef();
		def.setChannelCode(SysConstant.CAPITAL_POOL_6001);
		Map<String, List<TCfgPktDef>> pospPktDefMap = tCfgPktDefService.initPktDef(def);
		
		for (Entry<String, List<TCfgPktDef>> pktItem : pospPktDefMap.entrySet()) {
			if(pktItem.getValue().size() != (64 + 1) && pktItem.getValue().size() != (128 + 1))
				throw new Exception( String.format("FORMAT_EXCEPTION: 8583数据定义错误。T_POSP_PKT_DEF表：SERVICE(%s) 必须配置为64域或128域", pktItem.getKey()));
			
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
					if (type.equals("ASC"))
						pktdef[bit].setType(PacketDefineType.TYP_ASC);
					else if (type.equals("BIN"))
						pktdef[bit].setType(PacketDefineType.TYP_BIN);
					else if (type.equals("BIT"))
						pktdef[bit].setType(PacketDefineType.TYP_BIT);
					else if (type.equals("BCD"))
						pktdef[bit].setType(PacketDefineType.TYP_BCD);
					else if (type.equals("ASC2"))
						pktdef[bit].setType(PacketDefineType.TYP_ASC2);
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

	public PKT_DEF[] findPacketDefine(String msg_type) {
		PKT_DEF[] pkt = hashMapPktDef.get(msg_type);
		if (pkt == null) {
			// 查找默认值
			return hashMapPktDef.get("*");
		} else {
			return pkt;
		}
	}

}
