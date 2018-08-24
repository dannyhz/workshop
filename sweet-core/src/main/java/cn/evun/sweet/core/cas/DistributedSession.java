package cn.evun.sweet.core.cas;

import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import cn.evun.sweet.core.cache.CacheAccessor;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.spring.SpringContext;

/**
 * 基于集中式缓存(redis)的分布式会话
 *
 * @author yangw
 * @since 1.0.0
 */
@SuppressWarnings("deprecation")
public class DistributedSession implements HttpSession {
	
	private String sessionId;
	private long creationTime;
	private long lastAccessTime;
	
	public DistributedSession(){
		this.sessionId = "session_" + UUID.randomUUID().toString();//TODO 分布式情况下有可能出现重复
		this.creationTime = System.currentTimeMillis();
		this.lastAccessTime = System.currentTimeMillis();
		CacheAccessor.doPut(R.cache.cache_region_session, sessionId, new HashMap<String, Object>());
	}
	
	public DistributedSession(String sessionid){
		this.sessionId = sessionid;
		this.creationTime = System.currentTimeMillis();
		this.lastAccessTime = System.currentTimeMillis();
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}

	@Override
	public String getId() {
		return sessionId;
	}

	@Override
	public long getLastAccessedTime() {
		return lastAccessTime;
	}

	@Override
	public ServletContext getServletContext() {
		return SpringContext.getServletContext();
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return null;	
	}

	public Set<String> attributeNames() {
		this.lastAccessTime = System.currentTimeMillis();
		@SuppressWarnings("unchecked")
		Map<String, Object> data = (Map<String, Object>)CacheAccessor.doGet(R.cache.cache_region_session, sessionId);
		if(data != null){
			return data.keySet();	
		}
		return null;
	}

	@Override
	public Object getAttribute(String name) {
		this.lastAccessTime = System.currentTimeMillis();
		@SuppressWarnings("unchecked")
		Map<String, Object> data = (Map<String, Object>)CacheAccessor.doGet(R.cache.cache_region_session, sessionId);	
		if(data != null){
			return data.get(name);
		}
		return null;
	}

	@Override
	public void setAttribute(String name, Object value) {
		this.lastAccessTime = System.currentTimeMillis();
		@SuppressWarnings("unchecked")
		Map<String, Object> data = (Map<String, Object>)CacheAccessor.doGet(R.cache.cache_region_session, sessionId);
		if(data != null){
			data.put(name, value);
			CacheAccessor.doPut(R.cache.cache_region_session, sessionId, data);
		}
	}

	@Override
	public void removeAttribute(String name) {
		this.lastAccessTime = System.currentTimeMillis();
		@SuppressWarnings("unchecked")
		Map<String, Object> data = (Map<String, Object>)CacheAccessor.doGet(R.cache.cache_region_session, sessionId);
		if(data != null){
			data.remove(name);
			CacheAccessor.doPut(R.cache.cache_region_session, sessionId, data);
		}
	}

	@Override
	public Object getValue(String name) {
		return null;
	}

	@Override
	public void putValue(String name, Object value) {
	}

	@Override
	public String[] getValueNames() {
		return null;
	}

	@Override
	public void removeValue(String name) {
	}
	
	@Override
	public void invalidate() {
		CacheAccessor.doEvict(R.cache.cache_region_session, sessionId);
		this.sessionId = null;
	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public void setMaxInactiveInterval(int interval) {		
	}

	@Override
	public int getMaxInactiveInterval() {
		return 0;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return null;
	}

}
