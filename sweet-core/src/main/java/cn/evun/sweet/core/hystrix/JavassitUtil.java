package cn.evun.sweet.core.hystrix;

import cn.evun.sweet.core.common.R;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义注解工具类，用于HystrixCommand、HystrixProperty等注解的转换和解析<br/>
 * 利用javassist字节码技术
 *
 * @author shentao
 * @date 2017/4/28 9:43
 * @since 1.0.0
 */
public class JavassitUtil {

    private static final ClassLoader DEFAULT_CLASS_LOADER = JavassitUtil.class.getClassLoader();//默认类加载器
    private static final String CLASS_PATH = DEFAULT_CLASS_LOADER.getResource("").getPath();//系统Class Path
    private static final ClassPool CLASS_POOL = ClassPool.getDefault();//javassist类池

    static { //向javassist容器注册类
        CLASS_POOL.appendClassPath(new ClassClassPath(JavassitHelper.class));
        CLASS_POOL.appendClassPath(new ClassClassPath(HystrixProperty.class));
    }

    private static final String ANNOTATION_HELPER_CLASS_NAME = "cn.evun.sweet.core.hystrix.JavassitHelper";
    private static final String HYSTRIXPROPERTY_CLASS_NAME = "com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty";
    private static final CtClass HELPER_CT_CLAZZ = CLASS_POOL.getOrNull(ANNOTATION_HELPER_CLASS_NAME);

    public static HystrixProperty[] CURRENTLIMITING_COMMANDPROPERTIES;//接口限流命令参数
    public static HystrixProperty[] CURRENTLIMITING_THREADPOOLPROPERTIES;//接口限流线程池参数
    public static HystrixProperty[] FAULTTOLERANT_COMMANDPROPERTIES;//容错隔离命令参数
    public static HystrixProperty[] FAULTTOLERANT_THREADPOOLPROPERTIES;//容错隔离线程池参数

    public static HystrixProperty[] CURRENTLIMITING_COMMANDPROPERTIES_HC;//接口限流命令参数(高并发)
    public static HystrixProperty[] CURRENTLIMITING_THREADPOOLPROPERTIES_HC;//接口限流线程池参数(高并发)
    public static HystrixProperty[] FAULTTOLERANT_COMMANDPROPERTIES_HC;//容错隔离命令参数(高并发)
    public static HystrixProperty[] FAULTTOLERANT_THREADPOOLPROPERTIES_HC;//容错隔离线程池参数(高并发)

    /**
     * 初始化Hystrix参数
     *
     * @param hystrixParameters 保存Hystrix参数的内部类对象
     */
    public static void initHystrixProperties(HystrixConfiguration.HystrixParameters hystrixParameters) throws NotFoundException, NoSuchMethodException,
            ClassNotFoundException, IOException, CannotCompileException {
        ConstPool constPool = HELPER_CT_CLAZZ.getClassFile().getConstPool();//类常量池
        /* 获取方法句柄 */
        CtMethod currentLimitingCoproMethod = HELPER_CT_CLAZZ.getDeclaredMethod("currentLimitingCommandProperties");
        CtMethod faultTolerantCoproMethod = HELPER_CT_CLAZZ.getDeclaredMethod("faultTolerantCommandProperties");
        CtMethod faultTolerantThproMethod = HELPER_CT_CLAZZ.getDeclaredMethod("faultTolerantThreadPoolProperties");
        MethodInfo currentLimitingCoproMethodInfo = currentLimitingCoproMethod.getMethodInfo();
        MethodInfo faultTolerantCoproMethodInfo = faultTolerantCoproMethod.getMethodInfo();
        MethodInfo faultTolerantThproMethodInfo = faultTolerantThproMethod.getMethodInfo();

        /* 初始化CurrentLimiting命令参数 */
        AnnotationsAttribute annotationsAttribute1 = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        annotationsAttribute1.setAnnotations(new Annotation[]{genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, hystrixParameters.getExecutionTimeoutInMilliseconds()),
                genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, R.hystrix.ExecutionIsolationStrategy.SEMAPHORE.name()),
                genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, hystrixParameters.getSemaphoreMaxConcurrentRrequests())});
        currentLimitingCoproMethodInfo.addAttribute(annotationsAttribute1);
        /* 初始化FaultTolerant命令参数 */
        AnnotationsAttribute annotationsAttribute3 = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        annotationsAttribute3.setAnnotations(new Annotation[]{genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, hystrixParameters.getExecutionTimeoutInMilliseconds()),
                genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, hystrixParameters.getCircuitBreakerRequestVolumeThreshold()),
                genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, hystrixParameters.getCircuitBreakerErrorThresholdPercentage()),
                genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, hystrixParameters.getCircuitBreakerSleepWindowInMilliseconds())});
        faultTolerantCoproMethodInfo.addAttribute(annotationsAttribute3);
        /* 初始化FaultTolerant命令参数 */
        AnnotationsAttribute annotationsAttribute4 = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        annotationsAttribute4.setAnnotations(new Annotation[]{genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.CORE_SIZE, hystrixParameters.getThreadPoolCoreSize()),
                genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.MAX_QUEUE_SIZE, hystrixParameters.getThreadPoolMaxQueueSize()),
                genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, hystrixParameters.getThreadPoolQueueSizeRejectionThreshold())});
        faultTolerantThproMethodInfo.addAttribute(annotationsAttribute4);

        initHystrixPropertiesHighConcurrency(constPool, hystrixParameters);

        HELPER_CT_CLAZZ.writeFile(CLASS_PATH);//修改过的字节码写入本地磁盘
        initHystrixPropertyArrays();
        initHystrixPropertyArraysHighConcurrency();
    }

    /**
     * 初始化Hystrix高并发配置参数
     */
    public static void initHystrixPropertiesHighConcurrency(ConstPool constPool, HystrixConfiguration.HystrixParameters hystrixParameters)
            throws NotFoundException, NoSuchMethodException, ClassNotFoundException, IOException, CannotCompileException {
        /* 获取方法句柄 */
        CtMethod currentLimitingCoproMethod = HELPER_CT_CLAZZ.getDeclaredMethod("currentLimitingCommandPropertiesHighConcurrency");
        CtMethod faultTolerantCoproMethod = HELPER_CT_CLAZZ.getDeclaredMethod("faultTolerantCommandPropertiesHighConcurrency");
        CtMethod faultTolerantThproMethod = HELPER_CT_CLAZZ.getDeclaredMethod("faultTolerantThreadPoolPropertiesHighConcurrency");
        MethodInfo currentLimitingCoproMethodInfo = currentLimitingCoproMethod.getMethodInfo();
        MethodInfo faultTolerantCoproMethodInfo = faultTolerantCoproMethod.getMethodInfo();
        MethodInfo faultTolerantThproMethodInfo = faultTolerantThproMethod.getMethodInfo();

        /* 初始化CurrentLimiting命令参数 */
        AnnotationsAttribute annotationsAttribute1 = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        annotationsAttribute1.setAnnotations(new Annotation[]{genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, hystrixParameters.getExecutionTimeoutInMillisecondsHC()),
                genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, R.hystrix.ExecutionIsolationStrategy.SEMAPHORE.name()),
                genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, hystrixParameters.getSemaphoreMaxConcurrentRrequestsHC())});
        currentLimitingCoproMethodInfo.addAttribute(annotationsAttribute1);
        /* 初始化FaultTolerant命令参数 */
        AnnotationsAttribute annotationsAttribute3 = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        annotationsAttribute3.setAnnotations(new Annotation[]{genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, hystrixParameters.getExecutionTimeoutInMillisecondsHC()),
                genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, hystrixParameters.getCircuitBreakerRequestVolumeThresholdHC()),
                genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, hystrixParameters.getCircuitBreakerErrorThresholdPercentageHC()),
                genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, hystrixParameters.getCircuitBreakerSleepWindowInMillisecondsHC())});
        faultTolerantCoproMethodInfo.addAttribute(annotationsAttribute3);
        /* 初始化FaultTolerant命令参数 */
        AnnotationsAttribute annotationsAttribute4 = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        annotationsAttribute4.setAnnotations(new Annotation[]{genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.CORE_SIZE, hystrixParameters.getThreadPoolCoreSizeHC()),
                genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.MAX_QUEUE_SIZE, hystrixParameters.getThreadPoolMaxQueueSizeHC()),
                genHystrixPropertyAnnotation(constPool, HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, hystrixParameters.getThreadPoolQueueSizeRejectionThresholdHC())});
        faultTolerantThproMethodInfo.addAttribute(annotationsAttribute4);
    }

    /**
     * 生成注解代理类
     */
    private static Annotation genHystrixPropertyAnnotation(ConstPool constPool, String name, String value) throws ClassNotFoundException {
        Annotation annotation = new Annotation(HYSTRIXPROPERTY_CLASS_NAME, constPool);
        annotation.addMemberValue("name", new StringMemberValue(name, constPool));
        annotation.addMemberValue("value", new StringMemberValue(value, constPool));
        return annotation;
    }

    /**
     * 初始化HystrixProperty注解数组
     */
    private static void initHystrixPropertyArrays() throws NotFoundException, ClassNotFoundException, NoSuchMethodException {
        CURRENTLIMITING_COMMANDPROPERTIES = genHystrixProperties(HELPER_CT_CLAZZ.getDeclaredMethod("currentLimitingCommandProperties").getAnnotations());
        CURRENTLIMITING_THREADPOOLPROPERTIES = genHystrixProperties(HELPER_CT_CLAZZ.getDeclaredMethod("currentLimitingThreadPoolProperties").getAnnotations());
        FAULTTOLERANT_COMMANDPROPERTIES = genHystrixProperties(HELPER_CT_CLAZZ.getDeclaredMethod("faultTolerantCommandProperties").getAnnotations());
        FAULTTOLERANT_THREADPOOLPROPERTIES = genHystrixProperties(HELPER_CT_CLAZZ.getDeclaredMethod("faultTolerantThreadPoolProperties").getAnnotations());
    }

    /**
     * 初始化HystrixProperty注解数组
     */
    private static void initHystrixPropertyArraysHighConcurrency() throws NotFoundException, ClassNotFoundException, NoSuchMethodException {
        CURRENTLIMITING_COMMANDPROPERTIES_HC = genHystrixProperties(HELPER_CT_CLAZZ.getDeclaredMethod("currentLimitingCommandPropertiesHighConcurrency").getAnnotations());
        CURRENTLIMITING_THREADPOOLPROPERTIES_HC = genHystrixProperties(HELPER_CT_CLAZZ.getDeclaredMethod("currentLimitingThreadPoolPropertiesHighConcurrency").getAnnotations());
        FAULTTOLERANT_COMMANDPROPERTIES_HC = genHystrixProperties(HELPER_CT_CLAZZ.getDeclaredMethod("faultTolerantCommandPropertiesHighConcurrency").getAnnotations());
        FAULTTOLERANT_THREADPOOLPROPERTIES_HC = genHystrixProperties(HELPER_CT_CLAZZ.getDeclaredMethod("faultTolerantThreadPoolPropertiesHighConcurrency").getAnnotations());
    }

    /**
     * 生成注解数组
     *
     * @param annotations 原注解数组
     * @return
     */
    private static HystrixProperty[] genHystrixProperties(Object[] annotations) {
        HystrixProperty[] hystrixProperties = new HystrixProperty[annotations.length];
        for (int i = 0; i < annotations.length; i++) {
            hystrixProperties[i] = (HystrixProperty) annotations[i];
        }
        return hystrixProperties;
    }

    private static final Map<String, java.lang.reflect.Method> FALLBACK_METHOD_MAP = new ConcurrentHashMap<>(); //存储所有降级方法的Map

    /**
     * 获取降级处理方法
     *
     * @param fallbackMethodName 方法名称
     */
    public static java.lang.reflect.Method genFallbackMethod(String fallbackMethodName) {
        java.lang.reflect.Method method = FALLBACK_METHOD_MAP.get(fallbackMethodName);
        if (method == null) {
            try {
                method = JavassitHelper.class.getDeclaredMethod(fallbackMethodName, java.lang.reflect.Method.class, Object[].class);
                FALLBACK_METHOD_MAP.put(fallbackMethodName, method);
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
        return method;
    }

}