package cn.evun.sweet.core.cache;

import java.lang.annotation.*;

import cn.evun.sweet.core.common.R;

/**
 * 该注解加于方法之上，表示该方法优先从缓存中取数据。如果缓存（过期）则从方法的返回结果取并再次填充缓存
 *
 * @author shentao
 * @since 1.1.1
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface UseCache {

    /**
     * 缓存key,缺省下会使用方法名
     */
    String key() default "";

    /**
     * 缓存存活时间等级,参见：R.cache.usecache_expire系列的定义
     */
    String expireLevel() default R.cache.usecache_expire_normal;

    /**
     * 缓存key是否包含当前登录用户的ID
     */
    boolean hasUserId() default true;
    
    /**
     * 需要排除的参数所在位置（从0开始）
     */
    String excludeParamsIndex() default "";
    
    /**
     * 自动识别第一页缓存（分页参数必须为pageNum）
     */
    boolean pageIndex() default true;

}
