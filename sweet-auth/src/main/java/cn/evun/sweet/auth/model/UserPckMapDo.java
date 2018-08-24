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

import cn.evun.sweet.core.mybatis.general.RelationTable;

/**
 * 功能组（用户组）与用户的中间表对应的实体
 *
 * @author yangw
 * @since 1.0.0
 */
@Alias("userpckmap")
@Table(name="sweet_auth_user_pck_map")
public class UserPckMapDo implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	@Id
	@OrderBy("DESC")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long    id;
	public Long    userId;
	public Long    pckId;
	public Long    tenantId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date    createdon;
	
	@RelationTable(fkCol="pckId", col="pckId")
	@Transient
	public ItemPckDo sweetAuthItemPck;
	
	@RelationTable(fkCol="userId", col="userId")
	@Transient
	public UserDo sweetAuthUser;

	public UserDo getSweetAuthUser() {
		return sweetAuthUser;
	}

	public void setSweetAuthUser(UserDo sweetAuthUser) {
		this.sweetAuthUser = sweetAuthUser;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getPckId() {
		return pckId;
	}

	public void setPckId(Long pckId) {
		this.pckId = pckId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Date getCreatedon() {
		return createdon;
	}

	public void setCreatedon(Date createdon) {
		this.createdon = createdon;
	}

	public ItemPckDo getSweetAuthItemPck() {
		return sweetAuthItemPck;
	}

	public void setSweetAuthItemPck(ItemPckDo sweetAuthItemPck) {
		this.sweetAuthItemPck = sweetAuthItemPck;
	}
	
}
