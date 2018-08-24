package cn.evun.sweet.core.bussinesshelper;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.evun.sweet.common.util.UUIDGenerator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.common.util.VerifyCodeUtils;
import cn.evun.sweet.common.util.network.NetUtils;
import cn.evun.sweet.common.util.web.CookieHelper;
import cn.evun.sweet.core.cache.CacheAccessor;
import cn.evun.sweet.core.cas.DistributedSession;
import cn.evun.sweet.core.cas.ContextHolder;
import cn.evun.sweet.core.cas.LoginCookieHelper;
import cn.evun.sweet.core.cas.LoginToken;
import cn.evun.sweet.core.cas.OnlineUserManager;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.exception.ValidateException;


/**
 * 登入登出系统的基础服务，提供整个业务流程的模板。
 *
 * @author yangw
 * @since V1.0.0
 */
public abstract class LoginAndOutService {
	
	protected static final Logger LOGGER = LogManager.getLogger();	
	
	private final String PRINAME = "verifiycode_";
	
	@Autowired
	protected OnlineUserManager onlineUserManager;
	/**
	 *	根据登录参数检测用户是否有效，如果有效并查询出所有有效用户的信息，等待植入session中。
	 *	@Map<String,Object> 用户信息包含组织、租户等信息的集合。若为null则表示用户无效。
	 */
	protected abstract Map<String,Object> checkLoginUser(Object userParam, ValidateException result);
	
	/**
	 * 为sesion中加载用户资源，例如可访问的菜单等
	 * @param user checkLoginUser所返回的有效信息。
	 */
	protected abstract void loadUserResources(Map<String,Object> user, HttpSession session);
	
	
	/**
	 * 登录业务逻辑主线
	 */
	public Map<String,Object> loginIn(HttpServletRequest request, HttpServletResponse response,
			Object userParam, ValidateException result){
		Map<String,Object> user = checkLoginUser(userParam, result);
		if(user==null){//账户验证未通过
			return null;
		}
		
		/*验证通过后开始创建Session内容*/
		LoginToken token = new LoginToken();
		token.setUserIp(String.valueOf(request.getAttribute(R.request.mdc_ip)));//在filter中植入的ip地址信息
		token.setUserId(user.get("userId").toString());//要求业务层必须有userId字段。
		token.setSessionid(request.getSession(true).getId());//同时创建了session
		token.setClientId(request.getHeader("AppClientId"));//手机端登录时通过请求头携带
		try{
			LoginCookieHelper.generateCookie(request, response, token);
		}catch(Exception ex){
			LOGGER.error("Failed to generate custom cookie[{}]. {}", token.getUserId(), ex.getMessage());
		}
		ContextHolder.setSession((DistributedSession)request.getSession(false));//绑定当前线程
		
		/*异地登陆时，需要剔除原登陆信息*/
		LoginToken cacheToken = (LoginToken)onlineUserManager.getToken(token.getUserId());
		if(cacheToken != null){
			/*移除Session信息*/
			CacheAccessor.doEvict(R.cache.cache_region_session, cacheToken.getSessionid());
		}
		onlineUserManager.replaceToken(token.getUserId(), token);
		
		/*加载用户信息及资源*/	
		try{
			loadUserResources(user, request.getSession());
		}catch(Exception e){
			throw e;
		}
		
		return user;
	}
	
	
	/**
	 * 退出业务逻辑主线
	 */
	public void loginOut(HttpServletRequest request, HttpServletResponse response){	
		Object user = ContextHolder.getSession().getAttribute(R.session.user_info);
		try {
			onlineUserManager.removeTokenFromOnlineMap(PropertyUtils.getProperty(user, "userId").toString());//从在线用户列表里移除loginToken
			LoginCookieHelper.delCookie(response);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		if(null != request.getSession(false)){
			request.getSession().invalidate();
		}
	}

	
	/**
	 * 生成验证码，并加入到缓存中等待验证。
	 */
	public void verifyCode(HttpServletRequest request, HttpServletResponse response, int w, int h, int verifySize){
		try {
			String checkId = UUIDGenerator.genUUID();
			CookieHelper cookieHelper = new CookieHelper();
			cookieHelper.setCookieName("sweeter_verify");			
			if (!NetUtils.isValidAddress(request.getServerName()) && !NetUtils.isLocalHost(request.getServerName())) {
				String domain = "." + resolveTopDomain(request.getServerName());
				cookieHelper.setCookieDomain(domain);
			}
			cookieHelper.addCookie(response, checkId);
			
			String verifyCode = VerifyCodeUtils.outputVerifyImage(w, h, response.getOutputStream(), verifySize);
			CacheAccessor.doPut(R.cache.cache_region_verifycode, PRINAME+checkId, verifyCode);
		} catch (IOException e) {
			LOGGER.error("Catch exception when creat verify code! {}" + e.getMessage());
		}
	}
	
	
	/**
	 * 检查验证码是否正确
	 */
	public boolean checkVerifyCode(HttpServletRequest request, String currentCode){
		String checkId = CookieHelper.getCookieValue(request, "sweeter_verify");
		if(!StringUtils.hasText(checkId)){//无法获取存放验证码的位置，所以这里做false处理。
			return false;
		}
		String code = (String) CacheAccessor.doGet(PRINAME+checkId);		
		if(StringUtils.hasText(currentCode) && currentCode.equalsIgnoreCase(code)){
			CacheAccessor.doEvict(R.cache.cache_region_verifycode, PRINAME+checkId);//验证后立即清除验证码
			return true;
		}
		return false;
	}
	
	private String resolveTopDomain(String domain) {
		String[] strs = domain.split("\\.");
		if (strs.length < 2) {
			domain = strs[0];
		} else if (strs.length == 2) {
			domain = strs[0] + "." + strs[1];
		} else {
			domain = domain.substring(domain.indexOf(".") + 1);
		}
		return domain;
	}

}

