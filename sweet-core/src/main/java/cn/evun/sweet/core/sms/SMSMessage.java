package cn.evun.sweet.core.sms;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * @Description:短信发送的内容
 * @Author qinjun
 * @Date 15:43 2017/4/10
 * @CopyRight 浙江聚有财金融服务外包有限公司
 */
public class SMSMessage implements Serializable {

    private static final long serialVersionUID = -3761019309174105023L;

    /**
     * 短信接收端手机号码，多个用英文逗号分开
     */
    private String mobile;

    /**
     * 模板ID
     */
    private int templateId;

    /**
     * 模板内容，变量名和值的键值对集合，必须严格依照模板中的变量出现顺序
     */
    private LinkedHashMap<String, Object> templateContent;
    
    /**
     * 短信内容，不需要模板的平台使用
     */
    private String content;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public LinkedHashMap<String, Object> getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(LinkedHashMap<String, Object> templateContent) {
        this.templateContent = templateContent;
    }

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
    
    

}
