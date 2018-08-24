package cn.evun.sweet.core.cas;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 免密登陆接口
 *
 * @author yangw
 * @since 1.0.0
 */
public interface NoPasswordLoginHandler {

	/**
	 * 根据请求中的cookie重新构建session及登陆信息
	 */
	void creatNewSessWithCookie(LoginToken token, HttpServletRequest request, HttpServletResponse response);
	
}
