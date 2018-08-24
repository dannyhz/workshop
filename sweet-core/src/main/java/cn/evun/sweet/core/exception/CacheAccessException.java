package cn.evun.sweet.core.exception;

import org.apache.logging.log4j.Marker;

import cn.evun.sweet.core.common.R;

/**   
 * 缓存操作异常处理。<br/>
 * 除读以外的所有缓存操作，将抛出此异常。该异常的发生意味着系统内部错误，这里不做国际化展示，没有设置其excode。
 *
 * @author yangw 
 * @since V1.0.0  
 */
public class CacheAccessException extends SweetException {

	private static final long serialVersionUID = 826818505255435900L;

	public CacheAccessException(String excode) {
		super(excode);
	}

	public CacheAccessException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	/**需要被子类重写*/
	protected Marker getMarker(){
		return R.log.log_marker_exception_cache;
	}
}
