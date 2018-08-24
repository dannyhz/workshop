package cn.evun.sweet.core.cas.jwt;


import java.security.Key;

/**
 * 解析 JWT 的接口
 *
 * @author xiangli
 * @since V1.1.1
 */
public interface JwtParser {

    /**
     * 配置对称加密密钥，用于解析签名。
     */
    JwtParser setSigningKey(String key);

    /**
     * 配置非对称加密密钥，用于解析签名。
     */
    JwtParser setSigningKey(Key key);

    /**
     * 检查当前 token 是否有效（包含有效期/数据完整性）
     */
    boolean verify();

    /**
     * 从 token 的用户数据中解析出特定字段，并按照指定类型封装返回。
     */
    <T> T get(String key, Class<T> requiredType);

    /**
     * 设置待解析的 token
     */
    JwtParser setToken(String token);

}
