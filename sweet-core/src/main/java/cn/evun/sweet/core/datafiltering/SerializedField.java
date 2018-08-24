package cn.evun.sweet.core.datafiltering;

import java.lang.annotation.*;

/**
 * 需要过滤的字段信息注解<br/>
 * includes和excludes两个属性不能同时有值，否则会造成逻辑混乱
 *
 * @author shentao
 * @date 2017/5/4 10:22
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SerializedField {

    Terminal terminal() default Terminal.PC;

    /**
     * 需要返回的字段
     */
    String[] includes() default {};

    /**
     * 需要排除的字段
     */
    String[] excludes() default {};

    /**
     * 数据是否需要加密
     */
    boolean encode() default false;

}
