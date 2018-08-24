package cn.evun.sweet.auth.util;

import cn.evun.sweet.auth.model.OrgDo;
import cn.evun.sweet.auth.model.TenantDo;
import cn.evun.sweet.auth.model.UserDo;
import cn.evun.sweet.core.cas.ContextHolder;
import cn.evun.sweet.core.common.R;

/**
 * 获取当前登录用户存放在会话中的相关信息，包括身份及资源等信息。
 *
 * @author yangw
 * @since V1.0.0
 */
public class UserContext {

	public static UserDo getUser() {
		return (UserDo)ContextHolder.getSession().getAttribute(R.session.user_info);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getUser(Class<T> clazz) {
		return (T)ContextHolder.getSession().getAttribute(R.session.user_info);
	}

	public static Long getUserId() {
		return getUser().getUserId();
	}
	
	public static String getUserCode() {
		return getUser().getUserCode();
	}
	
	public static TenantDo getTenant() {
		return (TenantDo)ContextHolder.getSession().getAttribute(R.session.tenant_info);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getTenant(Class<T> clazz) {
		return (T)ContextHolder.getSession().getAttribute(R.session.tenant_info);
	}
	
	public static Long getTenantId() {
		return getTenant().getTenantId();
	}
	
	public static OrgDo getOrg() {
		return (OrgDo)ContextHolder.getSession().getAttribute(R.session.org_info);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getOrg(Class<T> clazz) {
		return (T)ContextHolder.getSession().getAttribute(R.session.org_info);
	}
	
	public static MenuNode getUserMenu() {
		return (MenuNode)ContextHolder.getSession().getAttribute(R.session.menu_info);
	}
	
}

