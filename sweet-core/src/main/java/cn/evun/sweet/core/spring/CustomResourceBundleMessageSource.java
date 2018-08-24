package cn.evun.sweet.core.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;

import cn.evun.sweet.common.util.Assert;

/**   
 * 国际化资源文件获取方式的扩展。<br/>
 * 允许通过basename=“module1,module2”模块名字符串的方式定义国际化文件的位置。
 * 其资源位置必须符合i18n/modul的路径规则。
 * 
 * @author yangw   
 * @since V1.0.0   
 */
public class CustomResourceBundleMessageSource extends ResourceBundleMessageSource {

	protected static final Logger LOGGER = LogManager.getLogger(CustomResourceBundleMessageSource.class.getName());
	
	private String defaultBaseName;
	
	public void setDefaultBaseName(String defaultBaseName) {
		this.defaultBaseName = defaultBaseName;
	}
	
	@Override
	public void setBasename(String basename) {		
		Assert.hasText(basename, "Basename must not be empty");
		Assert.hasText(defaultBaseName, "DefaultBaseName must not be empty");
		
		basename = defaultBaseName+","+basename;
		LOGGER.info("i18n message properties path '{}' is loaded", basename);		
		
		if(basename.contains(",")){
			String[] basesnameArray = basename.split(",");
			String[] realnameArray = new String[basesnameArray.length];
			for (int i=0; i<basesnameArray.length; i++) {
				realnameArray[i] = "i18n/"+basesnameArray[i].trim();				 
			}
			setBasenames(realnameArray);
		}else {
			setBasenames(basename);
		}
	}
	
}