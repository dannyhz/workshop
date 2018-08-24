package cn.evun.sweet.common.serialize.json;

import java.util.ArrayList;
import java.util.List;

import cn.evun.sweet.common.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

/**
 * Json字符串与bean之间相互转化的工具类<br/>
 * 注意：超大对象需要另行处理。
 * 
 * @author yangw
 * @since V1.0.0
 */
public class JsonUtils {

	/**
	 * 反序列化json对象字符串得到一个java对象<br/>	 
	 * 必要时，请在bean字段上使用@JSONField定制序列化/反序列化的特殊要求，如果过滤属性等。<br/>
	 * 如下所列的时间格式可以自动识别:<br/>
	 * ISO-8601日期格式<br/>
	 * yyyy-MM-dd<br/>
	 * yyyy-MM-dd HH:mm:ss<br/>
	 * yyyy-MM-dd HH:mm:ss.SSS<br/>
	 * 毫秒数字<br/>
	 * 毫秒数字字符串<br/>
	 * NET JSON日期格式<br/>
	 * new Date(198293238)
	 * 
	 * * @param closeCircularReference 是否关闭引用检测和生成，必要时尽量关闭提升性能。
	 */
	public static <T> T jsonToBean(String jsonString, Class<T> beanCalss, boolean closeCircularReference) {
		T bean = (T) JSON.parseObject(jsonString, beanCalss);
		if(closeCircularReference){
			bean = (T) JSON.parseObject(jsonString, beanCalss, Feature.DisableCircularReferenceDetect);
		}
		return bean;
	}
	
	/**
	 * 反序列化json对象字符串得到一个 java对象，默认关联引用将序列化。
	 */
	public static <T> T jsonToBean(String jsonString, Class<T> beanCalss) {
		return (T) JSON.parseObject(jsonString, beanCalss);
	}
	
	/**
	 * 反序列化json对象字符串得到一个 java泛型对象<br/>
	 * 例如：new JsonGenericType<List<VO>>(){}
	 */
	public static <T> T jsonToBean(String jsonString, JsonGenericType<T> type, boolean closeCircularReference) {
		T bean = (T) JSON.parseObject(jsonString, type);
		if(closeCircularReference){
			bean = (T) JSON.parseObject(jsonString, type, Feature.DisableCircularReferenceDetect);
		}
		return bean;
	}
	
	/**
	 * 反序列化json对象字符串得到一个 java泛型对象，默认关联引用将序列化。
	 */
	public static <T> T jsonToBean(String jsonString, JsonGenericType<T> type) {
		return (T) JSON.parseObject(jsonString, type);
	}

	/**
	 * 序列化java对象，转换成json字符串。时间格式包含如下：<br/>
	 * 
	 * @param bean 待转换对象
	 * @param dateFormat 时间数据的转换格式
	 * @param closeCircularReference 是否关闭引用检测和生成，解决当对象存在引用时，序列化后的结果浏览器不支持。
	 * @param include 属性过滤方式：true包含/false不包含
	 * @param propNames 属性名称
	 * @param writeNull 是否输出null值的属性
	 */
	public static String beanToJson(Object bean, String dateFormat, boolean closeCircularReference,
			boolean include, String... propNames) {
		return beanToJson(bean, dateFormat, closeCircularReference, include, true, propNames);
	}
	public static String beanToJson(Object bean, String dateFormat, boolean closeCircularReference,
			boolean include, boolean writeNull, String... propNames) {
		if(StringUtils.hasText(dateFormat)){
			JSON.DEFFAULT_DATE_FORMAT = dateFormat;
		}
		
		PropertyPreFilter filter = null;
		if(include){
			filter = new SimplePropertyPreFilter(bean.getClass(), propNames);
		}else {
			filter = new ExcludesPropertyPreFilter(bean.getClass(), propNames);
		}
		
		List<SerializerFeature> features = new ArrayList<SerializerFeature>();
		features.add(SerializerFeature.SkipTransientField);
		if(closeCircularReference){
			features.add(SerializerFeature.DisableCircularReferenceDetect);
		}
		if(writeNull){
			features.add(SerializerFeature.WriteMapNullValue);
		}
		
		String result = JSON.toJSONString(bean, filter, features.toArray(new SerializerFeature[]{}));
		 
		JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";//还原默认时间格式
		return result;
	}

	public static String beanToJson(Object bean, String dateFormat, boolean include, String... propNames) {
		return beanToJson(bean, dateFormat, false, include, propNames);
	}
	
	public static String beanToJson(Object bean, boolean include, String... propNames) {
		return beanToJson(bean, null, false, include, propNames);
	}
	
	public static String beanToJson(Object bean, String dateFormat) {
		return beanToJson(bean, dateFormat, false, false);
	}
	
	public static String beanToJson(Object bean) {
		return beanToJson(bean, null, false, false);
	}
	
	/**
	 * 不输出null值属性
	 * @param bean
	 * @param dateFormat
	 * @return
	 */
	public static String beanToJson2(Object bean, String dateFormat) {
		return beanToJson(bean, dateFormat, false, false, false);
	}
	
	/**
	 * 不输出null值属性
	 * @param bean
	 * @return
	 */
	public static String beanToJson2(Object bean) {
		return beanToJson(bean, null, false, false, false);
	}

	/**
	 * 将json字符串转化为List
	 * @param jsonString
	 * @param beanCalss
	 */
	public static <T> List<T> jsonToList(String jsonString, Class<T> beanCalss) {
		return JSON.parseArray(jsonString, beanCalss);
	}

}
