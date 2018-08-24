package cn.evun.sweet.core.ons;

import cn.evun.sweet.common.util.DateUtils;
import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.common.R;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.ons.model.v20170918.*;
import com.aliyuncs.ons.model.v20170918.OnsMessagePageQueryByTopicResponse.MsgFoundDo;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ONS监控服务<br/>
 * Created by Administrator on 2017/3/29.
 */
public class OnsMonitorService implements InitializingBean, DisposableBean {

    protected static final Logger logger = LogManager.getLogger();

    private static final String Product_Name = "Ons";

    @NotNull(message = "ONS公共配置不能为空！")
    private OnsConfig.OnsConfigBean onsConfigBean;

    private String defaultRegionId;
    private String accessKey;
    private String secretKey;

    /* 存储ONS区域连接客户端的MAP */
    private static final Map<String, IAcsClient> IAcsClient_Map = new ConcurrentHashMap<>();

    public OnsMonitorService() {
    }

    public OnsMonitorService(String defaultRegionId, String accessKey, String secretKey) {
        this.defaultRegionId = defaultRegionId;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public OnsMonitorService(OnsConfig.OnsConfigBean onsConfigBean) {
        this.onsConfigBean = onsConfigBean;
        this.defaultRegionId = onsConfigBean.getProperties().getProperty("onsRegionId");
        this.accessKey = onsConfigBean.getProperties().getProperty(PropertyKeyConst.AccessKey);
        this.secretKey = onsConfigBean.getProperties().getProperty(PropertyKeyConst.SecretKey);
    }

    /**
     * 获取Region连接客户端
     *
     * @param regionId 区域ID
     */
    public IAcsClient getIAcsClient(String regionId) {
        if (!StringUtils.hasText(regionId)) {
            regionId = defaultRegionId;
        }
        IAcsClient iAcsClient = IAcsClient_Map.get(regionId);
        if (iAcsClient != null) {
            return iAcsClient;
        }
        synchronized (regionId) {
            if ((iAcsClient = IAcsClient_Map.get(regionId)) != null) {
                return iAcsClient;
            }
            String endPointName = regionId;
            String domain = "ons." + regionId + ".aliyuncs.com";
            try {
                DefaultProfile.addEndpoint(endPointName, regionId, Product_Name, domain);
                IClientProfile profile = DefaultProfile.getProfile(regionId, accessKey, secretKey);
                iAcsClient = new DefaultAcsClient(profile);
                IAcsClient_Map.put(regionId, iAcsClient);
                return iAcsClient;
            } catch (Exception e) {
                logger.error(R.log.log_marker_ons, "获取ONS区域节点[" + regionId + "]失败", e);
            }
        }
        return null;
    }

    /**
     * 获取ONS区域列表
     */
    public List<OnsRegionListResponse.RegionDo> getOnsRegionList() {
        IAcsClient iAcsClient = getIAcsClient(defaultRegionId);
        OnsRegionListRequest request = new OnsRegionListRequest();
        request.setAcceptFormat(FormatType.JSON);
        request.setPreventCache(System.currentTimeMillis());
        try {
            OnsRegionListResponse response = iAcsClient.getAcsResponse(request);
            List<OnsRegionListResponse.RegionDo> regionDoList = response.getData();
            return regionDoList;
        } catch (Exception e) {
            logger.error(R.log.log_marker_ons, "获取ONS区域列表失败", e);
        }
        return null;
    }

    /**
     * 查询消费堆积
     *
     * @param regionId   区域ID
     * @param consumerId 消费者ID
     * @param idDetail   是否查询详细信息
     */
    public OnsConsumerAccumulateResponse.Data queyrOnsConsumerAccumulate(String regionId, String consumerId, boolean idDetail) {
        regionId = StringUtils.hasText(regionId) ? regionId : defaultRegionId;
        IAcsClient iAcsClient = getIAcsClient(regionId);
        OnsConsumerAccumulateRequest request = new OnsConsumerAccumulateRequest();
        request.setOnsRegionId(regionId);
        request.setPreventCache(System.currentTimeMillis());
        request.setAcceptFormat(FormatType.JSON);
        request.setDetail(idDetail);
        request.setConsumerId(consumerId);
        try {
            OnsConsumerAccumulateResponse response = iAcsClient.getAcsResponse(request);
            OnsConsumerAccumulateResponse.Data data = response.getData();
            return data;
        } catch (Exception e) {
            logger.error(R.log.log_marker_ons, "查询消费堆积出现异常[regionId=" + regionId + ",consumerId=" + consumerId + "]", e);
        }
        return null;
    }

    /**
     * 查询消费状态
     *
     * @param regionId     区域ID
     * @param consumerId   消费者ID
     * @param idDetail     是否查询详细信息
     * @param isNeedJstack 是否打印JStack信息
     */
    public OnsConsumerStatusResponse.Data queryOnsConsumerStatus(String regionId, String consumerId, boolean idDetail,
                                                                 boolean isNeedJstack) {
        regionId = StringUtils.hasText(regionId) ? regionId : defaultRegionId;
        IAcsClient iAcsClient = getIAcsClient(regionId);
        OnsConsumerStatusRequest request = new OnsConsumerStatusRequest();
        request.setOnsRegionId(regionId);
        request.setPreventCache(System.currentTimeMillis());
        request.setAcceptFormat(FormatType.JSON);
        request.setConsumerId(consumerId);
        request.setDetail(idDetail);
        request.setNeedJstack(isNeedJstack);
        try {
            OnsConsumerStatusResponse response = iAcsClient.getAcsResponse(request);
            OnsConsumerStatusResponse.Data data = response.getData();
            return data;
        } catch (Exception e) {
            logger.error(R.log.log_marker_ons, "查询消费状态出现异常[regionId=" + regionId + ",consumerId=" + consumerId + "]", e);
        }
        return null;
    }

    /**
     * 根据Topic查询消息详情，包括消息列表
     *
     * @param regionId    区域ID
     * @param topic       消息主题
     * @param beginTime   开始时间
     * @param endTime     结束时间
     * @param taskId      任务ID，首次查询时为空，再次查询时需传此参数，用于分页查询
     * @param currentPage 当前页号
     * @param pageSize    页大小
     */
    public OnsMessagePageQueryByTopicResponse queryOnsMessagePageQueryByTopic(String regionId, String topic, Date beginTime,
                                                                              Date endTime, String taskId, Integer currentPage, Integer pageSize) {
        regionId = StringUtils.hasText(regionId) ? regionId : defaultRegionId;
        IAcsClient iAcsClient = getIAcsClient(regionId);
        OnsMessagePageQueryByTopicRequest request = new OnsMessagePageQueryByTopicRequest();
        request.setOnsRegionId(regionId);
        request.setPreventCache(System.currentTimeMillis());
        request.setAcceptFormat(FormatType.JSON);
        request.setTopic(topic);
        request.setBeginTime(beginTime == null ? DateUtils.addDateDay(endTime == null ? new Date() : endTime, -1).getTime() : beginTime.getTime());
        request.setEndTime(endTime == null ? new Date().getTime() : endTime.getTime());
        request.setCurrentPage(currentPage < 1 ? 1 : currentPage);
        request.setPageSize(pageSize < 5 ? 5 : (pageSize > 50 ? 50 : pageSize));
        if (StringUtils.hasText(taskId)) {
            request.setTaskId(taskId);
        }
        try {
            OnsMessagePageQueryByTopicResponse response = iAcsClient.getAcsResponse(request);
            return response;
        } catch (Exception e) {
            logger.error(R.log.log_marker_ons, "根据Topic查询消息出现异常[regionId=" + regionId + ",topic=" + topic + "]", e);
        }
        return null;
    }

    /**
     * 根据Topic查询消息列表，返回结果中taskId为任务ID，分页时传入，msgFoundList为消息列表
     *
     * @param regionId    区域ID
     * @param topic       消息主题
     * @param beginTime   开始时间
     * @param endTime     结束时间
     * @param taskId      任务ID，首次查询时为空，再次查询时需传此参数，用于分页查询
     * @param currentPage 当前页号
     * @param pageSize    页大小
     */
    public MsgFoundDo queryOnsMessageListByTopic(String regionId, String topic, Date beginTime, Date endTime,
                                                 String taskId, Integer currentPage, Integer pageSize) {
        OnsMessagePageQueryByTopicResponse response = queryOnsMessagePageQueryByTopic(regionId, topic, beginTime,
                endTime, taskId, currentPage, pageSize);
        if (response != null) {
            return response.getMsgFoundDo();
        }
        return null;
    }

    /**
     * 根据MsgID查询消息，其中body必须经过Base64解码后才能转为字符串
     *
     * @param regionId 区域ID
     * @param topic    消息主题
     * @param msgId    消息ID
     */
    public OnsMessageGetByMsgIdResponse.Data queryOnsMessageGetByMsgId(String regionId, String topic, String msgId) {
        regionId = StringUtils.hasText(regionId) ? regionId : defaultRegionId;
        IAcsClient iAcsClient = getIAcsClient(regionId);
        OnsMessageGetByMsgIdRequest request = new OnsMessageGetByMsgIdRequest();
        request.setOnsRegionId(regionId);
        request.setPreventCache(System.currentTimeMillis());
        request.setAcceptFormat(FormatType.JSON);
        request.setTopic(topic);
        request.setMsgId(msgId);
        try {
            OnsMessageGetByMsgIdResponse response = iAcsClient.getAcsResponse(request);
            OnsMessageGetByMsgIdResponse.Data data = response.getData();
            return data;
        } catch (Exception e) {
            logger.error(R.log.log_marker_ons, "根据MsgID查询消息出现异常[regionId=" + regionId + ",topic=" +
                    topic + ",msgId=" + msgId + "]", e);
        }
        return null;
    }

    /**
     * 根据MsgID查询消息轨迹
     *
     * @param regionId 区域ID
     * @param topic    消息主题
     * @param msgId    消息ID
     */
    public List<OnsMessageTraceResponse.MessageTrack> queryOnsMessageTrack(String regionId, String topic, String msgId) {
        regionId = StringUtils.hasText(regionId) ? regionId : defaultRegionId;
        IAcsClient iAcsClient = getIAcsClient(regionId);
        OnsMessageTraceRequest request = new OnsMessageTraceRequest();
        request.setOnsRegionId(regionId);
        request.setPreventCache(System.currentTimeMillis());
        request.setAcceptFormat(FormatType.JSON);
        request.setTopic(topic);
        request.setMsgId(msgId);
        try {
            OnsMessageTraceResponse response = iAcsClient.getAcsResponse(request);
            List<OnsMessageTraceResponse.MessageTrack> trackList = response.getData();
            return trackList;
        } catch (Exception e) {
            logger.error(R.log.log_marker_ons, "根据MsgID查询消息轨迹出现异常[regionId=" + regionId + ",topic=" +
                    topic + ",msgId=" + msgId + "]", e);
        }
        return null;
    }

    /**
     * Topic写入统计
     *
     * @param regionId  区域ID
     * @param topic     消息主题
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param period    采样周期，单位分钟，支持（1，5，10）
     * @param type      查询的类型（0代表总量，1代表TPS）
     */
    public OnsTrendTopicInputTpsResponse.Data queryOnsTrendTopicInputTps(String regionId, String topic, Date beginTime,
                                                                         Date endTime, Long period, Integer type) {
        regionId = StringUtils.hasText(regionId) ? regionId : defaultRegionId;
        IAcsClient iAcsClient = getIAcsClient(regionId);
        OnsTrendTopicInputTpsRequest request = new OnsTrendTopicInputTpsRequest();
        request.setOnsRegionId(regionId);
        request.setPreventCache(System.currentTimeMillis());
        request.setAcceptFormat(FormatType.JSON);
        request.setTopic(topic);
        request.setBeginTime(beginTime == null ? DateUtils.addDateDay(endTime == null ? new Date() : endTime, -1).getTime() : beginTime.getTime());
        request.setEndTime(endTime == null ? new Date().getTime() : endTime.getTime());
        request.setPeriod(period == null ? 10 : period);
        request.setType(type == null ? 0 : 1);
        try {
            OnsTrendTopicInputTpsResponse response = iAcsClient.getAcsResponse(request);
            OnsTrendTopicInputTpsResponse.Data data = response.getData();
            return data;
        } catch (Exception e) {
            logger.error(R.log.log_marker_ons, "查询Topic写入统计出现异常[regionId=" + regionId + ",topic=" + topic + "]", e);
        }
        return null;
    }

    /**
     * CID(消费者ID)投递统计
     *
     * @param regionId   区域ID
     * @param topic      消息主题
     * @param consumerId 消费者ID
     * @param beginTime  开始时间
     * @param endTime    结束时间
     * @param period     采样周期，单位分钟，支持（1，5，10）
     * @param type       查询的类型（0代表总量，1代表TPS）
     */
    public OnsTrendGroupOutputTpsResponse.Data queryOnsTrendGroupOutputTps(String regionId, String topic, String consumerId,
                                                                           Date beginTime, Date endTime, Long period, Integer type) {
        regionId = StringUtils.hasText(regionId) ? regionId : defaultRegionId;
        IAcsClient iAcsClient = getIAcsClient(regionId);
        OnsTrendGroupOutputTpsRequest request = new OnsTrendGroupOutputTpsRequest();
        request.setOnsRegionId(regionId);
        request.setPreventCache(System.currentTimeMillis());
        request.setAcceptFormat(FormatType.JSON);
        request.setTopic(topic);
        request.setConsumerId(consumerId);
        request.setBeginTime(beginTime == null ? DateUtils.addDateDay(endTime == null ? new Date() : endTime, -1).getTime() : beginTime.getTime());
        request.setEndTime(endTime == null ? new Date().getTime() : endTime.getTime());
        request.setPeriod(period == null ? 10 : period);
        request.setType(type == null ? 0 : 1);
        try {
            OnsTrendGroupOutputTpsResponse response = iAcsClient.getAcsResponse(request);
            OnsTrendGroupOutputTpsResponse.Data data = response.getData();
            return data;
        } catch (Exception e) {
            logger.error(R.log.log_marker_ons, "查询CID投递统计出现异常[regionId=" + regionId + ",topic=" + topic +
                    ",consumerId=" + consumerId + "]", e);
        }
        return null;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info(R.log.log_marker_ons, "ONS监控服务已启动！");
    }
}
