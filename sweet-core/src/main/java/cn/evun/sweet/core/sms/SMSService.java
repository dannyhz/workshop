package cn.evun.sweet.core.sms;

import cn.evun.sweet.common.util.Assert;
import cn.evun.sweet.core.exception.SweetException;
import cn.evun.sweet.core.sms.channel.SMSChannel;
import cn.evun.sweet.core.sms.channel.impl.SMSJhChannel;
import cn.evun.sweet.core.sms.channel.impl.SMSRlChannel;
import cn.evun.sweet.core.sms.channel.impl.SMSdhChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description: 短信服务
 * @Author qinjun
 * @Date 16:07 2017/4/10
 * @CopyRight 浙江聚有财金融服务外包有限公司
 */
@Component
public class SMSService {

    protected static final Logger logger = LogManager.getLogger(SMSService.class);

    private List<SMSChannel> smsChannels = new ArrayList<>();

    @Autowired
    public SMSService(SMSConfig smsConfig) {
        /*
         * 如果使用 Spring boot，可以使用 @ConditionalOnProperty 注解依赖配置项进行自动装配。
         * Spring 不支持在注解分析阶段读取 property 文件，所以需要手工判断。
         */
        if (smsConfig.jhOpen()) {
            try {
                logger.info("Initializing SMS channel for Juhe");
                smsChannels.add(new SMSJhChannel(smsConfig));
            } catch (SweetException e) {
                logger.error("Initializing Juhe SMS channel failed, please check mapping rules in config file");
            }
        }

        if (smsConfig.rlOpen()) {
            logger.info("Initializing SMS channel for Ronglian");
            try {
                smsChannels.add(new SMSRlChannel(smsConfig));
            } catch (SweetException e) {
                logger.error("Initializing Ronglian SMS channel failed, please check mapping rules in config file");
            }
        }
        
        if (smsConfig.dhOpen()) {
            logger.info("Initializing SMS channel for dahan3t");
            try {
                smsChannels.add(new SMSdhChannel(smsConfig));
            } catch (SweetException e) {
                logger.error("Initializing dahan3t SMS channel failed, please check config file");
            }
        }

        // 将渠道按照优先级排序
        Collections.sort(smsChannels);
    }

    /*
    * 检查消息完整性并按照渠道优先级轮询调用渠道发送消息，直至消息发送成功。
     */
    public SMSResult send(SMSMessage smsMessage) {
        SMSResult smsResult = new SMSResult();//创建返回对象
        try {
            Assert.notNull(smsMessage, "smsMessage must not be null");
            Assert.hasText(smsMessage.getMobile(), "mobile must not be null");
            Assert.notNull(smsMessage.getTemplateId(), "templateId must not be null");
        }catch (IllegalArgumentException e) {
            smsResult.setSuccess(false);
            smsResult.setErrorMsg(e.getMessage());
            return smsResult;
        }

        for (SMSChannel smsChannel : smsChannels) {
            try {
                smsResult = smsChannel.send(smsMessage);
            } catch ( SweetException e) {
                smsResult = new SMSResult();
                smsResult.setSuccess(false);
                smsResult.setErrorMsg(e.getMessage());
            }
            if (smsResult != null && smsResult.isSuccess()) {
                break;
            }
        }

        return smsResult;
    }
}
