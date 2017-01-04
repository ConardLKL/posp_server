package com.bestpay.posp.system.cache;

import java.util.HashMap;
import java.util.List;

import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.system.dao.TInfoRespCodeDao;
import com.bestpay.posp.system.entity.TInfoRespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 错误响应码缓存
 *
 */
public final class RespCodeCache extends HashMap<Long, HashMap<String, TInfoRespCode>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6841886823310039253L;
	
	
	@Autowired
	private TInfoRespCodeDao respCodeDao;
	
	
	private RespCodeCache(){
	}

	public void cache(){
		for (Long channel : SysConstant.CHANNELS) {
			TInfoRespCode entity = new TInfoRespCode();
			entity.setChannelCode(channel);
			List<TInfoRespCode> list = respCodeDao.find(entity);
			if(list == null || list.size() == 0){
				continue;
			}
			HashMap<String, TInfoRespCode> cache = new HashMap<String, TInfoRespCode>();
			for (TInfoRespCode tInfoRespCode : list) {
				cache.put(tInfoRespCode.getPospCode(), tInfoRespCode);
			}
			this.put(channel, cache);
		}
	}
	
	public TInfoRespCode getCode(Long channelCode, String pospCode){
		if(this.get(channelCode) == null) return null;
		return this.get(channelCode).get(pospCode);
	}
	
}
