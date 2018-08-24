package cn.evun.sweet.auth.model.dto;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 功能信息传输实体
 *
 * @author yangw
 * @since 1.0.0
 */
public class ItemDTO implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	public String  itemTab;
	public String  itemDescription;
	public String  itemRequestmethod;
	
	@NotEmpty(message = "{base.itemurl.null}")  
	public String  itemUrl;
	
	public String getItemTab() {
		return itemTab;
	}
	public void setItemTab(String itemTab) {
		this.itemTab = itemTab;
	}
	public String getItemDescription() {
		return itemDescription;
	}
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	public String getItemUrl() {
		return itemUrl;
	}
	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}
	public String getItemRequestmethod() {
		return itemRequestmethod;
	}
	public void setItemRequestmethod(String itemRequestmethod) {
		this.itemRequestmethod = itemRequestmethod;
	} 
	
}
