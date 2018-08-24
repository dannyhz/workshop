package cn.evun.sweet.core.cache.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * spring-data-redis提供了序列化方案会导致反序列化有问题， 这里实现RedisSerializer为Redis自定义序列化方案
 * 
 * @author daringyun@gmail.com
 * @version 2016年11月24日 上午9:27:08
 */
public class ObjectRedisSerializer implements RedisSerializer<Object> {

	@Override
	public byte[] serialize(Object object) throws SerializationException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			return baos.toByteArray();
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			try {
				oos.close();
				baos.close();
			} catch (Exception e) {
			}
		}
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bais);
			return (Object) ois.readObject();
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			try {
				ois.close();
				bais.close();
			} catch (Exception e) {
			}
		}
	}

}
