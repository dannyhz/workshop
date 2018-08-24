package cn.evun.sweet.core.service;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodFilter;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.common.util.reflect.MethodInvoker;
import cn.evun.sweet.core.spring.AbstractCustomAnnotationScaner;

/**
 * RegistyService注解的扫描器，负责对扫描到的RegistyMethod进行注册登记。<br/>
 * 
 * @author yangw
 * @since V1.0.0
 */
@Component
public class RegistyServiceScaner extends AbstractCustomAnnotationScaner {

	@Override
	protected boolean isMatchBean(Class<?> beanType) {
		return AnnotationUtils.findAnnotation(beanType, RegistyService.class) != null;
	}

	@Override
	protected void doSomething(String beanName, Object bean) {		
		Set<Method> methods = selectMethods(ClassUtils.getUserClass(bean.getClass()), new MethodFilter() {
			@Override
			public boolean matches(Method method) {
				return AnnotationUtils.findAnnotation(method, RegistyMethod.class) != null;
			}
		});
		
		/*注册服务所在域*/
		RegistyService servAnnotation = AnnotationUtils.findAnnotation(bean.getClass(), RegistyService.class);
		String header = servAnnotation.value();
		if(StringUtils.hasText(header)){
			header = header+".";
		}
		
		/*注册服务*/
		for (Method method : methods) {
			RegistyMethod anno = method.getAnnotation(RegistyMethod.class);
			MethodInvoker service = new MethodInvoker();
			service.setTargetObject(bean);
			service.setPreparedMethod(method);
			RegistyServiceInvoker.getInstance().registyService(header+anno.value(), service);
		}
	}
	
	private Set<Method> selectMethods(final Class<?> handlerType, final MethodFilter methodFilter) {
		final Set<Method> targetMethods = new LinkedHashSet<Method>();
		Set<Class<?>> handlerTypes = new LinkedHashSet<Class<?>>();
		Class<?> specificHandlerType = null;
		if (!Proxy.isProxyClass(handlerType)) {//非动态代理生成的类
			handlerTypes.add(handlerType);
			specificHandlerType = handlerType;
		}
		handlerTypes.addAll(Arrays.asList(handlerType.getInterfaces()));
		for (Class<?> currentHandlerType : handlerTypes) {
			final Class<?> targetClass = (specificHandlerType != null ? specificHandlerType : currentHandlerType);
			ReflectionUtils.doWithMethods(currentHandlerType, new ReflectionUtils.MethodCallback() {
				@Override
				public void doWith(Method method) {
					Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
					Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
					if (methodFilter.matches(specificMethod) &&
							(bridgedMethod == specificMethod || !methodFilter.matches(bridgedMethod))) {
						targetMethods.add(specificMethod);
					}
				}
			}, ReflectionUtils.USER_DECLARED_METHODS);
		}
		return targetMethods;
	}

}
