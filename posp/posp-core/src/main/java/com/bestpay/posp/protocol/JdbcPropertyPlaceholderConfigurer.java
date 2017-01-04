package com.bestpay.posp.protocol;

import com.bestpay.posp.protocol.util.AESUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by HR on 2016/7/27.
 */
public class JdbcPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        Enumeration<?> keys = props.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = props.getProperty(key);
            if (key.endsWith(".encrypt") && null != value) {
                props.remove(key);
                key = key.substring(0, key.length() - 8);
                value = AESUtils.decrypt(value.trim());
                props.setProperty(key, value);
            }
            System.setProperty(key, value);
        }

        super.processProperties(beanFactoryToProcess, props);
    }
}
