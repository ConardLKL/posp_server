package com.bestpay.posp.service.platform.online;

import com.bestpay.posp.protocol.IsoMessage;
import com.unionpay.acp.sdk.SDKConfig;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
@Component("0200000022")
public class Trans_Consumer extends BaseService{


	/**
	 * 消费交易表单填写
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
		contentData.put("txnType", "01");//交易类型
		contentData.put("txnSubType", "01");//交易子类
		contentData.put("bizType", "000000");//产品类型
		contentData.put("channelType", "08");//渠道类型
		contentData.put("backUrl", backUrl);//后台通知地址
		contentData.put("accessType", "1");//接入类型
		contentData.put("acqInsCode", "48375800");//收单机构代码
		contentData.put("merCatCode", iso.getField(42).substring(8,12));//商户类别
		contentData.put("merId", iso.getField(42));//商户代码
		contentData.put("merName", "天翼电子商务有限公司");//商户名称
		contentData.put("merAbbr", "GDTYDZSW");//商户简称
		contentData.put("orderId", iso.getFlow().getYYYYMMDD()+iso.getFlow().getHhmmss()); //商户订单号
		contentData.put("txnTime", iso.getFlow().getYYYYMMDD()+iso.getFlow().getHhmmss());//订单发送时间
		contentData.put("accType", "01"); //账号类型
		contentData.put("accNo", iso.getField(2));//账号
		contentData.put("txnAmt", iso.getField(4)); //交易金额
		contentData.put("currencyCode", iso.getField(49));//交易币种
		contentData.put("reqReserved", "透传信息"); //请求方保留域，相关通知和查询接口原样返回，内容符合规范即可，如出现&、=等字符可能会影响报文解析，尽量不要出现。
		contentData.put("customerInfo", getCustomer(contentData, encoding,iso)); //请参考getCustomer方法内部，相关要素从读卡器采集。
		contentData.put("cardTransData", getCardTransData(contentData, iso)); //请参考getCardTransData方法内部，相关要素从读卡器采集。

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
