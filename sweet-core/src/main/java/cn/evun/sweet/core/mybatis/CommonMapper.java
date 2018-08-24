/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package cn.evun.sweet.core.mybatis;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import cn.evun.sweet.core.mybatis.general.CommonProvider;

/**
 * 通用的DAO操作，根据参数实体直接进行DAO操作。增加了对二级关键查询的支持。
 * @link https://github.com/abel533/Mapper 
 *
 * @author liuzh,yangw
 */
public interface CommonMapper {
    /**
     * 根据参数进行查询,record可以是Class<?>类型或实体对象<br>
     * 查询条件为非空的String属性，或非null的其他属性；例如：where property = ? and property2 = ?.
     * relation为true时，表示启动关联查询功能。
     */
    @Options(flushCache = true)
    @SelectProvider(type = CommonProvider.class, method = "select")
    <T> List<Map<String,Object>> select(@Param("record") T record, @Param("relation") boolean relation);

    /**
     * 根据主键查询结果，主键不能为null或空
     */
    @Options(flushCache = true)
    @SelectProvider(type = CommonProvider.class, method = "selectByPrimaryKey")
    <T> Map<String,Object> selectByPrimaryKey(@Param("entityClass") Class<T> entityClass, 
    		@Param("key") Object key, @Param("relation") boolean relation);

    /**
     * 通过Example来查询
     */
    @Options(flushCache = true)
    @SelectProvider(type = CommonProvider.class, method = "selectByExample")
    <T> List<Map<String,Object>> selectByExample(@Param("entityClass") Class<T> entityClass, @Param("example") Object example);

    /**
     * 根据参数进行查询总数统计,record可以是Class<?>类型或实体对象<br>
     * 查询条件为非空的String属性，或非null的其他属性；例如：where property = ? and property2 = ?
     */
    @Options(flushCache = true)
    @SelectProvider(type = CommonProvider.class, method = "count")
    <T> int count(@Param("record") T record, @Param("relation") boolean relation);

    /**
     * 通过Example类来查询count
     */
    @Options(flushCache = true)
    @SelectProvider(type = CommonProvider.class, method = "countByExample")
    <T> int countByExample(@Param("entityClass") Class<T> entityClass, @Param("example") Object example);

}