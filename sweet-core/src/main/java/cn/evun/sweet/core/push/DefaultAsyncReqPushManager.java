package cn.evun.sweet.core.push;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import cn.evun.sweet.core.push.app.AppPush;

/**
 * 默认的全局消息推送处理方案
 *
 * @author yangw
 * @since 1.0.0
 */
@Component
public class DefaultAsyncReqPushManager implements AsyncReqPushManager {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	public static final String MESSAGE_APP_CACHE_KEY = "message_app_cache_";
	
	//@Autowired
	private RedisTemplate<String, PushMessage> template;
	
	//@Resource(name="getui")
	private AppPush appPush;
	
	/*用户存放APP推送的消息队列，等待定时任务去推送消息*/
	//private BoundListOperations<String, PushMessage> appMsgQueue; 
	
	private static final ConcurrentMap<String, MsgPushDeferredResult> ASYNC_REQ_MAP = 
			new ConcurrentHashMap<String, MsgPushDeferredResult>();
	//private static final DefaultRedisMap<String, MsgPushDeferredResult> ASYNC_REQ_MAP = 
			//new DefaultRedisMap<String, MsgPushDeferredResult>(null);
	
	@Override
	public void addPollRequest(MsgPushDeferredResult req) {
		/*始终只有一个最新的请求站位中，要么是被更新的顶替，要么是被处理了离开站位*/
		DefaultAsyncReqPushManager.ASYNC_REQ_MAP.put(req.getUserId().toString(), req); 
		if(LOGGER.isTraceEnabled()){
			LOGGER.trace("User({}) poll request ({}) add to queue.", req.getUserId(), req.hashCode());
		}
	}

	@Override
	public void removePollRequest(MsgPushDeferredResult req) {
		DefaultAsyncReqPushManager.ASYNC_REQ_MAP.remove(req.getUserId().toString(), req);		
		if(LOGGER.isTraceEnabled()){
			LOGGER.trace("User({}) poll request ({}) remove from queue.", req.getUserId(), req.hashCode());
		}
	}

	@Override
	public void addPushMessage(PushMessage msg) {
		if(PushMessage.MESSAGE_TYPE_WEB == msg.getMsgType() || PushMessage.MESSAGE_TYPE_BOTH == msg.getMsgType()){
			template.convertAndSend("channel_push", msg); //基于Redis的Pub/Sub完成消息发布，由监听器统一执行push			
		}
		if(PushMessage.MESSAGE_TYPE_APP == msg.getMsgType() || PushMessage.MESSAGE_TYPE_BOTH == msg.getMsgType()){
			appPush.doPush(msg);
		}
		
		if(LOGGER.isTraceEnabled()){
			LOGGER.trace("Push-message ({}) was publish.", msg.getMessage().getI18nMessage());
		}	
	}	
	
	@Override
	public void handlePushMessage(PushMessage msg){
		if(msg.getUserIds().size() < 1){//全局消息
			for(String userID : DefaultAsyncReqPushManager.ASYNC_REQ_MAP.keySet()){
				doPush(userID, msg);
			}
		}else {
			for(String userid : msg.getUserIds()){
    			doPush(userid.toString(), msg);
    		}
		}
	}
	
	private boolean doPush(String userid, PushMessage msg){
		MsgPushDeferredResult req = DefaultAsyncReqPushManager.ASYNC_REQ_MAP.get(userid);
		if(req != null && !req.isSetOrExpired()){	
			boolean hasdone = req.setResult(msg.getMessage());
			if(hasdone && LOGGER.isTraceEnabled()){
				LOGGER.trace("Push-message ({}) was completed by poll-request({}).", msg.getMessage().getI18nMessage(), req.hashCode());
			}
			return hasdone;
		}
		return false;
	}

	/**
	 * 获取当前在线的请求数,非精确数据。
	 */
	public int getConcurrentPollRequestCount() {
		return DefaultAsyncReqPushManager.ASYNC_REQ_MAP.size();
	}
}
