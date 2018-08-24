package cn.evun.sweet.core.common;


import cn.evun.sweet.common.serialize.json.JsonUtils;
import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.spring.SpringContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**   
 * 主要用于使用json格式数据的交互中，对结果信息进行统一封装。
 * 
 * @author yangw   
 * @since V1.0.0   
 */
public final class JsonResultDO implements Serializable {
	
	private static final long serialVersionUID = -5208817809050895592L;
	
	public static final String RETURN_OBJECT_KEY = "result";
	public static final String RETURN_PAGEBAR_KEY = "pagebar";
	
	/**用于没有任何业务数据的返回，前端需要DATAS为NULL的情况*/
	public static final JsonResultDO EMPTY_RESULT = new JsonResultDO().setDatasNull();
	
	/**处理过程是否成功完成*/
	private boolean success = true;
	
	/**描述处理结果的消息*/
	private String message;
	
	/**消息编号（国际化key），用于获取国际化的处理结果的消息*/
	private String msgCode = R.message.msgcode_default;
	
	/**国际化信息中包含的参数列表*/
	private Object[] arguments;
	
	/**处理结果的状态码*/
	private String statusCode;

	/**处理结果的数据*/
	private Map<String, Object> datas = new HashMap<String, Object>();

	public JsonResultDO() {
	}
	
	public JsonResultDO(boolean success) {
		this.success = success;
	}

	public JsonResultDO(String msgcode) {
		this.msgCode = msgcode;
	}

	public JsonResultDO(boolean success, String msgcode) {
		this.success = success;
		this.msgCode = msgcode;
	}
	
	public JsonResultDO(boolean success, String msgcode, String statuscode){
		this.success = success;
		this.msgCode = msgcode;
		this.statusCode = statuscode;
	}

	public JsonResultDO(boolean success, String msgCode, Object[] arguments) {
		this.success = success;
		this.msgCode = msgCode;
		this.arguments = arguments;
	}

	public String toJson() {
		return JsonUtils.beanToJson(this);
	}
	
	public String getI18nMessage(){
		Locale locale = Locale.CHINA; //LocaleContextHolder.getLocale();
		String i18nMessage = SpringContext.getApplicationContext().getMessage(msgCode, arguments,this.message, locale==null?Locale.CHINA:locale);
		return "".equals(i18nMessage)? this.message : i18nMessage;
	}

	/**
	 * 返回系统当前时间戳
	 */
	public long getTimeMillis() {
		return System.currentTimeMillis();
	}
	
	public void addAttribute(String key, Object value){
		if (!StringUtils.isEmpty(key) ){
			this.datas.put(key, value);
		}
	}
	
	public Map<String, Object> getDatas(){
		return this.datas;
	}
	
	public void cleanAttribute(String key){
		if (!StringUtils.isEmpty(key) ){
			this.datas.remove(key);
		}
	}
	
	public void cleanAllAttributes(){
		this.datas.clear();
	}

	public void setErrorDefault(){
		this.success = false;
		this.msgCode = R.message.msgcode_error;
		this.message = getI18nMessage();
	}
	
	public void setError(String msgCode, Object... arguments){
		this.success = false;
		this.msgCode = msgCode;
		this.arguments = arguments;
		this.message = getI18nMessage();
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setMsgCode(String msgCode, Object... arguments) {
		this.msgCode = msgCode;
		this.arguments = arguments;
		this.message = getI18nMessage();
	}
	
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
		this.message = getI18nMessage();
	}
	
	public String getMsgCode() {
		return msgCode;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	private JsonResultDO setDatasNull(){
		this.datas = null;
		return this;
	}
		
}