package com.bestpay.posp.service.checker;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

import com.bestpay.posp.constant.FieldConstant;
import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.service.impl.Pub_UpdateTransactionRecord;
import com.bestpay.posp.system.entity.TLogOfflineTranFlow;
import com.bestpay.posp.system.entity.TLogTranFlowBatch;
import com.bestpay.posp.system.service.TLogTranFlowBatchService;
import com.bestpay.posp.system.service.TSymAppkeyService;
import com.bestpay.posp.utils.recombin.TLVDecoder;
import com.bestpay.posp.utils.recombin.TLVList;
import com.bestpay.posp.system.service.TLogOfflineTranFlowService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bestpay.posp.protocol.DecipherAppKey;
import com.bestpay.posp.service.impl.RespCodeInformation;
import com.bestpay.posp.system.entity.CipherCard;
import com.bestpay.posp.utils.recombin.HexBinary;


/**
 * 批上送脱机消费交易
 * @author FENGLI
 *
 */
@Slf4j
@Component
public class M_BatchSendOfflineDetailChecker {
	@Autowired 
	private TLogOfflineTranFlowService tLogOfflineTranFlowService;
	@Autowired 
	private TLogTranFlowBatchService tLogTranFlowBatchService;
	@Autowired 
	private Pub_UpdateTransactionRecord pub_UpdateTransactionRecord;
	@Autowired
	private TSymAppkeyService tSymAppkeyService;

	/**
	 * 批上送脱机入库
	 * @param requestIso Pos机上送报文
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public IsoMessage insertBatchSendOfflineDetail (IsoMessage requestIso){
		try{
			 //通知流水记录
			String tranType=requestIso.getField(60).substring(0,2);
			String merchCode=requestIso.getField(42);
	        String termCode=requestIso.getField(41);
	        String field60 = requestIso.getField(60);
	        String batchNo=field60.substring(2,8);
	        String termSerial=requestIso.getField(11);
	        //加密卡号
			CipherCard cipherCard = new CipherCard();
			cipherCard.setCardNo(requestIso.getField(FieldConstant.FIELD64_2));
			cipherCard.setAppEncrypt(DecipherAppKey.getAPPKey());
			String cardNo = tSymAppkeyService.cipherCard(cipherCard);
	        double tranAmt = new BigDecimal(requestIso.getField(4)).movePointLeft(2).doubleValue();
			if(tranType.equals("25") || tranType.equals("27") || tranType.equals("30") || tranType.equals("32") || tranType.equals("34") || tranType.equals("24")){
				TLogTranFlowBatch tLogTranFlowBatch=new TLogTranFlowBatch();
				
		         tLogTranFlowBatch.setMerchCode(merchCode);
		         tLogTranFlowBatch.setTermCode(termCode);
		         tLogTranFlowBatch.setBatchNo(batchNo);
		         
		         String seqNum=tLogTranFlowBatchService.getMaxSeqNum(tLogTranFlowBatch);
		         if(seqNum==null||seqNum.equals("")){
		             seqNum="01";
		         }else{
		             seqNum=String.format("%02d", Integer.parseInt(seqNum)+1);
		         }
		         tLogTranFlowBatch.setSerialNo(requestIso.getFlow().getSerialNo());
		         tLogTranFlowBatch.setTranDate(requestIso.getFlow().getTranDate());
		         tLogTranFlowBatch.setTranTime(requestIso.getFlow().getTranTime());
		         tLogTranFlowBatch.setSeqNum(seqNum);
		         tLogTranFlowBatch.setBatchNo(batchNo);
		         tLogTranFlowBatch.setTranCode("0320000");
		         tLogTranFlowBatch.setTermSerialNo(termSerial);
		         tLogTranFlowBatch.setCardNo(cardNo);
	//			 tLogTranFlowBatch.setCardFlag(cardType);
		         tLogTranFlowBatch.setTranAmount(tranAmt);
		         tLogTranFlowBatch.setTermCode(termCode);
		         tLogTranFlowBatch.setMerchCode(merchCode);
		         tLogTranFlowBatch.setBatchResult("0");
		         tLogTranFlowBatch.setReserve("通知类交易上送");
		         boolean state =tLogTranFlowBatchService.insertLogTranFlowBatch(tLogTranFlowBatch);
		         if(state){
		        	 requestIso.setPospCode(POSPConstant.POSP_00);
		        	 requestIso.setRspCode("00");
		         }
			}else{
				//脱机消费交易流水	
		        TLogOfflineTranFlow logOfflineTranFlow=new TLogOfflineTranFlow();
		        if(requestIso.getFlow().getTranDate()!=null){
		        	logOfflineTranFlow.setTranDate(requestIso.getFlow().getTranDate());
		        }
		        if(termSerial!=null){
		        	logOfflineTranFlow.setTermSerialNo(termSerial);//终端流水号
		        }
		        logOfflineTranFlow.setTermCode(termCode);
		        if(requestIso.getField(60)!=null){
		        	logOfflineTranFlow.setBatchNo(requestIso.getField(60).substring(2,8));
		        }
		        
		        TLogOfflineTranFlow getsd=tLogOfflineTranFlowService.getLogOfflineTranFlow(logOfflineTranFlow);
		        
		        logOfflineTranFlow.setCardNo(cardNo);//主账号
		        logOfflineTranFlow.setTranAmount(tranAmt);//交易金额
		        logOfflineTranFlow.setMcc(requestIso.getFlow().getMcc());//商户类型码
		        logOfflineTranFlow.setInputMode(requestIso.getField(22));//服务点输入方式码
		        if(requestIso.getField(23)!=null){
		        	logOfflineTranFlow.setCardSeqno(requestIso.getField(23));//卡片序列号
		        }
		        logOfflineTranFlow.setRcvBankCode(requestIso.getFlow().getRcvBankCode());//收单行代码
		        logOfflineTranFlow.setRcvBranchCode(requestIso.getFlow().getRcvBranchCode());//收单机构代码 
		        
		        if(requestIso.getFlow().getSerialNo()!=null){
		        	logOfflineTranFlow.setSerialNo(requestIso.getFlow().getSerialNo());//系统参考号
		        }
		        logOfflineTranFlow.setMerchCode(merchCode);
		        logOfflineTranFlow.setCcyCode(requestIso.getField(49));//货币代码
		        
//		        logOfflineTranFlow = seperater(requestIso.getField(55),logOfflineTranFlow);//拆分55域并放到bean中
		        
		        if(requestIso.getFlow().getHhmmss()!=null){
		        	logOfflineTranFlow.setTermTime(requestIso.getFlow().getHhmmss());
		        }
		        	logOfflineTranFlow.setTranType("36");
		        
		        //卡BIN获取
		        /*if(requestIso.getFlow().get!=null){
		            logOfflineTranFlow.setCardFlag(requestIso.getFlow().getCardFlag());
		        }*/
//		        logOfflineTranFlow.setIc55(requestIso.getField(55));
		        if (field60.length()>11) {
		        	logOfflineTranFlow.setTermReadab(field60.substring(11,12));//终端读取能力
		        }
		        if(field60.length()>12){
		        	logOfflineTranFlow.setIcCondition(field60.substring(12,13));//IC卡条件代码
		        }
		        
		        logOfflineTranFlow.setBatchState("0");
		        logOfflineTranFlow.setBatchResult("0");
		        logOfflineTranFlow.setSettleFlag("0");
		        logOfflineTranFlow.setState("0000");
		        logOfflineTranFlow.setResult("0000");
		        logOfflineTranFlow.setReserve("脱机消费明细");
		        if(getsd!=null){
		        	requestIso.setPospCode(POSPConstant.POSP_00);
		        	requestIso.setRspCode("00");
		        }else{
		            tLogOfflineTranFlowService.insertLogOfflineTranFlow(logOfflineTranFlow);
	            	requestIso.setPospCode(POSPConstant.POSP_00);
	            	requestIso.setRspCode("00");
		        }
			}
			return 	requestIso;
		}catch(Exception e){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_C796,requestIso);
			log.warn(String.format("[%s] error：批上送脱机消费交易异常! ", requestIso.getSeq()));
			log.error(e.getMessage());
			return 	requestIso;
		}
	}
	
	/**
     * PBOC 拆分55域
     */
//    public TLogOfflineTranFlow seperater(String field55,TLogOfflineTranFlow logOfflineTranFlow) {
//
//        TLVDecoder decoder = new TLVDecoder();
//        ByteBuffer byteBuff = ByteBuffer.wrap(HexBinary.decode(field55));
//        TLVList tlvList = decoder.decode(byteBuff);
//        // POS 55域包含下面字段
//        if (tlvList.get("9F26")!=null) {
//        	logOfflineTranFlow.setAppCrypt(HexBinary.encode(tlvList.get("9F26").getValue()));
//        }
//       /* if (tlvList.get("9F27")!=null) {
//        	logOfflineTranFlow.setTag9F27(HexBinary.encode(tlvList.get("9F27").getValue()));
//        }
//        if (tlvList.get("9F10")!=null) {
//        	logOfflineTranFlow.setTag9F10(HexBinary.encode(tlvList.get("9F10").getValue()));
//        }*/
//        if (tlvList.get("9F37")!=null) {
//        	logOfflineTranFlow.setUnpredictableNum(HexBinary.encode(tlvList.get("9F37").getValue()));
//        }
//        if (tlvList.get("9F36")!=null) {
//        	logOfflineTranFlow.setAtc(HexBinary.encode(tlvList.get("9F36").getValue()));
//        }
//        if (tlvList.get("95")!=null) {
//        	logOfflineTranFlow.setTermValidResults(HexBinary.encode(tlvList.get("95").getValue()));
//        }
//        if (tlvList.get("9A")!=null) {
//        	logOfflineTranFlow.setTermDate(HexBinary.encode(tlvList.get("9A").getValue()));
//        	logOfflineTranFlow.setTranDate55(HexBinary.encode(tlvList.get("9A").getValue()));
//        }
//        if (tlvList.get("9C")!=null) {
//        	logOfflineTranFlow.setTranType55(HexBinary.encode(tlvList.get("9C").getValue()));
//        }
//        if (tlvList.get("9F02")!=null) {
//        	logOfflineTranFlow.setAuthAmount(new java.math.BigDecimal(HexBinary.encode(tlvList.get("9F02").getValue())).movePointLeft(2).doubleValue());
//        }
//        if (tlvList.get("5F2A")!=null) {
//        	logOfflineTranFlow.setCcyCode55(HexBinary.encode(tlvList.get("5F2A").getValue()));
//        }
//        if (tlvList.get("82")!=null) {
//        	logOfflineTranFlow.setAip(HexBinary.encode(tlvList.get("82").getValue()));
//        }
//        if (tlvList.get("9F1A")!=null) {
//        	logOfflineTranFlow.setTermCountryCode(HexBinary.encode(tlvList.get("9F1A").getValue()));
//        }
//        if (tlvList.get("9F03")!=null) {
//        	logOfflineTranFlow.setOtherAmt(new java.math.BigDecimal(HexBinary.encode(tlvList.get("9F03").getValue())).movePointLeft(2).doubleValue());
//        }
//        if (tlvList.get("9F33")!=null) {
//        	logOfflineTranFlow.setTermCap(HexBinary.encode(tlvList.get("9F33").getValue()));
//        }
//       /* if (tlvList.get("9F74")!=null) {
//            // 20121116 POS终端规范 9F74为A
//            // 不做BCD转换
//        	logOfflineTranFlow.setTag9F74(new String(tlvList.get("9F74").getValue()));
//        }
//        if (tlvList.get("9F34")!=null) {
//        	logOfflineTranFlow.setTag9F34(HexBinary.encode(tlvList.get("9F34").getValue()));
//        }
//        if (tlvList.get("9F35")!=null) {
//        	logOfflineTranFlow.setTag9F35(HexBinary.encode(tlvList.get("9F35").getValue()));
//        }*/
//        /*if (tlvList.get("9F1E")!=null) {
//            // 20121116 POS终端规范 9F1E为AN
//            // 不做BCD转换
//            //response.setTag9F1E(HexBinary.encode(tlvList.get("9F1E").getValue()));
//        	logOfflineTranFlow.setTag9F1E(new String(tlvList.get("9F1E").getValue()));
//        }
//        if (tlvList.get("84")!=null) {
//        	logOfflineTranFlow.setTag84(HexBinary.encode(tlvList.get("84").getValue()));
//        }*/
//        if (tlvList.get("9F09")!=null) {
//        	logOfflineTranFlow.setAppVersion(HexBinary.encode(tlvList.get("9F09").getValue()));
//        }
//        /*if (tlvList.get("9F41")!=null) {
//        	logOfflineTranFlow.setTag9F41(HexBinary.encode(tlvList.get("9F41").getValue()));
//        }*/
//        if (tlvList.get("9F10")!=null) {
//        	logOfflineTranFlow.setIssueAppdata(HexBinary.encode(tlvList.get("9F10").getValue()));
//        }
///*        if (tlvList.get("71")!=null) {
//        	logOfflineTranFlow.setTag71(HexBinary.encode(tlvList.get("71").getValue()));
//        }
//        if (tlvList.get("72")!=null) {
//        	logOfflineTranFlow.setTag72(HexBinary.encode(tlvList.get("72").getValue()));
//        }
//        if (tlvList.get("DF31")!=null) {
//        	logOfflineTranFlow.setTagDF31(HexBinary.encode(tlvList.get("DF31").getValue()));
//        }
//        if (tlvList.get("9F63")!=null) {
//            // 20121116 POS终端规范 9F63为AN
//            // 不做BCD转换
//            //response.setTag9F63(new String(tlvList.get("9F63").getValue()));
//        	logOfflineTranFlow.setTag9F63(HexBinary.encode(tlvList.get("9F63").getValue()));
//        }
//        if (tlvList.get("8A")!=null) {
//            // 20121116 POS终端规范 8A为AN
//            // 不做BCD转换
//        	logOfflineTranFlow.setTag8A(new String(tlvList.get("8A").getValue()));
//        }*/
//        return logOfflineTranFlow;
//    }
    @Transactional(propagation=Propagation.REQUIRED)
    public IsoMessage updateFlow(IsoMessage iso){
    	TLogOfflineTranFlow logOfflineTranFlow=new TLogOfflineTranFlow();
    	logOfflineTranFlow.setSerialNo(iso.getFlow().getSerialNo());
    	if("00".equals(iso.getField(39))){
    		logOfflineTranFlow.setState("0001");
    		tLogOfflineTranFlowService.updateTLogOfflineTranFlow(logOfflineTranFlow);
    	}
    	return pub_UpdateTransactionRecord.updateFlowSign(iso);
    }
}
