package cn.evun.sweet.core.spring;

import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

/**   
 * 以静态变量保存Spring ApplicationContext,统一并简化ApplicaitonContext相关操作.<br/>
 * 使用方式：ApplicationContext.getBean('xxxx');
 * 
 * @author yangw   
 * @since V1.0.0   
 */
public class SpringContext implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;
	
	private static ServletContext servletContext;

	/**
	 * 实现ApplicationContextAware接口,注入目标.
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		if (null == SpringContext.applicationContext) {
			SpringContext.applicationContext = applicationContext;
		}
	}

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		checkApplicationContext();
		return applicationContext;
	}

	private static void checkApplicationContext() {
		if (applicationContext == null) {		
			throw new IllegalStateException("No applicaitonContext found, please check if SpringContextHolder has bean defined!");
		}
	}
	
	/**
	 * 根据name获取Bean,自动转型为所赋值对象的类型
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		checkApplicationContext();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 根据class获取Bean,自动转型为所赋值对象的类型
	 */
	public static <T> T getBean(Class<T> clazz) {
		checkApplicationContext();
		return (T) applicationContext.getBean(clazz);
	}

	/**
	 * 根据class获取系列Bean,自动转型为所赋值对象的类型
	 */
	public static <T>  Map<String,T>  getBeans(Class<T> clazz) {
		checkApplicationContext();
		return applicationContext.getBeansOfType(clazz);
	}
	
	/**
	 * 清楚所持有的applicationContext.
	 */
	public static void cleanApplicationContext() {
		applicationContext = null;
	}

	public static String getServletContextName(){
		if(getServletContext() != null){
			return servletContext.getServletContextName();
		}
		return "";
	}
	
	public static ServletContext getServletContext(){
		if(getApplicationContext() instanceof WebApplicationContext){
			servletContext = ((WebApplicationContext)getApplicationContext()).getServletContext();
		}
		return servletContext;
	}

}