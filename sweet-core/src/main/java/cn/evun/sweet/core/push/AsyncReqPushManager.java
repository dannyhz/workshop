package cn.evun.sweet.core.push;

/**
 * 对消息推送请求的统一处理方案
 *
 * @author yangw
 * @since 1.0.0
 */
public interface AsyncReqPushManager {

	/**
	 * 新加入一个等待处理的推送消息的请求，用于接收一条匹配的消息。
	 */
	void addPollRequest(MsgPushDeferredResult req);
	
	/**
	 * 移除一个等待处理的推送消息的请求（在其完成响应或超时后）。
	 */
	void removePollRequest(MsgPushDeferredResult req);
	
	/**
	 * 新加入一个等待推送给用户的消息，消息会发送给匹配的用户。
	 */
	void addPushMessage(PushMessage msg);
	
	/**
	 * 得到当前的推送请求数。
	 */
	int getConcurrentPollRequestCount();
	
	/**
	 * 处理待推送的WEB消息。
	 */
	void handlePushMessage(PushMessage msg);
}
