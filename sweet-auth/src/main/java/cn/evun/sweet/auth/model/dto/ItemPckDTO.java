package cn.evun.sweet.auth.model.dto;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 功能组（用户组）信息传输实体
 *
 * @author yangw
 * @since 1.0.0
 */
public class ItemPckDTO implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	@NotEmpty(message = "{base.pckname.null}")
	@Length(min = 3, max = 30, message = "{base.pckname.illegal}") 
	public String  pckName;
	
	public String[]  pckMenuId;
	
	
	public String getPckName() {
		return pckName;
	}
	public void setPckName(String pckName) {
		this.pckName = pckName;
	}
	
	public String[] getPckMenuId() {
		return pckMenuId;
	}
	public void setPckMenuId(String[] pckMenuId) {
		this.pckMenuId = pckMenuId;
	}
	
	
	
}
