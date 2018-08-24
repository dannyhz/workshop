package cn.evun.sweet.core.datafiltering;

import cn.evun.sweet.common.util.BeanUtils;
import cn.evun.sweet.common.util.CollectionUtils;
import cn.evun.sweet.core.common.JsonResultDO;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义ResponseBody扩展，用于数据过滤拦截DataFiltering注解，该注解只作用于接口方法上<br/>
 * 只对有Controller和RestController两种注解的类生效
 *
 * @author shentao
 * @date 2017/5/4 10:51
 * @since 1.0.0
 */
@ControllerAdvice(annotations = {Controller.class, RestController.class})
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    protected static final Logger LOGGER = LogManager.getLogger();

    /**
     * ，判断方法上是否有DataFiltering注解没有就不执行beforeBodyWrite方法
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getMethod().isAnnotationPresent(DataFiltering.class);
    }

    /**
     * ResponseBody返回之前的处理过程
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return null;
        }
        boolean isJsonResultDO = body instanceof JsonResultDO;//判断返回值是否为JsonResultDO类型
        Object datas = null;
        if (isJsonResultDO) {//如果返回值是JsonResultDO类型
            JsonResultDO jsonResultDO = (JsonResultDO) body;
            if (!jsonResultDO.isSuccess()) {//请求失败就直接返回
                return jsonResultDO;
            }
            datas = jsonResultDO.getDatas().get(JsonResultDO.RETURN_OBJECT_KEY);//从JsonResultDO中取出返回内容
            if (datas == null) {
                return jsonResultDO;
            }
        } else {
            datas = body;
        }

        /* 注解处理过程 */
        DataFiltering dataFiltering = returnType.getMethod().getAnnotation(DataFiltering.class);
        SerializedField[] serializedFields = dataFiltering.serializedFields();
        if (ArrayUtils.isNotEmpty(serializedFields)) {
            Terminal terminal = TerminalUtil.checkTerminal(request);//当前终端类型
            for (SerializedField serializedField : serializedFields) {
                if (serializedField.terminal().equals(terminal)) {
                    String[] includes = serializedField.includes();//需要返回的字段
                    String[] excludes = serializedField.excludes();//需要排除的字段
                    if ((ArrayUtils.isEmpty(includes) && ArrayUtils.isEmpty(excludes)) ||//includes和excludes都为空就不处理
                            (ArrayUtils.isNotEmpty(includes) && ArrayUtils.isNotEmpty(excludes))) {//includes和excludes不能都有值
                        break;
                    }
                    if (datas instanceof List) {//如果是List
                        if (CollectionUtils.isNotEmpty((List) datas)) {
                            datas = handleList((List) datas, stringArrayToString(includes), stringArrayToString(excludes));
                        }
                    } else {//如果是单个对象
                        datas = handleSingleObject(datas.getClass().getDeclaredFields(), datas, stringArrayToString(includes), stringArrayToString(excludes));
                    }
                    break;
                }
            }

            if (isJsonResultDO) {
                ((JsonResultDO) body).addAttribute(JsonResultDO.RETURN_OBJECT_KEY, datas);
            } else {
                body = datas;
            }
        }

        return body;
    }

    /**
     * 处理单个对象<br/>
     * 注意：对象的类必须有一个无参的构造函数
     *
     * @param fields   通过反射获得的当前对象所有字段
     * @param source   源对象
     * @param includes 需要返回的字段
     * @param excludes 需要排除的字段
     */
    private Object handleSingleObject(Field[] fields, Object source, String includes, String excludes) {
        Object target = null;
        try {
            target = source.getClass().newInstance();
        } catch (Exception e) {
            LOGGER.error("Create object instance failuerd", e);
            return null;
        }
        boolean isExcludes = excludes.length() > 0;
        if (isExcludes) {//excludes.length()大于0表示进行排除字段操作
            BeanUtils.copyProperties(source, target);
        }
        for (Field field : fields) {
            try {
                if (isExcludes) {//需要排除字段
                    if (excludes.indexOf(field.getName() + ";") > -1) {
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        field.set(target, null);
                    }
                } else {
                    if (includes.indexOf(field.getName() + ";") > -1) {
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        field.set(target, field.get(source));
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return target;
    }

    /**
     * 处理List
     *
     * @param sourceList 源list
     * @param includes   需要返回的字段
     * @param excludes   需要排除的字段
     */
    private List<Object> handleList(List<Object> sourceList, String includes, String excludes) {
        List<Object> targetList = new ArrayList<>(sourceList.size());
        Field[] fields = sourceList.get(0).getClass().getDeclaredFields();
        for (Object source : sourceList) {
            Object target = handleSingleObject(fields, source, includes, excludes);
            if (target != null) {
                targetList.add(target);
            }
        }
        return targetList;
    }

    /**
     * 字符串数组转化为字符串，以分号分隔
     *
     * @param strings 字符串数组
     */
    private String stringArrayToString(String[] strings) {
        StringBuilder sb = new StringBuilder();
        sb.append("");
        if (ArrayUtils.isNotEmpty(strings)) {
            for (String str : strings) {
                sb.append(str).append(";");
            }
        }
        return sb.toString();
    }

}
