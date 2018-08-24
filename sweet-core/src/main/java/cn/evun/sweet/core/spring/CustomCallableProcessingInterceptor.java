package cn.evun.sweet.core.spring;

import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.CallableProcessingInterceptorAdapter;

import cn.evun.sweet.common.util.web.WebUtils;
import cn.evun.sweet.core.common.JsonResultDO;
import cn.evun.sweet.core.common.R;

/**
 * 返回Callable的异步请求的拦截器，主要实现了超时处理
 *
 * @author yangw
 * @since 1.0.0
 */
public class CustomCallableProcessingInterceptor extends CallableProcessingInterceptorAdapter {

	@Override
	public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
		HttpServletResponse servletResponse = request.getNativeResponse(HttpServletResponse.class);	
		if (!servletResponse.isCommitted()) {
			if(WebUtils.isAjaxRequest(request.getNativeRequest(HttpServletRequest.class)) 
					|| task instanceof JsonResultCallable) {
				return new JsonResultDO(false, R.exception.excode_asyncreq_timeout, "503");
			}else {
				servletResponse.sendError(HttpStatus.SERVICE_UNAVAILABLE.value());
			}
		}
		
		return CallableProcessingInterceptor.RESPONSE_HANDLED;
	}
}
