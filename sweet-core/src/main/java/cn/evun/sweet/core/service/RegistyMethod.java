package cn.evun.sweet.core.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配合RegistyService一起使用。所有标记该注解的方法将会注册到容器中，
 * 可以通过RegistyServiceInvoker直接根据注册标识进行调用。
 * 
 * @author yangw
 * @since V1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RegistyMethod {
	/**
	 * 指定注册的服务标识，以便通过标识直接调用服务方法。
	 */
	String value() default "";
}
