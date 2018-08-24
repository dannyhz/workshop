package cn.evun.sweet.core.controller;

import java.lang.reflect.Method;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.common.util.reflect.ReflectionUtils;
import cn.evun.sweet.core.bussinesshelper.GeneralObjectAccessService;
import cn.evun.sweet.core.bussinesshelper.SqlInvokeService;
import cn.evun.sweet.core.cas.ModelAliasRegistry;
import cn.evun.sweet.core.common.JsonResultDO;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.exception.SweetException;
import cn.evun.sweet.core.service.RegistyServiceInvoker;

/**
 * 提供实体类资源（Model）的RESTFul服务（CRUD）。这些实体类必须是在容器中注册的。
 * 同时也为系统注册过的业务逻辑（Service层）或Sql语句（Dao层）提供RESTFul调用服务。
 * 注：RESTFul三大资源种类：数据资源（bean）、业务单元（service）、静态资源（css，jpg）
 *
 * @author yangw
 * @since V1.0.0
 */
@Controller
public class BaseController {

    @Resource
    private GeneralObjectAccessService objAccessService;

    @Resource
    private ModelAliasRegistry modelAlias;

    @Resource
    private SqlInvokeService sqlInvoker = null;

    static {
        /*解决属性填充时不支持各种格式的日期字符串到日期的转换问题*/
        DateConverter dConverter = new DateConverter();
        dConverter.setPatterns(new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH", "yyyy-MM-dd HH:mm"});
        ConvertUtils.register(dConverter, Date.class);
    }

    /**
     * 查询资源，使用json返回查询结果。需要传递实例参数
     * 请求匹配路径：\entity\**\{resourceName}\{resourceId}
     */
    @RequestMapping(value = "/entity/**/{resourceName}/{resourceId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResultDO get(@PathVariable("resourceName") String resourceName,
                            @PathVariable("resourceId") String resourceId,
                            @RequestParam(value = "relation", required = false, defaultValue = "false") Boolean relation) {
        JsonResultDO result = new JsonResultDO();
        Class<?> entityClass = modelAlias.resolveAlias(resourceName);
        if (entityClass == null) {
            result.setSuccess(false);
            result.setMsgCode(R.exception.excode_resource_notexist);
            return result;
        }
        Object resource = null;
        if (relation) {//开启关联查询
            resource = objAccessService.queryByIdWithRelation(entityClass, resourceId);
        } else {//仅查询单表
            resource = objAccessService.queryById(entityClass, resourceId);
        }
        result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, resource);//存放结果
        return result;
    }

    /**
     * 新建资源（单个），使用json返回结果。
     */
    //@RequestMapping(value="/entity/**/{resourceName}", method=RequestMethod.POST)
    //@ResponseBody
    public JsonResultDO post(@PathVariable("resourceName") String resourceName, HttpServletRequest request) {
        Class<?> entityClass = modelAlias.resolveAlias(resourceName);
        JsonResultDO result = new JsonResultDO();
        if (entityClass == null) {
            result.setSuccess(false);
            result.setMsgCode(R.exception.excode_resource_notexist);
            return result;
        }

        Object entity = BeanUtils.instantiate(entityClass);
        try {
            org.apache.commons.beanutils.BeanUtils.populate(entity, request.getParameterMap());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Method initMethod = ReflectionUtils.findMethod(entityClass, "initDefaultInfo");
        if (initMethod != null) {
            ReflectionUtils.invokeMethod(initMethod, entity);
        }
        //objAccessService.addSelective(entity);
        return result;
    }

    /**
     * 更新资源（单个），空字段也会更新数据库。 使用json返回查询结果。
     */
    //@RequestMapping(value="/entity/**/{resourceName}", method=RequestMethod.PUT)
    //@ResponseBody
    public JsonResultDO put(@PathVariable("resourceName") String resourceName, HttpServletRequest request,
                            @RequestParam(value = "updateAllField", required = false) boolean updateAllField) {
        JsonResultDO result = new JsonResultDO();
        Class<?> entityClass = modelAlias.resolveAlias(resourceName);
        if (entityClass == null) {
            result.setSuccess(false);
            result.setMsgCode(R.exception.excode_resource_notexist);
            return result;
        }

        Object entity = BeanUtils.instantiate(entityClass);
        try {
            org.apache.commons.beanutils.BeanUtils.populate(entity, request.getParameterMap());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Method initMethod = ReflectionUtils.findMethod(entityClass, "modifyDefaultInfo");
        if (initMethod != null) {
            ReflectionUtils.invokeMethod(initMethod, entity);
        }

        if (updateAllField) {
            //objAccessService.modifyByPrimaryKey(entity);
        } else {
            //objAccessService.modifyByPrimaryKeySelective(entity);
        }
        return result;
    }

    /**
     * 删除资源（单个或多个）。 使用json返回查询结果。
     */
    //@RequestMapping(value="/entity/**/{resourceName}", method=RequestMethod.DELETE)
    //@ResponseBody
    public JsonResultDO delete(@PathVariable("resourceName") String resourceName,
                               @RequestParam("ids") Integer[] ids) {
        JsonResultDO result = new JsonResultDO();
        Class<?> entityClass = modelAlias.resolveAlias(resourceName);
        if (entityClass == null) {
            result.setSuccess(false);
            result.setMsgCode(R.exception.excode_resource_notexist);
            return result;
        }

        for (int i = 0; i < ids.length; i++) {
            //objAccessService.deleteByPrimaryKey(entityClass, ids[i]);
        }

        return result;
    }

    /**
     * 调用通过@RegistyService、@RegistyMethod注册的服务以及SqlInvokeService。
     * 请求来源涵盖：headers="Accept=text/html" & headers="X-Requested-With=XMLHttpRequest"
     */
    @RequestMapping(value = "/service/**")
    @ResponseBody
    public JsonResultDO service(HttpServletRequest request,
                                @RequestParam(value = "params", required = false) Object[] servparams) {
        JsonResultDO result = new JsonResultDO();
        String serviceid = StringUtils.replace(StringUtils.delete(processPath(request), "/service/"), "/", ".");
       
    	/*如果找到对应的注册的sql语句，则优先选择sql语句执行*/
        if (sqlInvoker.isSupport(serviceid)) {
            result.addAttribute("result", sqlInvoker.excuteSql(serviceid,
                    (Integer) request.getAttribute("pageNum"), (Integer) request.getAttribute("pageSize")));
        } else {//否则尝试执行注册的服务
            try {
                result.addAttribute("result", RegistyServiceInvoker.invoker(serviceid, servparams));
            } catch (SweetException se) {
                result.setSuccess(false);
                result.setMsgCode(se.getExCode());//result.setMsgCode(R.exception.excode_illegalargument);
            }
        }
        return result;
    }

    /**
     * 直接跳转到对应的页面
     */
    @RequestMapping(value = "/direct/*", method = RequestMethod.GET)
    public String noBussinessForward(HttpServletRequest request, ModelMap model) {
        return StringUtils.delete(processPath(request), "/direct");
    }


    /**
     * {@code "  // /// ////  foo/bar"} 将解析为  {@code "/foo/bar"}.
     */
    protected String processPath(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        boolean slash = false;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/') {
                slash = true;
            } else if (path.charAt(i) > ' ' && path.charAt(i) != 127) {
                if (i == 0 || (i == 1 && slash)) {
                    return path;
                }
                path = slash ? "/" + path.substring(i) : path.substring(i);
                return path;
            }
        }
        return (slash ? "/" : "");
    }

}
