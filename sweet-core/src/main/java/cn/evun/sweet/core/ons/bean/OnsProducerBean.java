package cn.evun.sweet.core.ons.bean;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.cache.redis.FSTRedisSerializer;
import cn.evun.sweet.core.ons.OnsMessage;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Properties;

/**
 * ONS普通消息生产者扩展类
 * Created by Administrator on 2017/3/2.
 */
@SuppressWarnings("rawtypes")
public class OnsProducerBean extends ProducerBean implements InitializingBean, DisposableBean {

	private FSTRedisSerializer fSTRedisSerializer;

    public OnsProducerBean() {
    }

	public OnsProducerBean(Properties properties, FSTRedisSerializer fSTRedisSerializer) {
        super.setProperties(properties);
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
     * 同步发送消息，只要不抛异常就表示成功
     *
     * @param onsMessage 消息对象
     * @return
     */
    public SendResult send(OnsMessage onsMessage) {
        return super.send(genMessage(onsMessage));
    }

    /**
     * 发送消息，Oneway形式，服务器不应答，无法保证消息是否成功到达服务器
     *
     * @param onsMessage 消息对象
     */
    public void sendOneway(OnsMessage onsMessage) {
        super.sendOneway(genMessage(onsMessage));
    }

    /**
     * 发送消息，异步Callback形式
     *
     * @param onsMessage   消息对象
     * @param sendCallback 自定义回调函数
     */
    public void sendAsync(OnsMessage onsMessage, SendCallback sendCallback) {
        super.sendAsync(genMessage(onsMessage), sendCallback);
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
