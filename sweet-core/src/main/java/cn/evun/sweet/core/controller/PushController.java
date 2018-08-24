package cn.evun.sweet.core.controller;

import javax.annotation.Resource;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.evun.sweet.core.cas.ContextHolder;
import cn.evun.sweet.core.cas.OnlineUserManager;
import cn.evun.sweet.core.common.JsonResultDO;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.push.AsyncReqPushManager;
import cn.evun.sweet.core.push.MsgPushDeferredResult;

/**   
 * 通用消息推送请求。用户请求用户的推送消息，采用异步请求，支持commet方式
 * 
 * @author yangw   
 * @since V1.0.0   
 */
@Controller
public class PushController {
	
	//@Autowired
	private AsyncReqPushManager reqMgr;
	
	//@Resource
	private OnlineUserManager onlineUserManager;
	
	protected static final Logger LOGGER = LogManager.getLogger();	

	/**
	 * 消息推送请求入口。
	 */
	@RequestMapping(value="/push/msgpoll")
	@ResponseBody
	public MsgPushDeferredResult msgPollRequest(){		
		MsgPushDeferredResult deferredResult = new MsgPushDeferredResult();	
		Object userinfo = ContextHolder.getSession().getAttribute(R.session.user_info);
		try{
			deferredResult.setUserId((Long)MethodUtils.invokeMethod(userinfo, "getUserId", null));
			//deferredResult.setTenantId((Long)MethodUtils.invokeMethod(userinfo, "getUserTenantId", null));
		}catch(Exception e){
			LOGGER.error("Failed get userid for push request.", e);
		}
		reqMgr.addPollRequest(deferredResult);
		return deferredResult;
	}
	
	/**
	 * 必须在APP啓動时调用，用于环境清理
	 * 
	 * @param clientId  APP推送的唯一ID
	 */
	@RequestMapping(value="/push/init", method=RequestMethod.GET)
	@ResponseBody
	public JsonResultDO pushInit(String AppClientId){
		onlineUserManager.kick(AppClientId);
		return new JsonResultDO();
	}
}
