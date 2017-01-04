package com.bestpay.posp.service.platform.online;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.remoting.transport.client.EncryptionAPI;
import com.bestpay.posp.spring.PospApplicationContext;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;

import com.unionpay.acp.sdk.HttpClient;
import com.unionpay.acp.sdk.SDKConstants;
import com.unionpay.acp.sdk.SDKUtil;
import com.unionpay.acp.sdk.SecureUtil;
/**
 * 基础服务
 * @author TISSON
 *
 */
@Slf4j
public abstract class BaseService implements FullChannelService{
	
	public static String encoding = "UTF-8";
	public static String version = "5.0.0";
	// 前台通知服务对应的写法参照 FrontRcvResponse.java
	public static String frontUrl = "http://183.62.49.173:8025?method=front";

	// 后台通知服务对应的写法参照 BackRcvResponse.java
	public static String backUrl = "http://183.62.49.173:8025?method=back";// 受理方和发卡方自选填写的域[O]--后台通知地址
	// 模
	public static final String modulus = "23648629510357402173669374843546537318532861396089478651610490265597426690711092692490012429464861104676801339474220894685964389750254240882066338437712341498313076007251358899488346743554156067576120095739341094220657657611893755799646325194641430110114613586989866468748149428464174345443169749235358776080247588710246733575431530477273705811466095207773188767974550742707293785661521305267533098997705930724499157184797236612324838287379798375903922360666026664942383548006246201656190964746068225967889145661249463716565124050082767382345820178584568857820200627919768134084891356188058390460707236118612628845159";
	// 指数
	public static final String exponent = "65537";

	/**
	 * 数据组装进行提交 包含签名
	 * 
	 * @param contentData
	 * @return 返回报文 map
	 */
	public static Map<String, String> submitDate(Map<String, ?> contentData,
			String requestUrl) {
		Map<String, String> submitFromData = (Map<String, String>) signData(contentData);
		submitFromData.put("requestUrl", requestUrl);
		return submitFromData;
	}
	
	/**
	 * 数据提交 提交到后台
	 * 
	 * @param contentData
	 * @return 返回报文 map
	 */
	public static Map<String, String> submitUrl(
			Map<String, String> submitFromData, String requestUrl) {
		String resultString = "";
		System.out.println("requestUrl=[" + requestUrl + "]");
		/**
		 * 发送
		 */
		HttpClient hc = new HttpClient(requestUrl, 30000, 30000);
		try {
			int status = hc.send(submitFromData, encoding);
			if (200 == status) {
				resultString = hc.getResult();
				log.info("RESPONSE MESSAGE :"+resultString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, String> resData = new HashMap<String, String>();
		/**
		 * 验证签名
		 */
		if (null != resultString && !"".equals(resultString)) {
			// 将返回结果转换为map
			resData = SDKUtil.convertResultStringToMap(resultString);
			if (SDKUtil.validate(resData, encoding)) {
				System.out.println("验证签名成功");
			} else {
				System.out.println("验证签名失败");
			}
		}
		return resData;
	}
	
	/**
	 * 对数据进行签名
	 * 
	 * @param contentData
	 * @return　签名后的map对象
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> signData(Map<String, ?> contentData) {
		Entry<String, String> obj = null;
		Map<String, String> submitFromData = new HashMap<String, String>();
		for (Iterator<?> it = contentData.entrySet().iterator(); it.hasNext();) {
			obj = (Entry<String, String>) it.next();
			String value = obj.getValue();
			if (StringUtils.isNotBlank(value)) {
				// 对value值进行去除前后空处理
				submitFromData.put(obj.getKey(), value.trim());
			}
		}
		/**
		 * 签名
		 */
		SDKUtil.sign(submitFromData, encoding);
		return submitFromData;
	}
	/**
	 * 持卡人信息域操作
	 * 
	 * @param encoding
	 *            编码方式
	 * @return base64后的持卡人信息域字段
	 */
	public static String getCustomer(Map<String, ?> contentData,
			String encoding,IsoMessage iso) {

		String pin = getPin(iso); //密码明文请从读卡器让用户输入采集。
		pin = SDKUtil.encryptPin(contentData.get("accNo").toString(), pin, encoding);
		Map<String, String> customerInfoMap = new HashMap<String, String>(); 
		String cardProperty = iso.getField(22).substring(0,2);
		customerInfoMap.put("pin", pin);
		if((StringUtils.equals(cardProperty, "05")
				|| StringUtils.equals(cardProperty, "07")
				|| StringUtils.equals(cardProperty, "95")
				|| StringUtils.equals(cardProperty, "98")
				|| StringUtils.equals(cardProperty, "00"))
				&& StringUtils.isNotEmpty(iso.getField(55))){
			String expired = iso.getField(14); //有效期明文从读卡器读取，IC卡送，磁条卡不送。
			expired = SDKUtil.encryptAvailable(expired, encoding);
			customerInfoMap.put("expired", expired); //IC卡送，磁条卡不送。
		}
		StringBuffer sf = new StringBuffer();
		String customerInfo = sf.append(SDKConstants.LEFT_BRACE)
		.append(SDKUtil.coverMap2String(customerInfoMap))
		.append(SDKConstants.RIGHT_BRACE).toString();
		
		try {
			return new String(SecureUtil.base64Encode(sf.toString().getBytes(
					encoding)), encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return customerInfo;
	}
	/**
	 * 有卡交易信息域(cardTransData)构造示例<br>
	 * 所有子域需用“{}”包含，子域间以“&”符号链接。格式如下：{子域名1=值&子域名2=值&子域名3=值}<br>
	 * 说明：本示例仅供参考，开发时请根据接口文档中的报文要素组装
	 * 
	 * @param contentData
	 * @param encoding
	 * @return
	 */
	public static String getCardTransData(Map<String, ?> contentData,
			IsoMessage iso) {

		StringBuffer cardTransDataBuffer = new StringBuffer();
		Map<String, String> cardTransDataMap = new HashMap<String, String>();
		// 以下测试数据只是用来说明组装cardTransData域的基本步骤,真实数据请以实际业务为准
		String iCCardData = iso.getField(55); //IC卡信息域，IC卡必送，必须为从IC卡里读取，写死值的话cups解析时会出错会拒交易。
		String iCCardSeqNumber = iso.getField(23); //IC卡序列号，IC卡必送
		String track2Data = iso.getField(35);// 第二磁道数据
		String track3Data = iso.getField(36);// 第三磁道数据
		String cardProperty = iso.getField(22).substring(0,2);

		cardTransDataMap.put("carrierAppTp", "3");
		cardTransDataMap.put("carrierTp", "0");
		// 第二磁道数据 加密格式如下：merId|orderId|txnTime|txnAmt|track2Data
		if(StringUtils.isNotEmpty(track2Data)){
			StringBuffer track2Buffer = new StringBuffer();
			track2Buffer.append(contentData.get("merId"))
					.append(SDKConstants.COLON).append(contentData.get("orderId"))
					.append(SDKConstants.COLON).append(contentData.get("txnTime"))
					.append(SDKConstants.COLON).append(contentData.get("txnAmt")==null?0:contentData.get("txnAmt"))
					.append(SDKConstants.COLON).append(track2Data);
			String encryptedTrack2 = SDKUtil.encryptTrack(track2Buffer.toString(),
					encoding, modulus, exponent);
			// 生产如果提供了磁道加密公钥，可改为以下方式，读配置文件读公钥证书加密，只提供模和指数的情况请用上面那句加密。
	//		String encryptedTrack2 = SDKUtil.encryptTrack(track2Buffer.toString(),
	//				encoding);
			cardTransDataMap.put("track2Data", encryptedTrack2);
		}
		//判断是否为IC卡
		if((StringUtils.equals(cardProperty, "05")
				|| StringUtils.equals(cardProperty, "07")
				|| StringUtils.equals(cardProperty, "95")
				|| StringUtils.equals(cardProperty, "98")
				|| StringUtils.equals(cardProperty, "00"))
				&& StringUtils.isNotEmpty(iso.getField(55))){
			cardTransDataMap.put("ICCardSeqNumber", iCCardSeqNumber); //IC卡送，磁条卡不送。
			try {
				iCCardData = new String(SecureUtil.base64Encode(iCCardData.getBytes(encoding)), encoding);
				cardTransDataMap.put("ICCardData", iCCardData); //IC卡送，磁条卡不送。
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return cardTransDataBuffer.append(SDKConstants.LEFT_BRACE)
					.append(SDKUtil.coverMap2String(cardTransDataMap))
					.append(SDKConstants.RIGHT_BRACE).toString();
		}
		// 第三磁道数据 加密格式如下：merId|orderId|txnTime|txnAmt|track3Data
		if(StringUtils.isNotEmpty(track3Data)){
			StringBuffer track3Buffer = new StringBuffer();
			track3Buffer.append(contentData.get("merId"))
					.append(SDKConstants.COLON).append(contentData.get("orderId"))
					.append(SDKConstants.COLON).append(contentData.get("txnTime"))
					.append(SDKConstants.COLON).append(contentData.get("txnAmt")==null?0:contentData.get("txnAmt"))
					.append(SDKConstants.COLON).append(track3Data);
	
			String encryptedTrack3 = SDKUtil.encryptTrack(track3Buffer.toString(),
					encoding, modulus, exponent);
			cardTransDataMap.put("track3Data", encryptedTrack3);
		}
		return cardTransDataBuffer.append(SDKConstants.LEFT_BRACE)
				.append(SDKUtil.coverMap2String(cardTransDataMap))
				.append(SDKConstants.RIGHT_BRACE).toString();
	}
	/**
	 * 获取卡密码明文
	 * @param iso
	 * @return
	 */
	private static String getPin(IsoMessage iso){
		EncryptionAPI encryptionAPI = (EncryptionAPI) PospApplicationContext.getBean("EncryptionAPI");
		String pin = encryptionAPI.UnionDecryptPin(iso.getFlow().getUnipay52(), iso.getField(2));
		return pin;
	}
}
