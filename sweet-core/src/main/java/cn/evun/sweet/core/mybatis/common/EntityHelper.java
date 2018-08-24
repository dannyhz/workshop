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

package cn.evun.sweet.core.mybatis.common;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import cn.evun.sweet.common.util.ObjectUtils;
import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.common.util.reflect.ReflectionUtils;
import cn.evun.sweet.core.exception.SweetException;
import cn.evun.sweet.core.mybatis.general.FuzzyQuery;
import cn.evun.sweet.core.mybatis.general.RelationTable;

/**
 * 实体类工具类 - 处理实体和数据库表以及字段关键的一个类<p/>
 * @link https://github.com/abel533/Mapper
 *
 * @author liuzh,yangw
 */
public class EntityHelper {
	
	//private static BeanWrapperImpl wrapper = null;
	
	protected static final Logger LOGGER = LogManager.getLogger();

    /**
     * 实体对应表的配置信息
     */
    public static class EntityTable {
        private String name;
        private String catalog;
        private String schema;//做别名用
        
        private Set<EntityColumn> entityClassColumns;//实体类 => 全部列属性            
        private Set<EntityColumn> entityClassPKColumns;//实体类 => 主键信息
        
        private Set<EntityTable> entityClassFKColumns;//实体类 => 全部外键列实体 
        private Map<EntityTable, RelationTable> FKRelations = new HashMap<EntityTable, RelationTable>(10);//实体类 => 外键关联关系
        
        private Map<String, String> aliasMap;//字段名和属性名的映射

        public void setTable(Table table) {
            this.name = table.name();
            this.catalog = table.catalog();
            this.schema = table.schema();
        }
        
        public void addFkParameter(EntityTable entityTable, RelationTable table){
        	FKRelations.put(entityTable, table);
        }
        
        public String getFKCol(EntityTable entityTable){
        	RelationTable table = FKRelations.get(entityTable);
        	if(table != null){
        		return camelhumpToUnderline(table.fkCol());
        	}
        	return "";
        }
        
        public String getCol(EntityTable entityTable){
        	RelationTable table = FKRelations.get(entityTable);
        	if(table != null){
        		return camelhumpToUnderline(table.col());
        	}
        	return "";
        }
        
        public String getJoin(EntityTable entityTable){
        	RelationTable table = FKRelations.get(entityTable);
        	if(table != null){
        		return table.join();
        	}
        	return "";
        }

        public String getName() {
            return name;
        }

        public String getCatalog() {
            return catalog;
        }

        public String getSchema() {
            return schema;
        }

		public String getPrefix() {
            if (catalog != null && catalog.length() > 0) {
                return catalog;
            }
            if (schema != null && schema.length() > 0) {
                return catalog;
            }
            return "";
        }

        public Set<EntityColumn> getEntityClassColumns() {
            return entityClassColumns;
        }
        
        public Set<EntityTable> getEntityClassFKColumns() {
            return entityClassFKColumns;
        }

        public Set<EntityColumn> getEntityClassPKColumns() {
            return entityClassPKColumns;
        }

        public Map<String, String> getAliasMap() {
            return aliasMap;
        }
    }

    /**
     * 实体字段对应数据库列的信息
     */
    public static class EntityColumn {
        private String property;
        private String column;
        private Class<?> javaType;
        private String sequenceName;
        private boolean id = false;
        private boolean uuid = false;
        private boolean identity = false;
        private String generator;
        private boolean fuzzyQuery = false;
        private String orderBy;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public Class<?> getJavaType() {
            return javaType;
        }

        public void setJavaType(Class<?> javaType) {
            this.javaType = javaType;
        }

        public String getSequenceName() {
            return sequenceName;
        }

        public void setSequenceName(String sequenceName) {
            this.sequenceName = sequenceName;
        }

        public boolean isId() {
            return id;
        }

        public void setId(boolean id) {
            this.id = id;
        }

        public boolean isUuid() {
            return uuid;
        }

        public void setUuid(boolean uuid) {
            this.uuid = uuid;
        }

        public boolean isIdentity() {
            return identity;
        }

        public void setIdentity(boolean identity) {
            this.identity = identity;
        }

        public String getGenerator() {
            return generator;
        }

        public void setGenerator(String generator) {
            this.generator = generator;
        }

        public boolean getFuzzyQuery() {
			return fuzzyQuery;
		}

		public void setFuzzyQuery(boolean fuzzyQuery) {
			this.fuzzyQuery = fuzzyQuery;
		}

		public String getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(String orderBy) {
            this.orderBy = orderBy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EntityColumn that = (EntityColumn) o;

            if (id != that.id) return false;
            if (identity != that.identity) return false;
            if (uuid != that.uuid) return false;
            if (column != null ? !column.equals(that.column) : that.column != null) return false;
            if (generator != null ? !generator.equals(that.generator) : that.generator != null) return false;
            if (javaType != null ? !javaType.equals(that.javaType) : that.javaType != null) return false;
            if (orderBy != null ? !orderBy.equals(that.orderBy) : that.orderBy != null) return false;
            if (property != null ? !property.equals(that.property) : that.property != null) return false;
            if (sequenceName != null ? !sequenceName.equals(that.sequenceName) : that.sequenceName != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = property != null ? property.hashCode() : 0;
            result = 31 * result + (column != null ? column.hashCode() : 0);
            result = 31 * result + (javaType != null ? javaType.hashCode() : 0);
            result = 31 * result + (sequenceName != null ? sequenceName.hashCode() : 0);
            result = 31 * result + (id ? 1 : 0);
            result = 31 * result + (uuid ? 1 : 0);
            result = 31 * result + (identity ? 1 : 0);
            result = 31 * result + (generator != null ? generator.hashCode() : 0);
            result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
            return result;
        }
    }

    /**
     * 实体类 => 表对象
     */
    private static final Map<Class<?>, EntityTable> entityTableMap = new HashMap<Class<?>, EntityTable>();

    /**
     * 获取表对象
     */
    public static EntityTable getEntityTable(Class<?> entityClass) {
        EntityTable entityTable = entityTableMap.get(entityClass);
        if (entityTable == null) {
            initEntityNameMap(entityClass);
            entityTable = entityTableMap.get(entityClass);
        }
        if (entityTable == null) {
            throw new RuntimeException("无法获取实体类" + entityClass.getCanonicalName() + "对应的表名!");
        }
        return entityTable;
    }

    /**
     * 获取全部列
     */
    public static Set<EntityColumn> getColumns(Class<?> entityClass) {
        return getEntityTable(entityClass).getEntityClassColumns();
    }

    /**
     * 获取主键信息
     */
    public static Set<EntityColumn> getPKColumns(Class<?> entityClass) {
        return getEntityTable(entityClass).getEntityClassPKColumns();
    }

    /**
     * 获取字段映射关系
     */
    public static Map<String, String> getColumnAlias(Class<?> entityClass) {
        EntityTable entityTable = getEntityTable(entityClass);
        if (entityTable.aliasMap != null) {
            return entityTable.aliasMap;
        }       
        entityTable.aliasMap = new HashMap<String, String>();
        
        /*增加外联字段 */
        Set<EntityTable> fkList = entityTable.getEntityClassFKColumns();
        for(EntityTable tab : fkList){
        	Set<EntityColumn> colList = tab.getEntityClassColumns();        
            for (EntityColumn col : colList) {
                entityTable.aliasMap.put(col.getColumn(), StringUtils.underlineToCamelhump(tab.getName().toLowerCase())+"."+col.getProperty());
            }
        }
        /*增加本身字段*/
        Set<EntityColumn> columnList = entityTable.getEntityClassColumns();        
        for (EntityColumn column : columnList) {
            entityTable.aliasMap.put(column.getColumn(), column.getProperty());
        }
        
        return entityTable.aliasMap;
    }

    /**
     * 获取查询的Select
     */
    public static String getSelectColumns(Class<?> entityClass) {
        Set<EntityColumn> columnList = getColumns(entityClass);
        StringBuilder selectBuilder = new StringBuilder();
        boolean skipAlias = Map.class.isAssignableFrom(entityClass);
        for (EntityColumn entityColumn : columnList) {
            selectBuilder.append(entityColumn.getColumn());
            if (!skipAlias && !entityColumn.getColumn().equalsIgnoreCase(entityColumn.getProperty())) {
                selectBuilder.append(" ").append(entityColumn.getProperty().toUpperCase()).append(",");
            } else {
                selectBuilder.append(",");
            }
        }
        return selectBuilder.substring(0, selectBuilder.length() - 1);
    }

    /**
     * 获取查询的Select
     */
    public static String getAllColumns(Class<?> entityClass) {
        return getAllColumns(getEntityTable(entityClass));
    }
    
    /**
     * 获取查询的Select
     */
    public static String getAllColumns(EntityTable table) {
    	 StringBuilder selectBuilder = new StringBuilder();
         for (EntityColumn entityColumn : table.getEntityClassColumns()) {
        	 //为适应关联查询，这里加上了别名。
             selectBuilder.append(table.getSchema()).append(".").append(entityColumn.getColumn()).append(",");
         }
         return selectBuilder.substring(0, selectBuilder.length() - 1);
    }

    /**
     * 获取主键的Where语句
     */
    public static String getPrimaryKeyWhere(Class<?> entityClass) {
        Set<EntityColumn> entityColumns = getPKColumns(entityClass);
        StringBuilder whereBuilder = new StringBuilder();
        for (EntityColumn column : entityColumns) {
            whereBuilder.append(column.getColumn()).append(" = ?").append(" AND ");
        }
        return whereBuilder.substring(0, whereBuilder.length() - 4);
    }

    /**
     * 初始化实体属性
     */
    public static synchronized void initEntityNameMap(Class<?> entityClass) {
        if (entityTableMap.get(entityClass) != null) {
            return;
        }
        //表名
        EntityTable entityTable = new EntityTable();
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            entityTable.setTable(table);
        }
        if (!StringUtils.hasText(entityTable.getName())) {
            entityTable.name = camelhumpToUnderline(entityClass.getSimpleName()).toUpperCase();
        }
        if (!StringUtils.hasText(entityTable.getSchema())) {
            entityTable.schema = entityClass.getSimpleName().toLowerCase();
        }
        //列
        List<Field> fieldList = getAllField(entityClass, null);
        Set<EntityColumn> columnSet = new HashSet<EntityColumn>();
        Set<EntityColumn> pkColumnSet = new HashSet<EntityColumn>();
        Set<EntityTable> fkColumnSet = new HashSet<EntityTable>();
        for (Field field : fieldList) {
        	//外键关联字段  yangw
            if(ObjectUtils.isJavaBean(field.getType())){
            	if(field.isAnnotationPresent(RelationTable.class)){
            		if (entityTableMap.get(field.getType()) == null) {
                        initEntityNameMap(field.getType());
                    }
            		EntityTable table = entityTableMap.get(field.getType());
            		entityTable.addFkParameter(table, field.getAnnotation(RelationTable.class));
            		fkColumnSet.add(table);              	
            	}
            	continue;
            }
            //排除字段
            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }
            //常规字段
            EntityColumn entityColumn = new EntityColumn();
            if (field.isAnnotationPresent(Id.class)) {
                entityColumn.setId(true);
            }
            if (field.isAnnotationPresent(FuzzyQuery.class)) {
                entityColumn.setFuzzyQuery(true);
            }
            String columnName = null;
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                columnName = column.name();
            }
            if (columnName == null || columnName.equals("")) {
                columnName = camelhumpToUnderline(field.getName());
            }
            entityColumn.setProperty(field.getName());
            entityColumn.setColumn(columnName.toUpperCase());
            entityColumn.setJavaType(field.getType());
            //order by
            if (field.isAnnotationPresent(OrderBy.class)) {
                OrderBy orderBy = field.getAnnotation(OrderBy.class);
                if (orderBy.value().equals("")) {
                    entityColumn.setOrderBy("ASC");
                } else {
                    entityColumn.setOrderBy(orderBy.value());
                }
            }
            columnSet.add(entityColumn);
            if (entityColumn.isId()) {
                pkColumnSet.add(entityColumn);
            }
        }
        entityTable.entityClassColumns = columnSet;
        entityTable.entityClassFKColumns = fkColumnSet;
        if (pkColumnSet.size() == 0) {
            entityTable.entityClassPKColumns = columnSet;
        } else {
            entityTable.entityClassPKColumns = pkColumnSet;
        }
        //缓存
        entityTableMap.put(entityClass, entityTable);
    }

    public static void main(String[] args) {
        System.out.println(camelhumpToUnderline("userName"));
        System.out.println(camelhumpToUnderline("userPassWord"));
        System.out.println(camelhumpToUnderline("ISO9001"));
        System.out.println(camelhumpToUnderline("hello_world"));
    }

    /**
     * 将驼峰风格替换为下划线风格
     */
    public static String camelhumpToUnderline(String str) {
        final int size;
        final char[] chars;
        final StringBuilder sb = new StringBuilder(
                (size = (chars = str.toCharArray()).length) * 3 / 2 + 1);
        char c;
        for (int i = 0; i < size; i++) {
            c = chars[i];
            if (isUppercaseAlpha(c)) {
                sb.append('_').append(c);
            } else {
                sb.append(toUpperAscii(c));
            }
        }
        return sb.charAt(0) == '_' ? sb.substring(1) : sb.toString();
    }

    /**
     * 将下划线风格替换为驼峰风格
     */
    public static String underlineToCamelhump(String str) {
        Matcher matcher = Pattern.compile("_[a-z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() - i, matcher.end() - i, matcher.group().substring(1).toUpperCase());
        }
        if (Character.isUpperCase(builder.charAt(0))) {
            builder.replace(0, 1, String.valueOf(Character.toLowerCase(builder.charAt(0))));
        }
        return builder.toString();
    }

    public static boolean isUppercaseAlpha(char c) {
        return (c >= 'A') && (c <= 'Z');
    }

    public static char toUpperAscii(char c) {
        if (isUppercaseAlpha(c)) {
            c -= (char) 0x20;
        }
        return c;
    }

    /**
     * 获取全部的Field
     */
    private static List<Field> getAllField(Class<?> entityClass, List<Field> fieldList) {
        if (fieldList == null) {
            fieldList = new ArrayList<Field>();
        }
        if (entityClass.equals(Object.class)) {
            return fieldList;
        }
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            //排除静态字段，解决bug#2
            if (!Modifier.isStatic(field.getModifiers())
            		&&!Map.class.isAssignableFrom(field.getType())//排除one-many情况  yangw
                    && !Collection.class.isAssignableFrom(field.getType())
                    && !Array.class.isAssignableFrom(field.getType())) {
                fieldList.add(field);
            }
        }
        Class<?> superClass = entityClass.getSuperclass();
        if (superClass != null
                && !superClass.equals(Object.class)
                && (superClass.isAnnotationPresent(Entity.class)
                || (!Map.class.isAssignableFrom(superClass)
                && !Collection.class.isAssignableFrom(superClass)
                && !Array.class.isAssignableFrom(superClass)))) {
            return getAllField(entityClass.getSuperclass(), fieldList);
        }
        return fieldList;
    }

    /**
     * map转换为Map
     */
    public static Map<String, Object> map2AliasMap(Map<String, Object> map, Class<?> beanClass) {
        if (map == null) {
            return null;
        }
        Map<String, String> alias = getColumnAlias(beanClass);
        Map<String, Object> result = new HashMap<String, Object>();
        for (String name : map.keySet()) {
            String alia = name;
            //sql在被其他拦截器处理后，字段可能发生变化，例如分页插件增加rownum
            if (alias.containsKey(name)) {
                alia = alias.get(name);
            }
            result.put(alia, map.get(name));
        }
        return result;
    }
    
    /**
     * 根据类创建实体对象，并创建好下级的关联对象  yangw
     */
    public static Object instantiateBean(Class<?> beanClass){
    	Object bean = null;
        try{
        	bean = BeanUtils.instantiate(beanClass);
        	for (Field field : getAllField(beanClass, null)) {
                if (field.isAnnotationPresent(RelationTable.class) && ObjectUtils.isJavaBean(field.getType())) {
                    try {
    					PropertyUtils.setProperty(bean, field.getName(), instantiateBean(field.getType()));
    				} catch (Exception ex) {
    					throw new SweetException("Failed to creat field:"+field.getName()+".",ex);
    				}
                }	
            }
		}catch(Exception e){
			throw new SweetException("Failed to creat model:"+beanClass.getName()+".",e);
		}
        
        return bean;
    }

    /**
     * map转换为bean
     */
    public static Object map2Bean(Map<String, Object> map, Class<?> beanClass) {
    	if (map == null) {
            return null;
        }
    	Map<String, Object> aliasMap = map2AliasMap(map, beanClass);
        Object bean = instantiateBean(beanClass); 
        for(Map.Entry<?, ?> entry : aliasMap.entrySet()){
        	String key = (String)entry.getKey();
        	try {       		        		
        		if(!"row_id".equalsIgnoreCase(key)){//排除分页参数
    				//wrapper.setPropertyValue(key, entry.getValue());
    				PropertyUtils.setProperty(bean, key, entry.getValue());
        		}
			}catch(IllegalArgumentException ie){
				/*提供数据格式的兼容性*/
				try {
					Field field = ReflectionUtils.findField(beanClass, key);
					if(field == null && key.indexOf(".")>0){ //层级路径
						String[] paths = StringUtils.delimitedListToStringArray(key, ".");
						Class<?> fildClass = beanClass;
						for(int i=0; i<paths.length-1; i++){
							fildClass = ReflectionUtils.findField(fildClass, paths[i]).getType();
						}
						field = ReflectionUtils.findField(fildClass, paths[paths.length-1]);
					}
					if(Boolean.class.isAssignableFrom(field.getType())){
						PropertyUtils.setProperty(bean, key, "1".equals(String.valueOf(entry.getValue()))?Boolean.TRUE:Boolean.FALSE);
						continue;
					}else if(BigDecimal.class.isAssignableFrom(field.getType())){
						PropertyUtils.setProperty(bean, key, BigDecimal.valueOf(Long.valueOf(String.valueOf(entry.getValue()))));
						continue;
					}else if(Long.class.isAssignableFrom(field.getType())){
						PropertyUtils.setProperty(bean, key, Long.valueOf(String.valueOf(entry.getValue())));
						continue;
					}else if(Integer.class.isAssignableFrom(field.getType())){
						PropertyUtils.setProperty(bean, key, Integer.valueOf(String.valueOf(entry.getValue())));
						continue;
					}else if(Short.class.isAssignableFrom(field.getType())){
						PropertyUtils.setProperty(bean, key, Short.valueOf(String.valueOf(entry.getValue())));
						continue;
					}
				} catch (Exception e){	
					e.printStackTrace();
				}				
				LOGGER.error("Failed to set property:"+entry.getKey()+", when convert map to bean.", ie);
			}catch (Exception e) {
				LOGGER.error("Failed to set property:"+entry.getKey()+", when convert map to bean.", e);
			}
        }
        
        return bean;
    }

    /**
     * mapList转换为beanList
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<?> maplist2BeanList(List<?> mapList, Class<?> beanClass) {
        if (mapList == null || mapList.size() == 0) {
            return null;
        }
        List list = new ArrayList<Object>(mapList.size());
        for (Object map : mapList) {
            list.add(map2Bean((Map) map, beanClass));
        }
        mapList.clear();
        mapList.addAll(list);
        return mapList;
    }
}