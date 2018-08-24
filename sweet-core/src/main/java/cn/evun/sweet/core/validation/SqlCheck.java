package cn.evun.sweet.core.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 通过执行sql语句进行验证
 *
 * @author yangw
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})  
@Retention(RetentionPolicy.RUNTIME) 
@Constraint(validatedBy = SqlCheckValidator.class) 
@Documented 
public @interface SqlCheck {
	
	/**默认错误消息提示*/  
    String message() default "{validator.sql}";  
    
    /**需要用于验证的sql的注册id号*/
    String sqlid() ;
    
    /**验证结果的spel表达式，计算结果必须是布尔值*/
    String SpEL() ;
    
    /**分组*/  
    Class<?>[] groups() default { };  
  
    /**负载*/  
    Class<? extends Payload>[] payload() default { };  
  
    /**指定多个时使用*/  
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})  
    @Retention(RetentionPolicy.RUNTIME)  
    @Documented  
    @interface List {  
    	SqlCheck[] value();  
    }  
}
