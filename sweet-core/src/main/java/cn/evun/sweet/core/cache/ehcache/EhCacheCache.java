package cn.evun.sweet.core.cache.ehcache;

import org.ehcache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import cn.evun.sweet.common.util.Assert;

/**
 * 对原EhCacheCache进行了修改，使之匹配Ehcache3.1以上的API
 *
 * @see org.springframework.cache.ehcache.EhCacheCache
 * @author yangw
 * @since 1.0.0
 */
public class EhCacheCache implements org.springframework.cache.Cache {

	private final Cache<Object, Object> cache;
	
	private String cacheName;

	public EhCacheCache(Cache<Object, Object> ehcache) {
		Assert.notNull(ehcache, "Ehcache must not be null");
		this.cache = ehcache;
	}

	public EhCacheCache(String name, Cache<Object, Object> ehcache) {
		Assert.hasText(name, "Ehcache name must not be null");
		this.cacheName = name;
		Assert.notNull(ehcache, "Ehcache must not be null");
		this.cache = ehcache;
	}

	@Override
	public final String getName() {
		return this.cacheName;
	}
	
	public final void setName(String name) {
		this.cacheName = name;
	}

	@Override
	public final Cache<Object, Object> getNativeCache() {
		return this.cache;
	}

	@Override
	public ValueWrapper get(Object key) {
		Object element = this.cache.get(key);
		return toWrapper(element);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		Object value = this.cache.get(key);
		if (value != null && type != null && !type.isInstance(value)) {
			throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
		}
		return (T) value;
	}

	@Override
	public void put(Object key, Object value) {
		this.cache.put(key, value);
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		Object existingElement = this.cache.putIfAbsent(key, value);
		return toWrapper(existingElement);
	}

	@Override
	public void evict(Object key) {
		this.cache.remove(key);
	}

	@Override
	public void clear() {
		this.cache.clear();
	}

	private ValueWrapper toWrapper(Object element) {
		return (element != null ? new SimpleValueWrapper(element) : null);
	}

}
