package cn.com.bestpay.posp.system.cache;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.bestpay.posp.constant.SysConstant;
import cn.com.bestpay.posp.system.dao.TSysParaDao;
import cn.com.bestpay.posp.system.entity.TSysPara;


/**
 * 参数配置缓存
 *
 */
@Component
public class ConfigCache extends HashMap<Long, HashMap<String, String>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6628445908957747018L;
	private static final Logger log = LoggerFactory.getLogger(ConfigCache.class);
	@Autowired
	private TSysParaDao sysParaDao;
 
	
	private ConfigCache(){
	}
	
	@SuppressWarnings("unused")
	private void cache(){
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
			this.put(channel, cache);
		}
		log.info("已加载……");
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
}
