package cn.evun.sweet.core.bussinesshelper;

import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cn.evun.sweet.core.cas.ModelAliasRegistry;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.mybatis.GeneralMapper;
import cn.evun.sweet.core.mybatis.general.Example;
import cn.evun.sweet.core.mybatis.page.PageHelper;

/**
 * 基于MyBatis的ORM功能服务，可以对任意的javabean（带注解）进行CRUD操作，包含简单的二级关联操作。
 * 同时也提供了分页、批量以及自动管理主键等常用的服务。
 * 
 * @author yangw
 * @since V1.0.0
 */
@Service
public class GeneralObjectAccessService {

	@Resource
	private GeneralMapper mapper;
	
	@Resource
	private ModelAliasRegistry modelAlias;
	
	//@Autowired(required=false)
	//@Qualifier("identityManager")
	//private DataFieldMaxValueIncrementer springIncrement;
	
	private Integer pageSize = R.bussiness.default_pagesize;
	
	protected static final Logger LOGGER = LogManager.getLogger();	
	
	/**
     * 根据参数进行查询,record可以是Class类型或JavaBean(生成where条件)<br>
     * 查询条件为非空的String属性，或非null的其他属性，第一个page参数为pageNum，第二个为pageSize。均可省略。
     * 注意：该方法不启用关联查询功能,如果是分页查询，返回结果需强制转换为Page<T>
     */
	public <T> List<T> query(T record, Integer... page) {
    	if(page != null && page.length > 0){
    		PageHelper.startPage(page[0], page.length>1&&page[1]!=null?page[1]:pageSize);
    	}
    	return mapper.select(record);
    }
	
	/**
     * 与query方法不同的是将会开启外键关联查询
     */
	public <T> List<T> queryWithRelation(T record, Integer... page) {
    	if(page != null && page.length > 0){
    		PageHelper.startPage(page[0], page.length>1&&page[1]!=null?page[1]:pageSize);
    	}
    	return mapper.selectWithRelation(record);
    }
	
	/**
     * 根据主键查询结果，主键不能为null或空.不启用关联查询.<br/>
     * 注意：参数必须是注册过的模型名称
     */
	public <T> T queryById(String entityClassName, Object key) {
		Class<T> entityClass = modelAlias.resolveAlias(entityClassName);
		Assert.notNull(entityClass,"unregistry entity classname.");
		return mapper.selectByPrimaryKey(entityClass, key);
    }
	
	/**
     * 根据主键查询结果，主键不能为null或空.不启用关联查询
     */
	public <T> T queryById(Class<T> entityClass, Object key) {
		return mapper.selectByPrimaryKey(entityClass, key);
    }
	
	/**
     * 根据主键查询结果，主键不能为null或空.启用关联查询
     */
	public <T> T queryByIdWithRelation(String entityClassName, Object key) {
		Class<T> entityClass = modelAlias.resolveAlias(entityClassName);
		Assert.notNull(entityClass,"unregistry entity classname.");
		return mapper.selectByPrimaryKeyWithRelation(entityClass, key);
    }
	
	/**
     * 根据主键查询结果，主键不能为null或空.启用关联查询
     */
	public <T> T queryByIdWithRelation(Class<T> entityClass, Object key) {
		return mapper.selectByPrimaryKeyWithRelation(entityClass, key);
    }
	
	/**
     * 通过Example来查询，不支持关联查询。
     * 第一个page参数为pageNum，第二个为pageSize。均可省略。
     */
    public <T> List<T> selectByExample(Example example, Integer... page) {
    	if(page != null && page.length > 0){
    		PageHelper.startPage(page[0], page.length>1&&page[1]!=null?page[1]:pageSize);
    	}
    	return mapper.selectByExample(example);
    }
    
    /**
     * 根据参数进行查询总数统计,record可以是Class<?>类型或实体对象<br>
     * 查询条件为非空的String属性，或非null的其他属性，不启用关联查询
     */
    public <T> int count(T record) {
        return mapper.count(record);
    }
    
    /**
     * 根据参数进行查询总数统计,record可以是Class<?>类型或实体对象<br>
     * 查询条件为非空的String属性，或非null的其他属性，启用关联查询
     */
    public <T> int countWithRelation(T record) {
        return mapper.countWithRelation(record);
    }
    
    /**
     * 通过Example类来查询count。不支持关联查询
     */
    public <T> int countByExample(Example example) {
    	return mapper.countByExample(example);
    }
    
}