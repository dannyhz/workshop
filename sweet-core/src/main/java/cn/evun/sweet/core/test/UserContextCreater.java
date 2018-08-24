package cn.evun.sweet.core.test;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;

import cn.evun.sweet.core.cas.DistributedSession;
import cn.evun.sweet.core.cas.ContextHolder;
import cn.evun.sweet.core.cas.LoginCookieHelper;
import cn.evun.sweet.core.cas.LoginToken;
import cn.evun.sweet.core.mybatis.common.SqlMapper;
import cn.evun.sweet.core.spring.SpringContext;

/**
 * 为测试环境构建用户上下文
 *
 * @author yangw
 * @since 1.0.0
 */
public class UserContextCreater implements Filter{
	
	CustomContextHandler ctxHandler;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        SqlSession sqlSession = SpringContext.getBean("sqlSession");
        SqlMapper sqlExcutor = new SqlMapper(sqlSession);

        Map<String,Object> userMap = sqlExcutor.selectOne(ctxHandler.getUserByAccountSql(), ctxHandler.getUserAccount());
        userMap = sqlExcutor.mapKeyUnderscoreToCamelCase(userMap);
        
        LoginToken token = new LoginToken();
        token.setUserIp("127.0.0.1");
        token.setUserId(userMap.get("userId").toString());
		token.setSessionid(req.getSession(true).getId());//同时创建了session
		try{
			LoginCookieHelper.generateCookie(req, resp, token);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		ContextHolder.setSession((DistributedSession)req.getSession(false));//绑定当前线程
		

        ctxHandler.loadUserResources(userMap, req.getSession());
        chain.doFilter(req, resp);
    }

	@Override
	public void destroy() {		
	}
	
	public void setCustomContextHandler(CustomContextHandler handler){
		this.ctxHandler = handler;
	}
}
