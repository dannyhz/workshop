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

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.mybatis.general.RelationTable;

/**
 * 菜单信息表对应的实体
 *
 * @author yangw
 * @since 1.0.0
 */
@Alias("menu")
@Table(name="sweet_auth_menu")
public class MenuDo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final Integer MENU_TYPE_FRAMEPAGE = 1;
	public static final Integer MENU_TYPE_INNERPAGE = 2;
	public static final Integer MENU_TYPE_ITEM = 3;
	
	public static final Integer MENU_AUTHLEVEL_SYSTEM = 1;
	public static final Integer MENU_AUTHLEVEL_ADMIN = 2;
	public static final Integer MENU_AUTHLEVEL_NORMAL = 3;
	
	@Id
	@OrderBy("DESC")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long 	menuId;
	public String 	menuCode;
	public Long 	menuParentId;
	public String 	menuName;
	public Integer	menuType; 
	public Integer	menuAuthLevel;
	public Boolean	menuVisibility;
	public Integer	menuSerialNo;
	public String 	menuIconclass;
	public Long		menuItemId;
	public String 	menuHref;
	public Boolean	menuStatus;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date		menuCreatedon;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date		menuModifiedon;	
	
	@Transient
	public String 	itemURL;
	@RelationTable(fkCol="itemId", col="menuItemId", join="left")
	@Transient
	public ItemDo sweetAuthItem;
	
	
	public String getMenuCode() {
		return menuCode;
	}
	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
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
	public Boolean getMenuStatus() {
		return menuStatus;
	}
	public void setMenuStatus(Boolean menuStatus) {
		this.menuStatus = menuStatus;
	}
	public Date getMenuCreatedon() {
		return menuCreatedon;
	}
	public void setMenuCreatedon(Date menuCreatedon) {
		this.menuCreatedon = menuCreatedon;
	}
	public Date getMenuModifiedon() {
		return menuModifiedon;
	}
	public void setMenuModifiedon(Date menuModifiedon) {
		this.menuModifiedon = menuModifiedon;
	}	
	public void setItemURL(String itemURL) {
		this.itemURL = itemURL;
	}
	public String getMenuHref() {
		return menuHref;
	}
	public void setMenuHref(String menuHref) {
		this.menuHref = menuHref;
	}
	public ItemDo getSweetAuthItem() {
		return sweetAuthItem;
	}
	public void setSweetAuthItem(ItemDo sweetAuthItem) {
		this.sweetAuthItem = sweetAuthItem;
	}	
	public String getMenuIconclass() {
		return menuIconclass;
	}
	public void setMenuIconclass(String menuIconClass) {
		this.menuIconclass = menuIconClass;
	}
	public String getItemURL() {
		if(getSweetAuthItem() != null && !StringUtils.hasText(itemURL)){
			return getSweetAuthItem().getItemUrl();
		}
		return itemURL;
	}
	
	@Override  
    public boolean equals(Object otherObject) {  
        if (this == otherObject)  
            return true;  
        if (otherObject == null)  
            return false;   
        if (getClass() != otherObject.getClass())  
            return false; 
        MenuDo other = (MenuDo) otherObject; 
        if(this.menuId==null || other.getMenuId()==null)
        	return false;
        return this.menuId.longValue() == other.getMenuId().longValue();  
  
    }  
	
}
