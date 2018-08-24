package cn.evun.sweet.core.cache.redis;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 用于异步推送消息的线程池调度
 *
 * @author yangw
 * @since 1.0.0
 */
@Component("redisTaskExecutor")
public class RedisSubTaskExecutor extends ThreadPoolTaskExecutor {

	private static final long serialVersionUID = -3409874055491674692L;
	
	protected static final Logger LOGGER = LogManager.getLogger();	

	@Value("${redislistener.threadpool.corepoolsize}")
	private int corePoolSize = 10;

	@Value("${redislistener.threadpool.maxpoolsize}")
	private int maxPoolSize = 1000;

	@Value("${redislistener.threadpool.keepaliveseconds}")
	private int keepAliveSeconds = 60;

	@Value("${redislistener.threadpool.queuecapacity}")
	private int queueCapacity = 10000;
	
	
	@Override
	public void afterPropertiesSet() {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("RedisSubTaskExecutor ready! corePoolSize:{}, maxPoolSize:{}, keepAliveSeconds:{}, queueCapacity:{}.",
					corePoolSize, maxPoolSize, keepAliveSeconds, queueCapacity);
		}
		super.setCorePoolSize(corePoolSize);
		super.setMaxPoolSize(maxPoolSize);
		super.setKeepAliveSeconds(keepAliveSeconds);
		super.setQueueCapacity(queueCapacity);
		super.setAllowCoreThreadTimeOut(true);
		super.afterPropertiesSet();
		/*在任务线程池占满导致新的PushMessage无法获得推送机会时的处理方案*/
		super.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				throw new RejectedExecutionException("Task " + r.toString() + " rejected from " +  executor.toString());
			}
		});

	}
	
}
