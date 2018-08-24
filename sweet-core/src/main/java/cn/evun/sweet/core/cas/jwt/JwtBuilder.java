package cn.evun.sweet.core.cas.jwt;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * 生成 JWT 的接口
 *
 * @author xiangli
 * @since V1.1.1
 */
public interface JwtBuilder {

    /**
     * 配置对称加密算法及密钥。
     */
    JwtBuilder signWith(SignatureAlgorithm signatureAlgorithm, String key);

    /**
     * 配置非对称算法及密钥。
     */
    JwtBuilder signWith(SignatureAlgorithm signatureAlgorithm, Key key);

    /**
     * 配置用户自定义数据部分。
     */
    JwtBuilder setClaims(Map<String, Object> claims);

    /**
     * 配置 token 过期时间。
     */
    JwtBuilder setExpirationDate(Date expirationDate);

    /**
     * 配置 token 生效时间。
     */
    JwtBuilder setNotBefore(Date notBefore);

    /**
     * 配置 token 发布时间。
     */
    JwtBuilder setIssuedAt(Date issuedAt);

    /**
     * 配置 token id。
     */
    JwtBuilder setId(String id);

    /**
     * 生成 token。
     */
    String build();

}
