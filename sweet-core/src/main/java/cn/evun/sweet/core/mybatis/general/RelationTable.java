package cn.evun.sweet.core.mybatis.general;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为通用Mapper提供简单的关联查询支持。
 * 
 * @author yangw
 * @since V1.0.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RelationTable {
	/**
	 * 指定外键关联查询的方式。包括left、right、all、inner,默认为inner。
	 */
	String join() default "inner";
	
	/**
	 * 作为从表被关联查询时的主表的字段，必须指定。
	 */
	String fkCol() default "";
	
	/**
	 * 作为从表被关联查询时的字段，必须指定。
	 */
	String col() default "";
	
}
