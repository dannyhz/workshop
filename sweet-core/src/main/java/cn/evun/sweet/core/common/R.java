package cn.evun.sweet.core.common;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class R {
	
	public static final class bussiness {
		
		public static final int default_pagesize = 20;			
				
	}
		
	public static final class request {
		
		public static final String mdc_ip = "req_ip";
		public static final String mdc_method = "req_method";
		public static final String mdc_locale = "req_locale";
		public static final String mdc_timezone = "req_timezone";
		public static final String mdc_principal = "req_principal";
		public static final String mdc_uri = "req_uri";
		public static final String mdc_time = "req_time";
		
		/** 鉴权过滤器会将使用过的SessionCtx使用该key置入request中，ContextChainToIntercepter及后续过程可以直接使用 */
		public static final String sessctx_key = "sessctx_attr";		
		
	}
	
	public static final class session {
		
		public static final String user_info = "sess_user_info";
		public static final String org_info = "sess_org_info";
		public static final String tenant_info = "sess_tenant_info";
		public static final String menu_info = "sess_menu_info";
		
	}
	
	public static final class viewname {
		
		public static final String jsonview = "jsonView";
		public static final String excelview = "jeecgExcelView";
		public static final String templateexcelview = "jeecgTemplateExcelView";
		public static final String templatewordview = "jeecgTemplateWordView";
		public static final String mapexcelview = "jeecgMapExcelView";
		
	}
		
	public static final class log {

		/** 定义log重写策略配置中识别是自定义重写策略的属性名前缀 */
		public static final String log_rewritepolicy_config_flag = "rewrite";
		/** 定义log重写过滤识别的标记,需要对日志进行重写时，请使用此标记 */
		public static final Marker log_marker_rewrite = MarkerManager.getMarker("rewrite");	
		/** 定义监控日志标记-监控 */
		public static final Marker log_marker_monitor = MarkerManager.getMarker("monitor");
		/** 定义监控日志标记-推送 */
		public static final Marker log_marker_push = MarkerManager.getMarker("push");
		/** 定义监控ONS日志标记 */
		public static final Marker log_marker_ons = MarkerManager.getMarker("ons");
		/** 定义异常的标记 */
		public static final Marker log_marker_exception_root = MarkerManager.getMarker("SweetException");
		public static final Marker log_marker_exception_cache = MarkerManager.getMarker("CacheAccessException").addParents(log_marker_exception_root);
		public static final Marker log_marker_exception_validate = MarkerManager.getMarker("ValidateException").addParents(log_marker_exception_root);
		public static final Marker log_marker_exception_viewdef = MarkerManager.getMarker("ViewDefiningException");

		public static final String log_level_info = "info";
		public static final String log_level_debug = "debug";
		public static final String log_level_error = "error";
		public static final String log_level_off = "off";
		public static final String log_level_trace = "trace";
		public static final String log_level_fatal = "fatal";
		public static final String log_level_all = "all";
		public static final String log_level_warn = "warn";

	}

	public static final class exception {

		public static final String excode_default = "excode.default";
		public static final String excode_default_custom = "excode.default.customization";
		public static final String excode_objectexist = "excode.object.exist";
		public static final String excode_objectnotexist = "excode.object.notexist";
		public static final String excode_resource_notexist = "excode.resource.notexist";
		public static final String excode_illegalargument = "excode.illegalargument";
		public static final String excode_registyservice_notexist="excode.registyservice.notexist";
		public static final String excode_registyservice_invokefailed="excode.registyservice.invokefailed";
		public static final String excode_asyncreq_timeout = "excode.asyncreq.timeout";
		public static final String excode_connect_login = "excode.connect.login";
		public static final String excode_connect_refused = "excode.connect.refused";
		public static final String excode_connect_io = "excode.connect.io";
		public static final String excode_file_download = "excode.file.download";
		public static final String excode_file_upload = "excode.file.upload";
		public static final String excode_file_delete = "excode.file.delete";
		public static final String excode_file_rename = "excode.file.rename";
		public static final String excode_dir_create = "excode.dir.create";
		public static final String excode_dir_delete = "excode.dir.delete";
		public static final String excode_hystrix_current_limiting = "excode.hystrix.current.limiting";
		public static final String excode_hystrix_fault_tolerant = "excode.hystrix.fault.tolerant";
		
	}

	public static final class message {

		public static final String msgcode_default = "msgcode.default";
		public static final String msgcode_error = "msgcode.error";
		public static final String msgcode_error_validate = "msgcode.error.validate";
		public static final String msgcode_error_responsebody = "msgcode.error.responsebody";

	}
	
	public static final class cache {

		/** 主缓存区的名称标记 */
		public static final String cache_region_main = "cache_main";
		/** Session缓存区的名称标记 */
		public static final String cache_region_session = "cache_session";
		/** 登录验证码缓存区的名称标记 */
		public static final String cache_region_verifycode = "cache_verifycode";
		/** 静态资源缓存区的名称标记 */
		public static final String cache_region_resource = "cache_resource";
		/** 本地化缓存区的名称标记(ConcurrentHashMap) */
		public static final String cache_region_local = "cache_local";
		/** 本地化缓存区的名称标记(ehcache) */
		public static final String cache_region_local_ehcache = "cache_ehcache";
		/** 帐号登录错误次数存储key值的前缀 */
		public static final String cache_login_errorinput = "login_errorinput_";
		/** 在线用户缓存区域 */
		public static final String cache_online_map = "cache_online_map";
		/** UseCache缓冲区前缀 */
		public static final String cache_usecache = "cache_usecache_";		
		/** UserCache过期时间等级 */
    	public static final String usecache_expire_fast = "USECACHE_EXPIRE_FAST";
    	public static final String usecache_expire_normal = "USECACHE_EXPIRE_NORMAL";
    	public static final String usecache_expire_slowly = "USECACHE_EXPIRE_SLOWLY";
    	public static final String usecache_expire_slowest = "USECACHE_EXPIRE_SLOWEST";
		
	}

    public static final class redisKey {

        /** 防止重复提交生成token的前缀在Redis中存放的key */
        public static final String token_pre = "WEB_TOKEN_";
		/** 防止重复提交生成的token在Redis中存活时间(秒) */
		public static final long token_expire = 60;
    }

	public static final class hystrix {

		/** 线程池的大小 */
		public static final String thread_pool_core_size = "20";
		/** 线程池最大队列长度 */
		public static final String thread_pool_max_queue_size = "-1";
		/** 线程池拒绝请求的临界值 */
		public static final String thread_pool_queue_size_rejection_threshold = "10";
        /** 请求默认超时时间(毫秒) */
		public static final String execution_timeout_in_milliseconds = "3000";
		/** 触发熔断的最少请求量 */
		public static final String circuitBreaker_request_volume_threshold = "40";
		/** 触发熔断的错误比例 */
		public static final String circuitBreaker_error_threshold_percentage = "10";
		/** 触发熔断后开始尝试再次执行的时间(毫秒) */
		public static final String circuitBreaker_sleep_window_in_milliseconds = "30000";
		/** 限流最大并发请求数 */
		public static final String semaphore_max_concurrent_requests = "20";

		/** 线程池的大小(高并发) */
		public static final String thread_pool_core_size_hc = "50";
		/** 线程池最大队列长度(高并发) */
		public static final String thread_pool_max_queue_size_hc = "-1";
		/** 线程池拒绝请求的临界值(高并发) */
		public static final String thread_pool_queue_size_rejection_threshold_hc = "25";
		/** 请求默认超时时间(毫秒)(高并发) */
		public static final String execution_timeout_in_milliseconds_hc = "3000";
		/** 触发熔断的最少请求量(高并发) */
		public static final String circuitBreaker_request_volume_threshold_hc = "100";
		/** 触发熔断的错误比例(高并发) */
		public static final String circuitBreaker_error_threshold_percentage_hc = "25";
		/** 触发熔断后开始尝试再次执行的时间(毫秒)(高并发) */
		public static final String circuitBreaker_sleep_window_in_milliseconds_hc = "30000";
		/** 限流最大并发请求数(高并发) */
		public static final String semaphore_max_concurrent_requests_hc = "50";

		/** 接口限流默认降级方法名 */
		public static final String currentlimiting_fallback_method_name = "currentLimitingFallback";
		/** 容错隔离默认降级方法名 */
		public static final String faultTolerant_fallback_method_name = "faultTolerantFallback";

		public enum ExecutionIsolationStrategy { //隔离方式
			THREAD, SEMAPHORE
		}

		/** Hystrix应用场景-接口限流 */
		public static final String CURRENTLIMITING_SCENE = com.netflix.hystrix.contrib.javanica.conf.R.CURRENTLIMITING_SCENE;
		/** Hystrix应用场景-容错隔离 */
		public static final String FAULTTOLERANT_SCENE = com.netflix.hystrix.contrib.javanica.conf.R.FAULTTOLERANT_SCENE;
	}

}
