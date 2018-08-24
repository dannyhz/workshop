package cn.evun.sweet.core.cache.ehcache;

import java.nio.ByteBuffer;

import org.ehcache.impl.serialization.CompactJavaSerializer;
import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;

/**
 * Spring对Cache的集成接口采用的是Object对象作为k-v，但Ehcache3以后使用了泛型。
 * 并且为了支持off-heap，除了基础的数据类型，其他数据类型都需要配置序列化方案
 *
 * @author yangw
 * @since 1.0.0
 */
public class ObjectSerializer<T> implements Serializer<T> {
	
	private final Serializer<T> serializer;
	
	public ObjectSerializer(ClassLoader classLoader) {
	    this.serializer = new CompactJavaSerializer<T>(classLoader);	    
	}

	@Override
	public ByteBuffer serialize(T object) throws SerializerException {
		return serializer.serialize(object);
	}

	@Override
	public T read(ByteBuffer binary) throws ClassNotFoundException, SerializerException {
		return serializer.read(binary);
	}

	@Override
	public boolean equals(T object, ByteBuffer binary) throws ClassNotFoundException, SerializerException {
		return serializer.equals(object, binary);
	}

}
