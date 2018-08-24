package cn.evun.sweet.auth.util;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import cn.evun.sweet.auth.model.MenuDo;
import cn.evun.sweet.common.datastructure.TreeNode;
import cn.evun.sweet.common.util.CollectionUtils;
import cn.evun.sweet.core.exception.SweetException;

/**
 * 菜单的树结构的存储实现
 *
 * @author yangw
 * @since 1.0.0
 */
public class MenuNode extends TreeNode{

	private static final long serialVersionUID = 1L;

	public MenuNode(Long id, String name) {
		super(id, name);
	}
	
	public MenuNode(Long id, String name, Boolean leaf) {
		super(id, name, leaf);
	}
		
	public Integer	menuType; 
	public Integer	menuAuthLevel;
	public Boolean	menuVisibility;
	public Integer	menuSerialNo;
	public Long		menuItemId;
	public String 	menuIconclass;
	public String 	menuHref;
	public Boolean	menuStatus;
	public Date		menuCreatedon;
	public Date		menuModifiedon;	
	public String 	itemURL;
	public String 	menuCode;
	
	/**
     * 生成菜单树
     */
    public static MenuNode genMenuNode(List<MenuDo> menulist, MenuDo menu, int level){
    	if(menu == null || CollectionUtils.isEmpty(menulist)) {
            return null;
        }
    	
    	/*创建当前节点*/
    	MenuNode node = new MenuNode(menu.getMenuId(), menu.getMenuName());
    	BeanUtils.copyProperties(menu, node);
    	node.setLevel(level++);
    	if(level > 100){//防止数据错误导致的死循环
    		throw new SweetException(R_Auth.exception.base_treelevel_toodeep, "tree level too deep, please check source data.");
    	}
    	if(menu.getMenuId() == null){
    		node.setId(2L);
    	}
    	   
    	/*生成子节点*/
    	for(MenuDo menuDo : menulist){
    		if(menuDo.getMenuParentId()!= null && node.getId().longValue() == menuDo.getMenuParentId().longValue()){
    			MenuNode childNode = genMenuNode(menulist, menuDo, level);	
    			if(childNode != null){
    				node.addChild(childNode);
    			}
    		}
    	}
    	return node;
    }
	
	public String getMenuCode() {
		return menuCode;
	}

	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
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

	public String getItemURL() {
		return itemURL;
	}

	public void setItemURL(String itemURL) {
		this.itemURL = itemURL;
	}

	public String getMenuIconclass() {
		return menuIconclass;
	}

	public void setMenuIconclass(String menuIconClass) {
		this.menuIconclass = menuIconClass;
	}

}
