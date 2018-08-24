package cn.evun.sweet.core.cache;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.cache.ehcache.EhCacheCacheManager;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.exception.CacheAccessException;

/**
 * Cache管理方案配置。主要提供基于简易环境下的Map形式和生产环境的Ehcache形式缓存方案。
 *
 * @author yangw
 * @since V1.0.0
 */
@Configuration
@ComponentScan(basePackages="cn.evun.sweet.core,cn.**.service", includeFilters={@ComponentScan.Filter({CacheRequired.class})}, useDefaultFilters=false)
@EnableCaching(order=1)  //(mode=AdviceMode.ASPECTJ)主要解决避免对缓存方法的内部调用导致拦截动作失效的问题
public class CacheConfig extends CachingConfigurerSupport { 
	
	protected static final Logger LOGGER = LogManager.getLogger();	

	private static Boolean disableCache = Boolean.FALSE;
	
	@Resource
	private EhCacheCacheManager ehcacheCacheManager;
	@Autowired(required=false)
	private RedisCacheManager redisCacheManager;

	@Bean(name="cacheManager")  
    @Override
    public CacheManager cacheManager() { 
    	List<CacheManager> cacheManagerList = new ArrayList<CacheManager>(10);    	
    	if(disableCache.booleanValue()){
    		return new NoOpCacheManager();
    	}    	    	
    	ehcacheCacheManager.setTransactionAware(true);
		cacheManagerList.add(ehcacheCacheManager);
		if(redisCacheManager != null){
			redisCacheManager.setTransactionAware(true);
			cacheManagerList.add(redisCacheManager);
		}
		cacheManagerList.add(new ConcurrentMapCacheManager(new String[]{R.cache.cache_region_resource,
		                                                                R.cache.cache_region_local}));//静态资源缓存
    	CompositeCacheManager cacheManagers = new CompositeCacheManager();
    	cacheManagers.setFallbackToNoOpCache(true);//防止null异常
    	cacheManagers.setCacheManagers(cacheManagerList);
    	LOGGER.info("CompositeCacheManager named 'cacheManager' was created successfully.It contains these caches : {}", 
    			StringUtils.collectionToCommaDelimitedString(cacheManagers.getCacheNames()));
    	return cacheManagers;  
    }  
  
    @Bean(name="errorHandler")  
    @Override  
    public CacheErrorHandler errorHandler() {  
        return new LogableCacheErrorHandler() ;  
    }  
    
    private class LogableCacheErrorHandler extends SimpleCacheErrorHandler{
    	@Override
    	public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
    		LOGGER.error("Failed to get value from [{}.{}].{}, since exception thrown.",
    				cache.getName(), key, exception.getMessage());
    		MapMessage msg = new MapMessage();
    		msg.put("cache", cache.getName());
    		msg.put("key", key.toString());
    		throw new CacheAccessException("Failed to get from cache: "+msg.getFormattedMessage(), exception);
    	}

    	@Override
    	public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
    		LOGGER.error("Failed to put value in [{}.{}].{}, since exception thrown.",
    				cache.getName(), key, exception.getMessage());
    		MapMessage msg = new MapMessage();
    		msg.put("cache", cache.getName());
    		msg.put("key", key.toString());
    		msg.put("value", value.toString());
    		throw new CacheAccessException("Failed to put in cache: "+msg.getFormattedMessage(), exception);
    	}

    	@Override
    	public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
    		LOGGER.error("Failed to evict [{}.{}].{}, since exception thrown.",
    				cache.getName(), key, exception.getMessage());
    		MapMessage msg = new MapMessage();
    		msg.put("cache", cache.getName());
    		msg.put("key", key.toString());
    		throw new CacheAccessException("Failed to evict cache: "+msg.getFormattedMessage(), exception);
    	}

    	@Override
    	public void handleCacheClearError(RuntimeException exception, Cache cache) {
    		LOGGER.error("Failed to clear cache[{}], since exception thrown.", cache.getName());
    		throw new CacheAccessException("Failed to clear cache: "+cache.getName(), exception);
    	}
    }
 
}  