package cn.com.bestpay.posp.service.impl;

import cn.com.bestpay.posp.constant.SysConstant;
import cn.com.bestpay.posp.protocol.RespCodeInfo;
import cn.com.bestpay.posp.service.xml.BaseService;
import cn.com.bestpay.posp.spring.AppServer;
import cn.com.bestpay.posp.system.entity.XmlMessage;
import cn.com.bestpay.posp.transport.client.ClientBoot;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
/**
 * 业务流程
 * @author HR
 *
 */
@Component("ServiceProcess")
public class ServiceProcess {
	private static final Logger log = LoggerFactory.getLogger(ServiceProcess.class);
	/**
	 * 业务处理
	 * @param ctx
	 * @param message
	 * @return
	 * @throws Exception
     */
	public ByteBuf process(ChannelHandlerContext ctx,XmlMessage message) throws Exception{
		//设置初始渠道代码
		message.setChannelCode(SysConstant.CAPITAL_POOL_9001);
		if(!callProcess(message)){
			log.info(String.format("[%s][%s] 交易失败！", message.getTranCode(),message.getSerialNo()));
		}
		return assemblyReturnMessage(ctx,message);
	}
	/**
	 * 调用流程
	 * @param message
	 * @return
	 */
	private boolean callProcess(XmlMessage message){
		if(SysConstant.TRANSACTION_TYPES.contains(message.getTranCode())){
			return transactionProcess(message);
		}else{
			//管理类处理
		}
		return true;
	}
	/**
	 * 交易类业务处理
	 * @param message
	 * @return
	 */
	private boolean transactionProcess(XmlMessage message){
		ClientBoot clientBoot = new ClientBoot();
		String iso8583 = clientBoot.call(message);
		if(iso8583 == null){
			message.setIso8583("");
			log.info(String.format("[%s][%s] 交易超时！", message.getTranCode(),message.getSerialNo()));
			message.getHead().setRspCode("98");
			return false;
		}
		log.info(String.format("[%s] iso8583:"+iso8583.substring(8), message.getSerialNo()));
		message.setChannelCode(iso8583.substring(4,8));
		message.getHead().setRspCode("00"+iso8583.substring(2,4));
		RespCodeInfo.getAndReturnRespCodeInfo(iso8583.substring(0,4), message);
		message.setIso8583(iso8583.substring(8));
		return true;
	}
	/**
	 * 组装xml返回报文
	 * @param ctx
	 * @param message
	 * @return
	 * @throws Exception
	 */
	private ByteBuf assemblyReturnMessage(ChannelHandlerContext ctx,XmlMessage message) throws Exception{
		BaseService service = null;
		if(SysConstant.TRANSACTION_TYPES.contains(message.getTranCode())){
			service = (BaseService) AppServer.getBean("REQUEST");
		}else{
			service = (BaseService) AppServer.getBean(message.getTranCode());
		}
		byte[] dest = service.setMessage(message);
		ByteBuf destBuf = ctx.alloc().buffer(dest.length);
		destBuf.writeBytes(dest);
		return destBuf; 
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
}
