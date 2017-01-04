/**
 * 
 */
package com.bestpay.posp.protocol.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.PacketDefineInitializer;
import com.bestpay.posp.protocol.UnipayDefineInitializer;

import com.regaltec.nma.collector.adapter.common.NmaCollectorExpressionInterpretor;
import com.tisson.sfip.api.message.transformer.ObjectTransformerUtil;
import com.tisson.sfip.api.message.transformer.SfipBeanTransformerInf;

/**
 *
 */
@Log4j
@Component
public class PospIsoMap2MapTransformerImpl implements SfipBeanTransformerInf {
	@Autowired
	@Qualifier("UnipayDefineInitializer")
	private UnipayDefineInitializer unipayDefineInitializer;

	@Autowired
	@Qualifier("PacketDefineInitializer")
	private PacketDefineInitializer packetDefineInitializer;

	public Object doTransform(String beanParam, Object payload,
			Hashtable runVarSet, String currKeyfix) {
		if (!(payload instanceof IsoMessage)) {
			return payload;
		}
		IsoMessage in = (IsoMessage) payload;
		if (beanParam == null || beanParam.equals("")) {
			return in;
		}
		String[] cfgLineArry = beanParam.split("FD");
		if (cfgLineArry == null || cfgLineArry.length <= 0) {
			return in;
		}
		Hashtable tmpRunVarSet = new Hashtable();
		tmpRunVarSet.put("VARNullSign","");//定义用于判断域为空的NULL定义
		tmpRunVarSet.putAll(runVarSet);
		Hashtable tmpObjectVarSet = null;
		Object tmpObj = null;
		String objectKey = "";
		objectKey = "flow";
		tmpObj = runVarSet.get(currKeyfix + objectKey);//取当前flow
		tmpObjectVarSet = ObjectTransformerUtil.object2Hashtable(tmpObj, objectKey);//把flow转换成hashtable
		tmpRunVarSet.putAll(tmpObjectVarSet);

		objectKey = "orgFlow";
		tmpObj = runVarSet.get(currKeyfix + objectKey);
		tmpObjectVarSet = ObjectTransformerUtil.object2Hashtable(tmpObj, objectKey);
		tmpRunVarSet.putAll(tmpObjectVarSet);

		objectKey = "hashMapField";
		tmpObj = runVarSet.get(currKeyfix + objectKey);

		Map<Integer, String> hashMapField = (HashMap<Integer, String>) tmpObj;
		if (hashMapField != null) {
			java.util.Iterator<Integer> it = hashMapField.keySet().iterator();
			while (it.hasNext()) {
				Integer key = it.next();
				try {
					tmpRunVarSet.put("OLD" + key.toString(),hashMapField.get(key));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		objectKey = "transformerTmpField";
		tmpObj = runVarSet.get(currKeyfix + objectKey);
		Map<String, String> transformerTmpField = (HashMap<String, String>) tmpObj;
		if (hashMapField != null) {
			java.util.Iterator<String> it = transformerTmpField.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				try {
					tmpRunVarSet.put(key,transformerTmpField.get(key));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		IsoMessage out = null;
		try {
			if (beanParam.startsWith("unipayDefineInitializer(")  ) {
				int defTypeEndPos = beanParam.indexOf("),");
				String defType = beanParam.substring(
						"unipayDefineInitializer(".length(), defTypeEndPos);
				cfgLineArry[0] = cfgLineArry[0].substring(defTypeEndPos + 2);
				IsoMessage upSend = new IsoMessage();
				upSend.setMessageDefine(unipayDefineInitializer.findPacketDefine(defType));
				upSend.setOrgFlow(in.getOrgFlow());
				upSend.setRspCode(in.getRspCode());
				upSend.setPospCode(in.getPospCode());
				upSend.setRspMsg(in.getRspMsg());
				upSend.setTranCode(in.getTranCode());
				upSend.setChannelCode(in.getChannelCode());
				upSend.setSeq(in.getSeq());
				upSend.setFlow(in.getFlow());
				upSend.setField48(in.getField48());
				upSend.setPlatform(in.isPlatform());
				upSend.setPospMessage(in.getPospMessage());
				upSend.setCardProducts(in.getCardProducts());
				upSend.setFreePasswordSign(in.getFreePasswordSign());
				upSend.setStateKeySign(in.isStateKeySign());
				upSend.setISO8859(in.getISO8859());
				upSend.getTransformerTmpField().putAll(in.getTransformerTmpField());
				
				out = upSend;
			} else if (beanParam.startsWith("packetDefineInitializer(")) {
				int defTypeEndPos = beanParam.indexOf("),");
				String defType = beanParam.substring(
						"packetDefineInitializer(".length(), defTypeEndPos);
				cfgLineArry[0] = cfgLineArry[0].substring(defTypeEndPos + 2);
				IsoMessage upSend = new IsoMessage();
				
				upSend.setMessageDefine(packetDefineInitializer.findPacketDefine(defType));
				
				upSend.setOrgFlow(in.getOrgFlow());
				upSend.setRspCode(in.getRspCode());
				upSend.setPospCode(in.getPospCode());
				upSend.setRspMsg(in.getRspMsg());
				upSend.setSeq(in.getSeq());
				upSend.setTranCode(in.getTranCode());
				upSend.setChannelCode(in.getChannelCode());
				upSend.setFlow(in.getFlow());
				upSend.setPlatform(in.isPlatform());
				upSend.setPospMessage(in.getPospMessage());
				upSend.setCardProducts(in.getCardProducts());
				upSend.setFreePasswordSign(in.getFreePasswordSign());
				upSend.setStateKeySign(in.isStateKeySign());
				upSend.getTransformerTmpField().putAll(in.getTransformerTmpField());
				
				
				out = upSend;
			} else {
				out = in.clone();
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		if (cfgLineArry[0] == null
				|| cfgLineArry[0].trim().equalsIgnoreCase("")) {

		} else {
			String[] unchangedKeyStrArry = cfgLineArry[0].split(",");
			int[] a = new int[unchangedKeyStrArry.length];
			for (int i = 0; i < a.length; i++) {
				try {
					a[i] = Integer.parseInt(unchangedKeyStrArry[i].trim());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				if (in.getField(a[i]) != null && !in.getField(a[i]).equals("")) {
					try {
						out.setField(a[i], in.getField(a[i]));
					} catch (Exception e) {
						e.printStackTrace();
						log.error(currKeyfix +" error:" + e.toString());
					}
				}
			}
		}
		for (int i = 1; i < cfgLineArry.length; i++) {
			if (cfgLineArry[i] == null
					|| cfgLineArry[i].trim().equalsIgnoreCase("")) {
				continue;
			}
			String key = "";
			try {
				key = cfgLineArry[i].substring(0, cfgLineArry[i].indexOf(":"));
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				continue;
			}
			if (key.startsWith("IF")) {
				doIfTransform(cfgLineArry[i], in, out, tmpRunVarSet,currKeyfix);
				continue;
			}
			if (key.startsWith("FROMPOS")) {
				doFromPOSTransform(cfgLineArry[i], in, out, tmpRunVarSet);
				continue;
			}

			int id = -1;
			try {
				id = Integer.parseInt(key);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			}
			String expression = cfgLineArry[i].substring(cfgLineArry[i]
					.indexOf(":") + 1);

			expression = expression.replace("PAYLOAD_", currKeyfix);
			NmaCollectorExpressionInterpretor myinter = new NmaCollectorExpressionInterpretor(
					tmpRunVarSet, expression);
			Object res = null;
			try {
				res = myinter.operation();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (!(res == null || res.equals(""))) {
				try {
					out.setField(id, (String) res);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return out;
	}

	private boolean doIfTransform(String cfg, IsoMessage in, IsoMessage out,
			Hashtable tmpRunVarSet, String currKeyfix) {
		String key = cfg.substring(0, cfg.indexOf(":"));
		if (!(key.startsWith("IF"))) {
			return false;
		}
		NmaCollectorExpressionInterpretor myinter = null;
		Object res = null;
		String expressionCon = "";
		String expressionVal = "";
		int pos = 0;
		try {
			pos = cfg.indexOf(":", key.length() + 1);
			expressionCon = cfg.substring(key.length() + 1, pos);
			expressionVal = cfg.substring(pos + 1);
		} catch (Exception e2) {
			e2.printStackTrace();
			return false;
		}
		
		expressionCon = expressionCon.replace("PAYLOAD_", currKeyfix);
		myinter = new NmaCollectorExpressionInterpretor(tmpRunVarSet,
				expressionCon);
		try {
			res = myinter.operation();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!(res.toString().equalsIgnoreCase("true"))) {
			return false;
		}

		int id = -1;
		String fdName = "";
		try {
			fdName = key.substring(2);
			id = Integer.parseInt(fdName);
		} catch (NumberFormatException e1) {
			// e1.printStackTrace();
		}
		expressionVal = expressionVal.replace("PAYLOAD_", currKeyfix);
		myinter = new NmaCollectorExpressionInterpretor(tmpRunVarSet,
				expressionVal);
		try {
			res = myinter.operation();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!(res == null || res.equals(""))) {
			try {
				if (id == -1) {
					out.getTransformerTmpField().put(fdName, (String) res);
				} else {
					out.setField(id, (String) res);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private boolean doFromPOSTransform(String cfg, IsoMessage in,
			IsoMessage out, Hashtable tmpRunVarSet) {
		String frompos = "FROMPOS";
		String key = cfg.substring(0, cfg.indexOf(":"));
		if (!(key.startsWith(frompos))) {
			return false;
		}
		int id = -1;
		String fdName = "";
		try {
			fdName = key.substring(frompos.length());
			id = Integer.parseInt(fdName);
		} catch (NumberFormatException e1) {
			// e1.printStackTrace();
		}

		int pos = 0;
		String posKeyStr = "";
		try {
			pos = cfg.indexOf(":", key.length());
			posKeyStr = cfg.substring(pos + 1);
		} catch (Exception e2) {
			e2.printStackTrace();
			return false;
		}

		String[] unchangedKeyStrArry = posKeyStr.split(",");
		int[] a = new int[unchangedKeyStrArry.length];
		for (int i = 0; i < a.length; i++) {
			String fdname = unchangedKeyStrArry[i].trim();
			try {
				a[i] = Integer.parseInt(fdname);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (tmpRunVarSet.get(frompos + "_" + fdname) == null) {
				continue;
			}
			try {
				String value = (String) tmpRunVarSet
						.get(frompos + "_" + fdname);
				if (id != -1) {
					out.setField(id, value);
					return true;
				}
				out.setField(a[i], value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}
}
