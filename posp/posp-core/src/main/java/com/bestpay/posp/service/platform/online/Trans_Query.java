package com.bestpay.posp.service.platform.online;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.bestpay.posp.protocol.IsoMessage;
import org.springframework.stereotype.Component;

import com.unionpay.acp.sdk.SDKConfig;
@Component("0200")
public class Trans_Query extends BaseService{


	/**
	 * 查询交易表单填写
	 * @return
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
//	@Override
	public Map<String, String> setFormDate(IsoMessage iso) throws UnsupportedEncodingException, IOException {

		Map<String, String> contentData = new HashMap<String, String>();

		contentData.put("version", "5.0.0"); //版本号
		contentData.put("encoding", "UTF-8"); //编码方式
//		contentData.put("certId", ""); //证书 ID
//		contentData.put("signature", "");//签名
		contentData.put("signMethod", "01");//签名方法
		contentData.put("txnType", "00");//交易类型
		contentData.put("txnSubType", "00");//交易子类
		contentData.put("bizType", "000000");//产品类型
		contentData.put("channelType", "08");//渠道类型
		contentData.put("acqInsCode", "48375800");//收单机构代码
		contentData.put("accessType", "1");//接入类型
		contentData.put("merId", iso.getField(42));//商户代码
		contentData.put("txnTime", iso.getFlow().getYYYYMMDD()+iso.getFlow().getHhmmss());//订单发送时间
		contentData.put("orderId", iso.getFlow().getYYYYMMDD()+iso.getFlow().getHhmmss()); //商户订单号

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
		String requestBackUrl = SDKConfig.getConfig().getSingleQueryUrl();
		Map<String, String> resmap = submitDate(setFormDate(iso), requestBackUrl);
		return resmap;
	}
}
