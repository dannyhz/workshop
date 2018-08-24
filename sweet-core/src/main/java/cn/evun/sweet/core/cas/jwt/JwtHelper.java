package cn.evun.sweet.core.cas.jwt;

import cn.evun.sweet.core.cas.Token;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by xiang on 2017/8/1.
 */
public interface JwtHelper {

    /**
     * 传入 HttpServletRequest，从中解析出  JWT token。
     */
    String getJwtToken(HttpServletRequest request);

    /**
     * 传入 JWT token，获得 LoginToken（登录信息）。
     */
    <T extends Token> T  parse(String jwtToken, Class<T> requiredType);
    
    /**
     * 传入 JWT token，获得 LoginToken（登录信息）的Json格式。
     */
    String parse(String jwtToken);

    /**
     * 传入 HttpServletRequest，验证是否包含有效的 JWT Token。
     */
    boolean verify(HttpServletRequest request);

    /**
     * 传入 Token 对象，生成 JWT token。
     */
    String build(Token token);

}
