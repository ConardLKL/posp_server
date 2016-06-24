package cn.com.bestpay.posp.service.impl;

import cn.com.bestpay.posp.constant.SysConstant;
import cn.com.bestpay.posp.protocol.util.HexCodec;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.bestpay.posp.protocol.XmlAnalysis;
import cn.com.bestpay.posp.system.entity.XmlHead;
import cn.com.bestpay.posp.system.entity.XmlMessage;
import cn.com.bestpay.posp.system.service.SysSerialNoService;
/**
 * 报文解析
 * @author HR
 *
 */
@Component("MessageAnalysis")
public class MessageAnalysis {
	private static final Logger log = LoggerFactory.getLogger(MessageAnalysis.class);
	@Autowired
	private SysSerialNoService sysSerialNoService;

	/**
	 * xml报文解析
	 * @param msg
	 * @return
	 */
	public XmlMessage xmldecode(ByteBuf msg) throws Exception{
		String serialNo = getSerialNo();
		Map<String, Object> map = XmlAnalysis.parseXml(msg);//解析Xml报文
		XmlMessage message = new XmlMessage();
		message.setSerialNo(serialNo);
		message.setHead((XmlHead)map.get("head"));
		message.setBody((Element)map.get("body"));
		message.setTranCode((String)map.get("tranCode"));
		log.info(String.format("[%s] :" + getTranName(message.getTranCode()), message.getTranCode()));
		//交易类获取8583报文
		if(((Element)map.get("body")).element("REQUEST") != null){
			String iso8583 = ((Element)map.get("body")).element("REQUEST").elementText("ISO8583");
			message.setIso8583(HexCodec.hexEncode(Base64.decodeBase64(iso8583)));
			log.debug("iso8583:"+message.getIso8583());
		}
		return message;
	}

	/**
	 * 获取唯一识别流水号
	 * @return
	 * @throws Exception
	 */
	public String getSerialNo() {
		return sysSerialNoService.querySerialNo();
	}
	/**
	 * 获取交易名称
	 * @param tranCode
	 * @return
     */
	public String getTranName(String tranCode){
		String tranName = "";
		if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_8001)){
			tranName = "签到交易";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_8002)){
			tranName = "签退交易";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_8003)){
			tranName = "IC卡参数下载交易";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_8004)){
			tranName = "IC卡公钥下载交易";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_2001)){
			tranName = "查询余额交易";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_2002)){
			tranName = "消费交易";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_2003)){
			tranName = "消费撤销交易";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_2004)){
			tranName = "消费退货交易";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_2005)){
			tranName = "交易状态查询交易";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_2006)){
			tranName = "电子凭证签名上送交易";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_0100)){
			tranName = "版本检查";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_0101)){
			tranName = "获取短信验证码";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_1001)){
			tranName = "用户注册";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_1002)){
			tranName = "用户登录";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_1003)){
			tranName = "用户退出";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_1004)){
			tranName = "修改密码";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_1005)){
			tranName = "重置密码";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_1006)){
			tranName = "商户交易流水查询";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_1007)){
			tranName = "交易汇总查询";
		}else if(StringUtils.equals(tranCode, SysConstant.TRANS_TYPE_1008)){
			tranName = "获取公告";
		}else{
			tranName = "未知交易";
		}
		return tranName;
	}
}
