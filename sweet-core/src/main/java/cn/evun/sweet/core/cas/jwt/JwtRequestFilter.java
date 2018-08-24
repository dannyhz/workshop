package cn.evun.sweet.core.cas.jwt;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.server.ServletServerHttpRequest;

import cn.evun.sweet.common.util.web.WebUtils;
import cn.evun.sweet.core.cas.ContextHolder;
import cn.evun.sweet.core.common.R;

/**
 *
 * @author yangw
 * @since V1.0.0
 */
public class JwtRequestFilter implements Filter {
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, 
				FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest)servletRequest;
		
		String ip = WebUtils.getRealIpAddr(req);
		String url = new ServletServerHttpRequest(req).getURI().toString();
		
		/*MDC数据写入*/		
		ThreadContext.put(R.request.mdc_ip, ip);
        ThreadContext.put(R.request.mdc_uri, url);
	
        req.setAttribute(R.request.mdc_ip, ip);
        ContextHolder.setRequest(req);
        
        chain.doFilter(servletRequest, servletResponse);       
        ThreadContext.clearAll();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		
	}

}
