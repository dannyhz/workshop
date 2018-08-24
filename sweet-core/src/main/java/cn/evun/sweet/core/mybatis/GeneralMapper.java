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

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import cn.evun.sweet.common.util.Assert;
import cn.evun.sweet.core.mybatis.common.EntityHelper;
import cn.evun.sweet.core.mybatis.general.Example;

/**
 * 对外提供统一的泛型对象映射到数据库操作的支持。类似于Hibernate的session功效。 
 *
 * @author yangw
 */
@Repository
public class GeneralMapper {

    @Resource
    private CommonMapper commonMapper;

    /**
     * 根据参数进行查询,record可以是Class<?>类型或JavaBean(where条件时使用)<br>
     * 查询条件为非空的String属性，或非null的其他属性；例如：where property = ? and property2 = ?.
     * 不启用关联查询功能。
     */
    @SuppressWarnings("unchecked")
	public <T> List<T> select(T record) {
        Assert.notNull(record, "record must not be null");
        return (List<T>) EntityHelper.maplist2BeanList(commonMapper.select(record, false), record.getClass());
    }
    
    /**
     * 根据参数进行查询,record可以是Class<?>类型或实体对象<br>
     * 查询条件为非空的String属性，或非null的其他属性；例如：where property = ? and property2 = ?.
     * 启动关联查询功能。
     */
    @SuppressWarnings("unchecked")
	public <T> List<T> selectWithRelation(T record) {
        Assert.notNull(record, "record must not be null");
        return (List<T>) EntityHelper.maplist2BeanList(commonMapper.select(record, true), record.getClass());
    } 

    /**
     * 根据主键查询结果，主键不能为null或空.不启用关联查询
     */
    @SuppressWarnings("unchecked")
	public <T> T selectByPrimaryKey(Class<T> entityClass, Object key) {
        return (T) EntityHelper.map2Bean(commonMapper.selectByPrimaryKey(entityClass, key, false), entityClass);
    }
    
    /**
     * 根据主键查询结果，主键不能为null或空。启用关联查询
     */
    @SuppressWarnings("unchecked")
	public <T> T selectByPrimaryKeyWithRelation(Class<T> entityClass, Object key) {
        return (T) EntityHelper.map2Bean(commonMapper.selectByPrimaryKey(entityClass, key, true), entityClass);
    }
    
    /**
     * 通过Example来查询，不支持关联查询
     */
    @SuppressWarnings("unchecked")
	public <T> List<T> selectByExample(Class<T> entityClass, Object example) {
        return (List<T>) EntityHelper.maplist2BeanList(commonMapper.selectByExample(entityClass, example), entityClass);
    }
    
    /**
     * 通过Example来查询，不支持关联查询
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> selectByExample(Example example) {
    	Assert.notNull(example, "example must not be null");
        return (List<T>) EntityHelper.maplist2BeanList(commonMapper.selectByExample(example.getEntityClass(), example), example.getEntityClass());
    }
    
    /**
     * 根据参数进行查询总数统计,record可以是Class<?>类型或实体对象<br>
     * 查询条件为非空的String属性，或非null的其他属性；例如：where property = ? and property2 = ?
     * 不启用关联查询
     */
    public <T> int count(T record) {
        return commonMapper.count(record, false);
    }
    
    /**
     * 根据参数进行查询总数统计,record可以是Class<?>类型或实体对象<br>
     * 查询条件为非空的String属性，或非null的其他属性；例如：where property = ? and property2 = ?
     * 启用关联查询
     */
    public <T> int countWithRelation(T record) {
        return commonMapper.count(record, true);
    }
    
    /**
     * 通过Example类来查询count。不支持关联查询
     */
    public <T> int countByExample(Class<T> entityClass, Object example) {
        return commonMapper.countByExample(entityClass, example);
    }
    
    /**
     * 通过Example类来查询count。不支持关联查询
     */
    public <T> int countByExample(Example example) {
    	Assert.notNull(example, "example must not be null");
        return commonMapper.countByExample(example.getEntityClass(), example);
    }
    
}
