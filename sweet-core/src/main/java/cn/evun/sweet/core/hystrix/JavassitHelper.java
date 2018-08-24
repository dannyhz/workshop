package cn.evun.sweet.core.hystrix;

import cn.evun.sweet.core.common.JsonResultDO;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

/**
 * 生成HystrixProperty注解和降级方法的帮助类
 *
 * @author shentao
 * @date 2017/5/1 15:38
 * @since 1.0.0
 */
public class JavassitHelper {

    protected static final Logger LOGGER = LogManager.getLogger();

    /**
     * 下面八个方法运行时自动生成，用于动态加载Hystrix配置参数，因为注解的参数必须是常量
     */
    public static void currentLimitingCommandProperties() {
    }

    public static void currentLimitingThreadPoolProperties() {
    }

    public static void faultTolerantCommandProperties() {
    }

    public static void faultTolerantThreadPoolProperties() {
    }

    public static void currentLimitingCommandPropertiesHighConcurrency() {
    }

    public static void currentLimitingThreadPoolPropertiesHighConcurrency() {
    }

    public static void faultTolerantCommandPropertiesHighConcurrency() {
    }

    public static void faultTolerantThreadPoolPropertiesHighConcurrency() {
    }

    private HystrixConfiguration.HystrixParameters hystrixParameters; //保存Hystrix参数的内部类对象

    /**
     * 接口限流默认降级处理方法
     *
     * @param method 切面方法
     * @param args   切面方法的参数
     */
    public JsonResultDO currentLimitingFallback(Method method, Object[] args) {
        JsonResultDO result = new JsonResultDO();
        result.setSuccess(false);
        result.setMsgCode(hystrixParameters.getCurrentLimitingFallbackMsgCode());
        LOGGER.warn("CurrentLimiting Fallback[className={},methodName={},args={}]", parseFallbackArgs(method, args));
        return result;
    }

    /**
     * 容错隔离默认降级处理方法
     *
     * @param method 切面方法
     * @param args   切面方法的参数
     */
    public JsonResultDO faultTolerantFallback(Method method, Object[] args) {
        JsonResultDO result = new JsonResultDO();
        result.setSuccess(false);
        result.setMsgCode(hystrixParameters.getFaultTolerantFallbackMsgCode());
        LOGGER.warn("FaultTolerant Fallback[className={},methodName={},args={}]", parseFallbackArgs(method, args));
        return result;
    }

    /**
     * 解析切面方法的参数
     */
    private Object[] parseFallbackArgs(Method method, Object[] args) {
        String[] result = new String[3];
        result[0] = method.getDeclaringClass().getName();
        result[1] = method.getName();
        result[2] = ArrayUtils.isNotEmpty(args) ? JSONObject.toJSONString(args) : "";
        return result;
    }

    public void setHystrixParameters(HystrixConfiguration.HystrixParameters hystrixParameters) {
        this.hystrixParameters = hystrixParameters;
    }

    public HystrixConfiguration.HystrixParameters getHystrixParameters() {
        return hystrixParameters;
    }

    /**
     * 单例模式
     */
    private JavassitHelper() {
    }

    private static final JavassitHelper javassitHelper = new JavassitHelper();

    public static JavassitHelper getInstance() {
        return javassitHelper;
    }

}
