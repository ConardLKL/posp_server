package cn.com.bestpay.posp.service.xml;

import org.dom4j.Element;
import org.springframework.stereotype.Component;

import cn.com.bestpay.posp.system.entity.XmlMessage;
@Component("0101")
public class SMSValidationCode extends BaseService {

	@Override
	public byte[] setMessage(XmlMessage xmlMessage) {
		return super.setMessage(xmlMessage);
	}
	@Override
	public void setBody(Element root,XmlMessage xmlMessage) {

	}

}
