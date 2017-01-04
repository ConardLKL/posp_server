/**
 * 
 */
package com.bestpay.posp.protocol.util;

import java.util.Hashtable;

import com.bestpay.posp.protocol.IsoMessage;
import org.springframework.stereotype.Component;

import com.tisson.sfip.api.message.transformer.SfipBeanTransformerInf;

/**
 *
 */
@Component
public class PospSfipBeanTransformerImpl implements SfipBeanTransformerInf {
	public Object doTransform(String beanParam, Object payload,
			Hashtable runVarSet,String currKeyfix) {
		if(!(payload instanceof IsoMessage)){
			return payload;
		}
		String key = beanParam.substring(0,beanParam.indexOf(":"));
		String value = beanParam.substring(beanParam.indexOf(":") + 1);
		IsoMessage object = (IsoMessage)payload;
		object.getTransformerTmpField().put(key, value);
		return object;
	}

}
