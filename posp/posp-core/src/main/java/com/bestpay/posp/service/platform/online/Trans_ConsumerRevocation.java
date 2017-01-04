package com.bestpay.posp.service.platform.online;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.bestpay.posp.protocol.IsoMessage;
import org.springframework.stereotype.Component;

import com.unionpay.acp.sdk.SDKConfig;
@Component("0200200023")
public class Trans_ConsumerRevocation extends BaseService{


	/**
	 * 消费撤销交易表单填写
	 * @return
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	@Override
	public Map<String, String> setFormDate(IsoMessage iso) throws UnsupportedEncodingException, IOException {

		Map<String, String> contentData = new HashMap<String, String>();

		contentData.put("version", "5.0.0"); //版本号
		contentData.put("encoding", "UTF-8"); //编码方式
//		contentData.put("certId", ""); //证书 ID
//		contentData.put("signature", "");//签名
		contentData.put("signMethod", "01");//签名方法
		contentData.put("txnType", "31");//交易类型
		contentData.put("txnSubType", "00");//交易子类
		contentData.put("bizType", "000000");//产品类型
		contentData.put("channelType", "08");//渠道类型
		contentData.put("backUrl", backUrl);//后台通知地址
		contentData.put("accessType", "1");//接入类型
		contentData.put("acqInsCode", "48375800");//收单机构代码
		contentData.put("merCatCode", iso.getField(42).substring(8,12));//商户类别
		contentData.put("merId", iso.getField(42));//商户代码
		contentData.put("merName", "翼支付");//商户名称
		contentData.put("merAbbr", "YZF");//商户简称
		contentData.put("orderId", iso.getFlow().getYYYYMMDD()+iso.getFlow().getHhmmss()); //商户订单号
		contentData.put("origQryId", iso.getOrgFlow().getCupsSerialNo()); //原始交易流水号
		contentData.put("txnTime", iso.getFlow().getYYYYMMDD()+iso.getFlow().getHhmmss());//订单发送时间
		contentData.put("txnAmt", iso.getField(4)); //交易金额
		contentData.put("termId", iso.getField(41));//终端号

		return contentData;
	}
	@Override
	public Map<String, String> service(IsoMessage iso) throws UnsupportedEncodingException, IOException{
		/**
		 * 参数初始化 
		 * 1.以java main方法运行时,必须每次都执行加载配置文件 
		 * 2.以java web应用程序运行时,配置文件初始化代码可以写在listener中
		 * </pre>
		 */
		SDKConfig.getConfig().loadPropertiesFromSrc();

		/**
		 * 交易请求url 从配置文件读取
		 */
		String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();
		Map<String, String> resmap = submitDate(setFormDate(iso), requestBackUrl);
		return resmap;
	}
}
