package cn.evun.sweet.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;

/**
 * 自定义的注解探查器。在容器加载时，根据某些自定义的类和方法注解，完成相关的处理。<br/>
 * 
 * @author yangw
 * @since V1.0.0
 */
public abstract class AbstractCustomAnnotationScaner implements InitializingBean, ApplicationContextAware {
	
	private boolean detectInAncestorContexts = false;
	
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		if (null == this.applicationContext) {
			this.applicationContext = applicationContext;
		}
	} 

	@Override
	public void afterPropertiesSet() throws Exception {
		String[] beanNames = (this.detectInAncestorContexts ?
				BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext, Object.class) :
					applicationContext.getBeanNamesForType(Object.class));
		for (String beanName : beanNames) {
			if (!beanName.startsWith("scopedTarget.")){
				Object beanObj = null;
				try {
					beanObj = applicationContext.getBean(beanName);
				} catch (BeansException e) {
					continue;
				}
				if(beanObj != null && isMatchBean(ClassUtils.getUserClass(beanObj.getClass()))){
					doSomething(beanName, beanObj);
				}
			}
		}

	}
	
	protected abstract boolean isMatchBean(Class<?> beanType);
	
	protected abstract void doSomething(String beanName, Object beanObj);


}
