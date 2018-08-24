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
 * 用户组（角色）与岗位的中间表对应的实体
 *
 * @author yangw
 * @since 1.1.1
 */
@Alias("postpckmap")
@Table(name="sweet_auth_post_pck_map")
public class PostPckMapDo implements Serializable {

	private static final long serialVersionUID = 1L;	

	@Id
	@OrderBy("DESC")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long    id;
	public Long    postId;
	public Long    pckId;
	public Long    tenantId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date    createdon;
	
	@RelationTable(fkCol="pckId", col="pckId")
	@Transient
	public ItemPckDo sweetAuthItemPck;
	
	@RelationTable(fkCol="postId", col="postId")
	@Transient
	public PostDo sweetAuthPost;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
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

	public PostDo getSweetAuthPost() {
		return sweetAuthPost;
	}

	public void setSweetAuthPost(PostDo sweetAuthPost) {
		this.sweetAuthPost = sweetAuthPost;
	}
	
}
