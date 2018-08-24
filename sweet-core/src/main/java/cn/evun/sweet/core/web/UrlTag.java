package cn.evun.sweet.core.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;
import org.springframework.web.servlet.tags.Param;
import org.springframework.web.servlet.tags.ParamAware;
import org.springframework.web.util.JavaScriptUtils;
import org.springframework.web.util.TagUtils;
import org.springframework.web.util.UriUtils;

/**
 * 提供与静态处理相配合的静态资源处理方案。扩展与Spring Url Tag,使用方式相同。
 *
 * <p>例如:
 * <pre class="code">&lt;spring:url value="/url/path/{variableName}"&gt;
 *   &lt;spring:param name="variableName" value="more than JSTL c:url" /&gt;
 * &lt;/spring:url&gt;</pre>
 * 解析后返回:
 * {@code /currentApplicationContext/1.0.0/url/path/more%20than%20JSTL%20c%3Aurl}
 *
 * @author yangw
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class UrlTag extends HtmlEscapingAwareTag implements ParamAware {

	private static final String URL_TEMPLATE_DELIMITER_PREFIX = "{";

	private static final String URL_TEMPLATE_DELIMITER_SUFFIX = "}";

	private static final String URL_TYPE_ABSOLUTE = "://";


	private List<Param> params;

	private Set<String> templateParams;

	private UrlType type;

	private String value;

	private String context;

	private String var;

	private int scope = PageContext.PAGE_SCOPE;

	private boolean javaScriptEscape = false;


	/**
	 * Sets the value of the URL
	 */
	public void setValue(String value) {
		if (value.contains(URL_TYPE_ABSOLUTE)) {
			this.type = UrlType.ABSOLUTE;
			this.value = value;
		}
		else if (value.startsWith("/")) {
			this.type = UrlType.CONTEXT_RELATIVE;
			this.value = value;
		}
		else {
			this.type = UrlType.RELATIVE;
			this.value = value;
		}
	}

	/**
	 * Set the context path for the URL. Defaults to the current context
	 */
	public void setContext(String context) {
		if (context.startsWith("/")) {
			this.context = context;
		}
		else {
			this.context = "/" + context;
		}
	}

	/**
	 * Set the variable name to expose the URL under. Defaults to rendering the
	 * URL to the current JspWriter
	 */
	public void setVar(String var) {
		this.var = var;
	}

	/**
	 * Set the scope to export the URL variable to. This attribute has no
	 * meaning unless var is also defined.
	 */
	public void setScope(String scope) {
		this.scope = TagUtils.getScope(scope);
	}

	/**
	 * Set JavaScript escaping for this tag, as boolean value.
	 * Default is "false".
	 */
	public void setJavaScriptEscape(boolean javaScriptEscape) throws JspException {
		this.javaScriptEscape = javaScriptEscape;
	}

	@Override
	public void addParam(Param param) {
		this.params.add(param);
	}


	@Override
	public int doStartTagInternal() throws JspException {
		this.params = new LinkedList<Param>();
		this.templateParams = new HashSet<String>();
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();		
		ResourceUrlProvider resourceUrlProvider = (ResourceUrlProvider)request.getAttribute(
				ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR);
		String url = createUrl();
		if(StringUtils.hasText(url) && url.contains("/resources/")){
			url = resourceUrlProvider.getForRequestUrl(request, url);
		}
				
		RequestDataValueProcessor processor = getRequestContext().getRequestDataValueProcessor();
		if ((processor != null) && (request instanceof HttpServletRequest)) {
			url = processor.processUrl((HttpServletRequest) request, url);
		}

		if (this.var == null) {
			try {
				pageContext.getOut().print(url);
			}
			catch (IOException e) {
				throw new JspException(e);
			}
		}
		else {
			// store the url as a variable
			pageContext.setAttribute(var, url, scope);
		}
		return EVAL_PAGE;
	}


	/**
	 * Build the URL for the tag from the tag attributes and parameters.
	 * @return the URL value as a String
	 * @throws JspException
	 */
	private String createUrl() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		//HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		StringBuilder url = new StringBuilder();
		if (this.type == UrlType.CONTEXT_RELATIVE) {
			// add application context to url
			if (this.context == null) {
				url.append(request.getContextPath());
			}
			else {
				url.append(this.context);
			}
		}
		if (this.type != UrlType.RELATIVE && this.type != UrlType.ABSOLUTE && !this.value.startsWith("/")) {
			url.append("/");
		}
		url.append(replaceUriTemplateParams(this.value, this.params, this.templateParams));
		url.append(createQueryString(this.params, this.templateParams, (url.indexOf("?") == -1)));

		String urlStr = url.toString();
		//if (this.type != UrlType.ABSOLUTE) {
			// Add the session identifier if needed
			// (Do not embed the session identifier in a remote link!)
			//urlStr = response.encodeURL(urlStr);
		//}

		// HTML and/or JavaScript escape, if demanded.
		urlStr = htmlEscape(urlStr);
		urlStr = this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape(urlStr) : urlStr;

		return urlStr;
	}

	/**
	 * Build the query string from available parameters that have not already
	 * been applied as template params.
	 * <p>The names and values of parameters are URL encoded.
	 * @param params the parameters to build the query string from
	 * @param usedParams set of parameter names that have been applied as
	 * template params
	 * @param includeQueryStringDelimiter true if the query string should start
	 * with a '?' instead of '&'
	 * @return the query string
	 */
	protected String createQueryString(List<Param> params, Set<String> usedParams, boolean includeQueryStringDelimiter)
			throws JspException {

		String encoding = pageContext.getResponse().getCharacterEncoding();
		StringBuilder qs = new StringBuilder();
		for (Param param : params) {
			if (!usedParams.contains(param.getName()) && StringUtils.hasLength(param.getName())) {
				if (includeQueryStringDelimiter && qs.length() == 0) {
					qs.append("?");
				}
				else {
					qs.append("&");
				}
				try {
					qs.append(UriUtils.encodeQueryParam(param.getName(), encoding));
					if (param.getValue() != null) {
						qs.append("=");
						qs.append(UriUtils.encodeQueryParam(param.getValue(), encoding));
					}
				}
				catch (UnsupportedEncodingException ex) {
					throw new JspException(ex);
				}
			}
		}
		return qs.toString();
	}

	/**
	 * Replace template markers in the URL matching available parameters. The
	 * name of matched parameters are added to the used parameters set.
	 * <p>Parameter values are URL encoded.
	 * @param uri the URL with template parameters to replace
	 * @param params parameters used to replace template markers
	 * @param usedParams set of template parameter names that have been replaced
	 * @return the URL with template parameters replaced
	 */
	protected String replaceUriTemplateParams(String uri, List<Param> params, Set<String> usedParams)
			throws JspException {

		String encoding = pageContext.getResponse().getCharacterEncoding();
		for (Param param : params) {
			String template = URL_TEMPLATE_DELIMITER_PREFIX + param.getName() + URL_TEMPLATE_DELIMITER_SUFFIX;
			if (uri.contains(template)) {
				usedParams.add(param.getName());
				try {
					uri = uri.replace(template, UriUtils.encodePath(param.getValue(), encoding));
				}
				catch (UnsupportedEncodingException ex) {
					throw new JspException(ex);
				}
			}
			else {
				template = URL_TEMPLATE_DELIMITER_PREFIX + "/" + param.getName() + URL_TEMPLATE_DELIMITER_SUFFIX;
				if (uri.contains(template)) {
					usedParams.add(param.getName());
					try {
						uri = uri.replace(template, UriUtils.encodePathSegment(param.getValue(), encoding));
					}
					catch (UnsupportedEncodingException ex) {
						throw new JspException(ex);
					}
				}
			}
		}
		return uri;
	}

	/**
	 * Internal enum that classifies URLs by type.
	 */
	private enum UrlType {
		CONTEXT_RELATIVE, RELATIVE, ABSOLUTE
	}


}
