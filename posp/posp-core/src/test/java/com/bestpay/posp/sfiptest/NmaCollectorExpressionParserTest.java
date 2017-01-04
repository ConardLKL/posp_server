package com.bestpay.posp.sfiptest;

import java.util.Hashtable;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.util.PospIsoMap2MapTransformerImpl;
import lombok.extern.slf4j.Slf4j;
import com.bestpay.posp.protocol.FlowMessage;
import com.bestpay.posp.protocol.PKT_DEF;

import com.regaltec.nma.collector.adapter.common.NmaCollectorExpressionInterpretor;
import com.tisson.sfip.api.message.transformer.ObjectTransformerUtil;
import com.tisson.sfip.api.service.ForwardingConsumerProxy;
@Slf4j
public class NmaCollectorExpressionParserTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IsoMessage requestPosIso = new IsoMessage();
		String expression = "";

		try {
			PKT_DEF[] pktDef = new LoadPktDefFromExcel().load();
			requestPosIso.setMessageDefine(pktDef);
			requestPosIso.getTransformerTmpField().put("FROMPOS_133", "aaa");
			requestPosIso.getTransformerTmpField().put("FROMPOS_233", "bbb");
			requestPosIso.setField(0, "0200");
			requestPosIso.setField(1, "700406C028C0DA11");
			requestPosIso.setField(2, "6225000000000253");
			requestPosIso.setField(3, "000000");
			requestPosIso.setField(4, "000000000123");
			requestPosIso.setField(14, "3010");
			requestPosIso.setField(22, "051");
			requestPosIso.setField(23, "002");
			requestPosIso.setField(25, "00");
			requestPosIso.setField(26, "12");
			requestPosIso.setField(35, "6225000000000253=301022000000");
			requestPosIso.setField(37, "000000000094");
			requestPosIso.setField(41, "11200001");
			requestPosIso.setField(42, "837448010000000");
			requestPosIso.setField(49, "156");
			requestPosIso.setField(50, "0000000900000443");
			// requestPosIso.setField( 52,"DE8B19A378F53639");
			requestPosIso.setField(53, "2600000000000000");
			requestPosIso
					.setField(
							55,
							"9F2608F1899CEABC7BFF1B9F2701809F101307000103A0A012010A0100000888003D92DE959F37041E5C72309F3602004A9505000004E8009A031408019C01009F02060000000001235F2A02015682027C009F1A0201569F03060000000000009F3303E0E1C89F34034203009F3501229F1E084E65776C616E64318408A0000003330101019F090200209F410400000047");
			requestPosIso.setField(60, "2200000100050");
			requestPosIso.setField(64, "D9B887D000000000");
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		FlowMessage flowMessage = new FlowMessage();
		flowMessage.setMMDDhhmmss("MMDDhhmmss");
		requestPosIso.setFlow(flowMessage);

		String historyConsumerLocus = "";
		String currentConsumer = "200609145";
		String keyPrefix = historyConsumerLocus + "_" + currentConsumer;
		Hashtable requestPayload = ObjectTransformerUtil.object2Hashtable(requestPosIso,
				keyPrefix);
		Hashtable runVarSet = new Hashtable();
		if (requestPayload != null) {
			runVarSet.putAll(requestPayload);
		}
		runVarSet.put("OLD14", "3010");

		// expression = "subString(a0210,1,5)";
		// expression = "a0210";
		// expression = "subString(0210,0,4)";
		// expression = "0210";
		expression = "logicNot(equals(" + keyPrefix + "_state,true))";
		expression = "equals(OLD14,3010)";
		expression = "subString((a+subString(0000,0,4)+subString(0289,0,4)),1,9)";
		expression = "subString((a+subString(0000,0,4))+subString(0289,0,4),1,9)";
//		FDIF23:logicNot(equals(subString(FROMPOS_23,0,8),FROMPOS_)):FROMPOS_23
		runVarSet.put("_PASS_0100030610_PASS_CompleteCheck_PASS_ValidCheck_PASS_MerchRiskControl_PASS_UpdatePredict_IN_PASS_UtService_PASS_UpdatNullSign_rspCode","96");
		expression ="logicNot(equals(subString(_PASS_0100030610_PASS_CompleteCheck_PASS_ValidCheck_PASS_MerchRiskControl_PASS_UpdatePredict_IN_PASS_UtService_PASS_UpdatNullSign_rspCode,0,len(_PASS_0100030610_PASS_CompleteCheck_PASS_ValidCheck_PASS_MerchRiskControl_PASS_UpdatePredict_IN_PASS_UtService_PASS_UpdatNullSign_)),_PASS_0100030610_PASS_CompleteCheck_PASS_ValidCheck_PASS_MerchRiskControl_PASS_UpdatePredict_IN_PASS_UtService_PASS_UpdatNullSign_))";
		expression = "_PASS_0100030610_PASS_CompleteCheck_PASS_ValidCheck_PASS_MerchRiskControl_PASS_UpdatePredict_IN_PASS_UtService_PASS_UpdatNullSign_rspCode";

		NmaCollectorExpressionInterpretor myinter = new NmaCollectorExpressionInterpretor(
				runVarSet, expression);

		Object result = null;
		try {
			result = myinter.operation();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("[" + expression + "]" + " debug info:"
				+ myinter.getDebugBuf().toString());

		log.debug("NmaCollectorExpressionInterpretor:result=["
				+ result + "]");
		PospIsoMap2MapTransformerImpl m2m = new PospIsoMap2MapTransformerImpl();
		String transformParam = "";
		// transformParam = "unipayDefineInitializer(0200),";
		transformParam = transformParam + "0,1,2FD7:flow_MMDDhhmmss";
		transformParam = transformParam + "FDIF49:equals(OLD14,200):111";
		transformParam = transformParam + "FDIF49:equals(OLD14,3010):222";
		transformParam = transformParam + "FDIFpub_ValidCheck:equals(OLD14,3010):111";
//		FDIF23:logicNot(equals(subString(FROMPOS_23,0,8),FROMPOS_)):FROMPOS_23
		transformParam = "bean:pospIsoMap2MapTransformerImpl:0,2,3,4FDFROMPOS:133,125,132,133FDFROMPOS997:125FDFROMPOS998:133";
		
//		FDPOST3,4,7
		
		IsoMessage responsePosIso = (IsoMessage) m2m.doTransform(
				transformParam, requestPosIso, runVarSet, keyPrefix + "_");

		responsePosIso.printMessage();
		
		log.debug("transformerTmpField:" + responsePosIso.getTransformerTmpField());
		
		
		String [][] cfg = new ForwardingConsumerProxy().loadForwardingConfig("02000006210");
		log.debug("cfg.length="+cfg.length);
		
	}

}
