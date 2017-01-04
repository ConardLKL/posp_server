package com.bestpay.posp.service.checker;

import java.math.BigDecimal;
import java.util.List;

import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.system.entity.TLogTranFlow;
import com.bestpay.posp.system.entity.TLogTranFlowBatch;
import com.bestpay.posp.system.service.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bestpay.posp.protocol.DecipherAppKey;
import com.bestpay.posp.service.impl.RespCodeInformation;
import com.bestpay.posp.system.entity.CipherCard;
import com.bestpay.posp.system.entity.TLogTranBatchErrorFlow;

/**
 * 批上送金融交易
 * @author FENGLI
 *
 */
@Slf4j
@Component
public class M_BatchSendChecker {

	@Autowired
	TLogTranFlowBatchService tLogTranFlowBatchService;
	@Autowired
	TLogTranFlowService tLogTranFlowService;
	@Autowired
	TLogTranBatchErrorFlowService tLogTranBatchErrorFlowService;
	@Autowired
	private TSymAppkeyService tSymAppkeyService;
	@Autowired
	private SysSerialNoService sysSerialNoService;
	
	
	/**
	 * 批上送插入批上送表，比较不同的入差错表
	 * @param requestIso Pos机上送报文
	 * @return 处理结果
	 *  @throws Exception
	 *             by IsoMessage;
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public IsoMessage insertBatchSend(IsoMessage requestIso){
		IsoMessage outIso=requestIso.clone();
		try{
			// 处理48域的磁条卡信息
			String field48=requestIso.getField(48);
			// 头2位 数据条数
			int cnt=Integer.parseInt(requestIso.getField(48).substring(0,2));
			// 数据
			String data = field48.substring(2);
			// 商户号
	        String merchCode=requestIso.getField(42);
	       // 终端号
	        String termCode=requestIso.getField(41);
	       // 批次号
	        String batchNo=requestIso.getField(60).substring(2, 8);
			log.info("批结算，结算商户：" + merchCode);
			log.info("批结算，结算终端：" + termCode);
			log.info("批结算，结算终端批次：" + batchNo);
			log.info("批结算，结算终端笔数：" + cnt);

			TLogTranFlow tLogTranFlow=new TLogTranFlow();
//			tLogTranFlow.setSerialNo(requestIso.getFlow().getSerialNo());//修改
			tLogTranFlow.setMerchCode(merchCode);
			tLogTranFlow.setTermCode(termCode);
			tLogTranFlow.setBatchNo(batchNo);
			TLogTranBatchErrorFlow tLogTranBatchErrorFlow=new TLogTranBatchErrorFlow();
			tLogTranBatchErrorFlow.setMerchCode(merchCode);
			tLogTranBatchErrorFlow.setTermCode(termCode);
			tLogTranBatchErrorFlow.setBatchNo(batchNo);
	        for (int i=0;i<cnt;i++) {
	            log.info("交易第" + i  + " 起始索引：" + (40*i) + " 结束索引：" + (40*i+40));
	            // 每46位为一笔交易
	            String tranInfo = data.substring(40*i,40*i+40);
	            //卡类别 N2    00 内卡交易，01 外卡交易。 
	            String cardType = tranInfo.substring(0,2);
	            // 交易流水号    N6  POS流水号，原交易域11的值。
	            String termSerial = tranInfo.substring(2,8);
	            // 卡号       N20 卡号(右对齐，左补零)。
	            String cardNo = tranInfo.substring(8,28);
	            // 交易金额 N12
	            String tranAmt = tranInfo.substring(28,40);
	            int index = 0;
	            // 卡号16-19位 判断前4位是否为0
	            for (int k=0;k<4;k++){
	                if (cardNo.charAt(k) == '0') {
	                    index++;
	                }                   
	            }
				log.info("批结算，结算终端流水：" + termSerial);
	            //加密卡号
				CipherCard cipherCard = new CipherCard();
				cipherCard.setCardNo(cardNo.substring(index));
				cipherCard.setAppEncrypt(DecipherAppKey.getAPPKey());
				cardNo = tSymAppkeyService.cipherCard(cipherCard);
	            //插入批上送明细表
	            TLogTranFlowBatch tLogTranFlowBatch=new TLogTranFlowBatch();
//				tLogTranFlowBatch.setSerialNo(requestIso.getFlow().getSerialNo());
				String seqNum=tLogTranFlowBatchService.getMaxSeqNum(tLogTranFlowBatch);
				if(seqNum==null||seqNum.equals("")){
					seqNum="01";
				}else{
					seqNum=String.format("%02d", Integer.parseInt(seqNum)+1);
				}
//	            tLogTranFlowBatch.setTranDate(requestIso.getFlow().getTranDate());
//	            tLogTranFlowBatch.setTranTime(requestIso.getFlow().getTranTime());
				tLogTranFlowBatch.setSeqNum(seqNum);
				tLogTranFlowBatch.setBatchNo(batchNo);
				tLogTranFlowBatch.setTranCode("0320201");
				tLogTranFlowBatch.setTermSerialNo(termSerial);
				tLogTranFlowBatch.setCardNo(cardNo);
				tLogTranFlowBatch.setCardFlag(cardType);
				tLogTranFlowBatch.setTranAmount(new BigDecimal(tranAmt).movePointLeft(2).doubleValue());
				tLogTranFlowBatch.setTermCode(termCode);
				tLogTranFlowBatch.setMerchCode(merchCode);
				tLogTranFlowBatch.setBatchResult("5");
//				try {
//					tLogTranFlowBatchService.insertLogTranFlowBatch(tLogTranFlowBatch);
//				}catch(Exception e){
//					outIso.setState(false);
//					RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_C996,outIso);
//					log.error(String.format("[%s] 插入批上送表失败! ", outIso.getSeq()));
//					log.error(e.getMessage());
//					return outIso;
//				}
				tLogTranFlow.setTermSerialNo(termSerial);
				TLogTranFlow orgTranFlow = tLogTranFlowService.getLogTranFlow(tLogTranFlow);
				// 原始交易存在且原始卡号匹配并且金额一致
				// POS中心少的话，不进行处理：POS如果消费不成功，不会记录流水并打印小票
				try {
					if (orgTranFlow != null) {
						if (orgTranFlow.getCardNo().equals(cardNo)
								&& new BigDecimal(tranAmt).divide(new BigDecimal(100))
								.compareTo(new BigDecimal(orgTranFlow.getTranAmount())) ==0) {
							// 更新原始批次状态 1 已批结算 -->2 已批上送
							tLogTranFlow.setBatchState("2");
							tLogTranFlowBatch.setBatchState("2");
							// 更新原始批次结果  修改为3 批上送正常
							tLogTranFlow.setBatchResult("3");
							tLogTranFlowBatch.setBatchResult("3");
							log.info("批结算，正常");
						} else {
							// 更新原始批次状态 1 已批结算 -->2 已批上送
							tLogTranFlow.setBatchState("2");
							tLogTranFlowBatch.setBatchState("2");
							// 更新原始批次结果  修改为4 批上送与POS金额不符
							tLogTranFlow.setBatchResult("4");
							tLogTranFlowBatch.setBatchResult("4");
							log.info("批结算，卡号或者金额不符");
						}
						tLogTranFlow.setSerialNo(orgTranFlow.getSerialNo());
						tLogTranFlowBatch.setSerialNo(orgTranFlow.getSerialNo());
						tLogTranFlowBatch.setTranDate(orgTranFlow.getTranDate());
						tLogTranFlowBatch.setTranTime(orgTranFlow.getTranTime());
						// 更新原始交易流水
						tLogTranFlowService.updateLogTranFlow(tLogTranFlow);
						// 更新批上送流水
						// tLogTranFlowBatchService.updateLogTranFlowBatch(tLogTranFlowBatch);
					}else{
						tLogTranFlowBatch.setSerialNo(sysSerialNoService.querySerialNo());
						tLogTranFlowBatch.setTranDate(requestIso.getFlow().getTranDate());
						tLogTranFlowBatch.setTranTime(requestIso.getFlow().getTranTime());
					}
					tLogTranFlowBatchService.insertLogTranFlowBatch(tLogTranFlowBatch);
				}catch(Exception e){
					outIso.setState(false);
					RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_C996,outIso);
					log.error(String.format("[%s] 插入批上送表失败! ", outIso.getSeq()));
//					log.error(e.getMessage());
					e.printStackTrace();
					return outIso;
				}
	            //插入差错表
	            String seqerrorNum=tLogTranBatchErrorFlowService.getMaxSeqErrorNum(tLogTranBatchErrorFlow);
	            if(seqerrorNum==null||seqerrorNum.equals("")){
	            	seqerrorNum="01";
	            }else{
	            	seqerrorNum=String.format("%02d", Integer.parseInt(seqerrorNum)+1);
	            }
	            tLogTranBatchErrorFlow = new TLogTranBatchErrorFlow();
	            tLogTranBatchErrorFlow.setSerialNo(tLogTranFlowBatch.getSerialNo());
	            tLogTranBatchErrorFlow.setTranDate(tLogTranFlowBatch.getTranDate());
	            tLogTranBatchErrorFlow.setSeqerrorNum(seqerrorNum);
	            tLogTranBatchErrorFlow.setBatchNo(tLogTranFlowBatch.getBatchNo());
	            boolean flag = tLogTranBatchErrorFlowService.isLogTranBatchErrorFlow(tLogTranBatchErrorFlow);
	            tLogTranBatchErrorFlow.setTranTime(tLogTranFlowBatch.getTranTime());
	            tLogTranBatchErrorFlow.setTranCode(tLogTranFlowBatch.getTranCode());
	            tLogTranBatchErrorFlow.setTermSerialNo(tLogTranFlowBatch.getTermSerialNo());
	            tLogTranBatchErrorFlow.setCardNo(tLogTranFlowBatch.getCardNo());
	            tLogTranBatchErrorFlow.setCardFlag(tLogTranFlowBatch.getCardType());
	            tLogTranBatchErrorFlow.setTranAmount(tLogTranFlowBatch.getTranAmount());
	            tLogTranBatchErrorFlow.setTermCode(tLogTranFlowBatch.getTermCode());
	            tLogTranBatchErrorFlow.setMerchCode(tLogTranFlowBatch.getMerchCode());
				if(StringUtils.isNotEmpty(tLogTranFlowBatch.getBatchState())) {
					tLogTranBatchErrorFlow.setBatchState(tLogTranFlowBatch.getBatchState());
				}else{
					tLogTranBatchErrorFlow.setBatchState("0");//0：未批结
				}
	            tLogTranBatchErrorFlow.setBatchResult(tLogTranFlowBatch.getBatchResult());
				try{
					if (flag) {
						tLogTranBatchErrorFlowService.insertLogTranBatchErrorFlow(tLogTranBatchErrorFlow);
					} else {
						tLogTranBatchErrorFlowService.updateLogTranBatchErrorFlow(tLogTranBatchErrorFlow);
					}
				}catch(Exception e){
					outIso.setState(false);
					RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_C996,outIso);
					log.error(String.format("[%s] 插入批上送差错表失败! ", outIso.getSeq()));
					log.error(e.getMessage());
					return outIso;
				}
	        }
	        //反向遍历posp多
	        List<TLogTranFlow> orgTranLsNotBatchall=tLogTranFlowService.getAllBatchNoSend(tLogTranFlow);
	        for(TLogTranFlow orgTranLsNotBatch: orgTranLsNotBatchall){
	            if(orgTranLsNotBatch!=null){
	                orgTranLsNotBatch.setBatchState("2");
	                orgTranLsNotBatch.setBatchResult("6");
	                tLogTranFlowService.updateBantchLogTranFlow(orgTranLsNotBatch);
	                String seqerrorNum=tLogTranBatchErrorFlowService.getMaxSeqErrorNum(tLogTranBatchErrorFlow);
	                if(seqerrorNum==null||seqerrorNum.equals("")){
	                	seqerrorNum="01";
	                }else{
	                	seqerrorNum=String.format("%02d", Integer.parseInt(seqerrorNum)+1);
	                }
					tLogTranBatchErrorFlow = new TLogTranBatchErrorFlow();
	                tLogTranBatchErrorFlow.setSerialNo(orgTranLsNotBatch.getSerialNo());                                
	                tLogTranBatchErrorFlow.setTranDate(orgTranLsNotBatch.getTranDate());                                
	                tLogTranBatchErrorFlow.setBatchNo(orgTranLsNotBatch.getBatchNo());                                  
	                tLogTranBatchErrorFlow.setSeqerrorNum(seqerrorNum);       
	                boolean flag = tLogTranBatchErrorFlowService.isLogTranBatchErrorFlow(tLogTranBatchErrorFlow);
	                tLogTranBatchErrorFlow.setTranTime(orgTranLsNotBatch.getTranTime());                                
	                tLogTranBatchErrorFlow.setTranCode(orgTranLsNotBatch.getTranCode());                                
	                tLogTranBatchErrorFlow.setTranType(orgTranLsNotBatch.getTranType());                                
	                tLogTranBatchErrorFlow.setSettleFlag(orgTranLsNotBatch.getSettleFlag());                            
	                tLogTranBatchErrorFlow.setSettleDate(orgTranLsNotBatch.getSettleDate());                                                
	                tLogTranBatchErrorFlow.setOrigSerialNo(orgTranLsNotBatch.getOrigSerialNo());                        
	                tLogTranBatchErrorFlow.setOrigSerialDate(orgTranLsNotBatch.getOrigSerialDate());
	                tLogTranBatchErrorFlow.setOrigSerialTime(orgTranLsNotBatch.getOrigSerialTime());                       
	                tLogTranBatchErrorFlow.setOrigTranType(orgTranLsNotBatch.getOrigTranType());                       
	                tLogTranBatchErrorFlow.setTermSerialNo(orgTranLsNotBatch.getTermSerialNo());                        
	                tLogTranBatchErrorFlow.setTermDate(orgTranLsNotBatch.getTermDate());                                
	                tLogTranBatchErrorFlow.setTermTime(orgTranLsNotBatch.getTermTime());                                
	                tLogTranBatchErrorFlow.setCupsSerialNo(orgTranLsNotBatch.getCupsSerialNo());                        
	                tLogTranBatchErrorFlow.setCupsDate(orgTranLsNotBatch.getCupsDate());                               
	                tLogTranBatchErrorFlow.setCupsSerialNo(orgTranLsNotBatch.getCupsSerialNo());                        
	                tLogTranBatchErrorFlow.setCupsDate(orgTranLsNotBatch.getCupsDate());                                
	                tLogTranBatchErrorFlow.setCardNo(orgTranLsNotBatch.getCardNo());                                    
	                tLogTranBatchErrorFlow.setAddCardNo(orgTranLsNotBatch.getAddCardNo());                              
	                tLogTranBatchErrorFlow.setCardType(orgTranLsNotBatch.getCardType());                                
	                tLogTranBatchErrorFlow.setCardBin(orgTranLsNotBatch.getCardBin());                                  
	                tLogTranBatchErrorFlow.setCardFlag(orgTranLsNotBatch.getCardFlag());                                
	                tLogTranBatchErrorFlow.setTranAmount(orgTranLsNotBatch.getTranAmount());                                
	                tLogTranBatchErrorFlow.setTranFlag(orgTranLsNotBatch.getTranFlag());                                
	                tLogTranBatchErrorFlow.setReferNo(orgTranLsNotBatch.getReferNo());                                  
	                tLogTranBatchErrorFlow.setAuthNo(orgTranLsNotBatch.getAuthNo());                                    
	                tLogTranBatchErrorFlow.setAuthDate(orgTranLsNotBatch.getAuthDate());                                
	                tLogTranBatchErrorFlow.setAuthAmount(orgTranLsNotBatch.getAuthAmount());                            
	                tLogTranBatchErrorFlow.setRcvBankCode(orgTranLsNotBatch.getRcvBankCode());
	                tLogTranBatchErrorFlow.setRcvBranchCode(orgTranLsNotBatch.getRcvBranchCode());
//	                tLogTranBatchErrorFlow.setIssueAppdata(orgTranLsNotBatch.getIssueAppdata());
	                tLogTranBatchErrorFlow.setInputMode(orgTranLsNotBatch.getInputMode());                          
	                tLogTranBatchErrorFlow.setMcc(orgTranLsNotBatch.getMcc());                                          
	                tLogTranBatchErrorFlow.setMerchCode(orgTranLsNotBatch.getMerchCode());                                  
	                tLogTranBatchErrorFlow.setTermCode(orgTranLsNotBatch.getTermCode());                                    
	                tLogTranBatchErrorFlow.setTermFlag(orgTranLsNotBatch.getTermFlag());                                
	                tLogTranBatchErrorFlow.setOperNo(orgTranLsNotBatch.getOperNo());                                    
	                tLogTranBatchErrorFlow.setChannelType(orgTranLsNotBatch.getChannelType());                          
	                tLogTranBatchErrorFlow.setInputMode(orgTranLsNotBatch.getInputMode());                              
	                tLogTranBatchErrorFlow.setConditionMode(orgTranLsNotBatch.getConditionMode());                      
//	                tLogTranBatchErrorFlow.setIc55(orgTranLsNotBatch.getIc55());
//	                tLogTranBatchErrorFlow.setAppCrypt(orgTranLsNotBatch.getAppCrypt());
//	                tLogTranBatchErrorFlow.setTranType55(orgTranLsNotBatch.getTranType55());
//	                tLogTranBatchErrorFlow.setTermValidResults(orgTranLsNotBatch.getTermValidResults());
//	                tLogTranBatchErrorFlow.setUnpredictableNum(orgTranLsNotBatch.getUnpredictableNum());
//	                tLogTranBatchErrorFlow.setTranDate55(orgTranLsNotBatch.getTranDate55());
//	                tLogTranBatchErrorFlow.setTranAmt55(orgTranLsNotBatch.getTranAmt55());
//	                tLogTranBatchErrorFlow.setCcyCode55(orgTranLsNotBatch.getCcyCode55());
//	                tLogTranBatchErrorFlow.setIssueAppdata(orgTranLsNotBatch.getIssueAppdata());
//	                tLogTranBatchErrorFlow.setAtc(orgTranLsNotBatch.getAtc());
//	                tLogTranBatchErrorFlow.setAip(orgTranLsNotBatch.getAip());
	                tLogTranBatchErrorFlow.setCcyCode(orgTranLsNotBatch.getCcyCode());                                  
//	                tLogTranBatchErrorFlow.setCid(orgTranLsNotBatch.getCid());
	                tLogTranBatchErrorFlow.setOtherAmount(orgTranLsNotBatch.getOtherAmount());                                                                                                          
//	                tLogTranBatchErrorFlow.setEcBalance55(orgTranLsNotBatch.getEcBalance55());
	                tLogTranBatchErrorFlow.setRespCode(orgTranLsNotBatch.getRespCode());                                
	                tLogTranBatchErrorFlow.setTranState(orgTranLsNotBatch.getTranState());                              
	                tLogTranBatchErrorFlow.setBatchState(orgTranLsNotBatch.getBatchState());                            
	                tLogTranBatchErrorFlow.setBatchResult(orgTranLsNotBatch.getBatchResult());                          
	                tLogTranBatchErrorFlow.setTermSettleDate(orgTranLsNotBatch.getTermSettleDate());                                                          
	                tLogTranBatchErrorFlow.setReserve(orgTranLsNotBatch.getReserve());                                                    
	                if(flag){
	                	tLogTranBatchErrorFlowService.insertLogTranBatchErrorFlow(tLogTranBatchErrorFlow);
	                } else{
	                	tLogTranBatchErrorFlowService.updateLogTranBatchErrorFlow(tLogTranBatchErrorFlow);
	                }
	            }
	        }
			return outIso;
		}catch(Exception e){
			outIso.setState(false);
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_C996,outIso);
			log.error(String.format("[%s] error： 批上送失败! ", outIso.getSeq()));
			log.error(e.getMessage());
			return outIso;
		}
	}
}
