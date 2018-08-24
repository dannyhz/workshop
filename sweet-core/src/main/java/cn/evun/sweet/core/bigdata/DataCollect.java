package cn.evun.sweet.core.bigdata;

import cn.evun.sweet.common.serialize.json.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;

/**
 * 数据采集通用工具类
 *
 * @author shentao
 * @date 2018/2/28 19:43
 * @since 1.0.0
 */
public class DataCollect {

    private static Logger logger = LogManager.getLogger(DataCollect.class.getName());

    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXX";//时间数据的转换格式，带时区

    public static void log(DataEvent dataEvent) {
        try {
            LinkedHashMap<String, Object> data = new LinkedHashMap<>();
            data.put("app", dataEvent.getApp());
            data.put("business", dataEvent.getBusiness());
            data.put("source", dataEvent.getSource());
            logger.info(JsonUtils.beanToJson(data));
        } catch (Exception e) {
            logger.error("Data class could not be serialized as Json:" + dataEvent.toString(), e);
        }
    }

}
