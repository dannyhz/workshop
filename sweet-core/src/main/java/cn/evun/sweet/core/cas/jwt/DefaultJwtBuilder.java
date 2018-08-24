package cn.evun.sweet.core.cas.jwt;

import cn.evun.sweet.core.cas.SSOConfig;
import io.jsonwebtoken.Jwts;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.evun.sweet.core.exception.SweetException;

/**
 * 生成 JWT 的默认实现
 *
 * @author xiangli
 * @since V1.1.1
 */
@Component
public class DefaultJwtBuilder implements JwtBuilder {

    private io.jsonwebtoken.JwtBuilder jwtBuilder;

    /**用户数据*/
    private Map<String, Object> claims;

    /**用于 HS256 对称加密签名的密钥*/
    private String stringKey;

    /**用于 RS256非对称加密签名的密钥*/
    private Key key;

    private static Map<SignatureAlgorithm, io.jsonwebtoken.SignatureAlgorithm> algorithmMapping = createAlgoMap();

    private static Map<SignatureAlgorithm, io.jsonwebtoken.SignatureAlgorithm> createAlgoMap() {
        Map<SignatureAlgorithm, io.jsonwebtoken.SignatureAlgorithm> algoMap = new HashMap<>();

        algoMap.put(SignatureAlgorithm.HS256, io.jsonwebtoken.SignatureAlgorithm.HS256);
        algoMap.put(SignatureAlgorithm.RS256, io.jsonwebtoken.SignatureAlgorithm.RS256);

        return algoMap;
    }

    @Autowired
    public DefaultJwtBuilder(SSOConfig conf) {
        this.jwtBuilder = Jwts.builder();

        if (conf.JWT_KEY != null) {
            this.signWith(SignatureAlgorithm.HS256, conf.JWT_KEY);
        } else if(conf.JWT_PRIVATE_KEYFILE != null){
            this.signWith(SignatureAlgorithm.RS256, this.loadPrivateKeyFile(conf.JWT_PRIVATE_KEYFILE));
        }
    }

    private PrivateKey loadPrivateKeyFile(String privateKeyLocation) {
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(privateKeyLocation));
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            throw new SweetException("Error occurred during loading JWT private signature key.");
        }
    }

    @Override
    public JwtBuilder signWith(SignatureAlgorithm signatureAlgorithm, String key) {
        // The situation that receiving a unsupported algorithm is not exist.
        // The implementation of every builder should ensure that every algorithm in the Enum should be supported.
        this.stringKey = key;

        if (!signatureAlgorithm.isStringKeyAllowed()) {
            throw new SweetException("String key are only allowed for HMAC algorithem");
        }
        this.jwtBuilder.signWith(algorithmMapping.get(signatureAlgorithm), key);

        return this;
    }

    @Override
    public JwtBuilder signWith(SignatureAlgorithm signatureAlgorithm, Key key) {
        // The situation that receiving a unsupported algorithm is not exist.
        // The implementation of every builder should ensure that every algorithm in the Enum should be supported.
        this.key = key;
        this.jwtBuilder.signWith(algorithmMapping.get(signatureAlgorithm), key);

        return this;
    }

    @Override
    public JwtBuilder setClaims(Map<String, Object> claims) {
        this.claims = claims;
        this.jwtBuilder.setClaims(claims);
        return this;
    }

    @Override
    public JwtBuilder setExpirationDate(Date expirationDate) {
        this.jwtBuilder.setExpiration(expirationDate);
        return this;
    }

    @Override
    public JwtBuilder setNotBefore(Date notBefore) {
        this.jwtBuilder.setNotBefore(notBefore);
        return this;
    }

    @Override
    public JwtBuilder setIssuedAt(Date issuedAt) {
        this.jwtBuilder.setIssuedAt(issuedAt);
        return this;
    }

    @Override
    public JwtBuilder setId(String id) {
        this.jwtBuilder.setId(id);
        return this;
    }

    @Override
    public String build() {
        if (claims == null || (key == null && stringKey == null)) {
            throw new SweetException("Token or key not set before build");
        }
        return this.jwtBuilder.compact();
    }
}
