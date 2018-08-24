package cn.evun.sweet.core.cas;

import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.common.util.encode.CryptUtil;
import cn.evun.sweet.common.util.network.NetUtils;
import cn.evun.sweet.common.util.web.CookieHelper;
import cn.evun.sweet.core.spring.SpringContext;

/**
 * 提供Token与Cookie之间的转化
 * 
 * @author yangw
 * @since V1.0.0
 */
public abstract class TokenCookieHelper {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	private String cookieName;

	private String cookieDomain;
	
	private String secretKey;
	
	protected Class<? extends Token> tokenClass = Token.class;
	
	public TokenCookieHelper(String cookieName){
		setCookieName(cookieName);
		SSOConfig config = SpringContext.getBean(SSOConfig.class);
		this.secretKey = config.LOGIN_SECRETKEY;
		setTargetClass();
	}
	
	/**
	 * 根据Token生成Cookie到客户端
	 */
	public void addCookie(HttpServletRequest request, HttpServletResponse response, Token token) throws Exception {
		Assert.notNull(token, "Token must not be null !");
		
		CookieHelper cookieHelper = new CookieHelper();
		cookieHelper.setCookieName(this.cookieName);	
		cookieHelper.setCookieHttpOnly(true);//servlet3.0及以上支持
		setAddCookieParam(cookieHelper);
		
		// 如果request.getServerName()为localhost或者IP地址都可以不用设置domain
		//cookieHelper.setCookieDomain(config.LOGIN_COOKIEDOMAIN);
		if ((!NetUtils.isValidAddress(request.getServerName())) && !NetUtils.isLocalHost(request.getServerName())) {
			String domain = "." + resolveTopDomain(request.getServerName());
			this.cookieDomain = domain;
			cookieHelper.setCookieDomain(domain);
		}
		
		byte[] tokenByte = CryptUtil.encrypt(token.toJson().getBytes(), this.secretKey);
		/*Tomcat容器不支持cookie值中含一些汉字或特殊字符*/
		cookieHelper.addCookie(response, URLEncoder.encode(CryptUtil.encryptBASE64(tokenByte),"UTF-8") );	
	}
	
	/**
	 * 为addCookie方法给出具体的诸如maxage、name等cookie参数。默认会采用logincookie的配置设置
	 */
	protected abstract void setAddCookieParam(CookieHelper cookieHelper);
		
	
	/**
	 * 删除cookie信息
	 */
	public void removeCookie(HttpServletResponse response){
		CookieHelper cookieHelper = new CookieHelper();
		cookieHelper.setCookieName(this.cookieName);
		if(StringUtils.hasText(this.cookieDomain)){
			cookieHelper.setCookieDomain(this.cookieDomain);
		}
		cookieHelper.removeCookie(response);
	}
	
	/**
	 * 获取cookie中的Token信息
	 */
	@SuppressWarnings("unchecked")
	public <T extends Token> T parseToken(HttpServletRequest request) throws Exception {
		if(!StringUtils.hasText(this.cookieName)){
			return null;
		}
		String cookieValue = CookieHelper.getCookieValue(request, this.cookieName);
		if(!StringUtils.hasText(cookieValue)){
			return null;
		}
		
		byte[] tokenByte = CryptUtil.decrypt(CryptUtil.decryptBASE64(URLDecoder.decode(cookieValue, "UTF-8")), this.secretKey);
		return (T)Token.json2Token(new String(tokenByte), this.tokenClass);
		
	}
	
	abstract void setTargetClass();
		
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

	public String getCookieName() {
		return cookieName;
	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	public String getCookieDomain() {
		return cookieDomain;
	}

	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}	
	
		

	public static void main(String[] args) throws Exception {
		String cookieValue = "zmd62k0lEI4Y25C0KihM2nuzmM50iWsaWY0pL0Cz3oXBSKh5R6xCVRDU2iJaUv1XAdbvfOKSGr1rDwHqFyF2I"
				+ "9efssM3c9CVFpsZLIKGsIQ38EfWvbvm4P0U%2BodgpWTSS7C69UvfZIVtvtOq63Str1hxsOxgPkOxJqXOup0zoy8%3D";
		System.out.println("cookielue : "+cookieValue);
		
		String codeValue = URLDecoder.decode(cookieValue, "UTF-8");
		System.out.println("codeValue : "+codeValue);
		
		byte[] base64Value = CryptUtil.decryptBASE64(codeValue);
		System.out.println("base64Value : "+new String(base64Value));
		
		byte[] aesValue = CryptUtil.decrypt(base64Value, "Zb+s5vuqnpWPca28Gf2tAQ==");
		System.out.println(new String(aesValue));
	}
}
