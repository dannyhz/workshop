package cn.evun.sweet.core.cache.ehcache;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.ehcache.xml.exceptions.XmlConfigurationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * 对原EhCacheManagerFactoryBean进行了修改，使之匹配Ehcache3.1以上的API
 * 
 * @see org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
 * @author yangw
 * @since 1.0.0
 */
public class EhCacheManagerFactoryBean implements FactoryBean<CacheManager>, InitializingBean, DisposableBean {

	protected static final Logger LOGGER = LogManager.getLogger();	

	private Resource configLocation;
	
	private CacheManager cacheManager;
	
	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}
	
	@Override
	public void afterPropertiesSet() throws XmlConfigurationException, IOException {
		LOGGER.info("Initializing EhCache CacheManager");
		
		/*只支持使用路径来制定缓存配置文件地址*/
		Configuration xmlConfig = new XmlConfiguration(this.configLocation.getURL());		
		this.cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);		
	}


	@Override
	public CacheManager getObject() {
		return this.cacheManager;
	}

	@Override
	public Class<? extends CacheManager> getObjectType() {
		return (this.cacheManager != null ? this.cacheManager.getClass() : CacheManager.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void destroy() {
		LOGGER.info("Shutting down EhCache CacheManager");
		this.cacheManager.close();
	}
}
