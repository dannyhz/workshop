package cn.evun.sweet.core.push.app;

import org.springframework.stereotype.Component;

import cn.evun.sweet.core.push.PushMessage;

/**
 * 关闭消息推送时的空实现方案
 *
 * @author yangw
 * @since 1.0.0
 */
@Component("nopush")
public class NoOprPush implements AppPush{

	/* (non-Javadoc)
	 * @see cn.evun.sweet.core.push.app.AppPush#doPush(cn.evun.sweet.core.push.PushMessage)
	 */
	@Override
	public void doPush(PushMessage message) {
		
	}

}
