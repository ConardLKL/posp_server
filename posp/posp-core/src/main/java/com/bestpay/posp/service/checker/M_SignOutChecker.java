package com.bestpay.posp.service.checker;

import com.bestpay.posp.constant.CheckerConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.system.entity.TInfoTermSign;
import com.bestpay.posp.system.service.TInfoTermSignService;
import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.system.entity.TMcmPosinfo;
import com.bestpay.posp.system.service.TMcmPosinfoService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bestpay.posp.service.impl.RespCodeInformation;

@Slf4j
@Component
public class M_SignOutChecker {
	@Autowired
	TInfoTermSignService tInfoTermSignService;
	@Autowired
	TMcmPosinfoService tMcmPosinfoService;
	/**
	 * 更改终端签到表相应字段 将Sign_State改为2签退状态，更新签到时间
	 * POS机首次签退时返回96错
	 * @param inCheck 
	 * 
	 * @return
	 * @throws Exception
	 *             by getField;
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public IsoMessage updatePosSign(IsoMessage iso) throws Exception{
		boolean selSign = false;
			TInfoTermSign tInfoTermSign = new TInfoTermSign();
			TMcmPosinfo tMcmPosinfo = new TMcmPosinfo();
			tMcmPosinfo.setPosCode(iso.getField(41));
			tMcmPosinfo.setMctCode(iso.getField(42));
			tInfoTermSign.setTermCode(iso.getField(41));
			tInfoTermSign.setMerchCode(iso.getField(42));
			
			TInfoTermSign selInfoTermSign = tInfoTermSignService.getPospTermSign(tInfoTermSign);
			
			//签到时间
			tInfoTermSign.setUpdateTime(new java.util.Date());
			//签到状态
			tInfoTermSign.setSignState(CheckerConstant.SIGN_STATE_2);
			//交易流水
			tInfoTermSign.setSerialNo(iso.getFlow().getSerialNo());
			if(selInfoTermSign==null){
				RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A177,iso);
				log.info("终端未签到，请签到后再签退！");
				return iso;
			}else{
				//批次号循环使用  加判断
				String batchNo = "";
				int batchNocnt = Integer.parseInt(selInfoTermSign.getBatchNo());
				if(batchNocnt+1 >= 1000000) {
					// 重置
					batchNo = String.format("%06d", 1);
				} else {//批次号加一***********************************************
					batchNocnt = batchNocnt + 1;
					batchNo = String.format("%06d", batchNocnt);
				}
				tInfoTermSign.setBatchNo(batchNo);
				tMcmPosinfo.setIsLogon((long) 0);
				try {
					selSign = tInfoTermSignService.updatePospTermSign(tInfoTermSign);
					selSign = tMcmPosinfoService.updateMcmPosinfo(tMcmPosinfo);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		if (!selSign) {
			iso.setState(false);
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_C196,iso);
			log.error(String.format("[%s] 签退时更新终端或签到表失败! ", iso.getSeq()));
			iso.printMessage("update PosSign！");
		}else{
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_00,iso);
			iso.setState(true);
			log.info(String.format("签退成功！"));
		}
		return iso;
	}
}
