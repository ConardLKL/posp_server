package com.bestpay.posp.service.checker;

import com.bestpay.posp.constant.CheckerConstant;
import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.UnionAPI;
import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.remoting.transport.client.EncryptionAPI;
import com.bestpay.posp.service.exception.KeyNullException;
import com.bestpay.posp.service.impl.RespCodeInformation;
import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.system.entity.TInfoPublickeyManage;
import com.bestpay.posp.system.entity.TInfoTermSign;
import com.bestpay.posp.system.entity.TMcmPosinfo;
import com.bestpay.posp.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Component
public class M_SignInChecker {
	
	@Autowired
	TInfoTermSignService tInfoTermSignService;
	@Autowired
	TMcmPosinfoService tMcmPosinfoService;
	@Autowired
	TInfoPublickeyManageService tInfoPublickeyManageService;
	@Autowired
	TInfoTermParaService tInfoTermParaService;
	@Autowired
	TInfoNonSecretParaService tInfoNonSecretParaService;
	/**
	 * 更改终端签到表相应字段 将Sign_State改为1签到状态，更新签到时间
	 * POS机首次签到时插入一条数据
	 * @param iso
	 * 
	 * @remarks 备注： tmK_tpk、tmk_tdk、tmK_tak的修改未加入
	 * 
	 * @return
	 * @throws Exception
	 *             by getField;
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public IsoMessage updatePosSign(IsoMessage iso) throws Exception{
		iso.setState(false);
		String valF62 = null;
		String batchNo = "000001";
		boolean selSign = false;
		String algorithmID = null;
		try {
			//国际秘钥签到和国密秘钥签到使用不同的
			if(StringUtils.equals(iso.getTranCode(),PosConstant.TRANS_TYPE_0800004)){
				algorithmID = "DES";
			}else if(StringUtils.equals(iso.getTranCode(),PosConstant.TRANS_TYPE_0800006)){
				algorithmID = "SM4";
			}
			if(!iso.isPlatform()){
				valF62 = createTpkAndTak(algorithmID,iso.getField(32));
			}else{
				valF62 = createTpkAndTak(algorithmID,iso.getField(42)+iso.getField(41));
			}
		}catch (Exception e1) {
			iso.setChannelCode(SysConstant.CAPITAL_POOL_4001);
			RespCodeInformation.getAndReturnRespCodeInfo(e1.getMessage(),iso);
			log.error(String.format("[%s] 签到获取TPK,TAK时加密机出现异常! ", iso.getSeq()));
			log.error(e1.getMessage());
			return iso;
		}
		if ((StringUtils.equals(PosConstant.TRANS_TYPE_0800004,iso.getTranCode()) && valF62.length() == 120)
				|| (StringUtils.equals(PosConstant.TRANS_TYPE_0800006,iso.getTranCode()) && valF62.length() == 120)) {
			iso.setField(62, valF62);
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_00,iso);
			log.info(String.format("[%s] successfully signed in. [%s]",iso.getSeq(),iso.getField(50)));
		} else {
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_B896,iso);
			log.warn(String.format("[%s] 加密机没有录入终端商户信息.[%s]",iso.getSeq(),iso.getField(50)));
			return iso;
		}
		try {
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
			tInfoTermSign.setSignState(CheckerConstant.SIGN_STATE_1);
			//交易流水
			tInfoTermSign.setSerialNo(iso.getFlow().getSerialNo());
			//渠道编码
			tInfoTermSign.setChannelCode(iso.getFlow().getChannelCode());
			if(selInfoTermSign == null){
				tInfoTermSign.setBatchNo(batchNo);//首次初始化批次号，插入
				tMcmPosinfo.setBatchNo(batchNo);
				tMcmPosinfo.setIsLogon((long) 1);
				try {
					selSign = tMcmPosinfoService.updateMcmPosinfo(tMcmPosinfo);
					selSign = tInfoTermSignService.insertPospTermSign(tInfoTermSign);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}else{
				batchNo = selInfoTermSign.getBatchNo();
				//终端上次签到时间
				Date date = selInfoTermSign.getUpdateTime();
				//检测终端是否需要更新参数或公钥
				//区分国际版公钥和国密版公钥   0800373-国密公钥获取
				String sign;
				if(!StringUtils.equals(iso.getTranCode(), PosConstant.TRANS_TYPE_0820373)) {
					sign = "01";
				}else{
					sign = "04";
				}
				String header = updatePara(sign,date, HexCodec.hexEncode(iso.getPospMessage().getHeader()));
				iso.getPospMessage().setHeader(HexCodec.hexDecode(header));
				tMcmPosinfo.setBatchNo(selInfoTermSign.getBatchNo());
				tMcmPosinfo.setIsLogon((long) 1);
				try {
					selSign = tMcmPosinfoService.updateMcmPosinfo(tMcmPosinfo);
					selSign = tInfoTermSignService.updatePospTermSign(tInfoTermSign);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
			iso.setField(60, iso.getField(60).substring(0, 2)+batchNo+iso.getField(60).substring(8,11));
		} catch (Exception e) {
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_B996,iso);
			log.warn(String.format("[%s] 签到60域数据组合失败 [%s] ", iso.getSeq(), e.getMessage()));
			return iso;
		}
		
		if (!selSign) {
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_C096,iso);
			log.error(String.format("[%s] 签到时更新终端或签到表失败! ", iso.getSeq()));
		}else{
			iso.setState(true);
			log.info(String.format("签到成功！"));
		}
		return iso;
	}
	
	public  String createTpkAndTak(String algorithmID,String zpkzak) throws Exception{
		String signData = "";
		//获取加密机IP
//		EncryptionAPI encryptionAPI = (EncryptionAPI)PospApplicationContext.getBean("EncryptionAPI");
		UnionAPI unionAPI = (UnionAPI)PospApplicationContext.getBean("UnionAPI");
		String[] res = unionAPI.unionGenerateKeyZpk(algorithmID,zpkzak);
		for (int n = 0; n < res.length; n++) {
			log.info("[POS."+zpkzak+".zpk] TPK：[" + res[n] + "]");
			switch (n) {
			case 0:
				checkKey(res[n]);
				signData += res[n];
				break;
			case 1:
				signData += res[n].substring(0,8);
				break;
			default:
				break;
			}
		}
		res = unionAPI.unionGenerateKeyZak(algorithmID,zpkzak);
		for (int n = 0; n < res.length; n++) {
			log.info("[POS."+zpkzak+".zak] ZAK：[" + res[n] + "]");
			switch (n) {
			case 0:
				checkKey(res[n]);
				signData += res[n];
				break;
			case 1:
				if(StringUtils.equals(algorithmID,"DES")) {
					signData += "0000000000000000";
				}
				signData += res[n].substring(0,8);
				break;
			default:
				break;
			}
		}
		res = unionAPI.unionGenerateKeyEdk(algorithmID,zpkzak);
		for (int n = 0; n < res.length; n++) {
			log.info("[POS."+zpkzak+".edk] edk：[" + res[n] + "]");
			switch (n) {
			case 0:
				checkKey(res[n]);
				signData += res[n];
				break;
			case 1:
				signData += res[n].substring(0,8);
				break;
			default:
				break;
			}
		}
		
		log.info("signData：[" + signData + "]");
		return signData;
	}

	/**
	 * 检查秘钥值
	 * @param signData
	 * @throws KeyNullException
     */
	private void checkKey(String signData) throws KeyNullException {
		if(StringUtils.equals(signData.substring(0,1),"-")){
			throw new KeyNullException(String.format(signData.substring(1)));
		}
	}
	/**
	 * 更新参数
	 * @param date
	 * @param header
	 * @return
	 */
	public String updatePara(String sign,Date date,String header){
		TInfoPublickeyManage tInfoPublickeyManage = new TInfoPublickeyManage();
		tInfoPublickeyManage.setPkIdentify(sign);
		Date keyDate = tInfoPublickeyManageService.getInfoPublickeyManage(tInfoPublickeyManage).get(0).getUpdateTime();
		Date icKeyDate = tInfoTermParaService.getPospTermPara().get(0).getUpdateTime();
		Date nonSecretKey = tInfoNonSecretParaService.getInfoNonSecretPara().get(0).getUpdateTime();
		//比较签到时间与参数或者秘钥的更新时间
		//4	通知终端发起更新公钥信息操作
		//5	下载终端IC卡参数
		//9 非接业务参数下载
		if(date.before(keyDate)){
			header = header.substring(0, 5)+"4"+header.substring(6);
		}else if(date.before(icKeyDate)){
			header = header.substring(0, 5)+"5"+header.substring(6);
		}else if(date.before(nonSecretKey)) {
			header = header.substring(0, 5) + "9" + header.substring(6);
		}
		return header;
	}

}
