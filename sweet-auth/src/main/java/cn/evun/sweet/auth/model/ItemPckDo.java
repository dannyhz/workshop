package cn.evun.sweet.auth.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 功能组（用户组）表对应的实体
 *
 * @author yangw
 * @since 1.0.0
 */
@Alias("itempck")
@Table(name="sweet_auth_item_pck")
public class ItemPckDo implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	@Id
	@OrderBy("DESC")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long    pckId;
	public String  pckName;
	public Long    pckTenantId;
	public String  pckMenuId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date    pckCreatedon;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date    pckModifiedon;
	
	
	public Long getPckId() {
		return pckId;
	}
	public void setPckId(Long pckId) {
		this.pckId = pckId;
	}
	public String getPckName() {
		return pckName;
	}
	public void setPckName(String pckName) {
		this.pckName = pckName;
	}
	public Long getPckTenantId() {
		return pckTenantId;
	}
	public void setPckTenantId(Long pckTenantId) {
		this.pckTenantId = pckTenantId;
	}
	public String getPckMenuId() {
		return pckMenuId;
	}
	public void setPckMenuId(String pckMenuId) {
		this.pckMenuId = pckMenuId;
	}
	public Date getPckCreatedon() {
		return pckCreatedon;
	}
	public void setPckCreatedon(Date pckCreatedon) {
		this.pckCreatedon = pckCreatedon;
	}
	public Date getPckModifiedon() {
		return pckModifiedon;
	}
	public void setPckModifiedon(Date pckModifiedon) {
		this.pckModifiedon = pckModifiedon;
	}
	
	
}
