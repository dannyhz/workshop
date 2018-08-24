package cn.evun.sweet.core.cas;


/**
 * 客户端登录cookie信息的封装类。
 * 
 * @author yangw
 * @since V1.0.0
 */
public class LoginToken extends Token{

	private static final long serialVersionUID = 1L;
	
	private String sessionid;

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}
	
}
