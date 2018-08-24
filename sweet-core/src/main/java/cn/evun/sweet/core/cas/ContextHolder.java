package cn.evun.sweet.core.cas;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.NamedThreadLocal;

/**
 * 用于绑定请求的DistributedSession。使得在当前请求处理过程中能够获取DistributedSession资源<br/>
 *
 * @author yangw
 * @since V1.0.0
 */
public abstract class ContextHolder {

	private static final ThreadLocal<DistributedSession> sessionHolder = new NamedThreadLocal<DistributedSession>("DistributedSession Inherit Holder");
	
	private static final ThreadLocal<HttpServletRequest> requestHolder = new NamedThreadLocal<HttpServletRequest>("HttpServletRequest Inherit Holder");

	private static final ThreadLocal<Token> tokenHolder = new NamedThreadLocal<Token>("LoginToken Inherit Holder");

	
	public static Token currentToken() {
		return tokenHolder.get();
	}
	
	public static DistributedSession currentSession() {
		return sessionHolder.get();
	}
	
	public static HttpServletRequest currentRequest() {
		return requestHolder.get();
	}
	
	public static void cleanToken() {
		tokenHolder.remove();
	}
	
	public static void cleanSession() {
		sessionHolder.remove();
	}
	
	public static void cleanRequest() {
		requestHolder.remove();
	}
	
	public static void setToken(Token token) {
		if (token == null) {
			cleanToken();
		}else {
			tokenHolder.set(token);
		}
	}

	public static void setSession(DistributedSession stack) {
		if (stack == null) {
			cleanSession();
		}else {
			sessionHolder.set(stack);
		}
	}
	
	public static void setRequest(HttpServletRequest stack) {
		if (stack == null) {
			cleanRequest();
		}else {
			requestHolder.set(stack);
		}
	}


	public static DistributedSession getSession() throws IllegalStateException {
		DistributedSession stack = currentSession();
		if (stack == null) {
			throw new IllegalStateException("No thread-bound DistributedSession found: " +
					"Are you referring to DistributedSession outside of an actual web request, " +
					"or processing a request outside of the originally receiving thread? " +
					"If you are actually operating within a web request and still receive this message, " +
					"your code is probably running outside of DispatcherServlet and DistributedSessionChainToInterceptor.");
		}
		return stack;
	}
	
	public static HttpServletRequest getRequest() throws IllegalStateException {
		HttpServletRequest stack = currentRequest();
		if (stack == null) {
			throw new IllegalStateException("No thread-bound DistributedSession found: " +
					"Are you referring to DistributedSession outside of an actual web request, " +
					"or processing a request outside of the originally receiving thread? " +
					"If you are actually operating within a web request and still receive this message, " +
					"your code is probably running outside of DispatcherServlet and DistributedSessionChainToInterceptor.");
		}
		return stack;
	}
}
