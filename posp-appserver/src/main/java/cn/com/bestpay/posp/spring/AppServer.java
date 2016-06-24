package cn.com.bestpay.posp.spring;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppServer extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private static ClassPathXmlApplicationContext ctx;
	
	public static Object getBean(String beanName) {
		return ctx.getBean(beanName);
	}

	public static ApplicationContext getCtx() {
		return ctx;
	}
	
	public static void serverDestroy() {
		ctx.registerShutdownHook();
	}
	@Override
	public void init() throws ServletException {
		
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		ctx = new ClassPathXmlApplicationContext(
				"classpath:/app-server-spring-context.xml");
	}
}
