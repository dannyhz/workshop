package cn.evun.sweet.core.validation;

import cn.evun.sweet.core.common.R;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防止表单重复提交的token注解
 * Created by Administrator on 2017/3/15.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {

    /**
     * token保存到缓存中的生存周期(秒)
     */
    long expire() default R.redisKey.token_expire;

}
