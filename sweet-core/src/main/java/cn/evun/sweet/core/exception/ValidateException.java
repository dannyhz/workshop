package cn.evun.sweet.core.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Marker;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import cn.evun.sweet.common.datastructure.MultiValueMap;
import cn.evun.sweet.common.datastructure.MultiValueMapAdapter;
import cn.evun.sweet.core.common.R;

/**
 * 服务端异常检验异常结果封装.
 * 该服务已由GlobalAspect中的统一验证方案取代
 *
 * @author yangw
 * @since 1.0.0
 * @see {@link cn.evun.sweet.core.spring.GlobalAspect}
 */
public class ValidateException extends SweetException {

	private static final long serialVersionUID = 826818505255411100L;
	
	private static final String field_default = "global_field";
	
	public ValidateException(){
		super(R.message.msgcode_error_validate, "");
	}
	
	public ValidateException(String excode) {
		super(excode);
	}

	public ValidateException(String excode, String msg) {
		super(excode, msg);
	}

	public ValidateException(String excode, Object[] arguments) {
		super(excode, arguments);
	}
	
	public ValidateException(String excode, String msg, Object[] arguments) {
		super(excode, msg, arguments);
	}
	
	public ValidateException rejectValue(String field, String errorCode){
		super.addContextValue(field, new ValidateException(errorCode, errorCode));
		return this;
	}
	
	public ValidateException rejectValue(String field, String errorCode, String message){
		super.addContextValue(field, new ValidateException(errorCode, message));
		return this;
	}
	
	public ValidateException rejectValue(String field, String errorCode, Object[] errorArgs, String message){
		super.addContextValue(field, new ValidateException(errorCode, message, errorArgs));
		return this;
	}
	
	public ValidateException reject(String errorCode){
		super.addContextValue(field_default, new ValidateException(errorCode, ""));
		return this;
	}
	
	public ValidateException reject(String errorCode, String message){
		super.addContextValue(field_default, new ValidateException(errorCode, message));
		return this;
	}
	
	public ValidateException reject(String errorCode, Object[] errorArgs, String message){
		super.addContextValue(field_default, new ValidateException(errorCode, message, errorArgs));
		return this;
	}
	
	public boolean hasErrors(){
		return super.getContextEntries().size() > 0;
	}
	
	/**
	 * 获得所有验证异常的列表（包括全局异常和字段异常）
	 */
	public List<ValidateException> getAllErrors(){
		List<ValidateException> errors = new ArrayList<ValidateException>();
		for(String field : super.getContextLabels()){
			for(Object error : super.getContextValues(field)){
				errors.add((ValidateException)error);
			}
		}
		return errors;
	}
	
	/**
	 * 获得所有验证异常的国际化后的错误提示的列表（包括全局异常和字段异常）
	 */
	public List<String> getAllErrorMessage(){
		List<String> messages = new ArrayList<String>();
		for(ValidateException ex : getAllErrors()){
			messages.add(ex.getI18nMessage());
		}
		return messages;
	}
	
	/**
	 * 获得所有字段类型的验证异常的，Map<字段名,国际化后的错误提示>形式的内容
	 */
	public MultiValueMap<String, String> getAllFieldErrorMessage(){
		MultiValueMap<String, String> errorMsgs = new MultiValueMapAdapter<String, String>(new HashMap<String, List<String>>());
		for(Map.Entry<String, Object> error : super.getContextEntries()){
			if(!field_default.equals(error.getKey())){
				errorMsgs.add(error.getKey(), ((ValidateException)error.getValue()).getI18nMessage());
			}
		}
		return errorMsgs;
	}
	
	/**
	 * 获得所有全局类型的验证异常的国际化后的错误提示
	 */
	public List<String> getGlobaErrorMessage(){
		List<String> errorMsgs = new ArrayList<String>();
		for(Map.Entry<String, Object> error : super.getContextEntries()){
			if(field_default.equals(error.getKey())){
				errorMsgs.add(((ValidateException)error.getValue()).getI18nMessage());
			}
		}
		return errorMsgs;
	}
	
	public String getFirstErrorMessage(){
		if(hasErrors()){
			return getAllErrorMessage().get(0);
		}		
		return null;
	}
	
	/**
	 * 获得指定字段的异常链
	 */
	public List<ValidateException> getFieldErrors(String field){
		List<ValidateException> errors = new ArrayList<ValidateException>();
		for(Object error : super.getContextValues(field)){
			errors.add((ValidateException)error);
		}
		return errors;
	}
	
	/**
	 * 获得指定字段的国际化异常信息列表
	 */
	public List<String> getFieldErrorMessage(String field){
		List<String> messages = new ArrayList<String>();
		for(ValidateException ex : getFieldErrors(field)){
			messages.add(ex.getI18nMessage());
		}
		return messages;
	} 
	
	/**
	 * 根据BindingResult填充异常信息
	 */
	public ValidateException populate(BindingResult bresult){
		for(FieldError err : bresult.getFieldErrors()){
			rejectValue(err.getField(), err.getDefaultMessage());
		}
		for(ObjectError err : bresult.getGlobalErrors()){
			reject(err.getDefaultMessage());
		}
		return this;
	}
	
	/**需要被子类重写*/
	protected Marker getMarker(){
		return R.log.log_marker_exception_validate;
	}
	
}
