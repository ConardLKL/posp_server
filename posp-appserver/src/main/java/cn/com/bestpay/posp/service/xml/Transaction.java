package cn.com.bestpay.posp.service.xml;

import cn.com.bestpay.posp.protocol.util.HexCodec;
import org.apache.commons.codec.binary.Base64;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import cn.com.bestpay.posp.system.entity.XmlMessage;

import java.io.UnsupportedEncodingException;

@Component("REQUEST")
public class Transaction extends BaseService{

	@Override
	public byte[] setMessage(XmlMessage xmlMessage) {
		return super.setMessage(xmlMessage);
	}
	@Override
	public void setBody(Element root,XmlMessage xmlMessage){
        Element body = root.addElement("BODY");  
        Element response = body.addElement("RESPONSE");
        Element iso8583 = response.addElement("ISO8583");
        String message = new String(Base64.encodeBase64(HexCodec.hexDecode(xmlMessage.getIso8583())));
        iso8583.setText(message);
        Element sysTrId = response.addElement("SysTrId");
        sysTrId.setText("");
        Element reserve = response.addElement("Reserve");
        reserve.setText("");
    } 

}
