package cn.evun.sweet.core.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import cn.evun.sweet.common.util.web.WebUtils;
import cn.evun.sweet.core.exception.SweetException;
import cn.evun.sweet.core.exception.ValidateException;

/**   
 * mvc非ajax请求的全局异常处理方案,对控制层及以下的未捕获异常进行统一的处理。<br/>
 * 在spring对请求做doDispatcher时，handler执行过程会抛出控制层异常，此处给出一个统一的处理方案。<br/>
 * 如果有一些特殊的错误想转到特定的view时，而不希望进入该处理方案，请在控制层直接抛出ModelAndViewDefiningException，并指定view名称。
 * 
 * @author yangw   
 * @since V1.0.0   
 */
public class NoajaxMappingExceptionResolver extends SimpleMappingExceptionResolver {

	protected static final Logger LOGGER = LogManager.getLogger();
	
	public NoajaxMappingExceptionResolver() {
		super();
	}

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		String methodName = "";
		if(handler != null){
			if (handler instanceof HandlerMethod) {
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				handler = handlerMethod.getBean();
				if(handlerMethod.getMethod()!=null){
					methodName = handlerMethod.getMethod().getName();
				}
			}
			
			//记录日志
			if(SweetException.class.isInstance(ex)){
				if(!ValidateException.class.isInstance(ex)){
					SweetException se = (SweetException)ex;
					se.setLogPoint("excepiton in "+handler.getClass().getName()+"."+methodName);
					se.writeLog(LOGGER);
				}
			}else {
				LOGGER.error("Catch a exception in handler[{}.{}]", handler.getClass().getName(), methodName, ex);
			}		
		}
		
		//对非sweet异常进行包装，获得编号和国际化内容用于客户化展现
		if(!SweetException.class.isInstance(ex)){
			ex = new SweetException(ex.getMessage(), ex);
		}
		
		if (WebUtils.isAjaxRequest(request)) {//对于未使用@ResponseBody注解响应方法的ajax请求的处理
			LOGGER.error("{} apply to a mothod without @ResponseBody mothod ." , request.getRequestURL());
		}
		return super.doResolveException(request, response, handler, ex);		
	}

}
