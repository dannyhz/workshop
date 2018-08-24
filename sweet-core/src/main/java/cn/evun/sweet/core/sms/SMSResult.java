package cn.evun.sweet.core.sms;

import java.io.Serializable;

/**
 * @Description: 短信发送的返回结果
 * @Author qinjun
 * @Date 16:13 2017/4/10
 * @CopyRight 浙江聚有财金融服务外包有限公司
 */
public class SMSResult implements Serializable {
    private static final long serialVersionUID = 5198812065146032031L;

    /** 是否成功 */
    private boolean success;

    /** 错误信息 */
    private String errorMsg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "SMSResult{success=" + success + ", errorMsg='" + errorMsg + '\'' + '}';
    }
}
