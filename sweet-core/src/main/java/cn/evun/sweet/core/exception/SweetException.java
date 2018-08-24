package cn.evun.sweet.core.exception;

import java.util.Locale;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.spring.SpringContext;

/**   
 * sweet框架根异常，主要提供异常编码及国际化、日志等的支持<br/>
 * 该异常为unchecked异常，代表了代码中无法调解的问题，通常需要友好的方式反馈给用户或记录日志，可以由全局异常处理机制统一处理。
 *
 * @author yangw 
 * @since V1.0.0  
 */
public class SweetException extends ContextedRuntimeException {

	private static final long serialVersionUID = 4742437699753327308L;

	/**异常编号（国际化key）*/
	private String exCode = R.exception.excode_default;

	/**国际化信息中包含的参数列表*/
	private Object[] arguments;
	
	/**本异常的日志打印级别*/
	private String loglevel = R.log.log_level_error;
	
	/**异常发生的位置*/
	private String logPoint = "";
	
	/**
	 * 由原msg的入参改为exCode作为入参。
	 * @since 1.1.1
	 */
	public SweetException(String exCode) {
		//super(msg);
		if(StringUtils.hasText(exCode)){
			this.exCode = exCode;
		}
	}
	
	public SweetException(String msg, Throwable cause) {
		super(msg,cause);
	}
	
	public SweetException(String excode, String msg) {
		super(msg);
		if(StringUtils.hasText(excode)){
			this.exCode = excode;
		}
	}
	
	public SweetException(String excode, String msg, Object[] arguments) {
		super(msg);
		if(StringUtils.hasText(excode)){
			this.exCode = excode;
		}
		this.arguments = arguments;
	}

	public SweetException(String excode, Object[] arguments) {
		if(StringUtils.hasText(excode)){
			this.exCode = excode;
		}
		this.arguments = arguments;
	}
	
	/**设置日志级别，在统一异常处理时会根据级别使用不同的打印方式*/
	public SweetException setLogLevel(String loglevel){
		this.loglevel = loglevel;
		return this;
	} 
	
	public String getLogLevel(){
		return this.loglevel;
	}
	
	/**记录异常日志*/
	public SweetException writeLog(Logger logger){
		return writeLog(logger, this.loglevel);
	}
	
	/**记录异常日志*/
	public SweetException writeLog(Logger logger, String loglevel){
		logger.printf(Level.getLevel(loglevel), getMarker(), getLogPoint()+getNestedtMessage());
		return this;
	}
	
	/**获取异常相关的国际化信息，主要面向用户展现*/
	public String getI18nMessage() {
		Locale locale = Locale.CHINA; //LocaleContextHolder.getLocale();
		String i18nMessage = SpringContext.getApplicationContext().getMessage(exCode, arguments, super.getMessage(), locale==null?Locale.CHINA:locale);
		return "".equals(i18nMessage)?super.getMessage(): i18nMessage;
	}
	
	/**获取异常追踪信息，主要面向bug审查*/
	public String getNestedtMessage() {
		if (getCause() != null) {
			StringBuilder sb = new StringBuilder();
			if (getMessage() != null) {
				sb.append(getMessage()).append("; ");
			}
			sb.append("nested exception is ").append(getCause());
			return sb.toString();
		}
		else {
			return getMessage();
		}
	}

	/**需要被子类重写*/
	protected Marker getMarker(){
		return R.log.log_marker_exception_root;
	}
	
	public String getExCode() {
		return exCode;
	}

	public void setExCode(String exCode) {
		this.exCode = exCode;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public String getLogPoint() {
		if(StringUtils.hasText(this.logPoint)){
			return logPoint+" ==> ";
		}
		return logPoint;
	}

	public void setLogPoint(String logPoint) {
		this.logPoint = logPoint;
	}	

}
