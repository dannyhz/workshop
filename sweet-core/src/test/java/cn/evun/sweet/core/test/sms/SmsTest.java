package cn.evun.sweet.core.test.sms;

import java.util.LinkedHashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.evun.sweet.core.sms.SMSMessage;
import cn.evun.sweet.core.sms.SMSService;

/**
 * @Description: TODO
 * @Author qinjun
 * @Date 16:13 2017/4/11
 * @CopyRight 浙江聚有财金融服务外包有限公司
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:beans-test.xml"})
public class SmsTest {

    @Autowired
    private SMSService smsService;

    @Test
    public void sendTest() throws Exception {
        SMSMessage smsMessage = new SMSMessage();
        smsMessage.setMobile("13618664542");
        LinkedHashMap<String, Object> templateContent = new LinkedHashMap<String, Object>();
        templateContent.put("compName", "561551");
        smsMessage.setTemplateContent(templateContent);
        smsMessage.setTemplateId(1);
        System.out.println(smsService.send(smsMessage));
    }

}
