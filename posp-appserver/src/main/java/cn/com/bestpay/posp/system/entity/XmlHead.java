package cn.com.bestpay.posp.system.entity;

import java.io.Serializable;

public class XmlHead implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String app;
	private String ver;
	private String msgID;
	private String msgRef;
	private String trCode;
	private String workDate;
	private String workTime;
	private String IMSI;
	private String IMEI;
	private String operID;
	private String session;
	private String rspCode;
	private String rspMsg;
	private String reserve;
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public String getMsgID() {
		return msgID;
	}
	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}
	public String getMsgRef() {
		return msgRef;
	}
	public void setMsgRef(String msgRef) {
		this.msgRef = msgRef;
	}
	public String getTrCode() {
		return trCode;
	}
	public void setTrCode(String trCode) {
		this.trCode = trCode;
	}
	public String getWorkDate() {
		return workDate;
	}
	public void setWorkDate(String workDate) {
		this.workDate = workDate;
	}
	public String getWorkTime() {
		return workTime;
	}
	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}
	public String getOperID() {
		return operID;
	}
	public void setOperID(String operID) {
		this.operID = operID;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
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
	public String getReserve() {
		return reserve;
	}
	public void setReserve(String reserve) {
		this.reserve = reserve;
	}
	public String getIMSI() {
		return IMSI;
	}
	public void setIMSI(String iMSI) {
		IMSI = iMSI;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	

}
