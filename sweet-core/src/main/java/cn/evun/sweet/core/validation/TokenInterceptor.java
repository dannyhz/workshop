package cn.evun.sweet.core.validation;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.cache.redis.RedisTemplateUtil;
import cn.evun.sweet.core.common.R;

/**
 * Spring MVC拦截器+注解方式实现防止表单重复提交，需要在spring MVC的配置文件中启用本拦截器<br/>
 * 原理：前端新建页面时生成token，当页面提交时提交时请求参数中带上toekn(如token={token})。&nbsp;&nbsp;&nbsp;&nbsp;<br/>
 * 如果redis中不存在这个token，表示是第一次提交，把token保存到redis中；&nbsp;&nbsp;&nbsp;&nbsp;<br/>
 * 如果redis中已存在这个token，表示是重复提交，验证不通过。<br/>
 * 使用方法：Controller接受页面提交的方法上加注解@Token，可以带参数expire，表示token保存在redsi的存活时间(秒)<br/>
 *
 * @author shentao
 */
public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Token annotation = method.getAnnotation(Token.class);
            if (annotation != null) {
                String token = request.getParameter("token");
                if (!StringUtils.hasText(token)) {
                	response.setStatus(700);
                    return false; //请求参数中必须有token
                }
                String redisKey = R.redisKey.token_pre + token;
                if (!RedisTemplateUtil.exists(redisKey)) {//Redis中没有这个token，表示是第一次提交
                    long expire = annotation.expire();
                    RedisTemplateUtil.setString(redisKey, "", expire > 0 ? expire : R.redisKey.token_expire);
                } else {//Redis中存在这个token，表示是重复提交 
                	response.setStatus(700);
                    return false;
                }
            }
            return true;
        }

        return super.preHandle(request, response, handler);
    }

//    @Override    
//    public void postHandle(HttpServletRequest request,    
//            HttpServletResponse response, Object handler,    
//            ModelAndView modelAndView) throws Exception {
//    	if (handler instanceof HandlerMethod) {
//            HandlerMethod handlerMethod = (HandlerMethod) handler;
//            Method method = handlerMethod.getMethod();
//            Token annotation = method.getAnnotation(Token.class);
//            if (annotation != null) {
//                String token = request.getParameter("token");
//                if (StringUtils.hasText(token)) {
//                	RedisTemplateUtil.delete(R.redisKey.token_pre + token);
//                }
//            }
//        }
//
//        super.postHandle(request, response, handler, modelAndView);
//    }
}
