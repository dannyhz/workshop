package cn.evun.sweet.core.sms.channel.impl;

import cn.evun.sweet.common.util.encode.Base64Code;
import cn.evun.sweet.common.util.encode.MD5;
import cn.evun.sweet.common.util.network.HttpClientUtil;
import cn.evun.sweet.common.util.web.HttpHeadersConstant;
import cn.evun.sweet.core.exception.SweetException;

import cn.evun.sweet.core.sms.SMSConfig;
import cn.evun.sweet.core.sms.SMSMessage;
import cn.evun.sweet.core.sms.SMSResult;
import cn.evun.sweet.core.sms.channel.ChannelType;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 容联短信渠道实现类
 * @Author qinjun
 * @Date 16:07 2017/4/10
 * @CopyRight 浙江聚有财金融服务外包有限公司
 */
public class SMSRlChannel extends AbstractSMSChannel {

    private static final String reqUrl = "https://app.cloopen.com:8883/2013-12-26/Accounts/{accountSid}/SMS/TemplateSMS?sig={SigParameter}";

    private String appKey;

    private String accountId;

    private String accountToken;

    public SMSRlChannel(SMSConfig smsConfig) {
        super(smsConfig, ChannelType.RL);
        this.appKey = smsConfig.getRlAppKey();
        this.accountId = smsConfig.getAccountSid();
        this.accountToken = smsConfig.getAccountToken();
    }

    /**
     * 设置渠道优先级。
     */
    @Override
    protected int retrievePriority(SMSConfig smsConfig) {
        return smsConfig.getRlLevel();
    }

    /**
     * 生成请求签名。
     */
    private String generateSignature(String timestamp) {
        return MD5.MD5Encode(this.accountId + this.accountToken + timestamp).toUpperCase();
    }

    /**
     * 生成 headers 所需字段。
     */
    private Map<String, String> prepareHeader(String timestamp) {

        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeadersConstant.ACCEPT, "application/json");
        httpHeader.put(HttpHeadersConstant.CONTENT_TYPE, "application/json;charset=utf-8");
        httpHeader.put(HttpHeadersConstant.AUTHORIZATION, Base64Code.encodeToString((this.accountId + ":" + timestamp).getBytes()));

        return httpHeader;
    }

    /**
     * 准备 post 请求的 body。
     */
    private Map<String, Object> prepareData(String templateId, String mobile, Map<String, Object> messageContent) {
        Map<String, Object> reqCont = new HashMap<>();

        reqCont.put("templateId", templateId);
        reqCont.put("to", mobile);
        reqCont.put("appId", this.appKey);

        if (messageContent.size() > 0 ) {
            List<String> datas = new ArrayList<>();
            for (Map.Entry<String, Object> entry : messageContent.entrySet()) {
                datas.add(entry.getValue().toString());
            }
            reqCont.put("datas", datas);
        }

        return reqCont;
    }

    /**
     * 发送短信。
     */
    @Override
    public SMSResult sendRequest(SMSMessage smsMessage) {
        String targetTemplateId = this.getTargetTemplateId(String.valueOf(smsMessage.getTemplateId()));

        Map<String, Object> reqCont = this.prepareData(targetTemplateId, smsMessage.getMobile(), smsMessage.getTemplateContent());

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String composedUrl = reqUrl.replace("{accountSid}", this.accountId).replace("{SigParameter}", generateSignature(timestamp));
        Map<String, String> httpHeader = this.prepareHeader(timestamp);

        SMSResult smsResult = new SMSResult();
        String response;
		try {
            response = HttpClientUtil.httpsPost(composedUrl, JSONArray.toJSONString(reqCont), httpHeader);
		} catch (IOException e) {
			LOGGER.error(e);
			throw new SweetException("network anomaly");
		}
        JSONObject resultJsonObj = JSONObject.parseObject(response);
        if ("000000".equals(resultJsonObj.get("statusCode").toString())) {
            /*成功*/
            smsResult.setSuccess(true);
        } else {
            /*异常返回输出错误码和错误信息*/
            smsResult.setSuccess(false);
            smsResult.setErrorMsg(resultJsonObj.get("statusMsg").toString());
        }
        return smsResult;
    }

}
