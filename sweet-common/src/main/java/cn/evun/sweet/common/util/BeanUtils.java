package cn.evun.sweet.common.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;

public class BeanUtils {
	
	/**
	 * 默认覆盖target原有非空属性
	 * @param source
	 * @param target
	 */
	public static void copyProperties(Object source, Object target) {
		transBean2Bean(source, target, true);
	}
	
	/**
	 * 默认覆盖target原有非空属性
	 * 如果source的值为空，且target不为空，不会覆盖
	 * @param source
	 * @param target
	 */
	public static void copyPropertiesNotNull(Object source, Object target) {
		transBean2Bean(source, target, true, true);
	}
	
	/**
	 * @param source
	 * @param target
	 * @param override 是否覆盖target原有非空属性
	 * 如果是属性是对象，则整个对象覆盖
	 */
	public static void copyProperties(Object source, Object target, boolean override) {
		transBean2Bean(source, target, override);
	}

	public static Object transBean2Bean(Object source, Object target, boolean override) {
		return transBean2Bean(source, target, override, false);
	}
	/**
	 * org.apache.commons.beanutils.BeanUtils不支持target是map且是强制覆盖
	 * @param source
	 * @param target
	 * @param override
	 * @param notNull 不覆盖source为空的值
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object transBean2Bean(Object source, Object target, boolean override, boolean notNull) {
		if (source == null) {
			return null;
		}
		if(target == null){
			return source;
		}
		boolean isAssignableFrom = false,
				isMap = false;
		//source的类是否和target相等或者是超类
		if(source.getClass().isAssignableFrom(target.getClass())){
			isAssignableFrom = true;
		}
		//如果是map
		Map map = null;
		if(target instanceof Map){
			map = (Map)target;
			isMap = true;
		}
		try {
			//如果是强制覆盖且target不是map类型且不覆盖非空值
			if(override && !isMap && !notNull){
				org.apache.commons.beanutils.BeanUtils.copyProperties(target, source);
				return target;
			}
			if(source instanceof Map){
	            // Map properties are always of type <String, Object>
	            Map<String, Object> propMap = (Map<String, Object>) source;
	            for (Map.Entry<String, Object> entry : propMap.entrySet()) {
	                String key = entry.getKey();
	                Object value = entry.getValue();
	                if(value == null && notNull){
	                	continue;
	                }
	                if(isMap){
						if(override || map.get(key) == null){
							map.put(key, value);
						}
						if(map.get(key) == null){//去掉map的null值
							map.remove(key);
						}
					} else{
						PropertyDescriptor property = BeanUtilsBean.getInstance().getPropertyUtils().
								getPropertyDescriptor(target, key);
						//取不到表示target不存在对应的属性
						if(property == null){
							continue;
						}
						if(override || property.getReadMethod().invoke(target) == null){
							//转换成对应的数据类型
							value = BeanUtilsBean.getInstance().getConvertUtils().
									convert(value, property.getPropertyType());
							property.getWriteMethod().invoke(target, value);
						}
					}
	            }
			} else{
				BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass());
				PropertyDescriptor[] propertyDescriptors = beanInfo
						.getPropertyDescriptors();
				for (PropertyDescriptor property : propertyDescriptors) {
					String key = property.getName();

					// 过滤class属性
					if (!key.equals("class")) {
						// 得到property对应的getter方法
						Method getter = property.getReadMethod();
						Object value = getter.invoke(source);
						if(value == null && notNull){
							continue;
						}
						if(isMap){
							if(override || map.get(key) == null){
								map.put(key, value);
							}
							if(map.get(key) == null){
								map.remove(key);
							}
						} else{
							if(!isAssignableFrom){
								property = BeanUtilsBean.getInstance().getPropertyUtils().
										getPropertyDescriptor(target, key);
							}
							//取不到表示target不存在对应的属性
							if(property == null){
								continue;
							}
							if(override || property.getReadMethod().invoke(target) == null){
								//转换成对应的数据类型
								value = BeanUtilsBean.getInstance().getConvertUtils().
										convert(value, property.getPropertyType());
								property.getWriteMethod().invoke(target, value);
							}
						}

					}

				}
			}
		} catch (Exception e) {
			System.out.println("transBean2Bean Error " + e);
		}

		return target;

	}
	
}
