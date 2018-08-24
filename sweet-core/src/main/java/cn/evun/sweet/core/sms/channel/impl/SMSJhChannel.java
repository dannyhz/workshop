package cn.evun.sweet.core.sms.channel.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import cn.evun.sweet.common.util.network.HttpClientUtil;
import cn.evun.sweet.core.exception.SweetException;

import cn.evun.sweet.core.sms.*;
import cn.evun.sweet.core.sms.channel.ChannelType;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description: 聚合短信渠道实现类
 * @Author qinjun
 * @Date 16:07 2017/4/10
 * @CopyRight 浙江聚有财金融服务外包有限公司
 */
public class SMSJhChannel extends AbstractSMSChannel {

    private static final String reqUrl = "http://v.juhe.cn/sms/send?mobile=#MOBILE#&tpl_id=#TPLID#&tpl_value=#TPLVALUE#&key=#APPKEY#";

    private String appKey;

    public SMSJhChannel(SMSConfig smsConfig) {
        super(smsConfig, ChannelType.JH);
        this.appKey = smsConfig.getJhAppKey();
    }

    /**
     * 设置渠道优先级。
     */
    @Override
    protected int retrievePriority(SMSConfig smsConfig) {
        return smsConfig.getJhLevel();
    }

    /**
     * 聚合是 Get 接口，需要将请求的具体参数放入 URL 进行拼接。
     */
    private String buildURL(String mobile, String templateId, Map<String, Object> messageContent) {

        if (messageContent.size() > 0) {
            StringBuilder buf = new StringBuilder();
            String data;
            for (Map.Entry<String, Object> entry : messageContent.entrySet()) {
                buf.append("#").append(entry.getKey()).append("#=").append(entry.getValue());
            }
            try {
                data = URLEncoder.encode(buf.toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(e.getMessage(), e);
                throw new SweetException(e.getMessage());
            }

            return reqUrl.replace("#APPKEY#", this.appKey).replace("#MOBILE#", mobile)
                    .replace("#TPLID#", templateId).replace("#TPLVALUE#", data);
        }

        return reqUrl.replace("#APPKEY#", this.appKey).replace("#MOBILE#", mobile)
                .replace("#TPLID#", templateId);
    }

    /**
     * 发送短信。
     */
    @Override
    public SMSResult sendRequest(SMSMessage smsMessage) {
        String targetTemplateId = this.getTargetTemplateId(String.valueOf(smsMessage.getTemplateId()));

        String composedUrl = this.buildURL(smsMessage.getMobile(), targetTemplateId, smsMessage.getTemplateContent());

        SMSResult smsResult = new SMSResult();
        String response;

        try {
            response = HttpClientUtil.httpGet(composedUrl);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new SweetException("network anomaly");
        }
        JSONObject resultJsonObj = JSONObject.parseObject(response);
        if (resultJsonObj.getIntValue("error_code") == 0) {
            smsResult.setSuccess(true); //成功
        } else {
            /*异常返回输出错误码和错误信息*/
            smsResult.setSuccess(false);
            smsResult.setErrorMsg(resultJsonObj.get("reason").toString());
        }
        return smsResult;
    }
}
