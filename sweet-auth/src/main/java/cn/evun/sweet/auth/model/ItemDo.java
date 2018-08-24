package cn.evun.sweet.auth.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import cn.evun.sweet.common.util.StringUtils;

/**
 * 功能（API）表对应的实体
 *
 * @author yangw
 * @since 1.0.0
 */
@Alias("item")
@Table(name="sweet_auth_item")
public class ItemDo implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	public static final String ITEM_REQUESTTYPE_GET = "get";
	public static final String ITEM_REQUESTTYPE_PUT = "put";
	public static final String ITEM_REQUESTTYPE_POST = "post";
	public static final String ITEM_REQUESTTYPE_DELETE = "delete";
	
	@Id
	@OrderBy("DESC")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long    itemId;
	public String  itemTab;
	public String  itemDescription;
	public String  itemUrl;
	public String  itemRequestmethod;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date    itemCreatedon;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date    itemModifiedon;
	
	@Transient
	public ArrayList<String> params;
	@Transient
	public String  itemAllDesc; //解析后用于显示的功能说明
	
	public Long getItemId() {
		return itemId;
	}
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	public String getItemTab() {
		return itemTab;
	}
	public void setItemTab(String itemTab) {
		this.itemTab = itemTab;
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
	public Date getItemCreatedon() {
		return itemCreatedon;
	}
	public void setItemCreatedon(Date itemCreatedon) {
		this.itemCreatedon = itemCreatedon;
	}
	public Date getItemModifiedon() {
		return itemModifiedon;
	}
	public void setItemModifiedon(Date itemModifiedon) {
		this.itemModifiedon = itemModifiedon;
	}
	public String getItemRequestmethod() {
		return itemRequestmethod;
	}
	public void setItemRequestmethod(String itemRequestmethod) {
		this.itemRequestmethod = itemRequestmethod;
	} 	
	public String getItemAllDesc() {
		if(StringUtils.hasText(itemDescription) && itemDescription.indexOf("{")>0){
			return itemDescription.substring(0, itemDescription.indexOf("{"));
		}
		return itemDescription;
	}
	public String getItemDescription() {		
		return itemDescription;
	}
	public List<String> getParams(){
		if(params == null){
			params = new ArrayList<String>();
			if(StringUtils.hasText(itemDescription) && itemDescription.indexOf("{") > 0){//有参数注解
				String paramStr = itemDescription.substring(itemDescription.indexOf("{")+1,itemDescription.length()-1);
				if(StringUtils.hasText(paramStr)){
					String[] paramArray = StringUtils.delimitedListToStringArray(paramStr, "}{");
					for(int i=0; i<paramArray.length; i++){
						String[] param = paramArray[i].split(":");
						params.add(param[0]+" "+(param.length>1?param[1]:""));
					}
				}
			}	
		}
		return params;
	}
}
