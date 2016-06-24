package cn.com.bestpay.posp.system.entity;

import java.io.Serializable;

import org.dom4j.Element;

/**
 * 
 * @author HR
 *
 */
public class XmlMessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private XmlHead head;
	private Element body;
	private String tranCode;
	private String iso8583;
	private String channelCode;
	private String rspCode;
	private String rspMsg;
	private String serialNo;
	
	public String getTranCode() {
		return tranCode;
	}
	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}
	public String getIso8583() {
		return iso8583;
	}
	public void setIso8583(String iso8583) {
		this.iso8583 = iso8583;
	}
	public Element getBody() {
		return body;
	}
	public void setBody(Element body) {
		this.body = body;
	}
	public XmlHead getHead() {
		return head;
	}
	public void setHead(XmlHead head) {
		this.head = head;
	}
	public String getRspCode() {
		return rspCode;
	}
	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}
	public String getRspMsg() {
		return rspMsg;
	}
	public void setRspMsg(String rspMsg) {
		this.rspMsg = rspMsg;
	}
	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}


}
