package com.bestpay.posp.protocol;


/*
 * @author   PengGuoYi  
 * @version  1.0  2014/06/13 
 */
public interface IMessage {
	void setMessageDefine(PKT_DEF[] def) throws Exception;
	PKT_DEF[] getMessageDefine() ;
	void setMessage(byte [] data) throws Exception;
	void setMessageWithHex(String str) throws Exception;
	byte [] getMessage() throws Exception;
	String getMessageWithHex() throws Exception;
	String getField(int index);
	String getField2(int index);
	void setField(int index, String value) throws Exception;
	void printMessage(String id);
	void printMessage();
}