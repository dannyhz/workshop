package cn.evun.sweet.core.datafiltering;

import cn.evun.sweet.common.util.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 判断http请求中的终端类型
 *
 * @author shentao
 * @date 2017/5/4 11:26
 * @since 1.0.0
 */
public class TerminalUtil {

    /* \b 是单词边界(连着的两个(字母字符 与 非字母字符) 之间的逻辑上的间隔),
     字符串在编译时会被转码一次,所以是 "\\b"
     \B 是单词内部逻辑间隔(连着的两个字母字符之间的逻辑上的间隔) */
    private static final String PHONE_REG = "\\b(ip(hone|od)|android|opera m(ob|in)i"
            + "|windows (phone|ce)|blackberry"
            + "|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp"
            + "|laystation portable)|nokia|fennec|htc[-_]"
            + "|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";
    private static final String TABLE_REG = "\\b(ipad|tablet|(Nexus 7)|up.browser"
            + "|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";

    /* 移动设备正则匹配：手机端、平板 */
    private static final Pattern PHONE_PAT = Pattern.compile(PHONE_REG, Pattern.CASE_INSENSITIVE);
    private static final Pattern TABLE_PAT = Pattern.compile(TABLE_REG, Pattern.CASE_INSENSITIVE);

    private static final String SESSION_UA_ATTR = "user_agent_device";

    /**
     * 判断是否是移动端
     *
     * @param request
     */
    public static boolean isMobileDevice(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            HttpSession session = servletRequest.getSession();
            //检查session中是否已经记录访问方式（移动端或pc端）
            if (null == session.getAttribute(SESSION_UA_ATTR)) {
                String userAgent = servletRequest.getHeader("USER-AGENT").toLowerCase();//获取ua_device，用来判断是否为移动端访问
                boolean isMobileDevice = isMobileDevice(userAgent);
                if (isMobileDevice) {
                    session.setAttribute(SESSION_UA_ATTR, "mobile");
                } else {
                    session.setAttribute(SESSION_UA_ATTR, "pc");
                }
                return isMobileDevice;
            } else {
                return session.getAttribute(SESSION_UA_ATTR).equals("mobile");
            }
        }
        return false;
    }

    /**
     * 判断是否是移动端
     *
     * @param userAgent
     */
    public static boolean isMobileDevice(String userAgent) {
        if (StringUtils.isEmpty(userAgent)) {
            return false;
        }
        /* 匹配 */
        Matcher matcherPhone = PHONE_PAT.matcher(userAgent);
        Matcher matcherTable = TABLE_PAT.matcher(userAgent);
        if (matcherPhone.find() || matcherTable.find()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取终端类型
     *
     * @param request
     */
    public static Terminal checkTerminal(ServerHttpRequest request) {
        return isMobileDevice(request) ? Terminal.MOBILE : Terminal.PC;
    }

}
