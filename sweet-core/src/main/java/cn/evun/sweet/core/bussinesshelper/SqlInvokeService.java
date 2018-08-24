package cn.evun.sweet.core.bussinesshelper;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import cn.evun.sweet.common.datastructure.MultiValueMap;
import cn.evun.sweet.common.datastructure.MultiValueMapAdapter;
import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.cas.ContextHolder;
import cn.evun.sweet.core.cas.DistributedSession;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.exception.SweetException;
import cn.evun.sweet.core.mybatis.page.PageHelper;

/**
 * 通过ID直接执行Sql语句并得到返回结果。 注意：该服务的执行必须在Context机制运作的环境下
 * 
 * @author yangw
 * @since V1.0.0
 */
@Service
public class SqlInvokeService implements InitializingBean, ApplicationContextAware{

	protected static final Logger LOGGER = LogManager.getLogger();
	
	private MultiValueMap<String, String> sqlMap ;
	
	private ApplicationContext applicationContext;
	
	@javax.annotation.Resource(name="sqlSession")
	private SqlSession sqlSession;

	/**
	 * 判断给定的服务号是否是已经注册过的SQL语句对应的服务号
	 */
	public boolean isSupport(String sqlServiceId) {
		return sqlMap.get(sqlServiceId)!=null;
	}

	/**
	 * 执行sql语句并返回结果.<br/>
	 * 如果是查询语句需要分页，则第一个page参数为pageNum，第二个为pageSize,并且返回结果需强制转换为Page<T>
	 */
	public Object excuteSql(String sqlServiceId, Integer... page) {
		if(!isSupport(sqlServiceId)){
			throw new SweetException("sql not found: {}", sqlServiceId);
		}
		
		String sqlType = sqlMap.getFirst(sqlServiceId);
		String sqlId = sqlMap.get(sqlServiceId).get(1);

		ModelMap model = new ModelMap();
		model.addAllAttributes(ContextHolder.getRequest().getParameterMap());
		DistributedSession stack = ContextHolder.currentSession();
		if(stack != null){
			model.addAttribute(R.session.user_info, stack.getAttribute(R.session.user_info));
			model.addAttribute(R.session.tenant_info, stack.getAttribute(R.session.tenant_info));
			model.addAttribute(R.session.org_info, stack.getAttribute(R.session.org_info));
			//model.addAttribute("session", stack);
		}
		
		Object result = null;		
		if("select".equalsIgnoreCase(sqlType)){
			if(page != null && page.length > 0){
				Integer pageNum = page[0]==null?1:page[0];
				Integer pageSize = page.length>1&&page[1]!=null?page[1]:R.bussiness.default_pagesize;
				PageHelper.startPage(pageNum, pageSize);
			}
			result = sqlSession.selectList(sqlId, model);//分页需强制转换为Page<T>
		}else if("update".equalsIgnoreCase(sqlType)){
			/*此处仅传递了request中携带的数据，以及基本的session的数据，因此sql语句无法使用session的业务数据做条件*/
			result =  Integer.valueOf(sqlSession.update(sqlId, model));
		}else if("insert".equalsIgnoreCase(sqlType)){
			result =  Integer.valueOf(sqlSession.insert(sqlId, model));
		}else if("delete".equalsIgnoreCase(sqlType)){
			result =  Integer.valueOf(sqlSession.delete(sqlId, model));
		}
		return result;
	}

	/* 
	 * 初始化并加载所有注册的Sql语句
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			Resource[] resources = this.applicationContext.getResources("classpath*:conf/*-sql.properties");
			sqlMap = new MultiValueMapAdapter<String, String>(new HashMap<String, List<String>>());
			for(Resource resource : resources){
				Properties props = PropertiesLoaderUtils.loadProperties(resource);				
				Enumeration<?> e = props.propertyNames();
				while (e.hasMoreElements()) {
					String sqlKey = (String) e.nextElement();
					String[] sqlValue = StringUtils.delimitedListToStringArray(props.getProperty(sqlKey), "@");
					if(sqlValue.length != 2){
						LOGGER.error("Detected nonstandard sql maper: [{}={}]",sqlKey,props.getProperty(sqlKey));
						continue;
					}
					if(!StringUtils.hasText(sqlValue[1]) || "select,update,insert,delete".indexOf(sqlValue[0])<0){
						LOGGER.error("Sql type must one of them ['select update insert delete'] in sql maper: [{}={}]",
								sqlKey, props.getProperty(sqlKey));
						continue;
					}
					sqlMap.add(sqlKey, sqlValue[0]);
					sqlMap.add(sqlKey, sqlValue[1]);				
				}
			}
			
			/*每个分布式应用独立维护自己的sqlmap，此处无需缓存*/
			//cacheAccessor.put(R.cache.cache_region_main,R.cache.cache_sqlmap_key, sqlMap);
		} catch (Exception e) {
			throw new SweetException("Failed to regist all of the sql maper, becauser of exception [{}]", e.getMessage());
		}
	}
	

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;		
	}
}
