package cn.evun.sweet.core.ons.bean;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.cache.redis.FSTRedisSerializer;
import cn.evun.sweet.core.ons.OnsMessage;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.TransactionProducerBean;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Properties;

/**
 * ONS事务消息生产者扩展类
 * Created by Administrator on 2017/3/2.
 */
@SuppressWarnings("rawtypes")
public class OnsTransactionProducerBean extends TransactionProducerBean implements InitializingBean, DisposableBean {

    private FSTRedisSerializer fSTRedisSerializer;

    public OnsTransactionProducerBean() {
    }

    public OnsTransactionProducerBean(Properties properties, FSTRedisSerializer fSTRedisSerializer) {
        this.setProperties(properties);
        this.fSTRedisSerializer = fSTRedisSerializer;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.start();
    }

    @Override
    public void destroy() throws Exception {
        super.shutdown();
    }

    /**
     * 发送事务消息
     *
     * @param onsMessage 消息对象
     * @param executer   本地事务执行器
     * @param arg        附加参数
     * @return
     */
    public SendResult send(OnsMessage onsMessage, LocalTransactionExecuter executer, Object arg) {
        return super.send(genMessage(onsMessage), executer, arg);
    }

    @SuppressWarnings("unchecked")
    public Message genMessage(OnsMessage onsMessage) {
        Message msg = new Message();
        msg.setTopic(onsMessage.getTopic());
        msg.setTag(onsMessage.getTag());
        if (StringUtils.hasLength(onsMessage.getProducerId())) {
            super.getProperties().setProperty(PropertyKeyConst.ProducerId, onsMessage.getProducerId());
        }
        if (StringUtils.hasLength(onsMessage.getKey())) {
            msg.setKey(onsMessage.getKey());
        }
        msg.setBody(fSTRedisSerializer.serialize(onsMessage.getObj()));
        if (onsMessage.getDelayTime() > 0) {
            msg.setStartDeliverTime(onsMessage.getDelayTime());
        }
        return msg;
    }

    /**
     * 反序列化
     */
    public Object deserialize(byte[] bytes) {
        return fSTRedisSerializer.deserialize(bytes);
    }

}
