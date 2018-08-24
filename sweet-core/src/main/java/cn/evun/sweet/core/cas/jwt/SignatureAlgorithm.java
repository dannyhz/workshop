package cn.evun.sweet.core.cas.jwt;

/**
 * 当前支持的 JWT 签名加密算法的枚举类
 *
 * @author xiangli
 * @since V1.1.1
 */
public enum SignatureAlgorithm {
    HS256("HS256", "HMAC using SHA-256", "HMAC", "HmacSHA256", true),
    RS256("RS256", "RSASSA-PKCS-v1_5 using SHA-256", "RSA", "SHA256withRSA", true);

    private final String  value;
    private final String  description;
    private final String  familyName;
    private final String  jcaName;
    private final boolean jdkStandard;

    SignatureAlgorithm(String value, String description, String familyName, String jcaName, boolean jdkStandard) {
        this.value = value;
        this.description = description;
        this.familyName = familyName;
        this.jcaName = jcaName;
        this.jdkStandard = jdkStandard;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getJcaName() {
        return jcaName;
    }

    public boolean isJdkStandard() {
        return jdkStandard;
    }

    public boolean isStringKeyAllowed() {
        if (this.getValue().startsWith("HS")){
            return true;
        } else {
            return false;
        }
    }
}
