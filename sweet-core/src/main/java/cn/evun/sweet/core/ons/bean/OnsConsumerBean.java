package cn.evun.sweet.core.ons.bean;

import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Properties;

/**
 * ONS消息消费者扩展类
 * @author shentao 2017-3-2.
 */
public class OnsConsumerBean extends ConsumerBean implements InitializingBean, DisposableBean {

    public OnsConsumerBean() {
    }

    public OnsConsumerBean(Properties properties) {
        super.setProperties(properties);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.start();
    }

    @Override
    public void destroy() throws Exception {
        super.shutdown();
    }

}
