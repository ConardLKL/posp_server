package com.bestpay.posp.spring;

import java.io.File;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * 
 * @author DengPengHai
 * 
 */
@Slf4j
public final class PospApplicationContext {

	private static ApplicationContext ctx;
	
	public static Object getBean(String beanName) {
		return ctx.getBean(beanName);
	}

	public static ApplicationContext getCtx() {
		return ctx;
	}

	
	/**
	 * @param ctx the ctx to set
	 */
	public static void setCtx(ApplicationContext ctx) {
		PospApplicationContext.ctx = ctx;
	}

	private PospApplicationContext() {
	}

	public static void destroy() {
		if (ctx instanceof org.springframework.web.context.support.XmlWebApplicationContext) {
			org.springframework.web.context.support.XmlWebApplicationContext xmlWebCtx = (org.springframework.web.context.support.XmlWebApplicationContext) ctx;
			xmlWebCtx.registerShutdownHook();
		}
		if (ctx instanceof org.springframework.context.support.ClassPathXmlApplicationContext) {
			org.springframework.context.support.ClassPathXmlApplicationContext clXmlCtx = (org.springframework.context.support.ClassPathXmlApplicationContext) ctx;
			clXmlCtx.registerShutdownHook();
		}
	}

	public static void startup() {
		ctx = new ClassPathXmlApplicationContext(
				"classpath:/posp-core-spring-context.xml");
	}

	public static void startupTest() {
		String sfip_application_name = System
				.getProperty("sfip.application.name");
		if (sfip_application_name == null) {
			String classLoaderSystemResource = ClassLoader
					.getSystemResource("").getPath();
			log.debug("ClassLoader.getSystemResource(\"\").getPath()="
					+ classLoaderSystemResource);
			int pos = classLoaderSystemResource.indexOf("/target/");
			if (pos <= 0) {
				pos = classLoaderSystemResource.indexOf(".jar");
			}
			if (pos <= 0) {
				sfip_application_name = "sfip-app-xx";
			} else {
				sfip_application_name = classLoaderSystemResource.substring(0,
						pos);
			}
			pos = sfip_application_name.lastIndexOf("/");
			if (pos <= 0) {
				sfip_application_name = "sfip-app-xx";
			} else {
				sfip_application_name = sfip_application_name
						.substring(pos + 1);
			}
			System.setProperty("sfip.application.name", sfip_application_name);
		}
		log.debug("sfip_application_name=" + sfip_application_name);

		String logFileName = "../config/slf4j-" + sfip_application_name
				+ ".xml";
		File logFile = new File(logFileName);
		if (!logFile.exists()) {
			logFileName = "src/main/resources/slf4j-"
					+ sfip_application_name + ".xml";
			logFile = new File(logFileName);
		}
		if (!logFile.exists()) {
			logFileName = sfip_application_name + "/src/main/resources/slf4j-"
					+ sfip_application_name + ".xml";
			logFile = new File(logFileName);
		}

		if (!logFile.exists()) {
			logFileName = "../apps/" + sfip_application_name + "/config/slf4j-"
					+ sfip_application_name + ".xml";
			logFile = new File(logFileName);
		}

		LoggerContext loggerContext = (LoggerContext) StaticLoggerBinder
				.getSingleton().getLoggerFactory();
		loggerContext.reset();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(loggerContext);
		try {
			configurator.doConfigure(logFileName);
		} catch (JoranException e) {
			e.printStackTrace();
		}
		BasicConfigurator.configure(loggerContext);

		org.slf4j.Logger log = LoggerFactory.getLogger(PospApplicationContext.class
				.getName());
		log.info(sfip_application_name + " begin....");
		ctx = new ClassPathXmlApplicationContext(
				"classpath:/posp-core-test-spring-context.xml");
	}

	public static void main(String[] args) {
		String sfip_application_name = System
				.getProperty("sfip.application.name");
		if (sfip_application_name == null) {
			String classLoaderSystemResource = ClassLoader
					.getSystemResource("").getPath();
			log.debug("ClassLoader.getSystemResource(\"\").getPath()="
					+ classLoaderSystemResource);
			int pos = classLoaderSystemResource.indexOf("/target/");
			if (pos <= 0) {
				pos = classLoaderSystemResource.indexOf(".jar");
			}
			if (pos <= 0) {
				sfip_application_name = "sfip-app-xx";
			} else {
				sfip_application_name = classLoaderSystemResource.substring(0,
						pos);
			}
			pos = sfip_application_name.lastIndexOf("/");
			if (pos <= 0) {
				sfip_application_name = "posp-core";
			} else {
				sfip_application_name = sfip_application_name
						.substring(pos + 1);
			}
			System.setProperty("sfip.application.name", sfip_application_name);
		}
		log.debug("sfip_application_name=" + sfip_application_name);

		String logFileName = "../config/slf4j-" + sfip_application_name
				+ ".xml";
		File logFile = new File(logFileName);
		if (!logFile.exists()) {
			logFileName = "src/main/resources/slf4j-"
					+ sfip_application_name + ".xml";
			logFile = new File(logFileName);
		}
		if (!logFile.exists()) {
			logFileName = sfip_application_name + "/src/main/resources/slf4j-"
					+ sfip_application_name + ".xml";
			logFile = new File(logFileName);
		}

		if (!logFile.exists()) {
			logFileName = "../apps/" + sfip_application_name + "/config/slf4j-"
					+ sfip_application_name + ".xml";
			logFile = new File(logFileName);
		}

		if (!logFile.exists()) {
			logFileName = "../config/slf4j-posp-core.xml";
			logFile = new File(logFileName);
		}

		LoggerContext loggerContext = (LoggerContext) StaticLoggerBinder
				.getSingleton().getLoggerFactory();
		loggerContext.reset();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(loggerContext);
		try {
			configurator.doConfigure(logFileName);
		} catch (JoranException e) {
			e.printStackTrace();
		}
		BasicConfigurator.configure(loggerContext);

		org.slf4j.Logger log = LoggerFactory.getLogger(PospApplicationContext.class
				.getName());
		log.info(sfip_application_name + " begin....");

		PospApplicationContext.startup();
	}

	
}
