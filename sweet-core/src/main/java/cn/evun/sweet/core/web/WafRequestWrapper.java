package cn.evun.sweet.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import cn.evun.sweet.common.util.web.JavaScriptUtils;

/**
 * 主要包含XSS以及SQL注入的过滤方案
 *
 * @author yangw
 * @since 1.0.0
 */
public class WafRequestWrapper extends HttpServletRequestWrapper {
	
	private boolean filterXSS = true;

	private boolean filterSQL = true;
	
	private boolean filterJavaScript = true;

	public WafRequestWrapper(HttpServletRequest request, boolean filterXSS, boolean filterSQL, boolean filterJS) {
		super(request);
		this.filterXSS = filterXSS;
		this.filterSQL = filterSQL;
		this.filterJavaScript = filterJS;
	}
	
	public WafRequestWrapper(HttpServletRequest request) {
		super(request);
	}


	/**
	 * 数组参数过滤
	 */
	@Override
	public String[] getParameterValues( String parameter ) {
		String[] values = super.getParameterValues(parameter);
		if ( values == null ) {
			return null;
		}

		int count = values.length;
		String[] encodedValues = new String[count];
		for ( int i = 0 ; i < count ; i++ ) {
			encodedValues[i] = filterParamString(values[i]);
		}

		return encodedValues;
	}


	/**
	 * 参数过滤
	 */
	@Override
	public String getParameter( String parameter ) {
		return filterParamString(super.getParameter(parameter));
	}


	/**
	 * 请求头过滤 
	 */
	@Override
	public String getHeader( String name ) {
		boolean currentFJS = this.filterJavaScript;
		this.filterJavaScript = false;
		String result = filterParamString(super.getHeader(name));
		this.filterJavaScript = currentFJS;
		return result;
	}


	/**
	 * Cookie内容过滤	 
	@Override
	public Cookie[] getCookies() {
		Cookie[] existingCookies = super.getCookies();
		if (existingCookies != null) {
			for (int i = 0 ; i < existingCookies.length ; ++i) {
				Cookie cookie = existingCookies[i];
				cookie.setValue(filterParamString(cookie.getValue()));
			}
		}
		return existingCookies;
	}
	*/


	/**
	 * 过滤字符串内容
	 */
	protected String filterParamString( String rawValue ) {
		if ( rawValue == null ) {
			return null;
		}
		String tmpStr = rawValue;
		if(this.filterXSS) {
			tmpStr = WafHelper.stripXSS(rawValue);
		}
		if(this.filterSQL) {
			tmpStr = WafHelper.stripSqlInjection(tmpStr);
		}
		if(this.filterJavaScript){
			tmpStr = JavaScriptUtils.javaScriptEscape(tmpStr);
		}
		return tmpStr;
	}

}
