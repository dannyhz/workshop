package cn.evun.sweet.core.spring;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.common.JsonResultDO;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.exception.SweetException;
import cn.evun.sweet.core.exception.ValidateException;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

/**   
 * 扩展mvc层对@ResponseBody方法响应的请求的异常全局统一处理方案。<br/>
 * 如果有一些特殊的错误想转到特定的view时，而不希望进入该处理方案，请在控制层直接抛出ModelAndViewDefiningException，并指定view名称。
 * 
 * @author yangw   
 * @since V1.0.0   
 */
public class ResponseBodyHandlerExceptionResolver extends ExceptionHandlerExceptionResolver {

	protected static final Logger LOGGER = LogManager.getLogger();
	
	public ResponseBodyHandlerExceptionResolver() {
		super();
	}
	
	@Override
	protected ModelAndView doResolveHandlerMethodException( HttpServletRequest request, HttpServletResponse response,
			HandlerMethod handlerMethod, Exception ex){
		if(handlerMethod == null || handlerMethod.getMethod() == null){
			return null;
		}
		
		Method method = handlerMethod.getMethod(); 
		
		//记录日志
		if(SweetException.class.isInstance(ex)){
			if(!ValidateException.class.isInstance(ex)){
				SweetException se = (SweetException)ex;
				se.setLogPoint("excepiton in "+handlerMethod.getBean().getClass().getName()+"."+method.getName());
				se.writeLog(LOGGER);
			}
		}else {
			if(MissingServletRequestParameterException.class.isInstance(ex)){
				LOGGER.error("Catch a exception in handler[{}.{}]", handlerMethod.getBean().getClass().getName(), method.getName(), ex);
			}
		}
		
		
		/*使用@ExcetionHandler来处理该异常*/
		ModelAndView returnValue = super.doResolveHandlerMethodException(request, response, handlerMethod, ex);		
		if (AnnotationUtils.findAnnotation(method, ResponseBody.class)!=null
				|| AnnotationUtils.findAnnotation(handlerMethod.getBean().getClass(), RestController.class)!=null) {          	
    		try{
				ResponseStatus responseStatusAnn = AnnotationUtils.findAnnotation(method, ResponseStatus.class);
				if (responseStatusAnn != null) {
					HttpStatus responseStatus = responseStatusAnn.value();
					String reason = responseStatusAnn.reason();
					if (!StringUtils.hasText(reason)) {
						response.setStatus(responseStatus.value());
					} else {
						try {
							response.sendError(responseStatus.value(), reason);
						} catch (IOException e) {
						}
					}
				}
				
				ServletWebRequest webRequest = new ServletWebRequest(request, response);
				//对非sweet异常进行包装，获得编号和国际化内容用于客户化展现
				String exCode = R.message.msgcode_error_responsebody;
                Object[] arguments = null;
				if(SweetException.class.isInstance(ex)){
					exCode = ((SweetException)ex).getExCode();
                    arguments = ((SweetException)ex).getArguments();
				}
				/*如果没有ExceptionHandler注解那么returnValue就为空*/ 
				if (returnValue == null) {
					handleResponseBody(new JsonResultDO(false,exCode,arguments), webRequest);
				}else {
					JsonResultDO resp = new JsonResultDO(false,exCode,arguments);
					for(Map.Entry<String, Object> attr : returnValue.getModel().entrySet()){
						resp.addAttribute(attr.getKey(), attr.getValue());
					}
					handleResponseBody(resp, webRequest);
				}  
				
				return new ModelAndView();//中断异常链的执行
    		}catch(Exception e){
    			LOGGER.error("Failed to wirte exception message to reponse in json data",e);
    		}    		
        }  
        
        return returnValue;
        
	}

	@SuppressWarnings("resource")
	private void handleResponseBody(Object returnValue, ServletWebRequest webRequest)
			throws ServletException, IOException {
		HttpInputMessage inputMessage = new ServletServerHttpRequest(webRequest.getRequest());
		List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
		if (acceptedMediaTypes.isEmpty()) {
			acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
		}
		MediaType.sortByQualityValue(acceptedMediaTypes);
		
		HttpOutputMessage outputMessage = new ServletServerHttpResponse(webRequest.getResponse());
		Class<?> returnValueType = returnValue.getClass();
		HttpMessageConverter<Object> messageConverter = new FastJsonHttpMessageConverter();
		for (MediaType acceptedMediaType : acceptedMediaTypes) {
			if (messageConverter.canWrite(returnValueType, acceptedMediaType)) {
				messageConverter.write(returnValue, acceptedMediaType, outputMessage);
				return;
			}
		}
	}

}
