package cn.evun.sweet.auth.model.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import cn.evun.sweet.core.validation.GlobalUnique;

/**
 * 租户信息传输实体
 *
 * @author yangw
 * @since 1.0.0
 */
public class TenantDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty(message = "{base.code.null}")  
	@Pattern(regexp = "(\\w){3,16}", message = "{base.code.illegal}")
	@GlobalUnique(model="tenant", property="tenantCode", message="{base.code.exist}", conditions={"tenantIsdel"}, conditionValues={"0"})
	public String  tenantCode;   
	
	@Length(min = 3, max = 10, message = "{base.tenantshortname.illegal}") 	
	public String  tenantShortname;
	
	@NotEmpty(message = "{base.tenantname.null}")
	@Length(min = 3, max = 40, message = "{base.tenantname.illegal}") 
	public String  tenantName;
	
	@NotEmpty(message = "{base.tenantcontacter.null}")
	@Length(min = 3, max = 20, message = "{base.tenantcontacter.illegal}") 
	public String  tenantContacter;
	
	@NotEmpty(message = "{base.mobile.null}")
	@Pattern(regexp = "^[1](3|4|5|7|8)[0-9]{9}$", message = "{base.mobile.illegal}")	
	public String  tenantPhone;
	
	public String  tenantAddress;
	public Long    tenantParentId;
	public Boolean tenantStatus; 
		
	
	public String getTenantCode() {
		return tenantCode;
	}
	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}
	public String getTenantShortname() {
		return tenantShortname;
	}
	public void setTenantShortname(String tenantShortname) {
		this.tenantShortname = tenantShortname;
	}
	public String getTenantName() {
		return tenantName;
	}
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	public String getTenantContacter() {
		return tenantContacter;
	}
	public void setTenantContacter(String tenantContacter) {
		this.tenantContacter = tenantContacter;
	}
	public Long getTenantParentId() {
		return tenantParentId;
	}
	public void setTenantParentId(Long tenantParentId) {
		this.tenantParentId = tenantParentId;
	}
	public Boolean getTenantStatus() {
		return tenantStatus;
	}
	public void setTenantStatus(Boolean tenantStatus) {
		this.tenantStatus = tenantStatus;
	}
	public String getTenantPhone() {
		return tenantPhone;
	}
	public void setTenantPhone(String tenantPhone) {
		this.tenantPhone = tenantPhone;
	}
	public String getTenantAddress() {
		return tenantAddress;
	}
	public void setTenantAddress(String tenantAddress) {
		this.tenantAddress = tenantAddress;
	} 

}
