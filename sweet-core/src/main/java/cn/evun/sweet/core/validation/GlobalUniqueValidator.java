package cn.evun.sweet.core.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.bussinesshelper.GeneralObjectAccessService;
import cn.evun.sweet.core.cas.ModelAliasRegistry;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.exception.SweetException;
import cn.evun.sweet.core.mybatis.general.Example;
import cn.evun.sweet.core.mybatis.general.Example.Criteria;
import cn.evun.sweet.core.spring.SpringContext;

/**
 * 全局唯一校验
 *
 * @author yangw
 * @since 1.0.0
 */
public class GlobalUniqueValidator implements ConstraintValidator<GlobalUnique, String>{
	
	private String modelName = null;
	private String propName = null;
	private String[] conditions = null;
	private String[] conditionValues = null;
	
	private GeneralObjectAccessService objAccessService;
	
	private ModelAliasRegistry modelAlias;

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(GlobalUnique annoation) {
		propName = annoation.property();	
		modelName = annoation.model();
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
		Example example = new Example(modelClass);
		Criteria condition = example.createCriteria().andEqualTo(propName, value);
		if(conditions.length > 0){
			for(int i = 0 ; i<conditions.length; i++){
				condition.andEqualTo(conditions[i], conditionValues[i]);
			}
		}
		List<Object> queryObj = objAccessService.selectByExample(example);
		return null == queryObj || queryObj.size() < 1;
	}

}
