package cn.com.bestpay.posp.system.entity;

import java.io.Serializable;


public class TSysPara implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String paraKey;
    private Long channelCode;
    private String paraRange;
    private String paraName;
    private String paraValues;
    private String paraDesc;
   
  	public void setParaKey(String paraKey){
  		this.paraKey = paraKey;
  	}
  	public String getParaKey(){
  		return this.paraKey;
  	}
  	public void setChannelCode(Long channelCode){
  		this.channelCode = channelCode;
  	}
  	public Long getChannelCode(){
  		return this.channelCode;
  	}
  	public void setParaRange(String paraRange){
  		this.paraRange = paraRange;
  	}
  	public String getParaRange(){
  		return this.paraRange;
  	}
  	public void setParaName(String paraName){
  		this.paraName = paraName;
  	}
  	public String getParaName(){
  		return this.paraName;
  	}
  	public void setParaValues(String paraValues){
  		this.paraValues = paraValues;
  	}
  	public String getParaValues(){
  		return this.paraValues;
  	}
  	public void setParaDesc(String paraDesc){
  		this.paraDesc = paraDesc;
  	}
  	public String getParaDesc(){
  		return this.paraDesc;
  	}
 
}
