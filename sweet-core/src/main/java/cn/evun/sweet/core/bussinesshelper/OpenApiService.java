package cn.evun.sweet.core.bussinesshelper;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.common.util.network.HttpClientUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于Http Header进行签名的Open-Api校验方案
 *
 * @author yangw
 * @since 1.0.0
 */
@Component
public class OpenApiService implements InitializingBean {

    protected static final Logger LOGGER = LogManager.getLogger();

    @Value("${openapi.keyandsecret:default}")
    private String appKeyArray;

    private Map<String, String> appkeyMap = new HashMap<String, String>();

    /**
     * 判断请求是否具备合法签名
     */
    public boolean checkRequestSign(HttpServletRequest request) {
        String appkey = request.getHeader("appkey");
        String nonce = request.getHeader("nonce"); // 获取随机数。
        String timestamp = request.getHeader("timestamp"); // 获取时间戳。
        String signature = request.getHeader("signature"); // 获取数据签名。

        String appSecret = appkeyMap.get(appkey); // 开发者平台分配的 App Secret。
        if (!StringUtils.hasText(appSecret) || !StringUtils.hasText(nonce)
                || !StringUtils.hasText(timestamp) || !StringUtils.hasText(signature)) {
            return false;
        }

        String local_signature = org.apache.commons.codec.digest.DigestUtils.sha1Hex(
                appSecret + nonce + timestamp); //生成本地签名。
        if (!local_signature.equalsIgnoreCase(signature)) {
            return false;
        }
        return true;
    }

    /**
     * 发送带有签名信息的get请求，并得到返回结果
     */
    public String executeGetWithSign(String appkey, String url) {
        String appSecret = appkeyMap.get(appkey);
        if (!StringUtils.hasText(appkey) || !StringUtils.hasText(url) || !StringUtils.hasText(appSecret)) {
            return null;
        }

        String nonce = RandomStringUtils.random(15, true, true);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = org.apache.commons.codec.digest.DigestUtils.sha1Hex(appSecret + nonce + timestamp);
        Map<String, String> header = genHeader(appkey, nonce, timestamp, signature);
        try {
            return HttpClientUtil.httpGet(url, header);
        } catch (IOException e) {
            LOGGER.error(e);
            return null;
        }
    }

    /**
     * 发送带有签名信息的post请求，并得到返回结果，参数params用Base64编码
     */
    public String executePostWithSign(String appkey, String url, String params) {
        String appSecret = appkeyMap.get(appkey);
        if (!StringUtils.hasText(appkey) || !StringUtils.hasText(url) || !StringUtils.hasText(appSecret)) {
            return null;
        }
        String nonce = RandomStringUtils.random(15, true, true);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = org.apache.commons.codec.digest.DigestUtils.sha1Hex(appSecret + nonce + timestamp);
        Map<String, String> header = genHeader(appkey, nonce, timestamp, signature);
        try {
            return HttpClientUtil.httpPost(url, params, header);
        } catch (IOException e) {
            LOGGER.error(e);
            return null;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.hasText(appKeyArray) && !"default".equals(appKeyArray)) {
            String[] apps = StringUtils.delimitedListToStringArray(this.appKeyArray, ";");
            for (String app : apps) {
                String[] keyandValue = StringUtils.delimitedListToStringArray(app, ",");
                this.appkeyMap.put(keyandValue[0], keyandValue[1]);
            }
        }
    }

    private Map<String, String> genHeader(String... args) {
        Map<String, String> header = new HashMap<>(4);
        header.put("appkey", args[0]);
        header.put("nonce", args[1]);
        header.put("timestamp", args[2]);
        header.put("signature", args[3]);
        return header;
    }

}
