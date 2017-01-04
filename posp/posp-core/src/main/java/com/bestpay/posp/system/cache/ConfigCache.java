package com.bestpay.posp.system.cache;

import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.system.dao.TSysParaDao;
import com.bestpay.posp.system.entity.TSysPara;
import com.bestpay.posp.utils.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;


/**
 * 参数配置缓存
 *
 */
public class ConfigCache extends HashMap<Long, HashMap<String, String>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6628445908957747018L;
	
	@Autowired
	private TSysParaDao sysParaDao;
 
	
	private ConfigCache(){
	}
	
	public void cache(){
		for (Long channel : SysConstant.CHANNELS) {
			TSysPara tsp = new TSysPara();
			tsp.setChannelCode(channel);
			List<TSysPara> list = sysParaDao.find(tsp);
			if(list == null || list.size() == 0){
				continue;
			}
			HashMap<String, String> cache = new HashMap<String, String>();
			for (TSysPara tSysPara : list) {
				cache.put(tSysPara.getParaKey(), tSysPara.getParaValues());
			}
			setCache(cache,channel);
			this.put(channel, cache);
		}
	}
	
	public String getParaRange(Long channelCode, String paraValue){
		if(this.get(channelCode) == null) return null;
		TSysPara tsp = new TSysPara();
		tsp.setChannelCode(channelCode);
		tsp.setParaValues(paraValue);
		TSysPara tSysPara = sysParaDao.findUnique(tsp);
		return tSysPara.getParaRange();
	}
	
	public String getParaValues(Long channelCode, String paraKey){
		if(this.get(channelCode) == null) return null;
		return this.get(channelCode).get(paraKey);
	}
	
	public HashMap<String, String> getParaValues(Long channelCode){
		if(this.get(channelCode) == null) return null;
		HashMap<String, String> values = this.get(channelCode);
		return values;
	}

	/**
	 * 设置缓存
	 * @param cache
     */
	private void setCache(HashMap<String, String> cache,Long channel){
		if(channel.equals(Long.valueOf(8001))) {
			PropertiesUtil propertiesUtil = new PropertiesUtil("sys.properties");
			cache.put("union.ip", propertiesUtil.getProperty("union.ip"));
			cache.put("union.port", propertiesUtil.getProperty("union.port"));
			cache.put("union.id", propertiesUtil.getProperty("union.id"));
			cache.put("union.timeOut", propertiesUtil.getProperty("union.timeOut"));
			cache.put("union.conn", propertiesUtil.getProperty("union.conn"));
		}
	}
	/**
	 * 获取受理机构标识码
	 * @return
	 */
	public String getAcquiringInstitution(){
		return getParaValues(SysConstant.CL1, "000020");
	}
	/**
	 * 获取发送机构标识码
	 * @return
	 */
	public String getSendingInstitution(){
		return getParaValues(SysConstant.CL1, "000021");
	}
	/**
	 * 获取电话接入指定tpdu
	 * @return
	 */
	public String getTelephoneAccessTpdu(){
		return getParaValues(SysConstant.CL7001, "100014");
	}
	/**
	 * 获取第三方平台接入指定tpdu
	 * @return
	 */
	public String getThirdPartyPlatformTpdu(){
		return getParaValues(SysConstant.CL3001, "300001");
	}
	
}
