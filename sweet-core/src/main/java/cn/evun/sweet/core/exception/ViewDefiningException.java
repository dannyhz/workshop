package cn.evun.sweet.core.exception;

import java.util.Locale;

import org.apache.logging.log4j.Marker;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.spring.SpringContext;

/**   
 * 在SpringMVC主流程开始处理控制层抛出的异常时，会判断如果异常类型是本异常，将直接获取其中的ModelAndView进行渲染处理。
 * 因此当有特点的异常对应的显示界面时，可以使用该异常。
 * 
 * @author yangw   
 * @since V1.0.0   
 */
public class ViewDefiningException extends ModelAndViewDefiningException {
	
	public ViewDefiningException(String exCode, ModelAndView modelAndView) {
		super(modelAndView);
		setExCode(exCode);
	}

	private static final long serialVersionUID = 1L;

	/**异常编号（国际化key）*/
	private String exCode = R.exception.excode_default;

	/**国际化信息中包含的参数列表*/
	private Object[] arguments;
	
	/**获取异常相关的国际化信息，主要面向用户展现*/
	public String getI18nMessage() {
//		Locale locale = LocaleContextHolder.getLocale();
		//TODO 获取本地国际化
		Locale locale = Locale.CHINA;
		return SpringContext.getApplicationContext().getMessage(exCode, arguments, locale==null?Locale.CHINA:locale);
	}
	
	/**需要被子类重写*/
	protected Marker getMarker(){
		return R.log.log_marker_exception_viewdef;
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
	
}
