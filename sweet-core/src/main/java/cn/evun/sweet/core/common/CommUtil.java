package cn.evun.sweet.core.common;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import cn.evun.sweet.common.datastructure.LinkedMultiValueMap;

/**
 * 核心框架基础工具
 *
 * @author yangw
 * @since 1.0.0
 */
public class CommUtil {
	
	private static final String field_default = "global_field";
	
	/**
	 * 根据BindingResult解析异常信息
	 */
	public static JsonResultDO populateBindingResult(JsonResultDO result, BindingResult bresult){
		if(bresult!=null && bresult.hasErrors()){			
			LinkedMultiValueMap<String, String> errs = new LinkedMultiValueMap<String, String>();
			for(FieldError err : bresult.getFieldErrors()){
				errs.add(err.getField(), err.getDefaultMessage());
			}
			for(ObjectError err : bresult.getGlobalErrors()){
				errs.add(field_default, err.getDefaultMessage());
			}
			String firstKey = (String)errs.keySet().toArray()[0];
			result.setError("100");//设置一个未定义的code，以便国际化信息直接取用Message信息
	        result.addAttribute("validate", errs);
	        result.setMessage(errs.getFirst(firstKey));
		}
		return result;
		
	}
}
