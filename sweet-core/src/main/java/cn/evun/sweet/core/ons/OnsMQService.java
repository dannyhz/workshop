package cn.evun.sweet.core.ons;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.cache.redis.FSTRedisSerializer;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.ons.bean.OnsConsumerBean;
import cn.evun.sweet.core.ons.bean.OnsProducerBean;
import cn.evun.sweet.core.ons.bean.OnsTransactionProducerBean;
import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.bean.Subscription;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ONS消息服务
 * Created by Administrator on 2017/2/27.
 */
public class OnsMQService implements InitializingBean, DisposableBean {

    protected static final Logger logger = LogManager.getLogger();

    private static final String PROCUDER_DEFAULT = "Default_ProducerId";

    @SuppressWarnings("rawtypes")
    @Autowired
    protected FSTRedisSerializer fSTRedisSerializer;

    @NotNull(message = "ONS公共配置不能为空！")
    private OnsConfig.OnsConfigBean onsConfigBean;

    /* ONS普通消息生产者Map */
    private final Map<String, OnsProducerBean> producerBeanMap = new ConcurrentHashMap<>();
    /* ONS事务消息生产者Map */
    private final Map<String, OnsTransactionProducerBean> transactionProducerBeanMap = new ConcurrentHashMap<>();
    /* ONS普通消息消费者Map */
    private final Map<String, OnsConsumerBean> consumerBeanMap = new ConcurrentHashMap<>();

    /* 默认ONS普通消息生产者 */
    private OnsProducerBean onsProducerBean;
    /* 默认ONS事务消息生产者 */
    private OnsTransactionProducerBean onsTransactionProducerBean;
    /* 默认ONS普通消息消费者 */
    private OnsConsumerBean onsConsumerBean;
    /* 默认ONS顺序消息生产者 */
    private OrderProducer orderProducer;
    /* 默认ONS顺序消息消费者 */
    private OrderConsumer orderConsumer;
    /* 默认消息主题 */
    private String onsTopic;

    public OnsMQService(OnsConfig.OnsConfigBean onsConfigBean) {
        this.onsConfigBean = onsConfigBean;
        this.onsTopic = onsConfigBean.getProperties().getProperty("onsTopic");
    }

    /**
     * 创建普通消息生产者
     *
     * @param producerId 生产者ID
     * @return
     */
    public OnsProducerBean createProducerBean(String producerId) {
        producerId = !StringUtils.hasText(producerId) ? PROCUDER_DEFAULT : producerId;
        synchronized (producerId) {
            OnsProducerBean producerBean = producerBeanMap.get(producerId);
            if (producerBean != null) {
                if (producerBean.isClosed()) {
                    producerBean.start(); // 如果已停止就重新启动
                }
                return producerBean;
            }
            producerBean = new OnsProducerBean(cloneProperties(onsConfigBean.getProperties()), fSTRedisSerializer);
            if (!producerId.equals(PROCUDER_DEFAULT)) {
                producerBean.getProperties().setProperty(PropertyKeyConst.ProducerId, producerId);
            }
            producerBean.start();
            producerBeanMap.put(producerId, producerBean);
            logger.info(R.log.log_marker_ons, "创建普通消息生产者" + producerId);
            return producerBean;
        }
    }

    /**
     * 创建事务消息生产者
     *
     * @param producerId 生产者ID
     * @return
     */
    public OnsTransactionProducerBean createTransactionProducerBean(String producerId) {
        producerId = !StringUtils.hasText(producerId) ? PROCUDER_DEFAULT : producerId;
        synchronized (producerId) {
            OnsTransactionProducerBean transactionProducerBean = transactionProducerBeanMap.get(producerId + "_transaction");
            if (transactionProducerBean != null) {
                if (transactionProducerBean.isClosed()) {
                    transactionProducerBean.start(); // 如果已停止就重新启动
                }
                return transactionProducerBean;
            }
            transactionProducerBean = new OnsTransactionProducerBean(cloneProperties(onsConfigBean.getProperties()), fSTRedisSerializer);
            if (!producerId.equals(PROCUDER_DEFAULT)) {
                transactionProducerBean.getProperties().setProperty(PropertyKeyConst.ProducerId, producerId);
            }
            transactionProducerBean.start();
            transactionProducerBeanMap.put(producerId + "_transaction", transactionProducerBean);
            logger.info(R.log.log_marker_ons, "创建事务消息生产者" + producerId);
            return transactionProducerBean;
        }
    }

    /**
     * 创建消息消费者
     *
     * @param consumerId        消费者ID
     * @param subscriptionTable 自定义消息回调监听器Map
     * @param messageModel      消费模式：PropertyValueConst.BROADCASTING(广播消费)、PropertyValueConst.CLUSTERING（集群消费）
     */
    public OnsConsumerBean createConsumerBean(String consumerId, Map<Subscription, MessageListener> subscriptionTable,
                                              String messageModel) {
        consumerId = !StringUtils.hasText(consumerId) ? PROCUDER_DEFAULT : consumerId;
        synchronized (consumerId) {
            OnsConsumerBean onsConsumerBean = consumerBeanMap.get(consumerId);
            if (onsConsumerBean != null) {
                if (onsConsumerBean.isClosed()) {
                    onsConsumerBean.start();
                }
                return onsConsumerBean;
            }
            onsConsumerBean = new OnsConsumerBean(cloneProperties(onsConfigBean.getProperties()));
            if (!consumerId.equals(PROCUDER_DEFAULT)) {
                onsConsumerBean.getProperties().setProperty(PropertyKeyConst.ConsumerId, consumerId);
            }
            if (StringUtils.hasText(messageModel)) { // 消费模式，默认集群消费
                onsConsumerBean.getProperties().setProperty(PropertyKeyConst.MessageModel, messageModel);
            }
            if (subscriptionTable == null) {
                subscriptionTable = new HashMap<Subscription, MessageListener>();
            }
            onsConsumerBean.setSubscriptionTable(subscriptionTable);
            onsConsumerBean.start();
            consumerBeanMap.put(consumerId, onsConsumerBean);
            logger.info(R.log.log_marker_ons, "创建普通消息消费者" + consumerId);
            return onsConsumerBean;
        }
    }

    /**
     * 创建消息消费者(默认集群消费)
     *
     * @param consumerId        消费者ID
     * @param subscriptionTable 自定义消息回调监听器Map
     */
    public OnsConsumerBean createConsumerBean(String consumerId, Map<Subscription, MessageListener> subscriptionTable) {
        return createConsumerBean(consumerId, subscriptionTable, null);
    }

    /**
     * 创建顺序消息生产者
     */
    public synchronized OrderProducer createOrderProducer() {
        orderProducer = ONSFactory.createOrderProducer(cloneProperties(onsConfigBean.getProperties()));
        orderProducer.start();
        logger.info(R.log.log_marker_ons, "创建顺序消息生产者");
        return orderProducer;
    }

    /**
     * 创建顺序消息消费者
     *
     * @param messageModel 消费模式：PropertyValueConst.BROADCASTING(广播消费)、PropertyValueConst.CLUSTERING（集群消费）
     */
    public synchronized OrderConsumer createOrderConsumer(String messageModel) {
        Properties properties = cloneProperties(onsConfigBean.getProperties());
        if (StringUtils.hasText(messageModel)) { // 消费模式，默认集群消费
            properties.setProperty(PropertyKeyConst.MessageModel, messageModel);
        }
        orderConsumer = ONSFactory.createOrderedConsumer(properties);
        orderConsumer.start();
        logger.info(R.log.log_marker_ons, "创建顺序消息消费者");
        return orderConsumer;
    }

    /**
     * 同步发送消息，只要不抛异常就表示成功
     *
     * @param onsMessage 消息对象
     * @return
     */
    public SendResult send(OnsMessage onsMessage) {
        if (onsProducerBean == null) {
            onsProducerBean = createProducerBean(null);
        }
        return onsProducerBean.send(genMessage(onsMessage));
    }

    /**
     * 发送消息，Oneway形式，服务器不应答，无法保证消息是否成功到达服务器
     *
     * @param onsMessage 消息对象
     */
    public void sendOneway(OnsMessage onsMessage) {
        if (onsProducerBean == null) {
            onsProducerBean = createProducerBean(null);
        }
        onsProducerBean.sendOneway(genMessage(onsMessage));
    }

    /**
     * 发送消息，异步Callback形式
     *
     * @param onsMessage   消息对象
     * @param sendCallback 自定义回调函数
     */
    public void sendAsync(OnsMessage onsMessage, SendCallback sendCallback) {
        if (onsProducerBean == null) {
            onsProducerBean = createProducerBean(null);
        }
        if (sendCallback == null) {
            sendCallback = new OnsSendCallback();
        }
        onsProducerBean.sendAsync(genMessage(onsMessage), sendCallback);
    }

    /**
     * 发送事务消息
     *
     * @param onsMessage 消息对象
     * @param executer   本地事务执行器
     * @param arg        附加参数
     * @return
     */
    public SendResult sendTransaction(OnsMessage onsMessage, LocalTransactionExecuter executer, Object arg) {
        if (onsTransactionProducerBean == null) {
            onsTransactionProducerBean = createTransactionProducerBean(null);
        }
        return onsTransactionProducerBean.send(genMessage(onsMessage), executer, arg);
    }

    /**
     * 订阅消息
     *
     * @param topic         消息主题
     * @param subExpression 订阅过滤表达式字符串，ONS服务器依据此表达式进行过滤。只支持或运算<br>
     *                      eg: "tag1 || tag2 || tag3"<br>
     *                      如果subExpression等于null或者*，则表示全部订阅
     * @param listener      消息回调监听器
     */
    public void subscribe(String topic, String subExpression, MessageListener listener) {
        if (onsConsumerBean == null) {
            onsConsumerBean = createConsumerBean(null, null);
        }
        onsConsumerBean.subscribe(StringUtils.hasText(topic) ? topic : onsTopic, subExpression, listener);
    }

    /**
     * 取消某个主题订阅
     *
     * @param topic 消息主题
     */
    public void unsubscribe(String topic) {
        if (onsConsumerBean == null) {
            onsConsumerBean = createConsumerBean(null, null);
        }
        onsConsumerBean.unsubscribe(topic);
    }

    /**
     * 发送顺序消息
     *
     * @param onsMessage
     * @return
     */
    public SendResult sendOrder(OnsMessage onsMessage) {
        if (orderProducer == null) {
            orderProducer = createOrderProducer();
        }
        return orderProducer.send(genMessage(onsMessage), onsMessage.getShardingKey());
    }

    /**
     * 订阅顺序消息
     *
     * @param topic         消息主题
     * @param subExpression 订阅过滤表达式字符串，ONS服务器依据此表达式进行过滤。只支持或运算<br>
     *                      eg: "tag1 || tag2 || tag3"<br>
     *                      如果subExpression等于null或者*，则表示全部订阅
     * @param listener      消息回调监听器
     */
    public void subscribeOrder(String topic, String subExpression, MessageOrderListener listener) {
        if (orderConsumer == null) {
            orderConsumer = createOrderConsumer(null);
        }
        orderConsumer.subscribe(StringUtils.hasText(topic) ? topic : onsTopic, subExpression, listener);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info(R.log.log_marker_ons, "ONS服务已启动！");
    }

    @Override
    public void destroy() throws Exception {
        for (String key : producerBeanMap.keySet()) {
            OnsProducerBean producerBean = producerBeanMap.get(key);
            if (producerBean != null && producerBean.isStarted()) {
                producerBean.shutdown();
            }
        }
        for (String key : transactionProducerBeanMap.keySet()) {
            OnsTransactionProducerBean transactionProducerBean = transactionProducerBeanMap.get(key);
            if (transactionProducerBean != null && transactionProducerBean.isStarted()) {
                transactionProducerBean.shutdown();
            }
        }
        for (String key : consumerBeanMap.keySet()) {
            OnsConsumerBean consumerBean = consumerBeanMap.get(key);
            if (consumerBean != null && consumerBean.isStarted()) {
                consumerBean.shutdown();
            }
        }
        if (orderProducer != null && orderProducer.isStarted()) {
            orderProducer.shutdown();
        }
        if (orderConsumer != null && orderConsumer.isStarted()) {
            orderConsumer.shutdown();
        }
        logger.info(R.log.log_marker_ons, "ONS服务已停止！");
    }

    /**
     * 复制Properties
     *
     * @param source
     * @return
     */
    public Properties cloneProperties(Properties source) {
        Properties target = new Properties();
        for (Object key : source.keySet()) {
            String _key = (String) key;
            String value = source.getProperty(_key);
            target.setProperty(_key, value);
        }
        return target;
    }

    /**
     * 反序列化
     */
    public Object deserialize(byte[] bytes) {
        return fSTRedisSerializer.deserialize(bytes);
    }

    @SuppressWarnings("unchecked")
    public Message genMessage(OnsMessage onsMessage) {
        Message msg = new Message();
        String topic = onsMessage.getTopic();
        if (!StringUtils.hasText(topic)) {
            topic = onsTopic;
        }
        msg.setTopic(topic);
        msg.setTag(onsMessage.getTag());
        if (StringUtils.hasLength(onsMessage.getKey())) {
            msg.setKey(onsMessage.getKey());
        } else {
            msg.setKey(topic + "_" + onsMessage.getTag());
        }
        msg.setBody(fSTRedisSerializer.serialize(onsMessage.getObj()));
        if (onsMessage.getDelayTime() > 0) {
            msg.setStartDeliverTime(onsMessage.getDelayTime());
        }
        return msg;
    }

    private class OnsSendCallback implements SendCallback {

        @Override
        public void onSuccess(SendResult sendResult) {

        }

        @Override
        public void onException(OnExceptionContext context) {
            String errorMessage = "ONS发送消息失败[topic=" + context.getTopic() + ",messageId=" + context.getMessageId() + "]";
            logger.error(R.log.log_marker_ons, errorMessage, context.getException());
        }
    }

}
