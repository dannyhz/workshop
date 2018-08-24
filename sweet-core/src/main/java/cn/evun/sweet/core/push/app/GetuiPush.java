package cn.evun.sweet.core.push.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.evun.sweet.common.serialize.json.JsonUtils;
import cn.evun.sweet.common.util.CollectionUtils;
import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.cas.OnlineUserManager;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.push.PushMessage;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.exceptions.PushSingleException;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;

/**
 * 个推推送消息的实现
 *
 * @author yangw
 * @since 1.0.0
 */
@Component("getui")
public class GetuiPush implements AppPush {
	
	protected static final Logger LOGGER = LogManager.getLogger();

	@Value("${getui.appId:}")
	private String appId;
	@Value("${getui.appKey:}")
	private String appKey;
	@Value("${getui.masterSecret:}")
	private String masterSecret; 
	@Value("${getui.host:}")
	private String host;
	
	@Autowired
	private OnlineUserManager onlineUserManager;

    private static final long OFFLINE_EXPIRE_TIME = 24 * 3600 * 1000;
	
	@SuppressWarnings("unchecked")
	@Override
	public void doPush(PushMessage message) {
		Message appMsg = new Message();
		appMsg.setBody(String.valueOf(message.getMessage().getDatas().get("content")));
		appMsg.setTitle(String.valueOf(message.getMessage().getDatas().get("title")));
		appMsg.setType(message.getMsgFlag());
		appMsg.setProps((Map<String,Object>)message.getMessage().getDatas().get("source"));
		
		IPushResult res = null;
		if(CollectionUtils.isEmpty(message.getUserIds())){
			res = push(appMsg);
		}else if(message.getUserIds().size() == 1) {
			res = push(appMsg, onlineUserManager.getClientId(message.getUserId()));
		}else {
			List<String> clientIds = new ArrayList<String>();
			for(String userid : message.getUserIds()){
				clientIds.add(onlineUserManager.getClientId(userid));
			}
			res = push(appMsg, clientIds);
		}
		if(res != null){
			LOGGER.debug(R.log.log_marker_push, "message[{}] get return:{}", 
					message.getId(), res.getResponse());
		}	
	}
	
	private IPushResult push(Message message, String clientId) {	
		Target getuTarget = new Target();
		getuTarget.setAppId(appId);
		getuTarget.setClientId(clientId);
		
		IGtPush push = new IGtPush(host, appKey, masterSecret);
		TransmissionTemplate template = getTemplate(message);
		SingleMessage getuMessage = new SingleMessage();
		getuMessage.setOffline(true);
        //离线有效时间，单位为毫秒，可选
		getuMessage.setOfflineExpireTime(OFFLINE_EXPIRE_TIME);
		getuMessage.setData(template);
        //可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
		getuMessage.setPushNetWorkType(0);
		
		
        IPushResult ret = null;
        try {
            ret = push.pushMessageToSingle(getuMessage, getuTarget);
        } catch (RequestException e) {
        	try{
        		ret = push.pushMessageToSingle(getuMessage, getuTarget, e.getRequestId());
        	}catch (PushSingleException ex) {
        		LOGGER.error(R.log.log_marker_push, "failed to push message : {}, caurse : {}", 
            			message.getBody(), e.getMessage());
        	}
        }
		return ret;
	}

	private IPushResult push(Message message, List<String> clientId) {
		List<Target> targets = new ArrayList<Target>();
		for (String cId : clientId) {
			Target getuTarget = new Target();
			getuTarget.setAppId(appId);
			getuTarget.setClientId(cId);
			targets.add(getuTarget);
		}
		
		IGtPush push = new IGtPush(host, appKey, masterSecret);
		TransmissionTemplate template = getTemplate(message);
		ListMessage getuMessage = new ListMessage ();
		getuMessage.setOffline(true);      
		getuMessage.setOfflineExpireTime(OFFLINE_EXPIRE_TIME);
		getuMessage.setData(template);
		getuMessage.setPushNetWorkType(0);
		
        IPushResult ret = null;
        try {
            ret = push.pushMessageToList(push.getContentId(getuMessage), targets);
        } catch (RequestException e) {
        	LOGGER.error(R.log.log_marker_push, "failed to push message : {}, caurse : {}", 
        			message.getBody(), e.getMessage());
        }
		return ret;		
	}
	

	private IPushResult push(Message message) {
		IGtPush push = new IGtPush(host, appKey, masterSecret);
		TransmissionTemplate template = getTemplate(message);
		AppMessage getuMessage = new AppMessage();
		getuMessage.setOffline(true);
		getuMessage.setOfflineExpireTime(OFFLINE_EXPIRE_TIME);
		getuMessage.setData(template);
		getuMessage.setPushNetWorkType(0);
		List<String> appIdList = new ArrayList<String>();
        appIdList.add(appId);
        getuMessage.setAppIdList(appIdList);
		
        AppConditions cdt = new AppConditions();//推送给App的目标用户需要满足的条件               
        List<String> phoneTypeList = new ArrayList<String>();
        List<String> provinceList = new ArrayList<String>();
        List<String> tagList = new ArrayList<String>();
        cdt.addCondition(AppConditions.PHONE_TYPE, phoneTypeList);
        cdt.addCondition(AppConditions.REGION, provinceList);
        cdt.addCondition(AppConditions.TAG,tagList);
        
        IPushResult ret = null;
        try {
            ret = push.pushMessageToApp(getuMessage);
        } catch (RequestException e) {
        	LOGGER.error(R.log.log_marker_push, "failed to push message : {}, caurse : {}", 
        			message.getBody(), e.getMessage());
        }
		return ret;
	}
	
	private TransmissionTemplate getTemplate(Message message) {
	    TransmissionTemplate template = new TransmissionTemplate();
	    template.setAppId(appId);
	    template.setAppkey(appKey);
	    template.setTransmissionContent(JsonUtils.beanToJson(message));
	    template.setTransmissionType(2);
	    APNPayload payload = new APNPayload();
	    //在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
	    payload.setAutoBadge("+1");
	    payload.setContentAvailable(1);
	    if(StringUtils.hasText(message.getBody()) || StringUtils.hasText(message.getTitle())){
	    	payload.setSound("default");
	    }else {
	    	payload.setSound("com.gexin.ios.silence");
		}
	    //字典模式使用下者
	    payload.setAlertMsg(getDictionaryAlertMsg(message));
	    payload.setCategory(JsonUtils.beanToJson(message));
	    template.setAPNInfo(payload);
	    return template;
	}
	
	private APNPayload.DictionaryAlertMsg getDictionaryAlertMsg(Message message){
	    APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
	    if(StringUtils.hasText(message.getBody()) || StringUtils.hasText(message.getTitle())){
		    alertMsg.setBody(message.getBody());
		    alertMsg.setActionLocKey("ActionLockey");
		    //alertMsg.setLocKey("LocKey");
		    alertMsg.addLocArg("loc-args");
		    alertMsg.setLaunchImage("launch-image");
		    // IOS8.2以上版本支持
		    alertMsg.setTitle(message.getTitle());
		    //alertMsg.setTitleLocKey("TitleLocKey");
		    alertMsg.addTitleLocArg("TitleLocArg");
	    }
	    return alertMsg;
	}
}
