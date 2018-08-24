package cn.evun.sweet.auth.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import cn.evun.sweet.core.mybatis.general.FuzzyQuery;

/**
 * 租户信息表对应的实体
 *
 * @author yangw
 * @since 1.0.0
 */
@Table(name="sweet_auth_tenant")
@Alias("tenant")
public class TenantDo implements Serializable {

	/*静态字段自动排除，不予表字段映射*/
	private static final long serialVersionUID = 1L;
	
	@Id
	@OrderBy("DESC")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long    tenantId;          
	public String  tenantCode;     
	public String  tenantShortname;
	@FuzzyQuery
	public String  tenantName; 
	public String  tenantContacter;
	public String  tenantPhone;
	public String  tenantAddress;
	public Long    tenantParentId; 
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	public Date    tenantCreatedon;   
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	public Date    tenantModifiedon;    	
	public Boolean tenantStatus;         
	public Boolean tenantIsdel;
	
	/*实体对象会自动排除，不予映射。类似树结构的自关联由于别名冲突，无法使用关联查询功能*/
	@Transient
	private TenantDo parentTenant;
	
	
		
	public Long getTenantId() {
		return tenantId;
	}
	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
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
	public Long getTenantParentId() {
		return tenantParentId;
	}
	public void setTenantParentId(Long tenantParentId) {
		this.tenantParentId = tenantParentId;
	}
	public Date getTenantCreatedon() {
		return tenantCreatedon;
	}
	public void setTenantCreatedon(Date tenantCreatedon) {
		this.tenantCreatedon = tenantCreatedon;
	}
	public Date getTenantModifiedon() {
		return tenantModifiedon;
	}
	public void setTenantModifiedon(Date tenantModifiedon) {
		this.tenantModifiedon = tenantModifiedon;
	}
	public Boolean getTenantStatus() {
		return tenantStatus;
	}
	public void setTenantStatus(Boolean tenantStatus) {
		this.tenantStatus = tenantStatus;
	}
	public Boolean getTenantIsdel() {
		return tenantIsdel;
	}
	public void setTenantIsdel(Boolean tenantIsdel) {
		this.tenantIsdel = tenantIsdel;
	}
	
	public TenantDo getParentTenant() {
		return parentTenant;
	}
	public void setParentTenant(TenantDo parentTenant) {
		this.parentTenant = parentTenant;
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
	public String getTenantContacter() {
		return tenantContacter;
	}
	public void setTenantContacter(String tenantContacter) {
		this.tenantContacter = tenantContacter;
	}

}
