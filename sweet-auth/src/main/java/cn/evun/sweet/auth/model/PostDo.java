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

import cn.evun.sweet.core.mybatis.general.FuzzyQuery;
import cn.evun.sweet.core.mybatis.general.RelationTable;

/**
 * 岗位信息表对应的实体
 *
 * @author yangw
 * @since 1.1.1
 */
@Alias("post")
@Table(name="sweet_auth_post")
public class PostDo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@OrderBy("DESC")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long    postId;    
	public Long    postOrgId;   
	public String  postCode;
	@FuzzyQuery
	public String  postName;	
	public Long    postTenantId;
	public Date    postCreatedon;     	
	public Date    postModifiedon;          
	public Boolean postIsdel;	
	
	@RelationTable(fkCol="orgId", col="postOrgId")
	@Transient
	public OrgDo sweetAuthOrg;	
	

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public Long getPostOrgId() {
		return postOrgId;
	}

	public void setPostOrgId(Long postOrgId) {
		this.postOrgId = postOrgId;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public Long getPostTenantId() {
		return postTenantId;
	}

	public void setPostTenantId(Long postTenantId) {
		this.postTenantId = postTenantId;
	}

	public Date getPostCreatedon() {
		return postCreatedon;
	}

	public void setPostCreatedon(Date postCreatedon) {
		this.postCreatedon = postCreatedon;
	}

	public Date getPostModifiedon() {
		return postModifiedon;
	}

	public void setPostModifiedon(Date postModifiedon) {
		this.postModifiedon = postModifiedon;
	}

	public Boolean getPostIsdel() {
		return postIsdel;
	}

	public void setPostIsdel(Boolean postIsdel) {
		this.postIsdel = postIsdel;
	}

	public OrgDo getSweetAuthOrg() {
		return sweetAuthOrg;
	}

	public void setSweetAuthOrg(OrgDo sweetAuthOrg) {
		this.sweetAuthOrg = sweetAuthOrg;
	}

	@Override  
    public boolean equals(Object otherObject) {  
        if (this == otherObject)  
            return true;  
        if (otherObject == null)  
            return false;   
        if (getClass() != otherObject.getClass())  
            return false; 
        PostDo other = (PostDo) otherObject; 
        if(this.postId==null || other.getPostId()==null)
        	return false;
        return this.postId.longValue() == other.getPostId().longValue();  
  
    }  
	
}
