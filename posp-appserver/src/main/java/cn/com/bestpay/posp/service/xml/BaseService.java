package cn.com.bestpay.posp.service.xml;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import cn.com.bestpay.posp.protocol.XmlAnalysis;
import cn.com.bestpay.posp.system.entity.XmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class BaseService implements XmlService {

	private static final Logger log = LoggerFactory.getLogger(BaseService.class);
	@Override
	public byte[] setMessage(XmlMessage xmlMessage) {
		try {
			Document document = DocumentHelper.createDocument();// 创建根节点  
		    document.setXMLEncoding("GBK");
		    Element root = document.addElement("ROOT");
		    XmlAnalysis.setHead(root, xmlMessage.getHead());
		    this.setBody(root,xmlMessage);
			log.info(String.format(" response:" + document.asXML()));
		    return Base64.encodeBase64(document.asXML().getBytes("GBK"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setBody(Element root,XmlMessage xmlMessage) {

	}

}
