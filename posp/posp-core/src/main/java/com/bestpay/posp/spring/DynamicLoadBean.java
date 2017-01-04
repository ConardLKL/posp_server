/**
 * 
 */
package com.bestpay.posp.spring;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ChildBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author yihaijun
 * 
 */
public class DynamicLoadBean implements ApplicationContextAware {

	private ConfigurableApplicationContext applicationContext = null;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {

		this.applicationContext = (ConfigurableApplicationContext) applicationContext;
	}

	public ConfigurableApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * @param configLocationString
	 */
	public void loadBean(String fileName) {
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(
				(BeanDefinitionRegistry) getApplicationContext()
						.getBeanFactory());
		beanDefinitionReader.setResourceLoader(getApplicationContext());
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(
				getApplicationContext()));
		try {
			beanDefinitionReader.loadBeanDefinitions(getApplicationContext()
					.getResources(fileName));
		} catch (Exception e) {
			e.printStackTrace();
			
			FileInputStream ifs = null;
			org.xml.sax.InputSource is = null;
			try {
				ifs = new  FileInputStream(fileName);
				is = new org.xml.sax.InputSource(ifs);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
//			FileSystemResouce fsr = new FileSystemResouce();
			beanDefinitionReader.loadBeanDefinitions(is);
		}
	}

	public void registBean(String beanName, String parentName) {
		DefaultListableBeanFactory fcy = (DefaultListableBeanFactory) applicationContext
				.getAutowireCapableBeanFactory();
		BeanDefinition beanDefinition = new ChildBeanDefinition(parentName);
		fcy.registerBeanDefinition(beanName, beanDefinition);
	}
}