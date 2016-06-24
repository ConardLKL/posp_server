package cn.com.bestpay.posp.service.xml;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import cn.com.bestpay.posp.system.entity.XmlMessage;

@Component("0100")
public class VersionCheck extends BaseService{

	@Override
	public byte[] setMessage(XmlMessage xmlMessage) {
		return super.setMessage(xmlMessage);
	}
	@Override
	public void setBody(Element root,XmlMessage xmlMessage){ 
		String dataDesc = "0";
        Element body = root.addElement("BODY");  
        Element rsp0100 = body.addElement("RSP0100");  
        Element isUpdate = rsp0100.addElement("IsUpdate");
        isUpdate.setText(dataDesc);
        if(StringUtils.equals(dataDesc, "0")){
        	Element verDownInfo = rsp0100.addElement("VerDownInfo");
        	Element ver = verDownInfo.addElement("Ver");
        	ver.setText("");
        	Element updateFlag = verDownInfo.addElement("UpdateFlag");
        	updateFlag.setText("");
        	Element url = verDownInfo.addElement("URL");
        	url.setText("");
        	Element desc = verDownInfo.addElement("Desc");
        	desc.setText("");
        }
    } 

}
