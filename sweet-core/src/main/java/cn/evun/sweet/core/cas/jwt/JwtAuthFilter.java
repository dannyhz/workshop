package cn.evun.sweet.core.cas.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import cn.evun.sweet.common.serialize.json.JsonUtils;
import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.common.util.web.WebUtils;
import cn.evun.sweet.core.cas.ContextHolder;
import cn.evun.sweet.core.cas.OnlineUserManager;
import cn.evun.sweet.core.cas.SSOConfig;
import cn.evun.sweet.core.cas.Token;
import cn.evun.sweet.core.spring.SpringContext;

/**
 * 基于当前请求 Header 中是否包含了有效的 JWT 来进行过滤。
 *
 * @author xiangli
 * @since V1.1.1
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private JwtHelper jwtHelper;
    
    private OnlineUserManager onlineUserManager;

    /* (non-Javadoc)
     * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (this.jwtHelper == null) {
            jwtHelper = SpringContext.getBean(JwtHelper.class);
        }
        if(onlineUserManager == null){
			onlineUserManager = SpringContext.getBean(OnlineUserManager.class);
		}

		/*拦截URL、做登录校验 */       
        boolean verify = false;
        try{
        	verify = jwtHelper.verify(request);
        }catch(Exception e){        	
        }
        if (verify) { 
        	saveToken(response, jwtHelper.parse(jwtHelper.getJwtToken(request)));            	
        }else {
        	if (!inIgnoreURL(WebUtils.resolutionUrl(request.getRequestURI(), request.getContextPath()))) {
        		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    			return;	
        	}
        }
        
        filterChain.doFilter(request, response);
        
        ContextHolder.cleanToken();
    }
    
    /**
     * 根据传入的登录信息的Json格式，解析出对象信息，并存入上下文中，供控制层直接使用。
     */
    protected void saveToken(HttpServletResponse response, String tokenJson){
    	Token token = JsonUtils.jsonToBean(tokenJson, Token.class, false);
//    	if(onlineUserManager.isLoser(token)){ //被异地登陆时
//			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//			return;
//		}
		ContextHolder.setToken(token);
    }

    /**
     * 判断请求URL是否可以不做Session检查（即不用登录即可访问的请求）
     */
    private boolean inIgnoreURL(String url) {
        String[] urlArr = SpringContext.getBean(SSOConfig.class).SESS_IGNOREURL.split(";");
        if (StringUtils.hasText(url)) {
            for (int i = 0; i < urlArr.length; i++) {
                if (url.indexOf(urlArr[i]) == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
