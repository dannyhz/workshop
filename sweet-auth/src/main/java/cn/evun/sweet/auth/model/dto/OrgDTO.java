package cn.evun.sweet.auth.model.dto;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import cn.evun.sweet.core.validation.TenantUnique;

/**
 * 机构组织信息传输实体
 *
 * @author yangw
 * @since 1.0.0
 */
public class OrgDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	   
	@Pattern(regexp = "(\\w){3,16}", message = "{base.code.illegal}")
	@TenantUnique(model="org", property="orgCode", tenantAlia="orgTenantId", message="{base.code.exist}",
			conditions={"orgIsdel"}, conditionValues={"0"}) //编号租户内唯一	
	public String  orgCode;   
	
	@NotEmpty(message = "{base.orgname.null}") 
	@Length(min = 3, max = 40, message = "{base.orgname.illegal}") 
	public String  orgName;
	
	public Long    orgParentId;  
	public Long    orgResponsibleId;
	
	@Min(value=1, message="{base.orgserialno.illegal}") 
	public Integer orgSerialno;
	
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public Long getOrgParentId() {
		return orgParentId;
	}
	public void setOrgParentId(Long orgParentId) {
		this.orgParentId = orgParentId;
	}
	public Long getOrgResponsibleId() {
		return orgResponsibleId;
	}
	public void setOrgResponsibleId(Long orgResponsibleId) {
		this.orgResponsibleId = orgResponsibleId;
	}
	public Integer getOrgSerialno() {
		return orgSerialno;
	}
	public void setOrgSerialno(Integer orgSerialNo) {
		this.orgSerialno = orgSerialNo;
	}


}
