package cn.com.bestpay.posp.service.xml;

import org.dom4j.Element;
import org.springframework.stereotype.Component;

import cn.com.bestpay.posp.system.entity.XmlMessage;

@Component("1002")
public class UserLogin extends BaseService{

	@Override
	public byte[] setMessage(XmlMessage xmlMessage) {
		return super.setMessage(xmlMessage);
	}
	@Override
	public void setBody(Element root,XmlMessage xmlMessage){ 
        Element body = root.addElement("BODY");  
        Element rsp1002 = body.addElement("RSP1002");  
        Element userID = rsp1002.addElement("UserID");
        userID.setText("");
        Element nickName = rsp1002.addElement("NickName");
        nickName.setText("");
        Element email = rsp1002.addElement("Email");
        email.setText("");
        Element merchName = rsp1002.addElement("MerchName");
        merchName.setText("");
        Element address = rsp1002.addElement("Address");
        address.setText("");
    } 

}
