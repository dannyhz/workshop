package cn.evun.sweet.core.spring;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.ResourceTransformerSupport;
import org.springframework.web.servlet.resource.TransformedResource;

/**
 * 样式中静态资源管理的扩展方案，支持忽略样式文件列表以及不需要在样式的Url中指定ContextPath开头，增加通用性
 *
 * @author yangw
 * @since 1.0.0
 */
public class NoCtxPathCssLinkResourceTransformer extends ResourceTransformerSupport {

	protected static final Logger LOGGER = LogManager.getLogger();
	
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");


	private final List<CssLinkParser> linkParsers = new ArrayList<CssLinkParser>();
	
	private String[] ignoreCssFiles ; 


	public NoCtxPathCssLinkResourceTransformer() {
		this.linkParsers.add(new ImportStatementCssLinkParser());
		this.linkParsers.add(new UrlFunctionCssLinkParser());
	}


	@Override
	public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) throws IOException {

		resource = transformerChain.transform(request, resource);

		String filename = resource.getFilename();
		if (!"css".equals(StringUtils.getFilenameExtension(filename))) {
			return resource;
		}
		
		/*忽略指定的css文件，不进行转义*/
		if(this.ignoreCssFiles != null){
			for(int i=0; i<this.ignoreCssFiles.length; i++){
				if(filename.equals(this.ignoreCssFiles[i])){
					return resource;
				}
			}
		}		

		byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
		String content = new String(bytes, DEFAULT_CHARSET);

		Set<CssLinkInfo> infos = new HashSet<CssLinkInfo>(5);
		for (CssLinkParser parser : this.linkParsers) {
			parser.parseLink(content, infos);
		}

		List<CssLinkInfo> sortedInfos = new ArrayList<CssLinkInfo>(infos);
		Collections.sort(sortedInfos);

		int index = 0;
		StringWriter writer = new StringWriter();
		for (CssLinkInfo info : sortedInfos) {
			writer.write(content.substring(index, info.getStart()));
			String link = request.getContextPath()+content.substring(info.getStart(), info.getEnd());
			String newLink = null;
			if (!hasScheme(link)) {
				newLink = resolveUrlPath(link, request, resource, transformerChain);
			}
			writer.write(newLink != null ? newLink : link);
			index = info.getEnd();
		}
		writer.write(content.substring(index));

		return new TransformedResource(resource, writer.toString().getBytes(DEFAULT_CHARSET));
	}

	private boolean hasScheme(String link) {
		int schemeIndex = link.indexOf(":");
		return schemeIndex > 0 && !link.substring(0, schemeIndex).contains("/");
	}

	public String[] getIgnoreCssFiles() {
		return ignoreCssFiles;
	}

	public void setIgnoreCssFiles(String[] ignoreCssFiles) {
		this.ignoreCssFiles = ignoreCssFiles;
	}

	protected static interface CssLinkParser {
		void parseLink(String content, Set<CssLinkInfo> linkInfos);
	}

	protected static abstract class AbstractCssLinkParser implements CssLinkParser {

		protected abstract String getKeyword();

		@Override
		public void parseLink(String content, Set<CssLinkInfo> linkInfos) {
			int index = 0;
			do {
				index = content.indexOf(getKeyword(), index);
				if (index == -1) {
					break;
				}
				index = skipWhitespace(content, index + getKeyword().length());
				if (content.charAt(index) == '\'') {
					index = addLink(index, "'", content, linkInfos);
				}
				else if (content.charAt(index) == '"') {
					index = addLink(index, "\"", content, linkInfos);
				}
				else {
					index = extractLink(index, content, linkInfos);

				}
			}
			while (true);
		}

		private int skipWhitespace(String content, int index) {
			while (true) {
				if (Character.isWhitespace(content.charAt(index))) {
					index++;
					continue;
				}
				return index;
			}
		}

		protected int addLink(int index, String endKey, String content, Set<CssLinkInfo> linkInfos) {
			int start = index + 1;
			int end = content.indexOf(endKey, start);
			linkInfos.add(new CssLinkInfo(start, end));
			return end + endKey.length();
		}

		/**
		 * Invoked after a keyword match, after whitespaces removed, and when
		 * the next char is neither a single nor double quote.
		 */
		protected abstract int extractLink(int index, String content, Set<CssLinkInfo> linkInfos);

	}

	private static class ImportStatementCssLinkParser extends AbstractCssLinkParser {

		@Override
		protected String getKeyword() {
			return "@import";
		}

		@Override
		protected int extractLink(int index, String content, Set<CssLinkInfo> linkInfos) {
			if (content.substring(index, index + 4).equals("url(")) {
				// Ignore, UrlLinkParser will take care
			}
			else if (LOGGER.isErrorEnabled()) {
				LOGGER.error("Unexpected syntax for @import link at index " + index);
			}
			return index;
		}
	}

	private static class UrlFunctionCssLinkParser extends AbstractCssLinkParser {

		@Override
		protected String getKeyword() {
			return "url(";
		}

		@Override
		protected int extractLink(int index, String content, Set<CssLinkInfo> linkInfos) {
			// A url() function without unquoted
			return addLink(index - 1, ")", content, linkInfos);
		}
	}


	private static class CssLinkInfo implements Comparable<CssLinkInfo> {

		private final int start;

		private final int end;


		private CssLinkInfo(int start, int end) {
			this.start = start;
			this.end = end;
		}

		public int getStart() {
			return this.start;
		}

		public int getEnd() {
			return this.end;
		}

		@Override
		public int compareTo(CssLinkInfo other) {
			return (this.start < other.start ? -1 : (this.start == other.start ? 0 : 1));
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj != null && obj instanceof CssLinkInfo) {
				CssLinkInfo other = (CssLinkInfo) obj;
				return (this.start == other.start && this.end == other.end);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.start * 31 + this.end;
		}
	}
}
