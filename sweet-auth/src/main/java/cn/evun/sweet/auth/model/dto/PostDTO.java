package cn.evun.sweet.auth.model.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 岗位信息传输实体
 *
 * @author yangw
 * @since 1.1.1
 */
public class PostDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	   
	
	@NotEmpty(message = "{base.postname.null}") 
	@Length(min = 3, max = 40, message = "{base.postname.illegal}") 
	public String  postName;
	
	@NotNull(message = "{base.postorgid.null}") 
	public Long    postOrgId;
	
	public String getpostName() {
		return postName;
	}
	public void setpostName(String postName) {
		this.postName = postName;
	}
	
	public Long getPostOrgId() {
		return postOrgId;
	}

	public void setPostOrgId(Long postOrgId) {
		this.postOrgId = postOrgId;
	}
}
