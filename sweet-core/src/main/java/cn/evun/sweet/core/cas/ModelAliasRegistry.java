package cn.evun.sweet.core.cas;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.type.Alias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 所有模型层别名的注册。用于根据view层的参数path创建数据结构。
 * 
 * @author yangw
 * @since V1.0.0
 */
@Component("modelAlias")
public class ModelAliasRegistry implements InitializingBean {

	protected static final Logger LOGGER = LogManager.getLogger();

	private final Map<String, Class<?>> TYPE_ALIASES = new HashMap<String, Class<?>>();

	@Value("${aliases.modelpackages}")
	private String modelAliasesPackage;
	
	@SuppressWarnings("unchecked")
	public <T> Class<T> resolveAlias(String string) {
		try {
			if (string == null) {
				return null;
			}
			String key = string.toLowerCase(Locale.ENGLISH);
			Class<T> value;
			if (TYPE_ALIASES.containsKey(key)) {
				value = (Class<T>) TYPE_ALIASES.get(key);
			} else {
				value = (Class<T>) Resources.classForName(string);
			}
			return value;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String[] typeAliasPackageArray = tokenizeToStringArray(this.modelAliasesPackage,
				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
		for (String packageToScan : typeAliasPackageArray) {
			registerAliases(packageToScan, Object.class);
			LOGGER.debug("Scanned package: '" + packageToScan + "' for aliases");
		}
	}

	private void registerAliases(String packageName, Class<?> superType) {
		ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
		resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
		Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
		for (Class<?> type : typeSet) {
			if (!type.isAnonymousClass() && !type.isInterface() && !type.isMemberClass()) {
				String alias = type.getSimpleName();
				Alias aliasAnnotation = type.getAnnotation(Alias.class);
				if (aliasAnnotation != null) {
					alias = aliasAnnotation.value();
				}
				registerAlias(alias, type);
			}
		}
	}

	private void registerAlias(String alias, Class<?> value) {
	    if (alias == null) {
	      return;
	    }
	    String key = alias.toLowerCase(Locale.ENGLISH);
	    if (TYPE_ALIASES.containsKey(key) && TYPE_ALIASES.get(key) != null && !TYPE_ALIASES.get(key).equals(value)) {
	      LOGGER.error("The alias '" + alias + "' is already mapped to the value '" + TYPE_ALIASES.get(key).getName() + "'.");
	      return;
	    }
	    TYPE_ALIASES.put(key, value);
	  }

}
