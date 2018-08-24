package cn.evun.sweet.common.serialize.json;

import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;

/**
 * 允许指定需要排除的属性来定制JSON的序列化<br/>
 * 该filter携带了所有需要排除的属性。
 * 
 * @author yangw
 * @since V1.0.0
 */
public class ExcludesPropertyPreFilter implements PropertyPreFilter {

	private final Class<?>    clazz;
    private final Set<String> excludes = new HashSet<String>();
    
    public ExcludesPropertyPreFilter(String... properties){
        this(null, properties);
    }
    
	public ExcludesPropertyPreFilter(Class<?> clazz, String... properties) {
		super();
        this.clazz = clazz;
        for (String item : properties) {
            if (item != null) {
                this.excludes.add(item);
            }
        }
	}

    public Class<?> getClazz() {
        return clazz;
    }

    public Set<String> getExcludes() {
        return excludes;
    }

	/* (non-Javadoc)
	 * @see com.alibaba.fastjson.serializer.PropertyPreFilter#apply(com.alibaba.fastjson.serializer.JSONSerializer, java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean apply(JSONSerializer serializer, Object source, String name) {
		if (source == null) {
            return true;
        }

        if (clazz != null && !clazz.isInstance(source)) {
            return true;
        }
        
        if (excludes.size() > 0 && excludes.contains(name)) {
            return false;
        }

        return true;
	}

}
