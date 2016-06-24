package cn.com.bestpay.posp.service.xml;

import org.dom4j.Element;
import cn.com.bestpay.posp.system.entity.XmlMessage;

public interface XmlService {

	/**
	 * 设置xml报文
	 * @param xmlMessage
	 * @return
	 */
	public byte[] setMessage(XmlMessage xmlMessage);
	/**
	 * 设置xml报文body
	 * @param root
	 * @return
	 */
	public void setBody(Element root,XmlMessage xmlMessage);
}
