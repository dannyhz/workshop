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

import cn.evun.sweet.auth.util.UserContext;
import cn.evun.sweet.core.mybatis.general.RelationTable;

/**
 * 用户信息表对应的实体
 *
 * @author yangw
 * @since 1.0.0
 */
@Alias("user")
@Table(name="sweet_auth_user")//别名'U'主要用于关联查询，必须指定，'name'不指定则将使用类名的驼峰转换形式作为表名
public class UserDo implements Serializable {
	
	//静态字段自动排除不予表字段映射
	private static final long serialVersionUID = 1L;	
	
	public static final Integer USER_ROLE_SYSTEM = 1;
	public static final Integer USER_ROLE_ADMIN = 2;
	public static final Integer USER_ROLE_NORMAL = 3;
	
	@Id
	@OrderBy("DESC")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long    userId;   	
	public String  userCode;            
	public String  userName; 
	public String  userPingyin;
	public String  userPassword;
	public String  userMobile; 
	public String  userEmail;
	public String  userImg;
	public Boolean userForcemodifypsw; 	
	public String  userSign;
	public Integer userRole; //1，平台管理员；2，租户管理员；3，用户
	public Boolean userInit; //0，普通用户；1，初始化用户（无法删除）
	public Long    userTenantId;	
	public Long    userOrgId; 	
	public Long    userPostId; 
	public Date    userLocktime;	
	public Boolean userStatus;         
	public Boolean userIsdel;
	
	@Transient //不做表字段映射
	public String  userTenantCode;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date    userCreatedon;  	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date    userModifiedon;  
	
	@Transient //不做表字段映射
	private Date createdonStart;
	@Transient //不做表字段映射
	private Date createdonEnd;
	
	@RelationTable(fkCol="tenantId", col="userTenantId")
	@Transient
	public TenantDo sweetAuthTenant;//属性名必须和关联类对应的表名的驼峰形式一致，以保证级联查询有效。  	
	@RelationTable(fkCol="orgId", col="userOrgId")
	@Transient
	public OrgDo sweetAuthOrg; //属性名必须和关联类对应的表名的驼峰形式一致，以保证级联查询有效。

	
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

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

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public Boolean getUserForcemodifypsw() {
		return userForcemodifypsw;
	}

	public void setUserForcemodifypsw(Boolean userForcemodifypsw) {
		this.userForcemodifypsw = userForcemodifypsw;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public Long getUserTenantId() {
		return userTenantId;
	}

	public void setUserTenantId(Long userTenantId) {
		this.userTenantId = userTenantId;
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

	public Date getUserCreatedon() {
		return userCreatedon;
	}

	public void setUserCreatedon(Date userCreatedon) {
		this.userCreatedon = userCreatedon;
	}

	public Date getUserModifiedon() {
		return userModifiedon;
	}

	public void setUserModifiedon(Date userModifiedon) {
		this.userModifiedon = userModifiedon;
	}

	public Boolean getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(Boolean userStatus) {
		this.userStatus = userStatus;
	}

	public Boolean getUserIsdel() {
		return userIsdel;
	}

	public void setUserIsdel(Boolean userIsdel) {
		this.userIsdel = userIsdel;
	}

	public Date getUserLocktime() {
		return userLocktime;
	}

	public void setUserLocktime(Date userLocktime) {
		this.userLocktime = userLocktime;
	}

	public String getUserTenantCode() {
		return userTenantCode;
	}

	public void setUserTenantCode(String userTenantCode) {
		this.userTenantCode = userTenantCode;
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

	public TenantDo getSweetAuthTenant() {
		return sweetAuthTenant;
	}

	public void setSweetAuthTenant(TenantDo sweetAuthTenant) {
		this.sweetAuthTenant = sweetAuthTenant;
	}

	public OrgDo getSweetAuthOrg() {
		return sweetAuthOrg;
	}

	public void setSweetAuthOrg(OrgDo sweetAuthOrg) {
		this.sweetAuthOrg = sweetAuthOrg;
	}

	public Date getCreatedonStart() {
		return createdonStart;
	}

	public void setCreatedonStart(Date createdonStart) {
		this.createdonStart = createdonStart;
	}

	public Date getCreatedonEnd() {
		return createdonEnd;
	}

	public void setCreatedonEnd(Date createdonEnd) {
		this.createdonEnd = createdonEnd;
	}
	
	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserImg() {
		return userImg;
	}

	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}

	public Boolean getUserInit() {
		return userInit;
	}

	public void setUserInit(Boolean userInit) {
		this.userInit = userInit;
	}

	public String getUserPingyin() {
		return userPingyin;
	}

	public void setUserPingyin(String userPingyin) {
		this.userPingyin = userPingyin;
	}

	public UserDo initDefaultInfo(){
		if(this.userRole == null){
			this.setUserRole(USER_ROLE_NORMAL);
		}
		if(this.userInit == null){
			this.setUserInit(false);
		}
		this.setUserTenantId(UserContext.getTenantId());
		this.setUserPassword("7c4a8d09ca3762af61e59520943dc264");
		this.setUserForcemodifypsw(true);		
		return this;
	}

}
