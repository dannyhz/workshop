package cn.evun.sweet.core.cas.jwt;

import cn.evun.sweet.core.cas.SSOConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import cn.evun.sweet.core.exception.SweetException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 解析 JWT 的默认实现
 *
 * @author xiangli
 * @since V1.1.1
 */
@Component
public class DefaultJwtParser implements JwtParser {

    private io.jsonwebtoken.JwtParser jwtParser;
    private io.jsonwebtoken.Jws<Claims> claims;
    private String token;

    /**用于 HS256 对称加密签名的解密密钥*/
    private String stringKey;
    
    /**用于 RS256 非对称加密签名的解密密钥*/
    private Key key;

    @Autowired
    public DefaultJwtParser(SSOConfig conf) {
        this.jwtParser = Jwts.parser();
        
        if(conf.JWT_KEY != null){
        	this.setSigningKey(conf.JWT_KEY);
        }else if (conf.JWT_PUBLIC_KEYFILE != null) {
            this.setSigningKey(this.loadPublicKeyFile(conf.JWT_PUBLIC_KEYFILE));
        }
    }

    private PublicKey loadPublicKeyFile(String publicKeyLocation) {
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(publicKeyLocation));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw new SweetException("Error occurred during loading JWT public signature key.");
        }
    }

    @Override
    public JwtParser setSigningKey(String key) {
        this.jwtParser.setSigningKey(key);
        this.stringKey = key;
        return this;
    }

    @Override
    public JwtParser setSigningKey(Key key) {
        this.jwtParser.setSigningKey(key);
        this.key = key;
        return this;
    }

    @Override
    public JwtParser setToken(String token) {
        this.token = token;
        this.claims = this.jwtParser.parseClaimsJws(token);
        return this;
    }

    @Override
    public boolean verify() {
        try {
            if ((key == null && stringKey == null) || token == null) {
                throw new SweetException("Token or key not set before validation or parsing");
            }
            this.jwtParser.parse(this.token);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public <T> T get(String key, Class<T> requiredType) {
        return this.claims.getBody().get(key, requiredType);
    }
}
