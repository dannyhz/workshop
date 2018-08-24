package cn.evun.sweet.auth.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.evun.sweet.auth.model.ItemDo;
import cn.evun.sweet.auth.model.ItemPckDo;
import cn.evun.sweet.auth.model.MenuDo;
import cn.evun.sweet.auth.model.OrgDo;
import cn.evun.sweet.auth.model.PostDo;
import cn.evun.sweet.auth.model.PostPckMapDo;
import cn.evun.sweet.auth.model.TenantDo;
import cn.evun.sweet.auth.model.UserDo;
import cn.evun.sweet.auth.model.UserPckMapDo;
import cn.evun.sweet.auth.model.dto.ItemDTO;
import cn.evun.sweet.auth.model.dto.ItemPckDTO;
import cn.evun.sweet.auth.model.dto.MenuDTO;
import cn.evun.sweet.auth.model.dto.OrgDTO;
import cn.evun.sweet.auth.model.dto.PostDTO;
import cn.evun.sweet.auth.model.dto.TenantDTO;
import cn.evun.sweet.auth.model.dto.UserDTO;
import cn.evun.sweet.auth.service.AuthService;
import cn.evun.sweet.auth.util.R_Auth;
import cn.evun.sweet.auth.util.UserContext;
import cn.evun.sweet.common.util.CollectionUtils;
import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.common.util.encode.DigestUtil;
import cn.evun.sweet.core.bussinesshelper.GeneralObjectAccessService;
import cn.evun.sweet.core.cache.CacheAccessor;
import cn.evun.sweet.core.cas.ContextHolder;
import cn.evun.sweet.core.common.JsonResultDO;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.exception.ValidateException;
import cn.evun.sweet.core.mybatis.page.Page;
import cn.evun.sweet.core.mybatis.page.PageDTO;
import cn.evun.sweet.core.validation.ValidateStep;

/**   
 * 权限模块相关的控制器
 * 
 * @author yangw   
 * @since V1.0.0
 */
@RestController
public class AuthController {
	
	@Autowired
	private GeneralObjectAccessService objAccessService;
	@Autowired
	private AuthService authService;

	/**
	 * 租户列表查询。
	 * 
	 * @param tenantCode   		帐号  
	 * @param tenantShortname	简称
	 * @param tenantName		全称	
	 * @param tenantStatus  	启用/禁用
	 * @param tenantParentId	父级租户
	 */
	@RequestMapping(value="/auth/tenant/list")
	public JsonResultDO tenantList(TenantDTO param, @RequestParam(value="pageNum",defaultValue="1")Integer pageNum, 
			@RequestParam(value="pageSize",defaultValue="5")Integer pageSize){
		JsonResultDO result = new JsonResultDO();
		TenantDo tenant = new TenantDo();
		BeanUtils.copyProperties(param, tenant);
		tenant.setTenantIsdel(false);
		Page<TenantDo> list = (Page<TenantDo>)objAccessService.query(tenant, pageNum, pageSize);
		PageDTO pagebar = new PageDTO();
		BeanUtils.copyProperties(list, pagebar);
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, list);
		result.addAttribute(JsonResultDO.RETURN_PAGEBAR_KEY, pagebar);
		return result;
	}
	
	/**
	 * 查询租户详情。
	 * 
	 * @param tenantId 
	 */
	@RequestMapping(value="/auth/tenant", method=RequestMethod.GET)
	public JsonResultDO tenantGet(@RequestParam("tenantId")Long tenantId){
		JsonResultDO result = new JsonResultDO();	
		TenantDo tenant = objAccessService.queryById(TenantDo.class, tenantId);
		if(!tenant.getTenantIsdel()){
			result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, tenant);
		}		
		return result;
	}
	
	/**
	 * 当前租户详情。
	 */
	@RequestMapping(value="/auth/currenttenant", method=RequestMethod.GET)
	public JsonResultDO tenantGet(){
		JsonResultDO result = new JsonResultDO();	
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, UserContext.getTenant());		
		return result;
	}
	
	/**
	 * 新增租户，并创建初始管理员用户。
	 * 
	 * @param tenantCode   		帐号（必填）  
	 * @param tenantShortname	简称
	 * @param tenantName		全称（必填）
	 * @param tenantContacter  	联系人（必填）
	 * @param tenantPhone		手机号（必填）
	 * @param tenantAddress		租户地址
	 * @param tenantParentId	父级租户
	 */
	@RequestMapping(value="/auth/tenant", method=RequestMethod.POST)
	public JsonResultDO tenantAdd(@Validated TenantDTO param, BindingResult bresult){
		TenantDo tenant = new TenantDo();
		BeanUtils.copyProperties(param, tenant, "tenantStatus");
		authService.addTenant(tenant);
		return new JsonResultDO();
	}
	
	/**
	 * 删除租户，包括组织、用户数据。软删除方式。
	 * 
	 * @param tenantId 
	 */
	@RequestMapping(value="/auth/tenant", method=RequestMethod.DELETE)
	public JsonResultDO tenantDel(@RequestParam("tenantId")Long tenantId){
		authService.delTenant(tenantId);
		return new JsonResultDO();
	}
	
	/**
	 * 更新租户名称等信息。
	 * 
	 * @param tenantId
	 * @param tenantShortname	简称
	 * @param tenantName		全称		
	 * @param tenantContacter  	联系人
	 * @param tenantAddress		租户地址
	 * @param tenantParentId	父级租户 
	 */
	@RequestMapping(value="/auth/tenant", method=RequestMethod.PUT)
	public JsonResultDO tenantModify(@RequestParam("tenantId")Long tenantId, @Validated TenantDTO param, BindingResult bresult){
		TenantDo tenant = new TenantDo();
		BeanUtils.copyProperties(param, tenant, "tenantCode", "tenantStatus", "tenantPhone");
		tenant.setTenantId(tenantId);
		authService.modifyTenant(tenant);
		return new JsonResultDO();
	}
	
	/**
	 * 启用/禁用租户。
	 * 
	 * @param tenantId
	 * @param tenantStatus 
	 */
	@RequestMapping(value="/auth/tenant/status", method=RequestMethod.PUT)
	public JsonResultDO tenantStatus(@RequestParam("tenantId")Long tenantId, @RequestParam("tenantStatus")Boolean status){
		authService.modifyTenantStatus(tenantId, status);
		return new JsonResultDO();
	}
	
	/**
	 * 修改租户手机号（联系方式）。
	 * 
	 * @param tenantPhone 
	 */
	@RequestMapping(value="/auth/tenant/phone", method=RequestMethod.PUT)
	public JsonResultDO tenantPhone(@RequestParam("tenantPhone")String tenantPhone){
		authService.modifyTenantPhone(UserContext.getTenantId(), tenantPhone);
		return new JsonResultDO();
	}
	
	/**
	 * 设置租户不可见菜单项（覆盖原有设置）
	 * 
	 * @param tenantId
	 * @param menuId  
	 */
	@RequestMapping(value="/auth/tenantmenu", method=RequestMethod.POST)
	public JsonResultDO setTenantUnvisibleMenu(@RequestParam("tenantId")Long tenantId, @RequestParam("menuId")Long[] menuIds){
		authService.addTenantUnvisibleMenu(tenantId, menuIds);
		return new JsonResultDO();
	}
		
	/**
	 * 查询租户的不可见菜单项
	 * 
	 * @param tenantId 
	 */
	@RequestMapping(value="/auth/tenantmenu", method=RequestMethod.GET)
	public JsonResultDO getTenantUnvisibleMenu(){
		JsonResultDO result = new JsonResultDO();
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, authService.getTenantUnvisibleMenu(UserContext.getTenantId()));
		return result;
	}
	
	
	/**********************************************************组织相关操作******************************************************************/
	

	/**
	 * 租户的组织列表查询。
	 * 
	 * @param orgName			名称	
	 * @param orgParentId		父级组织
	 */
	@RequestMapping(value="/auth/org/list")
	public JsonResultDO orgList(OrgDTO param){
		JsonResultDO result = new JsonResultDO();
		OrgDo org = new OrgDo();
		org.setOrgName(param.getOrgName());
		org.setOrgParentId(param.getOrgParentId());
		org.setOrgTenantId(UserContext.getTenantId());
		org.setOrgIsdel(false);
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, objAccessService.query(org));
		return result;
	}
	
	/**
	 * 租户的组织（树）结构。
	 * 
	 * @return OrgNode 组织的树对象
	 */
	@RequestMapping(value="/auth/org/tree", method=RequestMethod.GET)
	public JsonResultDO orgTree(){		
		JsonResultDO result = new JsonResultDO();
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, authService.getTenantOrgTree());
		return result;
	}
	
	/**
	 * 新增组织
	 * 
	 * @param orgCode   		编号  
	 * @param orgName			名称（必填）		
	 * @param orgResponsibleId  负责人
	 * @param orgSerialNo		序号
	 * @param orgParentId		父级组织
	 */
	@RequestMapping(value="/auth/org", method=RequestMethod.POST)
	public JsonResultDO orgAdd(@Validated OrgDTO param, BindingResult bresult){
		JsonResultDO result = new JsonResultDO();
		OrgDo org = new OrgDo();
		BeanUtils.copyProperties(param, org);
		org.setOrgTenantId(UserContext.getTenantId());
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, authService.addOrg(org));
		return result;
	}
	
	/**
	 * 查询组织详情。
	 * 
	 * @param orgId 
	 */
	@RequestMapping(value="/auth/org", method=RequestMethod.GET)
	public JsonResultDO orgGet(@RequestParam("orgId")Integer orgId){
		JsonResultDO result = new JsonResultDO();	
		OrgDo org = objAccessService.queryById(OrgDo.class, orgId);
		if(!org.getOrgIsdel() && org.getOrgTenantId().longValue() == UserContext.getTenantId().longValue()){
			result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, org);
		}		
		return result;
	}
	
	/**
	 * 在指定的父组织下，批量新增组织信息。该方法不会设置编号、负责人等信息。
	 * 
	 * @param orgId
	 * @param orgName 名称数组
	 */
	@RequestMapping(value="/auth/org/battch", method=RequestMethod.POST)
	public JsonResultDO orgAddWithParent(Long orgId, @RequestParam("orgName")String[] orgName){
		JsonResultDO result = new JsonResultDO();	
		if(orgName.length > 0){
			result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, authService.addOrgWithParent(orgId, orgName));
		}
		return result;
	}
	
	/**
	 * 更新组织名称、父组织等信息。
	 * 
	 * @param orgId				组织标识
	 * @param orgName			组织名称		
	 * @param orgParentId  		父组织
	 * @param orgResponsibleId	负责人
	 * @param orgSerialNo		序号
	 */
	@RequestMapping(value="/auth/org", method=RequestMethod.PUT)
	public JsonResultDO orgModify(@RequestParam("orgId")Long orgId, @Validated OrgDTO param, BindingResult bresult){
		if(orgId.longValue() != param.getOrgParentId()){
			OrgDo org = new OrgDo();
			BeanUtils.copyProperties(param, org, "orgCode");
			org.setOrgTenantId(UserContext.getTenantId());
			org.setOrgId(orgId);
			authService.modifyOrg(org);
		}
		return new JsonResultDO();
	}
		
	/**
	 * 删除组织及其下属子组织
	 * 
	 * @param orgId 
	 * @param userDel TRUE,同时删除其下所有用户;FALSE,同时断开其下所有用户的组织关系;缺省为TRUE
	 */
	@RequestMapping(value="/auth/org", method=RequestMethod.DELETE)
	public JsonResultDO orgDel(@RequestParam("orgId")Long orgId, Boolean userDel){
		authService.delOrg(orgId, userDel);
		return new JsonResultDO();
	}
		
	/**
	 * 批量更新组织的用户。
	 * 
	 * @param userId 用户标识数组
	 */
	@RequestMapping(value="/auth/orguser", method=RequestMethod.PUT)
	public JsonResultDO orgUsersAdd(@RequestParam("orgId")Long orgId, @RequestParam("userId")Long[] userIds){	
		List<Object> params = new ArrayList<Object>();
		CollectionUtils.mergeArrayIntoCollection(userIds, params);
		authService.modifyOrgUsers(orgId, params);		
		return new JsonResultDO();
	}
	
	
	/**********************************************************岗位相关操作 since 1.1.1******************************************************************/
	
	/**
	 * 新增岗位
	 * 
	 * @param postName			名称（必填）		
	 * @param postOrgId  		岗位所属组织ID（必填）
	 */
	@RequestMapping(value="/auth/post", method=RequestMethod.POST)
	public JsonResultDO postAdd(@Validated PostDTO param, BindingResult bresult){
		JsonResultDO result = new JsonResultDO();
		PostDo post = new PostDo();
		BeanUtils.copyProperties(param, post);
		post.setPostTenantId(UserContext.getTenantId());
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, authService.addPost(post));
		return result;
	}
	
	/**
	 * 指定部门的岗位列表查询。
	 * 参数缺省情况下，将查询该租户下的所有岗位
	 * 
	 * @param orgId	
	 */
	@RequestMapping(value="/auth/post", method=RequestMethod.GET)
	public JsonResultDO postList(Long orgId){
		JsonResultDO result = new JsonResultDO();
		PostDo post = new PostDo();
		if(orgId == null){
			post.setPostTenantId(UserContext.getTenantId());
		}else {
			post.setPostOrgId(orgId);
		}
		post.setPostIsdel(false);
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, objAccessService.query(post));
		return result; 
	}
	
	/**
	 * 修改岗位名称。
	 * 
	 * @param postId
	 * @param postName
	 */
	@RequestMapping(value="/auth/post", method=RequestMethod.PUT)
	public JsonResultDO postName(@RequestParam("postId")Long postId, @RequestParam("postName")String postName){
		PostDo post = new PostDo();
		post.setPostId(postId);
		post.setPostName(postName);
		authService.modifyPostName(post);
		return new JsonResultDO();
	}
	
	/**
	 * 删除岗位及其功能组的关联关系，软删除方式。
	 * 
	 * @param postId 
	 */
	@RequestMapping(value="/auth/post", method=RequestMethod.DELETE)
	public JsonResultDO postDel(@RequestParam("postId")Long postId){
		authService.delPost(postId);
		return new JsonResultDO();
	}
	
	/**
	 * 设置岗位关联的功能组（覆盖原有设置）
	 * 
	 * @param postId
	 * @param pckid Long[]  
	 */
	@RequestMapping(value="/auth/post/itempck", method=RequestMethod.POST)
	public JsonResultDO setPostItemPcks(@RequestParam("postId")Long postId, @RequestParam("pckid")Long[] pckIds){
		authService.addPostItemPcks(postId, pckIds);
		return new JsonResultDO();
	}
	
	/**
	 * 查询岗位关联的功能组
	 * 
	 * @param postId 
	 */
	@RequestMapping(value="/auth/post/itempck", method=RequestMethod.GET)
	public JsonResultDO getPostItemPcks(@RequestParam("postId")Long postId){
		JsonResultDO result = new JsonResultDO();
		PostPckMapDo ppm = new PostPckMapDo();
		ppm.setPostId(postId);			
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY,  objAccessService.queryWithRelation(ppm));
		return result; 
	}
		
	/**********************************************************用户相关操作******************************************************************/
	
	/**
	 * 租户的用户列表查询。
	 * 
	 * @param userCode	
	 * @param userName	
	 * @param userPingyin	
	 * @param userMobile	
	 * @param userEmail	
	 * @param userRole	
	 * @param userOrgId
	 */
	@RequestMapping(value="/auth/user/list")
	public JsonResultDO userList(UserDTO param){
		JsonResultDO result = new JsonResultDO();
		UserDo user = new UserDo();
		BeanUtils.copyProperties(param, user, "userSign");
		user.setUserTenantId(UserContext.getTenantId());
		user.setUserIsdel(false);
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, objAccessService.query(user));		
		//TODO 屏蔽用户密码等敏感信息
		return result; 
	}
	
	/**
	 * 查询用户详情。
	 * 
	 * @param userId 
	 */
	@RequestMapping(value="/auth/user", method=RequestMethod.GET)
	public JsonResultDO userGet(@RequestParam("userId")Integer userId){
		JsonResultDO result = new JsonResultDO();	
		UserDo user = objAccessService.queryById(UserDo.class, userId);
		if(!user.getUserIsdel()){
			result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, user);
		}		
		return result;
	}
	
	/**
	 * 当前用户详情。
	 */
	@RequestMapping(value="/auth/currentuser", method=RequestMethod.GET)
	public JsonResultDO userGet(){
		JsonResultDO result = new JsonResultDO();	
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, UserContext.getUser());		
		return result;
	}
	
	/**
	 * 新增用户
	 * 
	 * @param userCode 		必填
	 * @param userName		必填
	 * @param userMobile
	 * @param userEmail
	 * @param userOrgId
	 */
	@RequestMapping(value="/auth/user", method=RequestMethod.POST)
	public JsonResultDO userAdd(@Validated({ValidateStep.First.class,ValidateStep.Third.class}) UserDTO param, BindingResult bresult){
		JsonResultDO result = new JsonResultDO();
		UserDo user = new UserDo();
		BeanUtils.copyProperties(param, user);
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, authService.addUser(user));
		return result;
	}
	
	/**
	 * 启用/禁用用户。
	 * 
	 * @param userId
	 * @param userStatus 
	 */
	@RequestMapping(value="/auth/user/status", method=RequestMethod.PUT)
	public JsonResultDO userStatus(@RequestParam("userId")Long userId, @RequestParam("userStatus")Boolean status){
		UserDo user = new UserDo();
		user.setUserId(userId);
		user.setUserStatus(status);
		authService.modifyUser(user);
		return new JsonResultDO();
	}
	
	/**
	 * 用户密码修改。
	 * 
	 * @param oldpsw
	 * @param newpsw 
	 */
	@RequestMapping(value="/auth/user/psw", method=RequestMethod.PUT)
	public JsonResultDO userPassword(@RequestParam("oldpsw")String oldpsw, @RequestParam("newpsw")String newpsw){		
		JsonResultDO result = new JsonResultDO();
		String newPassword = DigestUtil.shaDigestAsHex(newpsw.getBytes());
		if(!UserContext.getUser().getUserPassword().equals(DigestUtil.shaDigestAsHex(oldpsw.getBytes()))){
			result.setError("100");
			result.addAttribute("validate", new ValidateException().reject(R_Auth.exception.base_password_wrong));
			result.setMessage(new ValidateException(R_Auth.exception.base_password_wrong, "").getI18nMessage());
		}else if(UserContext.getUser().getUserPassword().equals(newPassword)){
			result.setError("100");
			result.addAttribute("validate", new ValidateException().reject(R_Auth.exception.base_password_equal));
			result.setMessage(new ValidateException(R_Auth.exception.base_password_equal, "").getI18nMessage());
		}else if(StringUtils.hasText(newpsw)){	
			UserDo user = new UserDo();
			user.setUserId(UserContext.getUserId());
			user.setUserPassword(newPassword);//密码使用SHA加密
			user.setUserForcemodifypsw(false);	
			authService.modifyUser(user);
			user = UserContext.getUser();
			user.setUserPassword(newPassword);
			ContextHolder.getSession().setAttribute(R.session.user_info, user);
		}
		return result;
	}
	
	/**
	 * 用户密码重置（初始密码：123456）。
	 * 
	 * @param userId 
	 */
	@RequestMapping(value="/auth/user/resetpsw", method=RequestMethod.PUT)
	public JsonResultDO userPassword(@RequestParam("userId")Long userId){
		UserDo user = new UserDo();
		user.setUserId(userId);
		user.setUserPassword("7c4a8d09ca3762af61e59520943dc264"); //重置密码为123456
		user.setUserForcemodifypsw(true);		
		authService.modifyUser(user);
		return new JsonResultDO();
	}
	
	/**
	 * 更新当前用户资料。
	 * 
	 * @param userSign 
	 * @param userName	
	 * @param userEmail
	 */
	@RequestMapping(value="/auth/user/info", method=RequestMethod.PUT)
	public JsonResultDO userInfo(@Validated({ValidateStep.Third.class}) UserDTO param, BindingResult bresult){
		UserDo user = new UserDo();
		user.setUserId(UserContext.getUserId());
		user.setUserSign(param.getUserSign());
		user.setUserName(param.getUserName());
		user.setUserEmail(param.getUserEmail());
		authService.modifyUser(user);
		return new JsonResultDO();
	}
	
	/**
	 * 当前用户头像上传。
	 * 
	 * @param userImg 为file类型
	 */
	@RequestMapping(value="/auth/user/img", method=RequestMethod.POST)
	public JsonResultDO userHeaderImg(@RequestParam(value="userimg") MultipartFile userimg){
		System.out.println("Name: "+userimg.getName());
        System.out.println("OriginalFilename: "+userimg.getOriginalFilename());
        System.out.println("ContentType: "+userimg.getContentType());
        System.out.println("Size: "+userimg.getSize());
		return new JsonResultDO();
	}
	
	/**
	 * 当前用户手机号码修改。
	 * 
	 * @param mobile 
	 */
	@RequestMapping(value="/auth/user/mobile", method=RequestMethod.PUT)
	public JsonResultDO userMobile(@RequestParam("mobile")String mobile){		
		UserDo user = new UserDo();
		user.setUserId(UserContext.getUserId());
		user.setUserMobile(mobile);	
		authService.modifyUser(user);
		return new JsonResultDO();
	}
	
	/**
	 * 更新用户名称、组织、角色等信息（管理员使用）。
	 * 
	 * @param userId
	 * @param userCode
	 * @param userName	
	 * @param userMobile
	 * @param userEmail
	 * @param userOrgId
	 * @param userPostId
	 * @param userRole
	 */
	@RequestMapping(value="/auth/user", method=RequestMethod.PUT)
	public JsonResultDO userModify(@Validated({ValidateStep.Second.class, ValidateStep.Third.class}) UserDTO param, BindingResult bresult){
		UserDo user = new UserDo();
		user.setUserId(param.getUserId());
		BeanUtils.copyProperties(param, user, "userSign");
		authService.modifyUser(user);
		return new JsonResultDO();
	}		
	
	/**
	 * 删除用户及其功能组的关联关系，软删除方式。
	 * 
	 * @param userId 
	 */
	@RequestMapping(value="/auth/user", method=RequestMethod.DELETE)
	public JsonResultDO userDel(@RequestParam("userId")Long userId){
		authService.delUser(userId);
		return new JsonResultDO();
	}
	
	/**
	 * 查询用户的功能组列表。
	 * 
	 * @param userId 
	 */
	@RequestMapping(value="/auth/user/itempck", method=RequestMethod.GET)
	public JsonResultDO userItemPcks(@RequestParam("userId")Long userId){
		JsonResultDO result = new JsonResultDO();
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, authService.getUserItemPcks(userId));
		return result; 
	}
	
	/**
	 * 设置用户关联的功能组（覆盖原有设置）
	 * 
	 * @param userId
	 * @param pckid Long[]  
	 */
	@RequestMapping(value="/auth/user/itempck", method=RequestMethod.POST)
	public JsonResultDO setUserItemPcks(@RequestParam("userId")Long userId, @RequestParam("pckid")Long[] pckIds){
		authService.addUserItemPcks(userId, pckIds);
		return new JsonResultDO();
	}
	

	/**********************************************************菜单相关操作******************************************************************/
	
	/**
	 * 当前用户的菜单树  
	 * 
	 * @return MenuNode 菜单的树对象
	 */
	@RequestMapping(value="/auth/menu/user", method=RequestMethod.GET)
	public JsonResultDO userMenuTree(){
		JsonResultDO result = new JsonResultDO();
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, authService.getCurrentUserMenuTree());
		return result; 
	}
	
	/**
	 * 当前租户的菜单树  
	 * 
	 * @return MenuNode 菜单的树对象
	 */
	@RequestMapping(value="/auth/menu/tenant", method=RequestMethod.GET)
	public JsonResultDO tenantMenuTree(){
		JsonResultDO result = new JsonResultDO();
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, authService.getTenantMenuTree(UserContext.getTenantId()));
		return result; 
	}
	
	/**
	 * 全局的菜单树  
	 * 
	 * @return MenuNode 菜单的树对象
	 */
	@RequestMapping(value="/auth/menu/global", method=RequestMethod.GET)
	public JsonResultDO globalMenuTree(){
		JsonResultDO result = new JsonResultDO();		
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, authService.getGlobalMenuTree());
		return result; 
	}
	
	/**
	 * 新增菜单
	 * 
	 * @param MenuName		必填	
	 * @param menuParentId
	 * @param menuType
	 * @param menuAuthLevel
	 * @param menuVisibility
	 * @param menuSerialNo
	 * @param menuIconClass
	 * @param menuItemId	与href互斥
	 * @param menuHref		与item互斥
	 */
	@RequestMapping(value="/auth/menu", method=RequestMethod.POST)
	public JsonResultDO menuAdd(@Validated MenuDTO param, BindingResult bresult){
		JsonResultDO result = new JsonResultDO();
		MenuDo menu = new MenuDo();
		BeanUtils.copyProperties(param, menu);
		authService.addMenu(menu);
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, menu);
		return result;
	}
	
	/**
	 * 重置菜单缓存
	 * @return
	 */
	@RequestMapping(value="/auth/menu/reset", method=RequestMethod.PUT)
	public JsonResultDO menuReset(){
		JsonResultDO result = new JsonResultDO();
		CacheAccessor.doEvict(AuthService.menu_catche_key);
		authService.getAllMenuRecord();
		return result;
	}
	
	/**
	 * 启用/禁用菜单。
	 * 
	 * @param menuId
	 * @param menuStatus 
	 */
	@RequestMapping(value="/auth/menu/status", method=RequestMethod.PUT)
	public JsonResultDO menuStatus(@RequestParam("menuId")Long menuId, @RequestParam("menuStatus")Boolean status){
		MenuDo menu = new MenuDo();
		menu.setMenuStatus(status);
		menu.setMenuId(menuId);
		menu.setMenuModifiedon(new Date());		
		authService.modifyMenu(menu);
		return new JsonResultDO();
	}
	
	/**
	 * 更新菜单信息。
	 * 
	 * @param menuId		必填
	 * @param menuName	
	 * @param menuParentId
	 * @param menuType
	 * @param menuAuthLevel
	 * @param menuVisibility
	 * @param menuSerialNo
	 * @param menuIconClass
	 * @param menuItemId	与href互斥
	 * @param menuHref		与item互斥
	 */
	@RequestMapping(value="/auth/menu", method=RequestMethod.PUT)
	public JsonResultDO menuModify(@RequestParam("menuId")Long menuId, @Validated MenuDTO param, BindingResult bresult){
		if(menuId.longValue() != param.getMenuParentId()){
			MenuDo menu = new MenuDo();
			menu.setMenuId(menuId);
			menu.setMenuModifiedon(new Date());	
			BeanUtils.copyProperties(param, menu);
			authService.modifyMenu(menu);
		}
		return new JsonResultDO();
	}
	
	/**
	 * 删除菜单及其子菜单。
	 * 
	 * @param menuId 
	 */
	@RequestMapping(value="/auth/menu", method=RequestMethod.DELETE)
	public JsonResultDO menuDel(@RequestParam("menuId")Long menuId){
		authService.delMenu(menuId);
		return new JsonResultDO();
	}
	
	
	/**********************************************************功能相关操作******************************************************************/
	
		
	/**
	 * 功能列表查询。
	 * 
	 * @param itemTab
	 * @param itemUrl
	 */
	@RequestMapping(value="/auth/item/list")
	public JsonResultDO itemList(ItemDTO param){
		JsonResultDO result = new JsonResultDO();
		ItemDo item = new ItemDo();
		BeanUtils.copyProperties(param, item);
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, objAccessService.query(item));
		return result;
	}
	
	/**
	 * 功能的所有TAB列表。
	 */
	@RequestMapping(value="/auth/item/tablist", method=RequestMethod.GET)
	public JsonResultDO itemTabList(){
		JsonResultDO result = new JsonResultDO();
		List<String> tabList = authService.queryItemTabList();
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, tabList);
		return result;
	}
	
	/**
	 * 新增功能
	 * 
	 * @param itemTab			
	 * @param itemDescription
	 * @param itemUrl			必填
	 * @param itemRequestmethod	必填
	 */
	@RequestMapping(value="/auth/item", method=RequestMethod.POST)
	public JsonResultDO itemAdd(@Validated ItemDTO param, BindingResult bresult){
		JsonResultDO result = new JsonResultDO();
		ItemDo item = new ItemDo();
		BeanUtils.copyProperties(param, item);
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, authService.addItem(item));
		return result;
	}
	
	/**
	 * 更新功能。
	 * 
	 * @param itemId
	 * @param itemTab
	 * @param itemDescription		
	 * @param itemUrl  		
	 * @param itemRequestmethod
	 */
	@RequestMapping(value="/auth/item", method=RequestMethod.PUT)
	public JsonResultDO itemModify(@RequestParam("itemId")Long itemId, ItemDTO param, BindingResult bresult){
		ItemDo item = new ItemDo();
		BeanUtils.copyProperties(param, item);
		item.setItemId(itemId);
		authService.modifyItem(item);
		return new JsonResultDO();
	}
	
	/**
	 * 删除功能。
	 * 
	 * @param itemId 
	 */
	@RequestMapping(value="/auth/item", method=RequestMethod.DELETE)
	public JsonResultDO itemDel(@RequestParam("itemId")Long itemId){
		authService.delItem(itemId);
		return new JsonResultDO();
	}
	
	
	/**********************************************************功能组相关操作******************************************************************/
	
	
	/**
	 * 功能组（用户组）列表查询。
	 * 
	 * @param pckName
	 */
	@RequestMapping(value="/auth/itempck/list")
	public JsonResultDO pckList(ItemPckDTO param){
		JsonResultDO result = new JsonResultDO();
		ItemPckDo pck = new ItemPckDo();
		pck.setPckName(param.getPckName());
		pck.setPckTenantId(UserContext.getTenantId());
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, objAccessService.query(pck));
		return result;
	}
	
	/**
	 * 新增功能组（用户组）
	 * 
	 * @param pckName			
	 * @param pckMenuId String[]
	 */
	@RequestMapping(value="/auth/itempck", method=RequestMethod.POST)
	public JsonResultDO pckAdd(@Validated ItemPckDTO param, BindingResult bresult){
		ItemPckDo pck = new ItemPckDo();
		pck.setPckName(param.getPckName());
		pck.setPckTenantId(UserContext.getTenantId());
		pck.setPckMenuId(StringUtils.arrayToDelimitedString(param.getPckMenuId(),","));
		authService.addItemPck(pck);
		return new JsonResultDO();
	}
	
	/**
	 * 更新功能组（用户组）。
	 * 
	 * @param pckId
	 * @param pckName			
	 * @param pckMenuId String[]
	 */
	@RequestMapping(value="/auth/itempck", method=RequestMethod.PUT)
	public JsonResultDO pckModify(@RequestParam("pckId")Long pckId, @Validated ItemPckDTO param, BindingResult bresult){
		ItemPckDo pck = new ItemPckDo();
		if(StringUtils.hasText(param.getPckName())){
			pck.setPckName(param.getPckName());
		}
		if(param.getPckMenuId() != null){
			pck.setPckMenuId(StringUtils.arrayToDelimitedString(param.getPckMenuId(),","));
		}
		pck.setPckId(pckId);
		authService.modifyItemPck(pck);
		return new JsonResultDO();
	}
	
	/**
	 * 删除功能组（用户组）及其和用户的关联。
	 * 
	 * @param pckId 
	 */
	@RequestMapping(value="/auth/itempck", method=RequestMethod.DELETE)
	public JsonResultDO pckDel(@RequestParam("pckId")Long pckId){
		authService.delItemPck(pckId);
		return new JsonResultDO();
	}
		
	/**
	 * 查询功能组的用户列表。
	 * 
	 * @param pckId 
	 */
	@RequestMapping(value="/auth/itempck/user", method=RequestMethod.GET)
	public JsonResultDO itemPckUsers(@RequestParam("pckId")Long pckId){
		JsonResultDO result = new JsonResultDO();
		UserPckMapDo upm = new UserPckMapDo();
		upm.setPckId(pckId);
		result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY,  objAccessService.queryWithRelation(upm));
		return result; 
	}
	
	/**
	 * 设置功能组关联的用户（覆盖原有设置）
	 * 
	 * @param pckId
	 * @param userId
	 */
	@RequestMapping(value="/auth/itempck/user", method=RequestMethod.POST)
	public JsonResultDO setItemPckUsers(@RequestParam("pckId")Long pckId, @RequestParam("userId")Long[] userIds){
		authService.addItemPckUsers(pckId, userIds);
		return new JsonResultDO();
	}
	
	
	/**********************************************************清空缓存的操作******************************************************************/
	
	/**
	 * 清空菜单数据的缓存
	 */
	@RequestMapping(value="/cache/menu", method=RequestMethod.GET)
	public JsonResultDO menuCacheEvict(){
		CacheAccessor.doEvict(AuthService.menu_catche_key);
		return new JsonResultDO();
	}
	
	/**
	 * 清空不可见菜单数据的缓存
	 */
	@RequestMapping(value="/cache/unvisimenu", method=RequestMethod.GET)
	public JsonResultDO unvisiMenuCacheEvict(){
		CacheAccessor.doEvict(AuthService.unvisible_menu_catche_key);
		return new JsonResultDO();
	}
	
}
