package cn.evun.sweet.core.cas;

import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.context.request.ServletWebRequest;

import cn.evun.sweet.common.util.web.WebUtils;
import cn.evun.sweet.core.common.R;

/**
 * 分布式session管理及日志的MDC方案。<br/>
 * ThreadContext为每个请求（线程）提供了一个Map或堆栈，以此来实现MDC（NDC）功能。
 * 使用<code>EventLogger.logEvent(msg)</code>可记录MDC日志。
 *
 * @author yangw
 * @since V1.0.0
 */
public class DistributedSessionRequestFilter implements Filter {
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, 
				FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest)servletRequest;
		HttpServletResponse resp = (HttpServletResponse)servletResponse;
		
		ServletWebRequest webreq = new ServletWebRequest(req, resp);
		String ip = WebUtils.getRealIpAddr(webreq.getRequest());
		HttpMethod method = webreq.getHttpMethod();
		Locale locale = webreq.getLocale();
		String url = new ServletServerHttpRequest(req).getURI().toString();
		
		/*MDC数据写入*/		
		ThreadContext.put(R.request.mdc_ip, ip);     
        ThreadContext.put(R.request.mdc_method, method.name());
        ThreadContext.put(R.request.mdc_locale, locale.getDisplayName());
        ThreadContext.put(R.request.mdc_uri, url);
        ThreadContext.put(R.request.mdc_time, String.valueOf(System.currentTimeMillis()));
	
        req.setAttribute(R.request.mdc_ip, ip);
        ContextHolder.setRequest(req);
        
        chain.doFilter(new DistributedSessionRequest(req), servletResponse);       
        ThreadContext.clearAll();
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		TimeZone userTimeZone = TimeZone.getTimeZone(System.getProperty("user.timezone"));
		TimeZone.setDefault(userTimeZone);
	}

	@Override
	public void destroy() {
	}
}
