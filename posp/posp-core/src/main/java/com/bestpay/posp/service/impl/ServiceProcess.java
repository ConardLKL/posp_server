package com.bestpay.posp.service.impl;

import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.protocol.CombineMac;
import com.bestpay.posp.protocol.IMacCallback;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.TrackDecryption;
import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.protocol.util.Utils;
import com.bestpay.posp.service.checker.Pub_ThirdPartyCheck;
import com.bestpay.posp.service.checker.Pub_ValidCheck;
import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.system.cache.CardBinCache;
import com.bestpay.posp.system.cache.ConfigCache;
import com.bestpay.posp.system.cache.RespCodeCache;
import com.bestpay.posp.utils.PropertiesUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务流程
 * @author HR
 *
 */
@Slf4j
@Component("ServiceProcess")
public class ServiceProcess {

	@Autowired
	private Pub_ThirdPartyCheck pub_ThirdPartyCheck;
	@Autowired
	private DataManipulations dataManipulations;
	@Autowired
	private Pub_ValidCheck pub_ValidCheck;
	@Autowired
	private InitialData initialData;
	@Autowired
	private ProcessEngine processEngine;
	@Autowired
	private Pub_UpdateTransactionRecord updateTransactionRecord;
	
	private String networkChannel;
	public void setNetworkChannel(String networkChannel) {
		this.networkChannel = networkChannel;
	}
	/**
	 * 服务调用流程
	 * @param iso
	 * @return
	 */
	public void process(IsoMessage iso) throws Exception{
		//设置初始渠道代码
		iso.setChannelCode(SysConstant.CAPITAL_POOL_5001);
		iso.setFreePasswordSign("0");//设置小额免密默认为否
		//设置国际国密标签
		setStateKeySign(iso);
		if(!checkTransactionFlow (iso)
				|| !initializationFlow(iso) 
				|| !checkNetworkChannel(iso) 
				|| !compareMac(iso)
				|| !callProcessEngine(iso)){
			setFailedMessage(iso);
			updateFlow(iso);
		}
		setinDubiousFormation(iso);
	}

	/**
	 * 缓存刷新调用流程
	 * @param message
	 * @return
     */
	public ByteBuf process(String message) {
		ByteBuf destBuf = Unpooled.buffer();
		try {
			String tran = (message.indexOf(":") == -1) ? message : message.substring(0, message.indexOf(":"));
			if (!StringUtils.equals(tran, "refresh")) {
				destBuf.writeBytes("01".getBytes());
				return destBuf;
			}
			PropertiesUtil propertiesUtil = new PropertiesUtil("sys.properties");
			String ip = propertiesUtil.getProperty("refresh.ip");
			if (ip != null && !StringUtils.equals(ip.trim(),"")
					&& !ip.contains(message.substring(message.indexOf(":") + 1))) {
				destBuf.writeBytes("01".getBytes());
				log.warn("不在指定主机范围内，不允许刷新缓存！"+message.substring(message.indexOf(":") + 1));
				return destBuf;
			}
			ConfigCache configCache = (ConfigCache) PospApplicationContext.getBean("ConfigCache");
			RespCodeCache respCodeCache = (RespCodeCache) PospApplicationContext.getBean("RespCodeCache");
			CardBinCache cardBinCache = (CardBinCache) PospApplicationContext.getBean("CardBinCache");
			configCache.cache();
			respCodeCache.cache();
			cardBinCache.cache();
			destBuf.writeBytes("00".getBytes());
			log.info("缓存已刷新");
		}catch(Exception e){
			destBuf.writeBytes("01".getBytes());
			log.error("刷新缓存失败！"+e.getMessage());
//			e.printStackTrace();
		}
		return destBuf;
	}

	/**
	 * 设置国密标志
	 * @param iso
     */
	public void setStateKeySign(IsoMessage iso){
		if(StringUtils.isNotEmpty(iso.getField(53))
				&& StringUtils.equals(iso.getField(53).substring(1,2),"4")){
			iso.setStateKeySign(true);
		}else{
			iso.setStateKeySign(false);
		}
	}
	/**
	 * 设置平台标志
	 * true 表示自己平台终端接入
	 * false 表示第三方平台终端接入
	 * @param iso
	 */
	public void setPlatform(IsoMessage iso){
		if(pub_ThirdPartyCheck.isThirdPartyPlatformTpdu(iso)){
			iso.setPlatform(false);
		}else{
			iso.setPlatform(true);
		}
	}
	/**
	 * 获取接入IP
	 * @param ctx
	 * @return
	 */
	public String setXRealIp(ChannelHandlerContext ctx){
		//设置接入IP
		String xRealIp = ctx.pipeline().channel().remoteAddress().toString();
		return xRealIp.substring(1,xRealIp.indexOf(":"));
	}
	/**
	 * 获取本机IP
	 * @param ctx
	 * @return
	 */
	public String setIp(ChannelHandlerContext ctx){
		//获取本机IP
		String localhost = ctx.pipeline().channel().localAddress().toString();
		return localhost.substring(1,localhost.indexOf(":"));
	}
	/**
	 * 检查交易流水
	 * @param iso
	 * @return
	 */
	private boolean checkTransactionFlow(IsoMessage iso){
		iso.setTranCode(initialData.generatingTranCode(iso));//设置交易码
		if(pub_ValidCheck.transactionFlowCheck(iso)){
			return true;
		}
		return false;
	}

	/**
	 * 检查是否支持此交易
	 * @param iso
	 * @return
     */
	private boolean checkTranCode(IsoMessage iso){
		if(!PosConstant.MANAGEMENT_TYPES.contains(iso.getTranCode())
				&& !PosConstant.TRANSACTION_TYPES.contains(iso.getTranCode())
				&& !PosConstant.TRANS_TYPE_SLIP.contains(iso.getTranCode())){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A140, iso);
			return false;
		}
		return true;
	}
	/**
	 * 初始化流水表
	 * @param iso
	 */
	private boolean initializationFlow(IsoMessage iso){
		boolean state = checkTranCode(iso);
		if(state) {
			//解密磁道信息
			//由于磁条卡需要从35或36域取得卡号，故初始化流水前先解密磁道信息
			state = trackDecryption(iso);
		}
		try {
			dataManipulations.initialFlow(iso);
		} catch (Exception e) {
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_B196, iso);
			log.error("InitializationFlow error :" + e.getMessage());
			return false;
		}
		if(iso.isState() && state){
			return true;
		}
		return false;
	}
	/**
	 * 判断是否为第三方平台接入并且是否只支持公网交易
	 * 若是,则拒绝交易,返回12错误码
	 * @param iso
	 * @return
	 */
	private boolean checkNetworkChannel(IsoMessage iso){
		if(iso.isPlatform()){
			return true;
		}
		ConfigCache cache = (ConfigCache)PospApplicationContext.getBean("ConfigCache");
		String paraRange = cache.getParaRange(SysConstant.CL3001, iso.getField(32));
		//若字段para_range值为0，则只允许公网交易
		if(StringUtils.equals(this.networkChannel, "TCP")
				&& StringUtils.equals(paraRange, "0")){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A212, iso);
			return false;
		}
		return true;
	}
	/**
	 * 检查MAC
	 * @param iso
	 */
	private boolean compareMac(IsoMessage iso){
		String errorCode = null;
		try{
			iso.setCompareMac((IMacCallback)PospApplicationContext.getBean("CompareMac"));
			iso.compareMac();
			return true;
		}catch(Exception e){
			errorCode = e.getMessage();
			log.error("CompareMac error :"+errorCode);
		}
		iso.setChannelCode(SysConstant.CAPITAL_POOL_4001);
		RespCodeInformation.getAndReturnRespCodeInfo(errorCode, iso);
		return false;
	}
	/**
	 * 解密磁道信息
	 * @param iso
	 */
	private boolean trackDecryption(IsoMessage iso){
		try{
			TrackDecryption.decryptTrack(iso);
			log.info(" 解密磁道信息成功！");
			return true;
		}catch(Exception e){
			log.error("TrackDecryption error :"+e.getMessage());
			e.printStackTrace();
		}
		RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A130, iso);
		return false;
	}
	/**
	 * 调用流程引擎
	 * @param iso
	 * @return
	 */
	private boolean callProcessEngine(IsoMessage iso){
		IsoMessage out = processEngine.callPospProcessEngine(iso);
		if(Utils.isNull(out)){
			RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A096, iso);
			return false;
		}
		out.clone(iso);
		return true;
	}
	/**
	 * 设置失败报文信息
	 * @param iso
	 */
	private void setFailedMessage(IsoMessage iso){
		try{
			if(StringUtils.equals(HexCodec.hexEncode(iso.getPospMessage().getType()),iso.getField(0))){
				iso.setField(0, String.format("%04d",Integer.parseInt(iso.getField(0)) + 10));
			}
			iso.setField(39, iso.getRspCode());
		}catch(Exception e){
			log.error("response pos Exception:" + e.getMessage());
		}
	}

	/**
	 * 更新（业务框架外）失败流水信息
	 * @param iso
     */
	private void updateFlow(IsoMessage iso){
		updateTransactionRecord.updateFlowSign(iso);
	}
	/**
	 * 组装8583返回报文
	 * @param ctx
	 * @param iso
	 * @return
	 */
	public ByteBuf assemblyReturnMessage(ChannelHandlerContext ctx, IsoMessage iso) throws Exception{
		ByteBuf destBuf = null;
		byte[] dest = combineMac(iso);
		String tpdu = HexCodec.hexEncode(iso.getPospMessage().getTpdu());
		byte[] header = iso.getPospMessage().getHeader();
		if(StringUtils.isNotEmpty(iso.getPospMessage().getMpos())){
			String pospCode = iso.getPospCode();
			try {
				if (pospCode.length() < 4) {
					pospCode = "00" + pospCode;
				}
			}catch(NullPointerException e){
				pospCode = "00"+iso.getField(39);
			}
			destBuf = Unpooled.buffer(2 + 2 + 2 + 5 + 6 + dest.length);
			destBuf.writeBytes(HexCodec.hexDecode(pospCode));
			destBuf.writeBytes(HexCodec.hexDecode(iso.getChannelCode()));
		}else{
			destBuf = Unpooled.buffer(2 + 5 + 6 + dest.length);
		}
		destBuf.writeShort(dest.length + 5 + 6);
		destBuf.writeBytes(HexCodec.hexDecode(tpdu.substring(0,2) + tpdu.substring(6,10) + tpdu.substring(2,6)));
		destBuf.writeBytes(header);
		destBuf.writeBytes(dest);
		iso.printMessage(iso.getSeq());
		return destBuf; 
	}
	/**
	 * 生成MAC并格式为16进制的字符串
	 * @param iso
	 * @throws Exception
	 */
	private byte[] combineMac(IsoMessage iso) throws Exception{
		String messageType = iso.getField(0);
		CombineMac combineMac = (CombineMac)PospApplicationContext.getBean("CombineMac");
		String MType = "0330.0510.0810.0830";
		if (
//				(StringUtils.equals("00", iso.getField(39))
//				|| StringUtils.equals("10", iso.getField(39)))
//				&&
			(!MType.contains(messageType)
			|| PosConstant.TRANS_TYPE_SLIP.contains(iso.getTranCode()))) {
			//第三方接入时单独生成MAC
			if(iso.isPlatform()){
				iso.setField(64, "ABCDABCDABCDABCD");
				iso.setCombineMac(combineMac);
			}else{
				iso.setField(64, combineMac.calculate(iso));
			}
		}
		return iso.getMessage();
	}
	/**
	 * 插入可疑表
	 * @param iso
	 * @throws Exception
	 */
	private void setinDubiousFormation(IsoMessage iso)throws Exception{
		if(StringUtils.isNotEmpty(iso.getPospCode())
				&& !StringUtils.equals(iso.getPospCode(), "00")
				&& !StringUtils.equals(iso.getPospCode(), POSPConstant.POSP_A000)
				&& !StringUtils.equals(iso.getPospCode(), POSPConstant.POSP_A100)
				&& !StringUtils.equals(iso.getPospCode(), POSPConstant.POSP_A200)){
			dataManipulations.insertDubious(iso);
		}
	}
}
