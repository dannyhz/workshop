package cn.evun.sweet.core.bigdata;

import org.springframework.context.ApplicationEvent;

/**
 * 数据日志的发送事件，应用发送此事件或其子类即可将数据写入数据日志。
 *
 * @author xiangli
 * @since V1.1.1
 */
public class DataEvent extends ApplicationEvent {

    private static final long serialVersionUID = 4245081181061447576L;

    /**应用，如聚有财、信e收**/
    private String app;

    /**业务，如注册、下单、撤销、绑卡**/
    private String business;

    public DataEvent(String app, String business, Object source) {
        super(source);
        this.app = app;
        this.business = business;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    @Override
    public String toString() {
        return "DataEvent{app=" + app + ",business=" + business + ",source=" + source + "}";
    }
}
