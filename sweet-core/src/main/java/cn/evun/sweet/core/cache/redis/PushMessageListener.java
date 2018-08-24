package cn.evun.sweet.core.cache.redis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

import cn.evun.sweet.common.serialize.json.JsonUtils;
import cn.evun.sweet.core.push.AsyncReqPushManager;
import cn.evun.sweet.core.push.PushMessage;

/**
 * 基于Redis的pubsub实现的消息推送功能的监听器。监听需要推送的消息
 *
 * @author yangw
 * @since 1.0.0
 */
public class PushMessageListener implements MessageListener {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	//@Autowired
	private AsyncReqPushManager reqMgr;
	
	//@Autowired
	private RedisTemplate<String, PushMessage> redisTemplate;


	/* (non-Javadoc)
	 * @see org.springframework.data.redis.connection.MessageListener#onMessage(org.springframework.data.redis.connection.Message, byte[])
	 */
	@Override
	public void onMessage(Message message, byte[] pattern) {
		PushMessage msg = (PushMessage)redisTemplate.getValueSerializer().deserialize(message.getBody());
		reqMgr.handlePushMessage(msg);
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("PushMessage[{}] was pushed", JsonUtils.beanToJson(msg));
		}
	}

}
