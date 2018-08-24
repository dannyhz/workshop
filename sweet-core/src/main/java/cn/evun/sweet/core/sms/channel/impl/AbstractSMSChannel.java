package cn.evun.sweet.core.sms.channel.impl;

import java.util.HashMap;
import java.util.Map;

import cn.evun.sweet.core.sms.SMSConfig;
import cn.evun.sweet.core.sms.SMSMessage;
import cn.evun.sweet.core.sms.SMSResult;
import cn.evun.sweet.core.sms.channel.ChannelType;
import cn.evun.sweet.core.sms.channel.SMSChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.evun.sweet.core.exception.SweetException;

/**
 * @Description: 短信发送抽象类，抽象类负责保存当前发送渠道的优先级和模板id映射关系
 * @Author qinjun
 * @Date 16:13 2017/4/10
 * @CopyRight 浙江聚有财金融服务外包有限公司
 */
public abstract class AbstractSMSChannel implements SMSChannel {
    static final Logger LOGGER = LogManager.getLogger(AbstractSMSChannel.class);

    /* 统一模板 id 和当前渠道的模板 id 之间的映射关系 */
    private Map<String, String> templateMapping;

    /* 当前渠道的优先级 */
    private int priority;

    protected AbstractSMSChannel(SMSConfig smsConfig, ChannelType channelType) {
        this.templateMapping = parseTemplateMappingByChannel(smsConfig, channelType);
        this.priority = retrievePriority(smsConfig);
    }

    /**
     * 子类实现，获取当前渠道的优先级。
     */
    protected abstract int retrievePriority(SMSConfig smsConfig);

    /**
     * 执行方法实现
     *
     * @return 处理结果
     */
    @Override
    public SMSResult send(SMSMessage smsMessage) {
        return sendRequest(smsMessage);//发送请求并返回结果
    }

    /**
     * 发送请求
     *
     * @return 处理结果
     */
    protected abstract SMSResult sendRequest(SMSMessage smsMessage);

    /**
     * 解析针对当前渠道的统一模板 id 映射规则
     *
     * @return 规则封装map
     */
    protected Map<String, String> parseTemplateMappingByChannel(SMSConfig smsConfig, ChannelType tempType) {
        Map<String, String> mappingRules = new HashMap<>();

        for (String singleTemplateMappings : smsConfig.getIdRule().split(smsConfig.getIdRuleRegex())) {
            String universalTemplateId = null;
            String targetTemplateId = null;
            for (String mapping : singleTemplateMappings.split(SMSConfig.distTempReg)) {
                try {
                    if (mapping.contains(ChannelType.ID.getChannelType())) {
                        universalTemplateId = mapping.split(SMSConfig.equalsRegex)[1];
                    } else if (mapping.contains(tempType.getChannelType())) {
                        targetTemplateId = mapping.split(SMSConfig.equalsRegex)[1];
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    LOGGER.error("Template parameter configuration error", e);
                    throw new SweetException("Template parameter configuration error to send sms.");
                }
            }
            mappingRules.put(universalTemplateId, targetTemplateId);
        }
        return mappingRules;
    }

    /**
     * 获取渠道的真实模板 id。
     */
    protected String getTargetTemplateId(String universalTemplateId) {
        return this.templateMapping.get(universalTemplateId);
    }

    /*
    数字越小，优先级越高。
     */
    public int compareTo(SMSChannel other) {
        return Integer.compare(this.getPriority(), other.getPriority());
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

}