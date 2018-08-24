package cn.evun.sweet.core.push;

import cn.evun.sweet.core.spring.JsonResultDeferredResult;

/**
 * 用于消息推送的DeferredResult扩展
 *
 * @author yangw
 * @since 1.0.0
 */
public class MsgPushDeferredResult extends JsonResultDeferredResult {
	
	private Long  userId;	
	private Long  tenantId;	

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	/**
	@Override  
    public boolean equals(Object obj){  
		if (this == obj) return true;  
        if (obj == null) return false;  
        if (getClass() != obj.getClass()) return false;  
        MsgPushDeferredResult other = (MsgPushDeferredResult) obj;  
        if(userId == null || other.getUserId() == null) return false;
        if (userId.longValue() == other.getUserId().longValue()){
        	return true;
        }else {
        	return false;
        }  
    } 
    */ 
}
