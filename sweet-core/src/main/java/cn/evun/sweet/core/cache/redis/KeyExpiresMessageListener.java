package cn.evun.sweet.core.cache.redis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import cn.evun.sweet.core.exception.SweetException;
import cn.evun.sweet.core.service.RegistyServiceInvoker;

/**
 * key过期监听，主要用于定时相关业务处理
 * @author yangw
 * @since 1.1.0
 */
public class KeyExpiresMessageListener implements MessageListener {

	private static final Logger LOGGER = LogManager.getLogger();
	
	private RedisSerializer<String> serializer = new StringRedisSerializer();
	
	/**
	 * 所有需要监听过期事件的Redis Key的格式应该为“serviceId_keyString”.
	 * 其中serviceId为注册过的服务ID，该服务用于处理该前缀的key过期出发的业务。
	 * keyString则为具体的业务数据，即key的实际值
	 */
	@Override
	public void onMessage(Message message, byte[] pattern) {
		String key = serializer.deserialize(message.getBody());
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("key[{}] was expired.", key);
		}
		if(key.indexOf("_") > 0){
			String[] keyArray = key.split("_");
			try{
				RegistyServiceInvoker.invoker(keyArray[0], keyArray[1]);
			}catch(SweetException e){
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("service[{}] not registied.", keyArray[0]);
				}
			}
		}
	}

}
