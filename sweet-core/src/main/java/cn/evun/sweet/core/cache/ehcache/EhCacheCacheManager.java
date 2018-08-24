package cn.evun.sweet.core.cache.ehcache;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.ehcache.CacheManager;
import org.ehcache.Status;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;

import cn.evun.sweet.common.util.Assert;

/**
 * 对原EhCacheCacheManager进行了修改，使之匹配Ehcache3.1以上的API
 *
 * @see org.springframework.cache.ehcache.EhCacheCacheManager
 * @author yangw
 * @since 1.0.0
 */
public class EhCacheCacheManager extends AbstractTransactionSupportingCacheManager{

	private CacheManager cacheManager;
	
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public CacheManager getCacheManager() {
		return this.cacheManager;
	}

	@Override
	protected Collection<Cache> loadCaches() {
		if(Status.UNINITIALIZED.equals(getCacheManager().getStatus())){
			getCacheManager().init();
		}
		Assert.isTrue(Status.AVAILABLE.equals(getCacheManager().getStatus()),
				"An 'alive' EhCache CacheManager is required - current cache is UNINITIALIZED");

		Set<String> names = getCacheManager().getRuntimeConfiguration().getCacheConfigurations().keySet();
		Collection<Cache> caches = new LinkedHashSet<Cache>(names.size());
		for (String name : names) {
			caches.add(new EhCacheCache(name, getCacheManager().getCache(name, Object.class, Object.class)));
		}
		return caches;
	}

	@Override
	protected Cache getMissingCache(String name) {
		// check the EhCache cache again (in case the cache was added at runtime)
		if(this.cacheManager != null && Status.AVAILABLE.equals(this.cacheManager.getStatus())){
			org.ehcache.Cache<Object, Object> ehcache = getCacheManager().getCache(name, Object.class, Object.class);
			if (ehcache != null) {
				return new EhCacheCache(ehcache);
			}
		}
		return null;
	}
}
