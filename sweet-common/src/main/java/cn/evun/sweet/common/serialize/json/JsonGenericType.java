package cn.evun.sweet.common.serialize.json;

import com.alibaba.fastjson.TypeReference;

/**
 * Json字符串反序列化的泛型支持及扩展
 * 
 * @author yangw
 * @since V1.0.0
 */
public class JsonGenericType<T> extends TypeReference<T> {
	
	public JsonGenericType(){
        super();
    }

}
