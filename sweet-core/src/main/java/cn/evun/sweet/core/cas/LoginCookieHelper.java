package cn.evun.sweet.core.cas;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.evun.sweet.common.util.web.CookieHelper;
import cn.evun.sweet.core.spring.SpringContext;

/**
 * 提供Token与Cookie之间的转化。并支持向Cookie中进行数据修改。
 * 具体应用参见ContextWebHelper以及ContextChainToInterceptor
 * 
 * @author yangw
 * @since V1.0.0
 */
public class LoginCookieHelper extends TokenCookieHelper{

	protected static final Logger LOGGER = LogManager.getLogger();
	
	private static class LoginCookieHelperHolder {
		private static final LoginCookieHelper INSTANCE = new LoginCookieHelper();
	}

	public static final LoginCookieHelper getInstance() {
		return LoginCookieHelperHolder.INSTANCE;
	}
	
	private LoginCookieHelper() {
		super(SpringContext.getBean(SSOConfig.class).LOGIN_COOKIENAME);		
	}
	
	@Override
	protected void setAddCookieParam(CookieHelper cookieHelper) {
		SSOConfig config = SpringContext.getBean(SSOConfig.class);
		cookieHelper.setCookieMaxAge(config.LOGIN_COOKIEMAXAGE);
		cookieHelper.setCookieSecure(config.LOGIN_COOKIESECURE);
	}

	
	/**
	 * 根据Token生成登录Cookie到客户端
	 * @throws Exception
	 */
	public static void generateCookie(HttpServletRequest request, HttpServletResponse response, LoginToken token) throws Exception {
		LoginCookieHelper.getInstance().addCookie(request, response, token);		
	}
	
	
	/**
	 * 删除登录cookie信息
	 */
	public static void delCookie(HttpServletResponse response){
		LoginCookieHelper.getInstance().removeCookie(response);
	}
	
	/**
	 * 获取当前登录用户的Token信息
	 * @throws Exception 
	 */
	public static LoginToken parseLoginToken(HttpServletRequest request) throws Exception {
		return LoginCookieHelper.getInstance().parseToken(request);		
	}
	
	/**
	 * 在当前登录用户的Cookie信息中查询SessionId。
	 */
	public static String getSessIdInLoginCookie(HttpServletRequest request){
		LoginToken token = null;
		try {
			token = parseLoginToken(request);
		} catch (Exception e) {
			LOGGER.error("Error when parse sessionId from login cookie : [{}]", e.getMessage());
			return null;
		}
		if(token != null){
			return token.getSessionid();
		}
		return null;
	}

	@Override
	void setTargetClass(){
		super.tokenClass = LoginToken.class;
	}

}
