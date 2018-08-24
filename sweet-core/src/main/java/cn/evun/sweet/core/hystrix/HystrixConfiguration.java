package cn.evun.sweet.core.hystrix;

import cn.evun.sweet.core.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Hystrix配置类，启用Hystrix拦截器
 *
 * @author shentao
 * @date 2017/4/27 21:12
 * @since 1.0.0
 */
@Configuration
public class HystrixConfiguration {

    //是否启用Hystrix
    @Value("${hystrix.enabled:false}")
    private boolean hystrixEnbled;

    //线程池的大小
    @Value("${hystrix.threadPoolCoreSize:" + R.hystrix.thread_pool_core_size + "}")
    private String threadPoolCoreSize;

    //线程池最大队列长度
    @Value("${hystrix.threadPoolMaxQueueSize:" + R.hystrix.thread_pool_max_queue_size + "}")
    private String threadPoolMaxQueueSize;

    //线程池拒绝请求的临界值
    @Value("${hystrix.threadPoolQueueSizeRejectionThreshold:" + R.hystrix.thread_pool_queue_size_rejection_threshold + "}")
    private String threadPoolQueueSizeRejectionThreshold;

    //请求默认超时时间(毫秒)
    @Value("${hystrix.executionTimeoutInMilliseconds:" + R.hystrix.execution_timeout_in_milliseconds + "}")
    private String executionTimeoutInMilliseconds;

    //触发熔断的最少请求量
    @Value("${hystrix.circuitBreakerRequestVolumeThreshold:" + R.hystrix.circuitBreaker_request_volume_threshold + "}")
    private String circuitBreakerRequestVolumeThreshold;

    //触发熔断的错误比例
    @Value("${hystrix.circuitBreakerErrorThresholdPercentage:" + R.hystrix.circuitBreaker_error_threshold_percentage + "}")
    private String circuitBreakerErrorThresholdPercentage;

    //触发熔断后开始尝试再次执行的时间(毫秒)
    @Value("${hystrix.circuitBreakerSleepWindowInMilliseconds:" + R.hystrix.circuitBreaker_sleep_window_in_milliseconds + "}")
    private String circuitBreakerSleepWindowInMilliseconds;

    //限流最大并发请求数
    @Value("${hystrix.semaphoreMaxConcurrentRrequests:" + R.hystrix.semaphore_max_concurrent_requests + "}")
    private String semaphoreMaxConcurrentRrequests;

    //-----------------------------------------------高并发配置方案start-------------------------------------------------
    //是否使用高并发配置方案
    @Value("${hystrix.highConcurrency.enabled:false}")
    private boolean useHighConcurrency;

    //线程池的大小(高并发)
    @Value("${hystrix.hc.threadPoolCoreSize:" + R.hystrix.thread_pool_core_size_hc + "}")
    private String threadPoolCoreSizeHC;

    //线程池最大队列长度(高并发)
    @Value("${hystrix.hc.threadPoolMaxQueueSize:" + R.hystrix.thread_pool_max_queue_size_hc + "}")
    private String threadPoolMaxQueueSizeHC;

    //线程池拒绝请求的临界值(高并发)
    @Value("${hystrix.hc.threadPoolQueueSizeRejectionThreshold:" + R.hystrix.thread_pool_queue_size_rejection_threshold_hc + "}")
    private String threadPoolQueueSizeRejectionThresholdHC;

    //请求默认超时时间(毫秒)(高并发)
    @Value("${hystrix.hc.executionTimeoutInMilliseconds:" + R.hystrix.execution_timeout_in_milliseconds_hc + "}")
    private String executionTimeoutInMillisecondsHC;

    //触发熔断的最少请求量(高并发)
    @Value("${hystrix.hc.circuitBreakerRequestVolumeThreshold:" + R.hystrix.circuitBreaker_request_volume_threshold_hc + "}")
    private String circuitBreakerRequestVolumeThresholdHC;

    //触发熔断的错误比例(高并发)
    @Value("${hystrix.hc.circuitBreakerErrorThresholdPercentage:" + R.hystrix.circuitBreaker_error_threshold_percentage_hc + "}")
    private String circuitBreakerErrorThresholdPercentageHC;

    //触发熔断后开始尝试再次执行的时间(毫秒)(高并发)
    @Value("${hystrix.hc.circuitBreakerSleepWindowInMilliseconds:" + R.hystrix.circuitBreaker_sleep_window_in_milliseconds_hc + "}")
    private String circuitBreakerSleepWindowInMillisecondsHC;

    //限流最大并发请求数(高并发)
    @Value("${hystrix.hc.semaphoreMaxConcurrentRrequests:" + R.hystrix.semaphore_max_concurrent_requests_hc + "}")
    private String semaphoreMaxConcurrentRrequestsHC;
    //-----------------------------------------------高并发配置方案end-------------------------------------------------

    //接口限流默认降级方法返回的message code
    @Value("${hystrix.currentLimitingFallback.msgCode:" + R.exception.excode_hystrix_current_limiting + "}")
    private String currentLimitingFallbackMsgCode;

    //容错隔离默认降级方法返回的message code
    @Value("${hystrix.faultTolerantFallback.msgCode:" + R.exception.excode_hystrix_fault_tolerant + "}")
    private String faultTolerantFallbackMsgCode;

    /**
     * 向spring容器注册CustomHystrixCommandAspect拦截器
     */
    @Bean
    public CustomHystrixCommandAspect customHystrixCommandAspect() throws Exception {
        if (hystrixEnbled) {
            HystrixParameters hystrixParameters = new HystrixParameters();
            JavassitUtil.initHystrixProperties(hystrixParameters);
            JavassitHelper.getInstance().setHystrixParameters(hystrixParameters);
        }
        CustomHystrixCommandAspect customHystrixCommandAspect = new CustomHystrixCommandAspect();
        customHystrixCommandAspect.setHystrixEnbled(hystrixEnbled);
        return customHystrixCommandAspect;
    }

    /**
     * Hystrix参数配置内部类，提供给外部调用
     */
    public class HystrixParameters {

        public String getThreadPoolCoreSize() {
            return threadPoolCoreSize;
        }

        public String getThreadPoolMaxQueueSize() {
            return threadPoolMaxQueueSize;
        }

        public String getThreadPoolQueueSizeRejectionThreshold() {
            return threadPoolQueueSizeRejectionThreshold;
        }

        public String getExecutionTimeoutInMilliseconds() {
            return executionTimeoutInMilliseconds;
        }

        public String getCircuitBreakerRequestVolumeThreshold() {
            return circuitBreakerRequestVolumeThreshold;
        }

        public String getCircuitBreakerErrorThresholdPercentage() {
            return circuitBreakerErrorThresholdPercentage;
        }

        public String getCircuitBreakerSleepWindowInMilliseconds() {
            return circuitBreakerSleepWindowInMilliseconds;
        }

        public String getSemaphoreMaxConcurrentRrequests() {
            return semaphoreMaxConcurrentRrequests;
        }

        public boolean isUseHighConcurrency() {
            return useHighConcurrency;
        }

        public String getThreadPoolCoreSizeHC() {
            return threadPoolCoreSizeHC;
        }

        public String getThreadPoolMaxQueueSizeHC() {
            return threadPoolMaxQueueSizeHC;
        }

        public String getThreadPoolQueueSizeRejectionThresholdHC() {
            return threadPoolQueueSizeRejectionThresholdHC;
        }

        public String getExecutionTimeoutInMillisecondsHC() {
            return executionTimeoutInMillisecondsHC;
        }

        public String getCircuitBreakerRequestVolumeThresholdHC() {
            return circuitBreakerRequestVolumeThresholdHC;
        }

        public String getCircuitBreakerErrorThresholdPercentageHC() {
            return circuitBreakerErrorThresholdPercentageHC;
        }

        public String getCircuitBreakerSleepWindowInMillisecondsHC() {
            return circuitBreakerSleepWindowInMillisecondsHC;
        }

        public String getSemaphoreMaxConcurrentRrequestsHC() {
            return semaphoreMaxConcurrentRrequestsHC;
        }

        public String getCurrentLimitingFallbackMsgCode() {
            return currentLimitingFallbackMsgCode;
        }

        public String getFaultTolerantFallbackMsgCode() {
            return faultTolerantFallbackMsgCode;
        }
    }

}
