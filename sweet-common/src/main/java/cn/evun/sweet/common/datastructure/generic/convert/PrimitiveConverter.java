package cn.evun.sweet.common.datastructure.generic.convert;

import java.math.BigDecimal;
import java.math.BigInteger;

import cn.evun.sweet.common.datastructure.generic.GenericObject;
import cn.evun.sweet.common.datastructure.generic.SingleGenericObject;

/**
 * 基础数据类型转换器，对应着“值”类型的ContextObject。
 * 
 * @author yangw
 * @since V1.0.0
 */
public class PrimitiveConverter implements GenericObjectConverter {

	@Override
	public boolean isSupportsType(Object source) {
		if (source.getClass().isPrimitive()
				||(source.getClass() == Integer.class)
				|| (source.getClass() == Double.class) 
				|| (source.getClass() == Boolean.class) 
				|| (source.getClass() == Byte.class)
				|| (source.getClass() == Character.class) 
				|| (source.getClass() == Short.class)
				|| (source.getClass() == Long.class)
				|| (source.getClass() == Float.class) 
				|| (source.getClass() == BigInteger.class) 
				|| (source.getClass() == BigDecimal.class)
				|| (source.getClass() == String.class)) {
			return true;
		}
		return false;
	}

	@Override
	public GenericObject convertObject(Object source) {
		return new SingleGenericObject(String.valueOf(source));	
	}

}
