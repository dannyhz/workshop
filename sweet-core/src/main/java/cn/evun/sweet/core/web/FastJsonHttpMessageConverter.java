package cn.evun.sweet.core.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * 自定义fastjson消息转换器
 *
 * @author shentao
 * @date 2017/8/10 11:10
 * @since 1.0.0
 */
public class FastJsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public final static Charset UTF8 = Charset.forName("UTF-8");

    //输出值为null字段的特性
    private final static SerializerFeature[] WRITE_NULL_VALUE_FEATURE = new SerializerFeature[]{
            SerializerFeature.WriteMapNullValue,       //输出值为null的字段
            SerializerFeature.WriteNullNumberAsZero,   //数值字段如果为null,输出为0,而非null
            SerializerFeature.WriteNullListAsEmpty,    //List字段如果为null,输出为[],而非null
            SerializerFeature.WriteNullStringAsEmpty,  //字符类型字段如果为null,输出为"",而非null
            SerializerFeature.WriteNullBooleanAsFalse};//Boolean字段如果为null,输出为false,而非null

    //是否输出值为null的字段
    private boolean writeNullValue;

    private Charset charset = UTF8;

    private SerializerFeature[] features = new SerializerFeature[0];

    public FastJsonHttpMessageConverter() {
        super(new MediaType("application", "json", UTF8), new MediaType("application", "*+json", UTF8));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setWriteNullValue(boolean writeNullValue) {
        this.writeNullValue = writeNullValue;
    }

    public SerializerFeature[] getFeatures() {
        return features;
    }

    public void setFeatures(SerializerFeature... features) {
        if (writeNullValue) { //输出值为null的字段
            if (ArrayUtils.isEmpty(features)) {
                this.features = WRITE_NULL_VALUE_FEATURE;
            } else { //数组拷贝
                SerializerFeature[] newFeatures = new SerializerFeature[features.length + WRITE_NULL_VALUE_FEATURE.length];
                System.arraycopy(features, 0, newFeatures, 0, features.length);
                System.arraycopy(WRITE_NULL_VALUE_FEATURE, 0, newFeatures, features.length, WRITE_NULL_VALUE_FEATURE.length);
                this.features = newFeatures;
            }
        } else {
            this.features = features;
        }
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        InputStream in = inputMessage.getBody();

        byte[] buf = new byte[1024];
        for (; ; ) {
            int len = in.read(buf);
            if (len == -1) {
                break;
            }

            if (len > 0) {
                baos.write(buf, 0, len);
            }
        }

        byte[] bytes = baos.toByteArray();
        return JSON.parseObject(bytes, 0, bytes.length, charset.newDecoder(), clazz);
    }

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException,
            HttpMessageNotWritableException {
        OutputStream out = outputMessage.getBody();
        String text = JSON.toJSONString(obj, features);
        byte[] bytes = text.getBytes(charset);
        out.write(bytes);
    }

}
