package cn.evun.sweet.core.cas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * CAS相关配置。
 *
 * @author yangw
 * @since V1.0.0
 */
@Component
public class SSOConfig {

    @Value("${cas.secretkey}")
    public String LOGIN_SECRETKEY;

    //@Value("${cas.logincookie.domain}")
    public String LOGIN_COOKIEDOMAIN;

    @Value("${cas.logincookie.name}")
    public String LOGIN_COOKIENAME;

    @Value("${cas.logincookie.secure}")
    public boolean LOGIN_COOKIESECURE;

    @Value("${cas.logincookie.maxage}")
    public int LOGIN_COOKIEMAXAGE; //单位 秒

    @Value("${cas.session.ignoreurl}")
    public String SESS_IGNOREURL;

    @Value("${cas.session.unauthurl}")
    public String SESS_UNAUTHURL;

    @Value("${cas.session.loginurl}")
    public String SESS_LOGINURL;

    @Value("${cas.session.expired}")
    public String SESS_EXPIRED;

    @Value("${cas.jwt.key:#{null}}")
    public String JWT_KEY;

    @Value("${cas.jwt.privatekeyfile:#{null}}")
    public String JWT_PRIVATE_KEYFILE;

    @Value("${cas.jwt.publickeyfile:#{null}}")
    public String JWT_PUBLIC_KEYFILE;

}
