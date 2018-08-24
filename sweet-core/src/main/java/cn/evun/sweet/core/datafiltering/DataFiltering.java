package cn.evun.sweet.core.datafiltering;

import java.lang.annotation.*;

/**
 * 数据过滤注解，根据不同的终端过滤返回数据的字段<br/>
 * 可过滤需要的字段和排除的字段，支持数据加密
 *
 * @author shentao
 * @date 2017/5/4 10:27
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DataFiltering {

    /**
     * 终端和需要过滤的字段信息
     */
    SerializedField[] serializedFields() default {@SerializedField};

}
