package cn.evun.sweet.core.sms.channel;

/**
 * @Description: 模板配置中发送通道
 * @Author qinjun
 * @Date 16:29 2017/4/10
 * @CopyRight 浙江聚有财金融服务外包有限公司
 */
public enum ChannelType {
    ID("id", "统一模板id"), RL("rl", "容联模板id"), JH("jh", "聚合模板id");

    private String channelType;
    private String text;

    ChannelType(String tempType, String text) {
        this.channelType = tempType;
        this.text = text;
    }

    public String getChannelType() {
        return channelType;
    }

    public String getText() {
        return text;
    }
}
