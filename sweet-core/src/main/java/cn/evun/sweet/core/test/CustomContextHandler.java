package cn.evun.sweet.core.test;

import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * 自定义测试环境的上下文内容。
 *
 * @author yangw
 * @since 1.0.0
 */
public interface CustomContextHandler {

	/**
	 * 根据账户获取用户记录的语句
	 */
	String getUserByAccountSql();
	
	/**
	 * 根据查询的结果填充用户上下文
	 */
	void loadUserResources(Map<String, Object> userMap, HttpSession session);
	
	/**
	 * 获取测试用户的账户
	 */
	String getUserAccount();
}
