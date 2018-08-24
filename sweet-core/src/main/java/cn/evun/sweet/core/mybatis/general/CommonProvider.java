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

package cn.evun.sweet.core.mybatis.general;

import java.util.Map;

import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.reflection.MetaObject;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.mybatis.common.EntityHelper;
import cn.evun.sweet.core.mybatis.common.MapperTemplate;

/**
 * 通用的DAO操作，根据参数实体直接进行DAO操作。增加了对二级关联查询的支持。
 * @link https://github.com/abel533/Mapper
 *
 * @author liuzh，yangw
 */
public class CommonProvider extends BaseProvider {

	/**
     * 查询，入参可以是Entity.class或new Entity()
     */
    public String select(final Map<String, Object> params) {
        return new SQL() {{
            Object entity = getEntity(params);
            Class<?> entityClass = getEntityClass(params);
            EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
            boolean relation = (boolean)params.get("relation");
            if(relation){//启用关联查询
            	StringBuilder allcols = new StringBuilder(EntityHelper.getAllColumns(entityClass));
            	for(EntityHelper.EntityTable tab : entityTable.getEntityClassFKColumns()){
            		allcols.append(",").append(EntityHelper.getAllColumns(tab));
            	}
            	SELECT(allcols.toString());
            	FROM(entityTable.getName()+" "+entityTable.getSchema());//别名
            	for(EntityHelper.EntityTable tab : entityTable.getEntityClassFKColumns()){
            		String joinStr = tab.getName()+" "+tab.getSchema()+" on "+tab.getSchema()+"."+entityTable.getFKCol(tab)
            				+"="+entityTable.getSchema()+"."+entityTable.getCol(tab);
            		if("left".equalsIgnoreCase(entityTable.getJoin(tab))){
            			LEFT_OUTER_JOIN(joinStr);
            		}else if("right".equalsIgnoreCase(entityTable.getJoin(tab))){
            			RIGHT_OUTER_JOIN(joinStr);
            		}else if("all".equalsIgnoreCase(entityTable.getJoin(tab))){
            			OUTER_JOIN(joinStr);
            		}else {
            			INNER_JOIN(joinStr);
            		}
            	}
            }else{
            	SELECT(EntityHelper.getAllColumns(entityClass));
            	FROM(entityTable.getName()+" "+entityTable.getSchema());
            }
            
            if (entity != null) {
                final MetaObject metaObject = MapperTemplate.forObject(entity);
                for (EntityHelper.EntityColumn column : entityTable.getEntityClassColumns()) {
                    Object value = metaObject.getValue(column.getProperty());
                    if (value == null) {
                        continue;
                    } else if (column.getJavaType().equals(String.class)) {
                        if (isNotEmpty((String) value)) {
                        	if(column.getFuzzyQuery()){
                        		WHERE(entityTable.getSchema()+"."+column.getColumn() + " like CONCAT('%', #{record."+column.getProperty()+"}, '%') ");
                        	}else{
                        		WHERE(entityTable.getSchema()+"."+column.getColumn() + "=#{record." + column.getProperty() + "}");
                        	} 
                        }
                    } else {
                        WHERE(entityTable.getSchema()+"."+column.getColumn() + "=#{record." + column.getProperty() + "}");
                    }
                }
                if(relation){//启用关联查询
                	for(EntityHelper.EntityTable tab : entityTable.getEntityClassFKColumns()){
                		for(EntityHelper.EntityColumn col : tab.getEntityClassColumns()){
                			String propertyName = StringUtils.underlineToCamelhump(tab.getName().toLowerCase())+"."+col.getProperty();
                			Object value = metaObject.getValue(propertyName);
                			if (value == null) {
                                continue;
                            } else if (col.getJavaType().equals(String.class)) {
                                if (isNotEmpty((String) value)) {
                                	if(col.getFuzzyQuery()){
                                		WHERE(tab.getSchema()+"."+col.getColumn() + " like '%'||#{record."+propertyName+"}||'%' ");
                                	}else {
                                		WHERE(tab.getSchema()+"."+col.getColumn() + "=#{record." + propertyName + "}");
                                	}
                                }
                            } else {
                                WHERE(tab.getSchema()+"."+col.getColumn() + "=#{record." + propertyName + "}");
                            }
                		}
                	}
                }
            }
            StringBuilder orderBy = new StringBuilder();
            for (EntityHelper.EntityColumn column : entityTable.getEntityClassColumns()) {
                if (column.getOrderBy() != null) {
                    orderBy.append(entityTable.getSchema()).append(".").append(column.getColumn()).append(" ").append(column.getOrderBy()).append(",");
                }
            }
            if (orderBy.length() > 0) {
                ORDER_BY(orderBy.substring(0, orderBy.length() - 1));
            }
        }}.toString();
    }
  
    /**
     * 通过主键查询，主键字段都不能为空
     */
    public String selectByPrimaryKey(final Map<String, Object> params) {
        return new SQL() {{
            Object entity = getEntity(params);
            Class<?> entityClass = getEntityClass(params);
            EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
            boolean relation = (boolean)params.get("relation");
            if(relation){//启用关联查询
            	StringBuilder allcols = new StringBuilder(EntityHelper.getAllColumns(entityClass));
            	for(EntityHelper.EntityTable tab : entityTable.getEntityClassFKColumns()){
            		allcols.append(",").append(EntityHelper.getAllColumns(tab));
            	}
            	SELECT(allcols.toString());
            	FROM(entityTable.getName()+" "+entityTable.getSchema());//别名
            	for(EntityHelper.EntityTable tab : entityTable.getEntityClassFKColumns()){
            		String joinStr = tab.getName()+" "+tab.getSchema()+" on "+tab.getSchema()+"."+entityTable.getFKCol(tab)
            				+"="+entityTable.getSchema()+"."+entityTable.getCol(tab);
            		if("left".equalsIgnoreCase(entityTable.getJoin(tab))){
            			LEFT_OUTER_JOIN(joinStr);
            		}else if("right".equalsIgnoreCase(entityTable.getJoin(tab))){
            			RIGHT_OUTER_JOIN(joinStr);
            		}else if("all".equalsIgnoreCase(entityTable.getJoin(tab))){
            			OUTER_JOIN(joinStr);
            		}else {
            			INNER_JOIN(joinStr);
            		}
            	}
            }else{
            	SELECT(EntityHelper.getAllColumns(entityClass));
            	FROM(entityTable.getName()+" "+entityTable.getSchema());
            }
            if (entityTable.getEntityClassPKColumns().size() == 1) {
                EntityHelper.EntityColumn column = entityTable.getEntityClassPKColumns().iterator().next();
                notNullKeyProperty(column.getProperty(), entity);
                WHERE(entityTable.getSchema()+"."+column.getColumn() + "=#{key}");
            } else {
                MetaObject metaObject = MapperTemplate.forObject(entity);
                for (EntityHelper.EntityColumn column : entityTable.getEntityClassPKColumns()) {
                    Object value = metaObject.getValue(column.getProperty());
                    notNullKeyProperty(column.getProperty(), value);
                    WHERE(entityTable.getSchema()+"."+column.getColumn() + "=#{key." + column.getProperty() + "}");
                }
            }
        }}.toString();
    }

    /**
     * 通过Example查询
     */
    public String selectByExample(final Map<String, Object> params) {
        return new SQL() {{
            MetaObject example = getExample(params);
            Class<?> entityClass = getEntityClass(params);
            EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
            SELECT(EntityHelper.getAllColumns(entityClass));
            FROM(entityTable.getName()+" "+entityTable.getSchema());
            applyWhere(this, example);
            applyOrderBy(this, example);
        }}.toString();
    }
    
    /**
     * 统计，入参可以是Entity.class或new Entity()
     */
    public String count(final Map<String, Object> params) {
        return new SQL() {{
            Object entity = getEntity(params);
            Class<?> entityClass;
            if (entity instanceof Class<?>) {
                entityClass = (Class<?>) entity;
                entity = null;
            } else {
                entityClass = getEntityClass(params);
            }
            EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
            boolean relation = (boolean)params.get("relation");
            SELECT("count(*)");
            FROM(entityTable.getName()+" "+entityTable.getSchema());//别名
            if(relation){//启用关联查询           	
            	for(EntityHelper.EntityTable tab : entityTable.getEntityClassFKColumns()){
            		String joinStr = tab.getName()+" "+tab.getSchema()+" on "+tab.getSchema()+"."+entityTable.getFKCol(tab)
            				+"="+entityTable.getSchema()+"."+entityTable.getCol(tab);
            		if("left".equalsIgnoreCase(entityTable.getJoin(tab))){
            			LEFT_OUTER_JOIN(joinStr);
            		}else if("right".equalsIgnoreCase(entityTable.getJoin(tab))){
            			RIGHT_OUTER_JOIN(joinStr);
            		}else if("all".equalsIgnoreCase(entityTable.getJoin(tab))){
            			OUTER_JOIN(joinStr);
            		}else {
            			INNER_JOIN(joinStr);
            		}
            	}
            }
            if (entity != null) {
                MetaObject metaObject = MapperTemplate.forObject(entity);
                for (EntityHelper.EntityColumn column : entityTable.getEntityClassColumns()) {
                    Object value = metaObject.getValue(column.getProperty());
                    if (value == null) {
                        continue;
                    } else if (column.getJavaType().equals(String.class)) {
                        if (isNotEmpty((String) value)) {
                            WHERE(entityTable.getSchema()+"."+column.getColumn() + "=#{record." + column.getProperty() + "}");
                        }
                    } else {
                        WHERE(entityTable.getSchema()+"."+column.getColumn() + "=#{record." + column.getProperty() + "}");
                    }
                }
                if(relation){//启用关联查询
                	for(EntityHelper.EntityTable tab : entityTable.getEntityClassFKColumns()){
                		for(EntityHelper.EntityColumn col : tab.getEntityClassColumns()){
                			String propertyName = StringUtils.underlineToCamelhump(tab.getName().toLowerCase())+"."+col.getProperty();
                			Object value = metaObject.getValue(propertyName);
                			if (value == null) {
                                continue;
                            } else if (col.getJavaType().equals(String.class)) {
                                if (isNotEmpty((String) value)) {
                                    WHERE(tab.getSchema()+"."+col.getColumn() + "=#{record." + propertyName + "}");
                                }
                            } else {
                                WHERE(tab.getSchema()+"."+col.getColumn() + "=#{record." + propertyName + "}");
                            }
                		}
                	}
                }
            }
        }}.toString();
    }

    /**
     * 不支持关键查询的统计
     */
    public String countByExample(final Map<String, Object> params) {
        return new SQL() {{
            MetaObject example = getExample(params);
            Class<?> entityClass = getEntityClass(params);
            EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
            SELECT("count(*)");
            FROM(entityTable.getName());
            applyWhere(this, example);
        }}.toString();
    }
    
}
