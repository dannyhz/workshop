package cn.evun.sweet.core.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:短信配置
 * @Author qinjun
 * @Date 15:43 2017/4/10
 * @CopyRight 浙江聚有财金融服务外包有限公司
 */
@Configuration
public class SMSConfig {
    /* 公用配置 */
    /**
     * 模板id配置规则 如下配置： id-1,rl-1,jh-2;id-2,rl-3,jh-4;
     * (模板id为1，对应容联短信模板为1、聚合短信模板为2;模板id为2，对应容联短信模板为3、聚合短信模板为4;)
     */
    @Value("${sms.templateId.rule:}")
    private String idRule;

    /** 模板id配置拼接规则 （默认为;） */
    @Value("${sms.templateId.rule.splitRegex:;}")
    private String idRuleRegex;

    /** 获取值拼接规则 */
    public static final String equalsRegex = "-";

    /** 模板id拼接规则 */
    public static final String distTempReg = ",";

    /* 聚合配置 */
    /** 聚合注册appkey */
    @Value("${sms.jh.appkey:}")
    private String jhAppKey;

    /** 聚合短信发送优先级 */
    @Value("${sms.jh.level:1}")
    private int jhLevel;

    /* 容联配置 */
    /** 荣联短信发送优先级 */
    @Value("${sms.rl.level:1}")
    private int rlLevel;

    /** 容联注册的appid */
    @Value("${sms.rl.appkey:}")
    private String rlAppKey;

    /** 容联注册的ACCOUNT_SID */
    @Value("${sms.rl.account.sid:}")
    private String accountSid;

    /** 容联注册的ACCOUNT_TOKEN */
    @Value("${sms.rl.account.token:}")
    private String accountToken;
    
    /* 大汉三通配置 */
    /** 大汉三通账号 */
    @Value("${sms.dahan.account:}")
    private String dhAccount;
    /** 大汉三通密码 */
    @Value("${sms.dahan.password:}")
    private String dhPassword;
    /** 大汉三通短信签名 */
    @Value("${sms.dahan.sign:【聚优财】}")
    private String dhSign;
    /** 大汉三通短信发送优先级 */
    @Value("${sms.dahan.level:1}")
    private int dhLevel;

    /** 容联短信是否开启 */
    public boolean rlOpen(){
        return !rlAppKey.isEmpty() && !accountSid.isEmpty() && !accountToken.isEmpty();
    }
    /** 聚合短信是否开启 */
    public boolean jhOpen(){
        return !jhAppKey.isEmpty();
    }
    
    /** 大汉三通短信是否开启 */
    public boolean dhOpen(){
    	return !dhAccount.isEmpty() && !dhPassword.isEmpty();
    }

    public String getIdRule() {
        return idRule;
    }

    public String getIdRuleRegex() {
        return idRuleRegex;
    }

    public String getJhAppKey() {
        return jhAppKey;
    }

    public String getRlAppKey() {
        return rlAppKey;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public String getAccountToken() {
        return accountToken;
    }

    public int getJhLevel() {
        return jhLevel;
    }

    public int getRlLevel() {
        return rlLevel;
    }
	public String getDhAccount() {
		return dhAccount;
	}
	public void setDhAccount(String dhAccount) {
		this.dhAccount = dhAccount;
	}
	public String getDhPassword() {
		return dhPassword;
	}
	public void setDhPassword(String dhPassword) {
		this.dhPassword = dhPassword;
	}
	public int getDhLevel() {
		return dhLevel;
	}
	public void setDhLevel(int dhLevel) {
		this.dhLevel = dhLevel;
	}
	public String getDhSign() {
		return dhSign;
	}
	public void setDhSign(String dhSign) {
		this.dhSign = dhSign;
	}
    
    
}
