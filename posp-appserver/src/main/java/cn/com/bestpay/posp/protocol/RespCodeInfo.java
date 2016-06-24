package cn.com.bestpay.posp.protocol;


import cn.com.bestpay.posp.constant.SysConstant;
import cn.com.bestpay.posp.spring.AppServer;
import cn.com.bestpay.posp.system.cache.RespCodeCache;
import cn.com.bestpay.posp.system.entity.TInfoRespCode;
import cn.com.bestpay.posp.system.entity.XmlMessage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取应答码信息
 * @author HR
 *
 */

public class RespCodeInfo {

	private static final Logger log = LoggerFactory.getLogger(RespCodeInfo.class);
	/**
	 * 获取应答码信息并返回
	 * @param respCode
	 * @param message
     */
	public static void getAndReturnRespCodeInfo(String respCode,XmlMessage message){
		try{
			TInfoRespCode infoRespCode = getTInfoRespCode(Long.valueOf(message.getChannelCode()), respCode);
			setRespCodeInfo(infoRespCode,message);
		}catch(Exception e){
			e.printStackTrace();
			log.error(String.format("[%s][%s] Get respCodeInfo error :" + respCode,message.getTranCode(),message.getSerialNo()));
		}
	}
	/**
	 * 获取应答码信息
	 * @param channelCode
	 * @param respCode
	 * @return
	 */
	private static TInfoRespCode getTInfoRespCode(Long channelCode, String respCode) {
		RespCodeCache respCodeCache = (RespCodeCache) AppServer.getBean("RespCodeCache");
		if(!StringUtils.equals(channelCode.toString(), SysConstant.CAPITAL_POOL_5001)
				|| StringUtils.equals(respCode, "0000")){
			respCode = respCode.substring(respCode.length()-2);
		}
		TInfoRespCode infoRespCode = respCodeCache.getCode(channelCode, respCode);
		if(infoRespCode == null){
			infoRespCode = new TInfoRespCode();
			infoRespCode.setPospCode(respCode);
			infoRespCode.setRespCode(respCode.substring(respCode.length()-2));
			log.warn(String.format("应答码表无此应答码： "+respCode));
		}
		return infoRespCode;
	}
	/**
	 * 设置应答码信息
	 * @param infoRespCode
	 * @param message
     */
	private static void setRespCodeInfo(TInfoRespCode infoRespCode,XmlMessage message) {
		message.setRspCode(infoRespCode.getRespCode());
		message.setRspMsg(infoRespCode.getPospDesc());
		setLogInfo(infoRespCode,message);
	}
	/**
	 * 打印应答码信息
	 * @param infoRespCode
	 * @param message
     */
	private static void setLogInfo(TInfoRespCode infoRespCode,XmlMessage message){
//		switch (Integer.valueOf(message.getChannelCode())) {
//		case 5001:
//			log.info(String.format("[%s][%s] MPOS_RESPONSE(%s):[%s] [%s]", message.getTranCode(),message.getSerialNo(), message.getChannelCode(), infoRespCode.getPospCode() , infoRespCode.getPospDesc()));
//			break;
//		default:
			log.info(String.format("[%s][%s] POSP_RESPONSE(%s):[%s] [%s]", message.getTranCode(),message.getSerialNo(), message.getChannelCode(), infoRespCode.getPospCode() , infoRespCode.getPospDesc()));
//			break;
//		}
	}
}
