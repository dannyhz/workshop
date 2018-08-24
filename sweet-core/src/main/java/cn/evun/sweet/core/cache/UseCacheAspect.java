package cn.evun.sweet.core.cache;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.cache.redis.RedisTemplateUtil;
import cn.evun.sweet.core.cas.ContextHolder;
import cn.evun.sweet.core.cas.Token;
import cn.evun.sweet.core.common.R;

import com.netflix.hystrix.contrib.javanica.utils.AopUtils;

/**
 * 缓存拦截器，用于拦截有UseCache注解的方法<br/>
 * 如果方法上有UseCache注解，则优先从缓存中取数据；如果缓存不存在或已过期，则将方法的返回值写入缓存。<br/>
 * 缓存key的生成规则，由以下部分组成：<br/>
 * (1)缓存key前缀 UseCache.keyPrefix() <br/>
 * (2)方法所有传递参数的值，如果参数是对象类型，则该对象必须重写toString()方法，以保证key的唯一性<br/>
 * (3)如果缓存key包含用户ID，则从token中取出用户ID增加到key的末尾
 *
 * @author shentao
 * @date 2017/10/9 16:11
 * @since 1.0.0
 */
@Aspect
@Component
public class UseCacheAspect {

    @Value("${usecache.expired.fast:30}")
    public Integer expired_fast;

    @Value("${usecache.expired.normal:60}")
    public Integer expired_normal;

    @Value("${usecache.expired.slowly:120}")
    public Integer expired_slowly;

    @Value("${usecache.expired.slowest:600}")
    public Integer expired_slowest;


    @Pointcut("@annotation(cn.evun.sweet.core.cache.UseCache)")
    public void useCacheAnnotationPointcut() {
    }

    @Around("useCacheAnnotationPointcut()")
    public Object methodsAnnotatedWithUseCache(final ProceedingJoinPoint joinPoint) throws Throwable {  
    	MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();  
    	String[] paramNames = methodSignature.getParameterNames(); //参数名
    	Object[] args = joinPoint.getArgs();//方法参数值
    	boolean hasArgs = args != null && args.length > 0;//方法是否有参数
        Method method = AopUtils.getMethodFromTarget(joinPoint);//被拦截的方法
        UseCache useCache = AnnotationUtils.findAnnotation(method, UseCache.class);

        StringBuilder sbCacheKey = new StringBuilder();
        if (StringUtils.hasText(useCache.key())) {
            sbCacheKey.append(useCache.key());
        } else {
            sbCacheKey.append(method.getName());
        }
        if (useCache.hasUserId()) {
            Token token = ContextHolder.currentToken();
            if (token != null) {
                sbCacheKey.append("_").append(token.getUserId());
            }
        }
        if (hasArgs) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null && !useCache.excludeParamsIndex().contains(String.valueOf(i))) {
                	if(useCache.pageIndex() && "pageNum".equals(paramNames[i]) && Integer.valueOf(args[i].toString()) != 1){//第一页缓存
                		sbCacheKey = null;
            			break;
                	}
                    sbCacheKey.append("_").append(args[i].toString());
                }
            }
        }

        Object result = null;
        if(sbCacheKey != null){
        	result = RedisTemplateUtil.get(sbCacheKey.toString());
        }
        if (result == null) { //缓存不存在或已过期
            if (!hasArgs)
                result = joinPoint.proceed();
            else
                result = joinPoint.proceed(args);
            if(sbCacheKey != null){
                RedisTemplateUtil.set(sbCacheKey.toString(), result, getExpire(useCache.expireLevel()));
            }
        }

        return result;
    }

    private Integer getExpire(String level) {
        if (R.cache.usecache_expire_fast.equals(level)) {
            return expired_fast;
        } else if (R.cache.usecache_expire_normal.equals(level)) {
            return expired_normal;
        } else if (R.cache.usecache_expire_slowly.equals(level)) {
            return expired_slowly;
        } else if (R.cache.usecache_expire_slowest.equals(level)) {
            return expired_slowest;
        }
        return 0;
    }

}
