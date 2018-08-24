package cn.evun.sweet.core.push;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import cn.evun.sweet.core.common.JsonResultDO;
import cn.evun.sweet.core.mongodb.IdEntity;

/**
 * 待推送到消息实体
 *
 * @author yangw
 * @since 1.0.0
 */
public class PushMessage extends IdEntity implements Serializable{

	private static final long serialVersionUID = 3996648831473209376L;
	
	public static final String MESSAGE_LEVEL_INFO = "info";
	public static final String MESSAGE_LEVEL_SUCCESS = "success";
	public static final String MESSAGE_LEVEL_WAIT = "wait";
	public static final String MESSAGE_LEVEL_WARNING = "warning";
	public static final String MESSAGE_LEVEL_DANGER = "danger";
	
	public static final int MESSAGE_TYPE_WEB = 1;
	public static final int MESSAGE_TYPE_APP = 2;
	public static final int MESSAGE_TYPE_BOTH = 3;
	
	private String msgId;

	/**推送的目标用户，优先级最高*/
	private List<String>  userIds = new ArrayList<String>();
	
	/**推送的租户，优先级次之*/
	private Long  tenantId;
	
	/**推送的消息内容*/
	private JsonResultDO message;
	
	/**推送的消息类型:1,仅web端; 2,仅移动端;3,均推送*/
	private int msgType = 1;
	
	/**消息级别*/
	private String msgLevel;
	
	/**消息业务标记，为客户端处理消息提供依据*/
	private String msgFlag ;
	
	public PushMessage(){
		this.msgId = RandomStringUtils.random(30, true, true);
	}
	
	public PushMessage(JsonResultDO msg){
		this.msgId = RandomStringUtils.random(30, true, true);
		this.message = msg;
	}
	
	public PushMessage(String userid, JsonResultDO msg){
		this.msgId = RandomStringUtils.random(30, true, true);
		setUserId(userid);
		this.message = msg;
	}
	
	public PushMessage(String userid, String msg){
		this.msgId = RandomStringUtils.random(30, true, true);
		setUserId(userid);
		this.message = new JsonResultDO(msg);
	}
	
	public PushMessage(String msg){
		this.msgId = RandomStringUtils.random(30, true, true);
		this.message = new JsonResultDO(msg);
	}
	
	public PushMessage(String userid, String msg, String msglevel, String msgflag){
		this(userid, msg, msglevel);
		this.msgFlag = msgflag;
		getMessage().addAttribute("msgflag", msgflag);
	}
	
	public PushMessage(String msg, String msglevel, String msgflag){
		this(msg, msglevel);
		this.msgFlag = msgflag;
		getMessage().addAttribute("msgflag", msgflag);
	}
	
	public void addAttribute(String key, Object value){
		getMessage().addAttribute(key, value);
	}

	public String getUserId() {
		if(userIds.isEmpty()){
			return null;
		}
		return userIds.get(0);
	}

	public void setUserId(String userId) {
		this.userIds = new ArrayList<String>();
		this.userIds.add(userId);
	}	
	
	public void addUserId(String userId) {
		this.userIds.add(userId);
	}
	
	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public JsonResultDO getMessage() {
		return message;
	}

	public void setMessage(JsonResultDO message) {
		this.message = message;
	}

	public void setMessage(String message) {
		this.message = new JsonResultDO(message);
	}

	public int getMsgType() {
		return msgType;
	}
	
	public void setMsgType(int msgType) {		
		this.msgType = msgType;
	}
	
	public String getMsgLevel() {
		return msgLevel;
	}

	public void setMsgLevel(String msgLevel) {
		this.msgLevel = msgLevel;
		getMessage().addAttribute("msglevel", msgLevel);
	}

	public String getMsgFlag() {
		return msgFlag;
	}

	public void setMsgFlag(String msgFlag) {
		this.msgFlag = msgFlag;
		getMessage().addAttribute("msgflag", msgFlag);
	}
	
	public String getMsgId() {
		return msgId;
	}
}
