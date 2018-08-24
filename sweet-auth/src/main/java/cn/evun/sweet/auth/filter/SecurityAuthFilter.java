package cn.evun.sweet.auth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.evun.sweet.auth.service.AuthService;
import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.common.util.web.WebUtils;
import cn.evun.sweet.core.cache.CacheAccessor;
import cn.evun.sweet.core.cas.DistributedSession;
import cn.evun.sweet.core.cas.ContextHolder;
import cn.evun.sweet.core.cas.LoginCookieHelper;
import cn.evun.sweet.core.cas.LoginToken;
import cn.evun.sweet.core.cas.NoPasswordLoginHandler;
import cn.evun.sweet.core.cas.OnlineUserManager;
import cn.evun.sweet.core.cas.SSOConfig;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.spring.SpringContext;

/**
 * 权限及安全过滤器
 * 
 * @author yangw
 * @since V1.0.0
 */
public class SecurityAuthFilter extends OncePerRequestFilter {
	
	protected static final Logger LOGGER = LogManager.getLogger();

	private NoPasswordLoginHandler noPasswordLoginServ;
	
	private AuthService authService;
	
	private OnlineUserManager onlineUserManager;


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		/*拦截URL、做登录校验 */
		if(!inIgnoreURL(WebUtils.resolutionUrl(request.getRequestURI(), request.getContextPath()))){
			LoginToken token = null;
			try {
				token = LoginCookieHelper.parseLoginToken(request);
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			
			if(onlineUserManager == null){
				onlineUserManager = SpringContext.getBean(OnlineUserManager.class);
			}
			if(onlineUserManager.isLoser(token)){ //被异地登陆时
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			
			HttpSession sess = request.getSession(false);
			if(sess != null){//redis的put方法才能修改最后活跃时间
				Object data = CacheAccessor.doGet(R.cache.cache_region_session, sess.getId());
				if(data != null){
					CacheAccessor.doPut(R.cache.cache_region_session, sess.getId(), data);
				}
				ContextHolder.setSession((DistributedSession)sess);//绑定当前线程
			}
			
			if(isApp(request)){ //不需要进入后台权限管理的情况
				if(noPasswordLoginServ == null){
					noPasswordLoginServ = SpringContext.getBean(NoPasswordLoginHandler.class);//需要在实际业务中继承AbstractNoPasswordLoginService
				}
				if(noPasswordLoginServ != null && token != null && sess == null){
					noPasswordLoginServ.creatNewSessWithCookie(token, request, response);//免密登陆
				}else if(sess == null){
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}
			}else if(sess == null){//未登录或过期
				if(isAjax(request)){
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				}else {
					response.sendRedirect(request.getContextPath() + SpringContext.getBean(SSOConfig.class).SESS_LOGINURL);
				}	
				return;
			}else {
				if(authService == null){
					authService = SpringContext.getBean(AuthService.class);
				}
				if(!authService.authCheck(request)){
					if(isAjax(request)){
						response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					}else {
						response.sendError(HttpServletResponse.SC_FORBIDDEN);
					}
					return;
				}
			}
		}
		
		filterChain.doFilter(request, response);
	}
	
	/**
	 * 判断请求URL是否可以不做Session检查（即不用登录即可访问的请求）
	 */
	private boolean inIgnoreURL(String url) {
		String[] urlArr = SpringContext.getBean(SSOConfig.class).SESS_IGNOREURL.split(";");
		if(StringUtils.hasText(url)) {
			for (int i = 0 ; i < urlArr.length ; i++) {
				if (url.indexOf(urlArr[i]) == 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 描述: 判断请求是否为AJAX请求的方法
	 * @param request 请求对象
	 * @return true:是AJAX请求
	 */
	private static boolean isAjax(HttpServletRequest request) {
		String xReq = request.getHeader("x-requested-with");
		String accept = request.getHeader("accept");
		if (StringUtils.hasText(xReq) && "xmlhttprequest".equals(xReq.toLowerCase())) {
			return true;
		}else if(StringUtils.hasText(accept) && accept.indexOf("application/json") == 0){//针对angularjs的情况
			return true;
		}
		return false;
	}
	
	/**
	 * 描述: 判断请求是否为APP请求的方法
	 * @param request 请求对象
	 * @return true:是APP请求
	 */
	private static boolean isApp(HttpServletRequest request) {
		return StringUtils.hasText(request.getHeader("UserIp"));
	}

}
