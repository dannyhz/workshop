package cn.evun.sweet.core.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 防火墙及字符编码转换过滤。
 * 
 * @author yangw
 * @since V1.0.0
 */
public class WafAndEncodingFilter extends OncePerRequestFilter {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	private String encoding = "UTF-8";

	private boolean forceEncoding = true;
	
	private boolean filterXSS = true;

	private boolean filterSqlInjection = true;
	
	private boolean filterJavaScript = true;
	
	@Override
	protected void initFilterBean() throws ServletException {
		LOGGER.info("WafFilter init . filterXSS: {} , filterSqlInjection: {}, filterJavaScript: {}", 
				filterXSS, filterSqlInjection, filterJavaScript);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		/*转换为编码*/
		if (this.encoding != null && (this.forceEncoding || request.getCharacterEncoding() == null)) {
			request.setCharacterEncoding(this.encoding);
			if (this.forceEncoding) {
				response.setCharacterEncoding(this.encoding);
			}
		}
		
		/*安全过滤，主要针对XSS以及SQL注入*/
		filterChain.doFilter(new WafRequestWrapper(request, filterXSS, filterSqlInjection, filterJavaScript), response);		
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public void setForceEncoding(boolean forceEncoding) {
		this.forceEncoding = forceEncoding;
	}
	public boolean isFilterXSS() {
		return filterXSS;
	}

	public void setFilterXSS(boolean filterXSS) {
		this.filterXSS = filterXSS;
	}

	public boolean isFilterSqlInjection() {
		return filterSqlInjection;
	}
	
	public boolean isFilterJavaScript() {
		return filterJavaScript;
	}

	public void setFilterJavaScript(boolean filterJavaScript) {
		this.filterJavaScript = filterJavaScript;
	}

	public void setFilterSqlInjection(boolean filterSqlInjection) {
		this.filterSqlInjection = filterSqlInjection;
	}
}
