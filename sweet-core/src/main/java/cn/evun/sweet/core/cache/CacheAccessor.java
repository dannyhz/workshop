package cn.evun.sweet.core.cache;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.AbstractCacheInvoker;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;

import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.spring.SpringContext;

/**
 * 提供非注解形式下的缓存操作工具。<br/>
 * 该缓存操作方式下CacheResolver以及KeyGenerator将会失效。实际上在直接操作缓存的时候，是不需要这两项功能的。
 * 
 * @author yangw
 * @since V1.0.0
 */
@Component
public class CacheAccessor extends AbstractCacheInvoker {
	
	protected static final Logger LOGGER = LogManager.getLogger();	
	
	/********************************************静态操作方法区开始******************************************** */
	
	/**
	 * 从指定的缓存区取值
	 */
	public static Object doGet(String cacheName, Object key){
		return CacheAccessor.getInstance().get(cacheName, key);
	}
	
	/**
	 * 从默认的主缓存区取值
	 */
	public static Object doGet(Object key){
		return doGet(R.cache.cache_region_main, key);
	}
	
	/**
	 * 从所有缓存区寻找值
	 */
	public static Object doGetInAllCaches(Object key){
		return CacheAccessor.getInstance().getInCaches(key);
	}
	
	/**
	 * 向指定的缓存区存放值
	 */
	public static void doPut(String cacheName, Object key, Object result){
		CacheAccessor.getInstance().put(cacheName, key, result);
	}
	
	/**
	 * 向默认的主缓存区存放值
	 */
	public static void doPut(Object key, Object result){
		doPut(R.cache.cache_region_main, key, result);
	}
	
	/**
	 * 向默认的主缓存区存放值，前提要求放入的key不存在。
	 */
	public static void doPutIfAbsent(Object key, Object result){
		CacheAccessor.getInstance().putIfAbsent(R.cache.cache_region_main, key, result);
	}
	
	/**
	 * 从指定的缓存区移除值
	 */
	public static void doEvict(String cacheName, Object key){
		CacheAccessor.getInstance().evict(cacheName, key);
	}
	
	/**
	 * 从默认的主缓存区移除值
	 */
	public static void doEvict(Object key){
		doEvict(R.cache.cache_region_main, key);
	}
	
	/**
	 * 清空指定的缓存区
	 */
	public static void doClear(String cacheName){
		CacheAccessor.getInstance().clear(cacheName);
	}
	
	/**
	 * 清空默认的主缓存区
	 */
	public static void doClear(){
		doClear(R.cache.cache_region_main);
	}
	
	/**
	 * 清空所有缓存区
	 */
	public static void doClearAll(){
		CacheAccessor.getInstance().resetCache();
	}
	
	public static CacheAccessor getInstance(){
		return SpringContext.getApplicationContext().getBean(CacheAccessor.class);
	}
	
	/********************************************静态操作方法区结束******************************************** */

	@Resource(name="cacheManager")
	private CacheManager cacheManager;

	/**
	 * 初始化配置环境
	 */
	@Autowired
	public CacheAccessor(CacheErrorHandler errorHandler) {
		super(errorHandler);
		LOGGER.info("CacheAccessor initialize successfully.");
	}
	
	public Object get(String cacheName, Object key){
		Cache.ValueWrapper value = doGet(this.cacheManager.getCache(cacheName), key);
		return value==null?null:value.get();
	}
	
	public Object getInCaches(Object key){
		for (String cacheName : this.cacheManager.getCacheNames()) {
			Object value = get(cacheName, key);
			if(value != null){
				return value;
			}
		}
		return null;
	}
	
	public void put(String cacheName, Object key, Object result){
		doPut(this.cacheManager.getCache(cacheName), key, result);
	}
	
	public void putIfAbsent(String cacheName, Object key, Object result) {
		Cache cache = this.cacheManager.getCache(cacheName);		
		try {
			cache.putIfAbsent(key, result);
		}
		catch (RuntimeException e) {
			getErrorHandler().handleCachePutError(e, cache, key, result);
		}
	}
	
	public void evict(String cacheName, Object key){
		doEvict(this.cacheManager.getCache(cacheName), key);
	}
	
	public void clear(String cacheName){
		doClear(this.cacheManager.getCache(cacheName));
	}
	
	public void resetCache(){
		for (String cacheName : this.cacheManager.getCacheNames()) {
			clear(cacheName);
		}
		LOGGER.warn("All of the cache was cleared, please check that it is invoked by a necessary operation!");
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	/*
	protected <T> T getBean(String beanName, Class<T> expectedType) {
		return BeanFactoryAnnotationUtils.qualifiedBeanOfType(this.applicationContext, expectedType, beanName);
	}*/

	/**
	 * 同时从主缓存区和本地缓存区读取值
	 * @param key
	 */
	public static Object doGetMainLocal(Object key){
		Object o = doGet(R.cache.cache_region_local, key);
		if (o == null){
			o = doGet(R.cache.cache_region_main, key);
		}
		return o;
	}

	/**
	 * 同时向主缓存区和本地缓存区写入值
	 * @param key
	 * @param result
	 */
	public static void doPutMainLocal(Object key, Object result){
		doPut(R.cache.cache_region_local, key, result);
		doPut(R.cache.cache_region_main, key, result);
	}

	/**
	 * 同时从主缓存区和本地缓存区清除值
	 * @param key
	 */
	public static void doEvictMainLocal(Object key){
		doEvict(R.cache.cache_region_local, key);
		doEvict(R.cache.cache_region_main, key);
	}

	/**
	 * 清空本地缓存区
	 */
	public static void doClearLocal(){
		doClear(R.cache.cache_region_local);
	}

}
