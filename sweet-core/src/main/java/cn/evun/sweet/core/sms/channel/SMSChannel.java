package cn.evun.sweet.core.sms.channel;

import cn.evun.sweet.core.exception.SweetException;
import cn.evun.sweet.core.sms.SMSMessage;
import cn.evun.sweet.core.sms.SMSResult;

/**
 * SMS发送渠道接口
 *
 * @author xiangli
 * @since V1.1.1
 */
public interface SMSChannel extends Comparable<SMSChannel> {

    /**
     * 通过渠道发送短信。
     */
    SMSResult send(SMSMessage smsMessage) throws SweetException;

    /**
     * 获取渠道优先级。
     */
    int getPriority();

}
