package cn.evun.sweet.core.ons;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.common.R;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * ONS公共配置
 * Created by Administrator on 2017/2/22.
 */
@Configuration
public class OnsConfig {

    protected static final Logger logger = LogManager.getLogger();

    /* 阿里云身份验证 AccessKey */
    @Value("${ons.accessKey:}")
    private String accessKey;

    /* 阿里云身份验证 SecretKey */
    @Value("${ons.secretKey:}")
    private String secretKey;

    /* ONS地址 */
    @Value("${ons.onsAddr:}")
    private String onsAddr;

    /* 默认消息主题 */
    @Value("${ons.topic:}")
    private String topic;

    /* 默认阿里云生产者ID */
    @Value("${ons.producerId:}")
    private String producerId;

    /* 默认阿里云消费者ID */
    @Value("${ons.consumerId:}")
    private String consumerId;

    /* 消费者线程数 */
    @Value("${ons.consumeThreadNums:50}")
    private String consumeThreadNums;

    /* 顺序消息消费失败进行重试前的等待时间 单位(毫秒) */
    @Value("${ons.suspendTimeMillis:100}")
    private String suspendTimeMillis;

    /* 消息消费失败时的最大重试次数 */
    @Value("${ons.maxReconsumeTimes:20}")
    private String maxReconsumeTimes;

    /* 发送超时时间，单位毫秒 */
    @Value("${ons.sendMsgTimeoutMillis:3000}")
    private String sendMsgTimeoutMillis;

    /* ONS默认区域ID */
    @Value("${ons.regionId:cn-hangzhou-finance}")
    private String onsRegionId;

    @Bean(name = "onsConfigBean")
    public OnsConfigBean onsConfigBean() {
        if (StringUtils.isEmpty(accessKey) || StringUtils.isEmpty(secretKey)) {
            logger.error(R.log.log_marker_ons, "ONS公共配置初始化失败: accessKey or secretKey is null!");
            return null;
        }
        if (StringUtils.isEmpty(onsAddr)) {
            logger.error(R.log.log_marker_ons, "ONS公共配置初始化失败: onsAddr is null!");
            return null;
        }
        return new OnsConfigBean();
    }

    @Bean(name = "onsMQService")
    public OnsMQService onsMQService(@Qualifier("onsConfigBean") OnsConfigBean onsConfigBean) {
        if (onsConfigBean == null) {
            logger.error(R.log.log_marker_ons, "创建ONS服务失败: onsConfigBean is null!");
            return null;
        }
        return new OnsMQService(onsConfigBean);
    }

    @Bean(name = "onsMonitorService")
    public OnsMonitorService onsMonitorService(@Qualifier("onsConfigBean") OnsConfigBean onsConfigBean) {
        if (onsConfigBean == null) {
            logger.error(R.log.log_marker_ons, "创建ONS监控服务失败: onsConfigBean is null!");
            return null;
        }
        return new OnsMonitorService(onsConfigBean);
    }

    public class OnsConfigBean implements InitializingBean {

        private Properties properties = new Properties();

        public Properties getProperties() {
            return properties;
        }

        /**
         * 新增ONS配置项
         *
         * @param key
         * @param value
         */
        public void setPropertie(String key, String value) {
            properties.setProperty(key, value);
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            properties.setProperty(PropertyKeyConst.AccessKey, accessKey);
            properties.setProperty(PropertyKeyConst.SecretKey, secretKey);
            properties.setProperty(PropertyKeyConst.ONSAddr, onsAddr);
            properties.setProperty(PropertyKeyConst.ProducerId, producerId);
            properties.setProperty(PropertyKeyConst.ConsumerId, consumerId);
            properties.setProperty(PropertyKeyConst.ConsumeThreadNums, consumeThreadNums);
            properties.setProperty(PropertyKeyConst.SuspendTimeMillis, suspendTimeMillis);
            properties.setProperty(PropertyKeyConst.MaxReconsumeTimes, maxReconsumeTimes);
            properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, sendMsgTimeoutMillis);
            properties.setProperty("onsRegionId", onsRegionId);
            properties.setProperty("onsTopic", topic);
        }

    }

}
