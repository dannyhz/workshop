package cn.evun.sweet.auth.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 租户的不可见菜单实体
 *
 * @author yangw
 * @since 1.0.0
 */
@Table(name="sweet_auth_tenant_menu_unvisible")
public class TenantMenuUnvisibleDo implements Serializable {

	/*静态字段自动排除，不予表字段映射*/
	private static final long serialVersionUID = 1L;
	
	@Id
	@OrderBy("DESC")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long    	id;    
	public Long 	tenantId;
	public Long 	menuId;    
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date    	createdon;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

	public Date getCreatedon() {
		return createdon;
	}

	public void setCreatedon(Date createdon) {
		this.createdon = createdon;
	}
	
	
}
