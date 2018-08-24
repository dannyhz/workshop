package cn.evun.sweet.core.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.common.CommUtil;
import cn.evun.sweet.core.common.JsonResultDO;
import cn.evun.sweet.core.common.R;

/**
 * 基础服务功能的拦截器
 *
 * @author yangw
 * @since 1.0.0
 */
@Aspect
@Component
public class GlobalAspect {
	
	 protected static final Logger LOGGER = LogManager.getLogger();
	
	public GlobalAspect(){
		LOGGER.info("GlobalAspect initialize successfully.");
	}

	/**
	 * 在控制器处理之前，如果有验证bresult的异常。则直接按标准方式返回，不执行控制单元。
	 */
	@Around("execution(public * cn..controller.*.*(..,org.springframework.validation.BindingResult))")
    public Object checkBindingResult(ProceedingJoinPoint joinPoint) throws Throwable {		
		if(LOGGER.isDebugEnabled()){
			MethodSignature method=(MethodSignature)joinPoint.getSignature();
			LOGGER.debug("{}.{} was catched by checkbindingresult", joinPoint.getTarget().getClass().getName(), method.getName());
		}
		
		Object[] args = joinPoint.getArgs();
		BindingResult bresult = (BindingResult)args[args.length-1];
		if(bresult.hasErrors()){			
			return CommUtil.populateBindingResult(new JsonResultDO(), bresult);
		}else {
			return joinPoint.proceed(args);
		}	
	}
	
	/**
	 * 耗时服务的监控，如果服务耗时超过3秒，将被日志记录。
	 */
	@Around("execution(public * cn.jyc..service.*.*(..))")
    public Object logRemoteService(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature method=(MethodSignature)joinPoint.getSignature();
        Object[] args=joinPoint.getArgs();

        StringBuffer params=new StringBuffer();
        if (args != null) {
            for (int i=0; i < args.length; i++) {
                params.append(",param" + (i + 1) + ":");
                params.append(args[i] == null ? null : args[i].toString());
            }
        }
        String paramStr=params.toString();
        paramStr=StringUtils.hasText(paramStr) ? paramStr.substring(1) : "";

        long start=System.currentTimeMillis();
        Object response = joinPoint.proceed();
        long end=System.currentTimeMillis();

        if(end - start > 3000){
        	LOGGER.warn(R.log.log_marker_monitor, "Time consuming:{}|interface:{}.{}({})", (end - start),
                    joinPoint.getTarget().getClass().getName(), method.getName(), paramStr);
        }

        return response;
    }
}
