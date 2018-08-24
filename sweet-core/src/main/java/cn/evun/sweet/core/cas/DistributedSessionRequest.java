package cn.evun.sweet.core.cas;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.cache.CacheAccessor;
import cn.evun.sweet.core.common.R;

/**
 * 基于缓存机制，实现分布式session托管。覆盖Request的getSession方法，返回基于分布式缓存的实现的自定义session对象。<br/>
 * 其主要代理request以及session的数据存取方法，将其转接到对应的Context上。也即你可以用request或session的原生API。
 * 
 * @author yangw
 * @since V1.0.0
 */
public class DistributedSessionRequest extends HttpServletRequestWrapper {
	
	private String sessionId = null;
	
	private HttpSession session = null;

	public DistributedSessionRequest(HttpServletRequest request) {
		super(request);	
		sessionId = LoginCookieHelper.getSessIdInLoginCookie(request);
		if(StringUtils.hasText(sessionId) && null != CacheAccessor.doGet(R.cache.cache_region_session, sessionId)){
			session = new DistributedSession(sessionId);
		}else {
			sessionId = null; //被清理了的将无法恢复
		}
	}
	
	@Override
	public HttpSession getSession(boolean create) {
		if(create){
			this.session = new DistributedSession();
			this.sessionId = session.getId();
		}
		if(StringUtils.hasText(sessionId) && null == CacheAccessor.doGet(R.cache.cache_region_session, sessionId)){//被其他情况清理了
			this.session = null;
			this.sessionId = null;
		}
		return session; 
	}
	
	/**
	 * @return
	 * 无论如何不会返回null
	 */
	public HttpSession getSessionEvenNone() {
		HttpSession session = getSession(false);
		if(session == null){
			return getSession(true);
		}
		return session; 
	}
	
	@Override
    public HttpSession getSession() {
        return getSessionEvenNone();
    }
	
	@Override
	public String getRequestedSessionId(){
		return sessionId;
	}

}
