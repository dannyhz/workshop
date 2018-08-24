/*
 * Copyright 2015-2019 Evun Technology. 
 * 
 * This software is the confidential and proprietary information of
 * Evun Technology. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with evun.cn.
 */
package cn.evun.sweet.core.mybatis.general;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记字段为使用模糊查询方式
 *
 * @author yangw
 * @since 1.0.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FuzzyQuery {

}
