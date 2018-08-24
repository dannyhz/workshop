package cn.evun.sweet.core.validation;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.bussinesshelper.GeneralObjectAccessService;
import cn.evun.sweet.core.cas.ContextHolder;
import cn.evun.sweet.core.cas.ModelAliasRegistry;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.exception.SweetException;
import cn.evun.sweet.core.mybatis.general.Example;
import cn.evun.sweet.core.mybatis.general.Example.Criteria;
import cn.evun.sweet.core.spring.SpringContext;

/**
 * 租户内唯一校验
 *
 * @author yangw
 * @since 1.0.0
 */
public class TenantUniqueValidator implements ConstraintValidator<TenantUnique, String>{

	private String modelName ;
	private String propName ;
	private String tenantAlia ;
	private String[] conditions ;
	private String[] conditionValues ;
	
	private GeneralObjectAccessService objAccessService;
	
	private ModelAliasRegistry modelAlias;

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(TenantUnique annoation) {
		propName = annoation.property();	
		modelName = annoation.model();
		tenantAlia = annoation.tenantAlia();
		conditions = annoation.conditions();
		conditionValues = annoation.conditionValues();
		if(conditions.length != conditionValues.length){
			throw new SweetException(R.exception.excode_illegalargument, "conditions not match conditionValues");
		}
		objAccessService = SpringContext.getApplicationContext().getBean(GeneralObjectAccessService.class);
		modelAlias = SpringContext.getApplicationContext().getBean(ModelAliasRegistry.class);
	}

	/* 
	 * ConstraintValidatorContext主要用来替换默认的模板
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String value, ConstraintValidatorContext ctx) {
		if(!StringUtils.hasText(value)){
			return true;
		}
		Class<?> modelClass = modelAlias.resolveAlias(modelName);
		if(null == modelClass){
			throw new SweetException(R.exception.excode_resource_notexist, modelName+" not registerd");
		}
		
		Long currentTenantId = null;
		try {
			currentTenantId = Long.valueOf(BeanUtils.getProperty(ContextHolder.getSession().getAttribute(R.session.tenant_info), "tenantId"));
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IllegalStateException e) {
			throw new SweetException("Failed to get login tenant id", e);
		}
		
		Example example = new Example(modelClass);
		Criteria condition = example.createCriteria().andEqualTo(propName, value).andEqualTo(tenantAlia, currentTenantId);
		if(conditions.length > 0){
			for(int i = 0 ; i<conditions.length; i++){
				condition.andEqualTo(conditions[i], conditionValues[i]);
			}
		}
		List<Object> queryObj = objAccessService.selectByExample(example);
		return null == queryObj || queryObj.size() < 1;
	}
}
