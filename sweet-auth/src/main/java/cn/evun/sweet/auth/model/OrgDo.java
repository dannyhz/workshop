package cn.evun.sweet.auth.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.ibatis.type.Alias;

import cn.evun.sweet.core.mybatis.general.FuzzyQuery;
import cn.evun.sweet.core.mybatis.general.RelationTable;

/**
 * 组织部门信息表对应的实体
 *
 * @author yangw
 * @since 1.0.0
 */
@Alias("org")
@Table(name="sweet_auth_org")
public class OrgDo implements Serializable {

	/*静态字段自动排除，不予表字段映射*/
	private static final long serialVersionUID = 1L;
	
	@Id
	@OrderBy("DESC")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long    orgId;          
	public String  orgCode;
	@FuzzyQuery
	public String  orgName;
	public Long    orgParentId;  
	public Long    orgResponsibleId;
	public Integer orgSerialno;
	public Long    orgTenantId;
	public Date    orgCreatedon;     	
	public Date    orgModifiedon;    	
	public Boolean orgStatus;         
	public Boolean orgIsdel;
	
	/*实体对象会自动排除，不予映射。类似树结构的自关联由于别名冲突，无法使用关联查询功能*/
	@Transient
	public OrgDo parentOrg; 
	
	/*属性名必须和关联类对应的表名的驼峰形式一致，以保证级联查询有效。*/
	@RelationTable(fkCol="tenantId", col="orgTenantId")
	@Transient
	public TenantDo sweetAuthTenant;	
	
	@Transient
	private List<UserDo> userList;
	
	
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
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
	public Date getOrgCreatedon() {
		return orgCreatedon;
	}
	public void setOrgCreatedon(Date orgCreatedon) {
		this.orgCreatedon = orgCreatedon;
	}
	public Date getOrgModifiedon() {
		return orgModifiedon;
	}
	public void setOrgModifiedon(Date orgModifiedon) {
		this.orgModifiedon = orgModifiedon;
	}
	public Boolean getOrgStatus() {
		return orgStatus;
	}
	public void setOrgStatus(Boolean orgStatus) {
		this.orgStatus = orgStatus;
	}
	public Boolean getOrgIsdel() {
		return orgIsdel;
	}
	public void setOrgIsdel(Boolean orgIsdel) {
		this.orgIsdel = orgIsdel;
	}
	public Long getOrgTenantId() {
		return orgTenantId;
	}
	public void setOrgTenantId(Long orgTenantId) {
		this.orgTenantId = orgTenantId;
	}

	public OrgDo getParentOrg() {
		return parentOrg;
	}

	public void setParentOrg(OrgDo parentOrg) {
		this.parentOrg = parentOrg;
	}

	public TenantDo getSweetAuthTenant() {
		return sweetAuthTenant;
	}

	public void setSweetAuthTenant(TenantDo sweetAuthTenant) {
		this.sweetAuthTenant = sweetAuthTenant;
	}

	public Long getOrgResponsibleId() {
		return orgResponsibleId;
	}

	public void setOrgResponsibleId(Long orgResponsibleId) {
		this.orgResponsibleId = orgResponsibleId;
	}

	public List<UserDo> getUserList() {
		return userList;
	}

	public void setUserList(List<UserDo> userList) {
		this.userList = userList;
	}
	public Integer getOrgSerialno() {
		return orgSerialno;
	}
	public void setOrgSerialno(Integer orgSerialNo) {
		this.orgSerialno = orgSerialNo;
	}
	
	@Override  
    public boolean equals(Object otherObject) {  
        if (this == otherObject)  
            return true;  
        if (otherObject == null)  
            return false;   
        if (getClass() != otherObject.getClass())  
            return false; 
        OrgDo other = (OrgDo) otherObject; 
        if(this.orgId==null || other.getOrgId()==null)
        	return false;
        return this.orgId.longValue() == other.getOrgId().longValue();  
  
    }  
	
}
