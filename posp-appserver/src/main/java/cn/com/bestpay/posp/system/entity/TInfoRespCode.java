package cn.com.bestpay.posp.system.entity;

import java.io.Serializable;

public class TInfoRespCode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pospCode;
	private String pospDesc;
    private String respCode;
    private String respDesc;
	private String extsysRespCode;
    private Long channelCode;
    private String extsysRespDesc;
   
    
    public String getPospCode() {
		return pospCode;
	}
	public void setPospCode(String pospCode) {
		this.pospCode = pospCode;
	}
	public String getPospDesc() {
		return pospDesc;
	}
	public void setPospDesc(String pospDesc) {
		this.pospDesc = pospDesc;
	}
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
  	public void setRespCode(String respCode){
  		this.respCode = respCode;
  	}
  	public String getRespCode(){
  		return this.respCode;
  	}
  	public void setExtsysRespCode(String extsysRespCode){
  		this.extsysRespCode = extsysRespCode;
  	}
  	public String getExtsysRespCode(){
  		return this.extsysRespCode;
  	}
  	public void setChannelCode(Long channelCode){
  		this.channelCode = channelCode;
  	}
  	public Long getChannelCode(){
  		return this.channelCode;
  	}
  	public void setExtsysRespDesc(String extsysRespDesc){
  		this.extsysRespDesc = extsysRespDesc;
  	}
  	public String getExtsysRespDesc(){
  		return this.extsysRespDesc;
  	}
 
}
