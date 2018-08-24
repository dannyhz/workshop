package cn.evun.sweet.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.bussinesshelper.SqlInvokeService;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.exception.SweetException;
import cn.evun.sweet.core.spring.SpringContext;

/**
 * 通过sql校验
 *
 * @author yangw
 * @since 1.0.0
 */
public class SqlCheckValidator implements ConstraintValidator<SqlCheck, String>{
	
	private String sqlid = null;
	private String spel = null;
	
	private SqlInvokeService sqlInvoker;

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(SqlCheck annoation) {
		sqlid = annoation.sqlid();	
		spel = annoation.SpEL();
		sqlInvoker = SpringContext.getApplicationContext().getBean(SqlInvokeService.class);
	}

	/* 
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String value, ConstraintValidatorContext ctx) {
		if(!StringUtils.hasText(value)){
			return true;
		}
		if(sqlInvoker.isSupport(sqlid)){
			Object result = sqlInvoker.excuteSql(sqlid);
			ExpressionParser parser = new SpelExpressionParser();  
		    EvaluationContext context = new StandardEvaluationContext();  
		    context.setVariable("result", result);  
		    return parser.parseExpression(spel).getValue(context, Boolean.class);
		}else {
			throw new SweetException(R.exception.excode_resource_notexist, sqlid+" not registry");
		}
	}

}
