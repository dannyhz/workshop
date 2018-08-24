package cn.evun.sweet.auth.model.dto;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 菜单信息传输实体
 *
 * @author yangw
 * @since 1.0.0
 */
public class MenuDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NotEmpty(message = "{base.menuname.null}")  
	@Length(min = 1, max = 20, message = "{base.menuname.illegal}") 
	public String 	menuName;
	
	@Min(value=1, message="{base.menutype.illegal}") 
	@Max(value=3, message="{base.menutype.illegal}") 
	public Integer	menuType; 
	
	@Min(value=1, message="{base.menuauthlevel.illegal}") 
	@Max(value=3, message="{base.menuauthlevel.illegal}") 
	public Integer	menuAuthLevel;
	
	public Boolean	menuVisibility;
	public Integer	menuSerialNo;
	public String 	menuIconclass;
	public Long		menuItemId;
	public String 	menuHref;
	public Long 	menuParentId;
	public String 	menuCode;
	
	public String getMenuCode() {
		return menuCode;
	}
	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}
	public Long getMenuParentId() {
		return menuParentId;
	}
	public void setMenuParentId(Long menuParentId) {
		this.menuParentId = menuParentId;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public Integer getMenuType() {
		return menuType;
	}
	public void setMenuType(Integer menuType) {
		this.menuType = menuType;
	}
	public Integer getMenuAuthLevel() {
		return menuAuthLevel;
	}
	public void setMenuAuthLevel(Integer menuAuthLevel) {
		this.menuAuthLevel = menuAuthLevel;
	}
	public Boolean getMenuVisibility() {
		return menuVisibility;
	}
	public void setMenuVisibility(Boolean menuVisibility) {
		this.menuVisibility = menuVisibility;
	}
	public Integer getMenuSerialNo() {
		return menuSerialNo;
	}
	public void setMenuSerialNo(Integer menuSerialNo) {
		this.menuSerialNo = menuSerialNo;
	}
	public Long getMenuItemId() {
		return menuItemId;
	}
	public void setMenuItemId(Long menuItemId) {
		this.menuItemId = menuItemId;
	}
	public String getMenuHref() {
		return menuHref;
	}
	public void setMenuHref(String menuHref) {
		this.menuHref = menuHref;
	}
	public String getMenuIconclass() {
		return menuIconclass;
	}
	public void setMenuIconclass(String menuIconClass) {
		this.menuIconclass = menuIconClass;
	}
}
