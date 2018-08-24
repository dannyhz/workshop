package cn.evun.sweet.auth.model.dto;

import javax.validation.GroupSequence;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import cn.evun.sweet.core.validation.SqlCheck;
import cn.evun.sweet.core.validation.TenantUnique;
import cn.evun.sweet.core.validation.ValidateStep;

/**
 * 用户信息传输实体。
 *
 * @author yangw
 * @since 1.0.0
 */
@GroupSequence({ValidateStep.First.class, ValidateStep.Second.class, ValidateStep.Third.class, UserDTO.class})
public class UserDTO {
	
	public Long userId;  
	
	@NotEmpty(message = "{base.code.null}", groups = {ValidateStep.Third.class})  
	@Length(min = 1, max = 16, message = "{base.usercode.illegal}", groups = {ValidateStep.Third.class}) 
	@TenantUnique(model="user", property="userCode", tenantAlia="userTenantId", message="{base.code.exist}",
			conditions={"userIsdel"}, conditionValues={"0"}, groups = {ValidateStep.First.class}) //用户编号租户内唯一
	@SqlCheck(sqlid="auth.validate.usercode", SpEL="#result.get(0)<1",
			message="{base.code.exist}", groups = {ValidateStep.Second.class}) //用户编号编辑时租户内唯一
	public String  userCode;   
	
	@NotEmpty(message = "{base.username.null}", groups = {ValidateStep.Third.class})  
	@Length(min = 1, max = 20, message = "{base.username.illegal}", groups = {ValidateStep.Third.class}) 	     
	public String  userName;
	
	//@NotEmpty(message = "{base.mobile.null}", groups = {ValidateStep.Third.class}) 
	@Pattern(regexp = "^[1](3|4|5|7|8)[0-9]{9}$", message = "{base.mobile.illegal}", groups = {ValidateStep.Third.class})	
	public String  userMobile;
	
	@Email(message="{base.email.illegal}", groups = {ValidateStep.Third.class})
	public String  userEmail;
	
	@Min(value=1, message="{base.userrole.illegal}", groups = {ValidateStep.Third.class}) 
	@Max(value=3, message="{base.userrole.illegal}", groups = {ValidateStep.Third.class}) 
	public Integer userRole;
	
	public String  userPingyin;		
	public Long    userOrgId;
	public Long    userPostId;	
	public String  userSign;
	
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserMobile() {
		return userMobile;
	}
	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}
	public String getUserSign() {
		return userSign;
	}
	public void setUserSign(String userSign) {
		this.userSign = userSign;
	}
	public Integer getUserRole() {
		return userRole;
	}
	public void setUserRole(Integer userRole) {
		this.userRole = userRole;
	}
	public Long getUserOrgId() {
		return userOrgId;
	}
	public void setUserOrgId(Long userOrgId) {
		this.userOrgId = userOrgId;
	}
	public Long getUserPostId() {
		return userPostId;
	}
	public void setUserPostId(Long userPostId) {
		this.userPostId = userPostId;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserPingyin() {
		return userPingyin;
	}
	public void setUserPingyin(String userPingyin) {
		this.userPingyin = userPingyin;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}	
}
