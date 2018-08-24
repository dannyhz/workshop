package cn.evun.sweet.core.push.app;

import java.io.Serializable;
import java.util.Map;

public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/*消息类型(决定APP点击后的不同动作处理)*/
	private String type;

	/*消息标题*/
	private String title;
	
	/*消息内容*/
	private String body;
	
	/*消息附带属性，用于跳转页面时携带的参数*/
	private Map<String, Object> props;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, Object> getProps() {
		return props;
	}

	public void setProps(Map<String, Object> props) {
		this.props = props;
	}
	
}
