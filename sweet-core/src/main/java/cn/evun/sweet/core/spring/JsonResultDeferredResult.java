package cn.evun.sweet.core.spring;

import org.springframework.web.context.request.async.DeferredResult;

import cn.evun.sweet.core.common.JsonResultDO;

/**
 * 由于$resource提交的请求头不含X-Request-With，使得在异步请求的统一超时处理时无法判断出是否是ajax请求，从而
 * 作出不同的应答。这里通过定义具体的类来实现区分。<br/>
 * 因此，在涉及到$resource发起的异步请求时，需要使用该子类来完成异步的工作内容。
 *
 * @author yangw
 * @since 1.0.0
 */
public class JsonResultDeferredResult extends DeferredResult<JsonResultDO>{

}
