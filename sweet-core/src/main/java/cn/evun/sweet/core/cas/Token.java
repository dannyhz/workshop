package cn.evun.sweet.core.cas;

import java.io.Serializable;

import org.apache.commons.lang3.RandomStringUtils;

import cn.evun.sweet.common.serialize.json.JsonUtils;

/**
 * 客户端cookie信息的封装类。可以继承并提供更多的业务数据存储到客户端。
 * 
 * @author yangw
 * @since V1.0.0
 */
public class Token implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String userIp;
	
	private String userId;
	
	private String clientId;//手机唯一识别号或推送ID
	
	public Token(){
		this.id = RandomStringUtils.random(30, true, true);
	}
	
	public String toJson() {
		return JsonUtils.beanToJson(this);
	}

	public static Token json2Token(String jsonToken, Class<? extends Token> cls) {
		return JsonUtils.jsonToBean(jsonToken, cls, false);
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}

}
