package cn.evun.sweet.core.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 验证全局唯一.针对编辑情况，此时需要排除正在编辑的记录。
 *
 * @author yangw
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})  
@Retention(RetentionPolicy.RUNTIME) 
@Constraint(validatedBy = GlobalEditUniqueValidator.class) 
@Documented 
public @interface GlobalEditUnique {
	
	/**默认错误消息提示*/  
    String message() default "{validator.globalunique}";  
    
    /**需要验证的模型别名，例如：user*/
    String model() ;
    
    /**需要验证的属性名，例如：userMobile*/
    String property() ;
    
    /**用于验证存在性的辅助条件列表，例如：{"userIsDel"}*/
    String[] conditions() default {};
    
    /**用于验证存在性的辅助条件值列表，例如：{"0"}*/
    String[] conditionValues() default {};
  
    /**分组*/  
    Class<?>[] groups() default { };  
  
    /**负载*/  
    Class<? extends Payload>[] payload() default { };  
  
    /**指定多个时使用*/  
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})  
    @Retention(RetentionPolicy.RUNTIME)  
    @Documented  
    @interface List {  
    	GlobalUnique[] value();  
    }  
}
