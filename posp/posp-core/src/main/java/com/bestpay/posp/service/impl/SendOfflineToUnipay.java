package com.bestpay.posp.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.system.dao.TMcmMerchantDao;
import com.bestpay.posp.system.entity.TLogOfflineTranFlow;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bestpay.posp.protocol.FlowMessage;
import com.bestpay.posp.protocol.UnipayDefineInitializer;
import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.system.dao.TLogOfflineTranFlowDao;
import com.bestpay.posp.system.dao.TLogTranFlowDao;
import com.bestpay.posp.system.entity.TMcmMerchant;
import com.bestpay.posp.utils.recombin.HexBinary;
import com.bestpay.posp.utils.recombin.TLV;
import com.bestpay.posp.utils.recombin.TLVEncoder;
import com.bestpay.posp.utils.recombin.TLVList;

/**
 * 从数据库读取未记账的记录发往银联
 * @author YZH
 *
 */
@Slf4j
@Service
public class SendOfflineToUnipay {
	/**
	 * 任务列表
	 */
	List<TLogOfflineTranFlow> tLogOfflineTranFlows;
	@Autowired 
	TLogOfflineTranFlowDao tLogOfflineTranFlowDao;
	@Autowired 
	TLogTranFlowDao tLogTranFlowDao;
	@Autowired
	TMcmMerchantDao tMcmMerchantDao;
	@Autowired
	@Qualifier("UnipayDefineInitializer")
	private UnipayDefineInitializer unipayDefineInitializer;
	/**
	 * 执行次数
	 */
	Map<String,String> entityCount = new HashMap<String,String>();
	public void sendOffline(){
		TLogOfflineTranFlow logOfflineTranFlow=new TLogOfflineTranFlow();
		logOfflineTranFlow.setState("0000");
		tLogOfflineTranFlows = tLogOfflineTranFlowDao.find(logOfflineTranFlow);
		log.info(String.format("离线交易上送，当前未上送离线交易数量是[%s]",getCount()+""));
		if(tLogOfflineTranFlows!=null&&tLogOfflineTranFlows.size()>0){
			handle();
		}
	}
	/**
	 * 获取带发送列表个数
	 * @return
	 */
	private int getCount(){
		if(tLogOfflineTranFlows==null){
			return 0;
		}else{
			return tLogOfflineTranFlows.size();
		}
	}
	private void removeTask(int i){
		tLogOfflineTranFlows.remove(i);
	}
	/**
	 * 获取任务
	 * @param i
	 * @return
	 */
	private TLogOfflineTranFlow getTask(int i){
		return tLogOfflineTranFlows.get(i);
	}
	/**
	 * 
	 * @param i
	 * @return
	 */
	private int getCount(int i){
		TLogOfflineTranFlow of = getTask(i);
		String c = entityCount.get(of.getSerialNo());
		int count;
		if(c!=null)
			count = Integer.parseInt(c)+1;
		else count =0;
		return count;
		
	}
	/**
	 * 对发送列表处理
	 */
	private void handle(){
		if(getCount()>0){
			TLogOfflineTranFlow of = null;
			int count = 0;
			for(int i = 0;;i++){
				if(i >= getCount()){
					break;
				}
				try {
					of = getTask(i);
					IsoMessage iso = getUnipayIso(of);
					count = getCount(i);
					IsoMessage back = send(iso);
					if(back!=null&&"00".equals(back.getField(39))){
						sendSuccess(of.getSerialNo(),i);
						i--;
			    	}else if(count>=3){
			    		if(back!=null){
			    			of.setRespCode(back.getField(39));
			    		}
			    		sendFail(of,i);
						i--;
			    	}else{
			    		entityCount.put(of.getSerialNo(), count+"");
			    	}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
     * 发送成功 - 更新数据库，移除任务
     * @param i
     */
	@Transactional(propagation=Propagation.REQUIRED)
    private void sendSuccess(String serialNo,int i){
    	TLogOfflineTranFlow of = new TLogOfflineTranFlow();
    	of.setSerialNo(serialNo);
    	of.setState("0001");//上送银联成功
    	of.setRespCode("00");
//    	TLogOfflineTranFlowDao tLogOfflineTranFlowDao = (TLogOfflineTranFlowDao)
//				PospApplicationContext.getBean("tLogOfflineTranFlowDao");
    	tLogOfflineTranFlowDao.update(of);
    	removeTask(i);
    }
	/**
	 * 发送失败 - 更新数据库，移除任务
	 * @param s
	 * @param i
     */
	@Transactional(propagation=Propagation.REQUIRED)
    private void sendFail(TLogOfflineTranFlow s,int i){
    	TLogOfflineTranFlow of = new TLogOfflineTranFlow();
    	of.setSerialNo(s.getSerialNo());
    	of.setState("0002");//上送银联成功
    	if(s.getRespCode()!=null){
    		of.setRespCode(s.getRespCode());
    	}
//    	TLogOfflineTranFlowDao tLogOfflineTranFlowDao = (TLogOfflineTranFlowDao)
//				PospApplicationContext.getBean("tLogOfflineTranFlowDao");
    	tLogOfflineTranFlowDao.update(of);
    	removeTask(i);
    	log.info(String.format("离线交易上送失败[%s].",s.getSerialNo()));
    }
	/**
     * 发往银联
     * @param iso
     * @return
     */
    private IsoMessage send(IsoMessage iso){
    	Pub_UtService pub_UtService = (Pub_UtService)
    			PospApplicationContext.getBean("pub_UtService");
    	return pub_UtService.call(iso);
    }
	/**
	 * 获取消费报文 - 183
	 * @param offlineFlow
	 * @return
	 * @throws Exception
	 */
	private IsoMessage getUnipayIso(TLogOfflineTranFlow offlineFlow) throws Exception{
		IsoMessage iso = new IsoMessage();
		iso.setMessageDefine(unipayDefineInitializer.findPacketDefine("0220"));
		
		iso.setField(0, "0220");//消费
		iso.setField(2, offlineFlow.getCardNo());//主账号
		iso.setField(3, "000000");
		iso.setField(4, get12BCD(offlineFlow.getTranAmount())+ "");
		iso.setField(7, offlineFlow.getTranDate().substring(4)+offlineFlow.getTermTime());
		iso.setField(11, offlineFlow.getTermSerialNo());
		iso.setField(14, "0000");
		iso.setField(18, offlineFlow.getMcc());
		iso.setField(22, offlineFlow.getInputMode());
		iso.setField(23, offlineFlow.getCardSeqno());//C51
		iso.setField(25, "00");
		iso.setField(32, offlineFlow.getRcvBankCode());
		iso.setField(33, offlineFlow.getRcvBranchCode());
		
//		iso.setField(35, "");//C1
//		iso.setField(36, "");//C2
		iso.setField(37, offlineFlow.getSerialNo().substring(6, 18));
		iso.setField(41, offlineFlow.getTermCode());
		iso.setField(42, offlineFlow.getMerchCode());
		iso.setField(43, getMctName(offlineFlow.getMerchCode()));
//		iso.setField(48, "");//C22
		iso.setField(49, offlineFlow.getCcyCode());
//		iso.setField(55, offlineFlow.getIc55());
		iso.setField(60, createField60(offlineFlow));//6.47.6.4
		iso.setField(122, "0");
//		iso.setField(128, "");//C9
		FlowMessage flow = new FlowMessage();
		flow.setTranCode("0220000000");
		iso.setFlow(flow);
		iso.setSeq(offlineFlow.getSerialNo());
		return iso;	
	}
	/**
	 * 获取机构名
	 * @param merchCode
	 * @return
	 */
	private String getMctName(String merchCode){
		TMcmMerchant merchant = tMcmMerchantDao.findByMctCode(merchCode);
		if(merchant!=null){
			return merchant.getMctName();
		}
		else{
			return null;
		}
	}
	private String get12BCD(double c){
		int intc = new java.math.BigDecimal(c).movePointRight(2).intValue();
		return String.format("%012d", intc);
	}
	/**
	 * 组装60域
	 * @param offlineFlow
	 * @return
	 */
	private String createField60(TLogOfflineTranFlow offlineFlow){
		StringBuilder str = new StringBuilder();
		str.append("36");//交易类型码
		str.append(offlineFlow.getBatchNo());//批次号
		str.append("000");//网络管理码
		str.append(offlineFlow.getTermReadab());//终端读取能力
		str.append(offlineFlow.getIcCondition());//IC卡条件代码
		return str.toString();
	}
	/**
	 * 组55域 - 弃用(可以直接在数据库得到)
	 * @param offlineFlow
	 * @return
	 * @throws Exception
	 */
//	private String getField55(TLogOfflineTranFlow offlineFlow) throws Exception{
//		TLVList returnList = new TLVList();
//		TLV tlv9F26 = TLVEncoder.genTLV("9F26",offlineFlow.getAppCrypt(),false);
//		returnList.add(tlv9F26);
////		TLV tlv9F27 = TLVEncoder.genTLV("9F27",offlineFlow.getAppCrypt(),false);
//		TLV tlv9F10 = TLVEncoder.genTLV("9F10",offlineFlow.getIssueAppdata(),false);
//		returnList.add(tlv9F10);
//		TLV tlv9F37 = TLVEncoder.genTLV("9F37",offlineFlow.getUnpredictableNum(),false);
//		returnList.add(tlv9F37);
//		TLV tlv9F36 = TLVEncoder.genTLV("9F36",offlineFlow.getAtc(),false);
//		returnList.add(tlv9F36);
//		TLV tlv95 = TLVEncoder.genTLV("95",offlineFlow.getTermValidResults(),false);
//		returnList.add(tlv95);
//		TLV tlv9A = TLVEncoder.genTLV("9A",offlineFlow.getTermDate(),true);
//		returnList.add(tlv9A);
//		TLV tlv9C = TLVEncoder.genTLV("9C",offlineFlow.getTranType55(),true);
//		returnList.add(tlv9C);
//		TLV tlv9F02 = TLVEncoder.genTLV("9F02",get12BCD(offlineFlow.getAuthAmount())+"",true);
//		returnList.add(tlv9F02);
//		TLV tlv5F2A = TLVEncoder.genTLV("5F2A",offlineFlow.getCcyCode55(),true);
//		returnList.add(tlv5F2A);
//		TLV tlv82 = TLVEncoder.genTLV("82",offlineFlow.getAip(),false);
//		returnList.add(tlv82);
//		TLV tlv9F1A = TLVEncoder.genTLV("9F1A",offlineFlow.getTermCountryCode(),true);
//		returnList.add(tlv9F1A);
//		TLV tlv9F03 = TLVEncoder.genTLV("9F03",get12BCD(offlineFlow.getOtherAmt())+"",true);
//		returnList.add(tlv9F03);
//		TLV tlv9F33 = TLVEncoder.genTLV("9F33",offlineFlow.getTermCap(),false);
//		returnList.add(tlv9F33);
////		TLV tlv9F1E = TLVEncoder.genTLV("9F1E",offlineFlow.getAppCrypt(),false);
////		TLV tlv84 = TLVEncoder.genTLV("84",offlineFlow.getAppCrypt(),false);
////		TLV tlv9F09 = TLVEncoder.genTLV("9F09",offlineFlow.getAppVersion(),false);
////		TLV tlv9F41 = TLVEncoder.genTLV("9F41",offlineFlow.getAppCrypt(),true);
////		TLV tlv9F34 = TLVEncoder.genTLV("9F34",offlineFlow.getAppCrypt(),false);
////		TLV tlv9F35 = TLVEncoder.genTLV("9F35",offlineFlow.getAppCrypt(),true);
////		TLV tlv9F63 = TLVEncoder.genTLV("9F63",offlineFlow.getAppCrypt(),false);
////		TLV tlv9F74 = TLVEncoder.genTLV("9F74",offlineFlow.getAppCrypt(),false);
////		TLV tlv8A = TLVEncoder.genTLV("8A",offlineFlow.getAppCrypt(),false);
//
//		return HexBinary.encode(returnList.toBinary());
//	}

}
