package cn.evun.sweet.core.bussinesshelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.evun.sweet.core.cas.ContextHolder;
import cn.evun.sweet.core.cas.DistributedSession;
import cn.evun.sweet.core.cas.LoginCookieHelper;
import cn.evun.sweet.core.cas.LoginToken;
import cn.evun.sweet.core.cas.NoPasswordLoginHandler;

/**
 * 用于免密登陆的抽象类
 *
 * @author yangw
 * @since 1.0.0
 */
public abstract class AbstractNoPasswordLoginService implements NoPasswordLoginHandler{
	
	protected static final Logger LOGGER = LogManager.getLogger();

	public void creatNewSessWithCookie(LoginToken token, HttpServletRequest request, HttpServletResponse response){
		token.setSessionid(request.getSession(true).getId());
		try{
			LoginCookieHelper.generateCookie(request, response, token);
		}catch(Exception ex){
			LOGGER.error("Failed to generate custom cookie[{}]. {}", token.getUserId(), ex.getMessage());
		}
		
		ContextHolder.setSession((DistributedSession)request.getSession(false));//绑定当前线程
		
		/*加载用户信息及资源*/	
		try{
			loadUserResources(request.getSession(), token, response);
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * 为sesion中加载用户资源，例如可访问的菜单等.
	 */
	protected abstract void loadUserResources(HttpSession session, LoginToken token, HttpServletResponse response);
	
}
