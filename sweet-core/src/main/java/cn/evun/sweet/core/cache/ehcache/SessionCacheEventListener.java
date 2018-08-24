package cn.evun.sweet.core.cache.ehcache;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.impl.events.CacheEventAdapter;

/**
 * 监听Session的移除或过期操作。
 *
 * @author yangw
 * @since 1.0.0
 */
public class SessionCacheEventListener extends CacheEventAdapter<String, HashMap<String, Object>> {

	protected static final Logger LOGGER = LogManager.getLogger();	
	
	/* (non-Javadoc)
	 * @see org.ehcache.impl.events.CacheEventAdapter#onEviction(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void onEviction(String key, HashMap<String, Object> evictedValue) {
		LOGGER.info("User session[{}] was evicted.", key);
		super.onEviction(key, evictedValue);
	}
	
	/* 
	 * @see org.ehcache.impl.events.CacheEventAdapter#onExpiry(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void onExpiry(String key, HashMap<String, Object> expiredValue) {		
		LOGGER.info("User session[{}] was expiried.", key);
		super.onExpiry(key, expiredValue);
	}
	
	/* (non-Javadoc)
	 * @see org.ehcache.impl.events.CacheEventAdapter#onRemoval(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void onRemoval(String key, HashMap<String, Object> removedValue) {
		LOGGER.info("User session[{}] was removed.", key);
		super.onRemoval(key, removedValue);
	}
}
