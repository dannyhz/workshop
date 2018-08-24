package cn.evun.sweet.core.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;

/**
 * 告诉容器，该注解标记的服务类将会有需要注册到服务调用器中的方法。<br/>
 * 主要配合RegistyMethod一起使用
 * 
 * @author yangw
 * @since V1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface RegistyService {
	
	/*注册服务所在域，可以为空*/
	String value() default "";
}
