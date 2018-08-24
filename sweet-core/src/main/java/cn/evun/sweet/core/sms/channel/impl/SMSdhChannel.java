package cn.evun.sweet.core.sms.channel.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSONObject;

import cn.evun.sweet.common.util.encode.MD5;
import cn.evun.sweet.common.util.network.HttpClientUtil;
import cn.evun.sweet.common.util.web.HttpHeadersConstant;
import cn.evun.sweet.core.exception.SweetException;
import cn.evun.sweet.core.sms.SMSConfig;
import cn.evun.sweet.core.sms.SMSMessage;
import cn.evun.sweet.core.sms.SMSResult;
import cn.evun.sweet.core.sms.channel.ChannelType;

/**
 * 
 * 大汉三通短信渠道实现类
 * @author Weixw
 * @date 2018年1月25日
 * @since V1.0.0
 * @CopyRight 浙江聚有财金融服务外包有限公司
 */
public class SMSdhChannel extends AbstractSMSChannel {

    private static final String reqUrl = "http://www.dh3t.com/json/sms/Submit";

    private String account;

    private String password;
    
    /**短信签名*/
    private String sign;


    public SMSdhChannel(SMSConfig smsConfig) {
        super(smsConfig, null);
        this.account = smsConfig.getDhAccount();
        this.password = smsConfig.getDhPassword();
        this.sign = smsConfig.getDhSign();
    }

    /**
     * 设置渠道优先级。
     */
    @Override
    protected int retrievePriority(SMSConfig smsConfig) {
        return smsConfig.getDhLevel();
    }

    /**
     * 生成 headers 所需字段。
     */
    private Map<String, String> prepareHeader() {
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeadersConstant.ACCEPT, "application/json");
        httpHeader.put(HttpHeadersConstant.CONTENT_TYPE, "application/json;charset=utf-8");
        return httpHeader;
    }

    /**
     * 发送短信。
     */
    @Override
    public SMSResult sendRequest(SMSMessage smsMessage) {
    
		JSONObject param = new JSONObject();
	    param.put("account", account);
	    param.put("password", MD5.MD5Encode(password));
	    param.put("msgid", UUID.randomUUID().toString().replace("-", ""));
	    param.put("phones", smsMessage.getMobile());
	    param.put("content", smsMessage.getContent());
	    param.put("sign", sign);  
        Map<String, String> httpHeader = this.prepareHeader();

        SMSResult smsResult = new SMSResult();
        String response;
		try {
            response = HttpClientUtil.httpsPost(reqUrl, param.toJSONString(), httpHeader);
		} catch (IOException e) {
			LOGGER.error(e);
			throw new SweetException("network anomaly");
		}
        JSONObject resultJsonObj = JSONObject.parseObject(response);
        if ("0".equals(resultJsonObj.get("result").toString())) {
            /*成功*/
            smsResult.setSuccess(true);
        } else {
            /*异常返回输出错误码和错误信息*/
            smsResult.setSuccess(false);
            smsResult.setErrorMsg(response);
        }
        return smsResult;
    }

    /**
     * 大汉不需要模板
     */
	@Override
	protected Map<String, String> parseTemplateMappingByChannel(SMSConfig smsConfig, ChannelType tempType) {
		return new HashMap<>();
	}
    

}
