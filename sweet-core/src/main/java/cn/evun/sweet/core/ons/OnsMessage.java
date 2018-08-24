package cn.evun.sweet.core.ons;

/**
 * Ons发送消息对象
 * Created by Administrator on 2017/2/27.
 */
public class OnsMessage {

    /* 消息主题 */
    private String topic;

    /* 生产者ID */
    private String producerId;

    /* 消费者ID */
    private String consumerId;

    /* 消息标签，用于消息再分类，可以为空 */
    private String tag;

    /* 消息key，可以为空 */
    private String key;

    /* 消息体 */
    private Object obj;

    /* 延时投递时间(毫秒)，小于等于0表示立即发送 */
    private long delayTime = 0;

    /* 分区顺序消息中区分不同分区的关键字段, 全局顺序消息该字段可以设置为任意非空字符串 */
    private String shardingKey;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public String getShardingKey() {
        return shardingKey;
    }

    public void setShardingKey(String shardingKey) {
        this.shardingKey = shardingKey;
    }

    public OnsMessage() {
    }

    public OnsMessage(String topic, String producerId, String consumerId, String tag, String key, Object obj, long delayTime) {
        this.topic = topic;
        this.producerId = producerId;
        this.consumerId = consumerId;
        this.tag = tag;
        this.key = key;
        this.obj = obj;
        this.delayTime = delayTime;
    }

    /**
     * @param topic 消息主题
     * @param tag   消息标签
     * @param obj   消息体
     */
    public OnsMessage(String topic, String tag, Object obj) {
        this.topic = topic;
        this.tag = tag;
        this.obj = obj;
    }

    /**
     * @param topic 消息主题
     * @param tag   消息标签
     * @param key   消息标识
     * @param obj   消息体
     */
    public OnsMessage(String topic, String tag, String key, Object obj) {
        this.topic = topic;
        this.tag = tag;
        this.key = key;
        this.obj = obj;
    }

    /**
     * @param topic       消息主题
     * @param tag         消息标签
     * @param obj         消息体
     * @param shardingKey 发送顺序消息的key
     */
    public OnsMessage(String topic, String tag, Object obj, String shardingKey) {
        this.topic = topic;
        this.tag = tag;
        this.obj = obj;
        this.shardingKey = shardingKey;
    }

    @Override
    public String toString() {
        return "OnsMessage{topic='" + topic + '\'' + ", producerId='" + producerId + '\'' + ", consumerId='" + consumerId + '\'' +
                ", tag='" + tag + '\'' + ", key='" + key + '\'' + ", obj=" + obj + ", delayTime=" + delayTime +
                ", shardingKey='" + shardingKey + '\'' + '}';
    }
}
