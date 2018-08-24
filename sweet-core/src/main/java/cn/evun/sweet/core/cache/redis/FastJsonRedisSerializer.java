package cn.evun.sweet.core.cache.redis;

import java.io.UnsupportedEncodingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import cn.evun.sweet.common.serialize.json.JsonUtils;

/**
 * @author yangw
 * @since 1.1.0
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	private final Class<T> classtype;
	
	public FastJsonRedisSerializer(Class<T> type) {
		this.classtype = type;
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.redis.serializer.RedisSerializer#serialize(java.lang.Object)
	 */
	@Override
	public byte[] serialize(T t) throws SerializationException {
		if (t == null) {
			return new byte[0];
		}
		String json = JsonUtils.beanToJson(t);
		try {
			return json.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Object[{}] failed to get bytes", json);
			throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.redis.serializer.RedisSerializer#deserialize(byte[])
	 */
	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		try {
			return JsonUtils.jsonToBean(new String(bytes, "UTF-8"), classtype);
		} catch (UnsupportedEncodingException e) {
			throw new SerializationException("Could not read JSON: " + e.getMessage(), e);
		}
	}

}
