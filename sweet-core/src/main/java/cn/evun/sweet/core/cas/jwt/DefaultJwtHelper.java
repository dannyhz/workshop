package cn.evun.sweet.core.cas.jwt;

import cn.evun.sweet.common.serialize.json.JsonUtils;
import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.common.util.encode.CryptUtil;
import cn.evun.sweet.core.cas.SSOConfig;
import cn.evun.sweet.core.cas.Token;
import cn.evun.sweet.core.exception.SweetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiang on 2017/8/1.
 */
@Component
public class DefaultJwtHelper implements JwtHelper {

    private static final String TOKEN_KEY = "loginToken";

    private static final String HEADER_STRING = "Authorization";

    private JwtBuilder jwtBuilder;

    private JwtParser jwtParser;

    /*For token encryption and decryption*/
    private String secretKey;

    @Autowired
    public DefaultJwtHelper(JwtBuilder jwtBuilder, JwtParser jwtParser, SSOConfig conf) {
        this.jwtBuilder = jwtBuilder;
        this.jwtParser = jwtParser;
        this.secretKey = conf.LOGIN_SECRETKEY;//加密body内容的密钥
    }

    @Override
    public String build(Token token) {
        try {
            Map<String, Object> claims = new HashMap<String, Object>();
            byte[] tokenByte = CryptUtil.encrypt(token.toJson().getBytes(), this.secretKey);
            String encryptedToken = CryptUtil.encryptBASE64(tokenByte);
            claims.put(TOKEN_KEY, encryptedToken);

            return this.jwtBuilder
                    .setExpirationDate(getExpirationtimeAfterOneWeek())
                    .setClaims(claims).build();
        } catch (Exception e) {
            throw new SweetException("Error creating JWT token");
        }
    }

    private static Date getExpirationtimeAfterOneWeek(){
        Calendar current = Calendar.getInstance();
        current.add(java.util.Calendar.DATE, 7);
        current.set(java.util.Calendar.HOUR_OF_DAY, 2);
        current.set(java.util.Calendar.MINUTE, 0);
        current.set(java.util.Calendar.SECOND, 0);

        return current.getTime();
    }
    
    @Override
    public String getJwtToken(HttpServletRequest request){
    	return request.getHeader(HEADER_STRING);
    }

    @Override
    public <T extends Token> T parse(String jwtToken, Class<T> requiredType) {
    	return JsonUtils.jsonToBean(parse(jwtToken), requiredType, false);
    }

    @Override
    public String parse(String jwtToken) {
        try {
            String encryptedToken = this.jwtParser.setToken(jwtToken).get(TOKEN_KEY, String.class);
            byte[] tokenByte = CryptUtil.decrypt(CryptUtil.decryptBASE64(encryptedToken), this.secretKey);
            return new String(tokenByte);
        } catch (Exception e) {
            throw new SweetException("Error parsing JWT token", e);
        }
    }

    @Override
    public boolean verify(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if(!StringUtils.hasText(token)){
        	return false;
        }
        return this.jwtParser.setToken(token).verify();
    }

}
