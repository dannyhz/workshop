package cn.evun.sweet.auth.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.evun.sweet.auth.model.OrgDo;
import cn.evun.sweet.auth.model.TenantDo;
import cn.evun.sweet.auth.model.UserDo;
import cn.evun.sweet.auth.service.AuthService;
import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.common.util.encode.DigestUtil;
import cn.evun.sweet.core.bussinesshelper.LoginAndOutService;
import cn.evun.sweet.core.cache.CacheAccessor;
import cn.evun.sweet.core.cache.redis.DistributedLockUtil;
import cn.evun.sweet.core.cache.redis.RedisTemplateUtil;
import cn.evun.sweet.core.common.JsonResultDO;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.exception.ValidateException;
import cn.evun.sweet.core.mybatis.common.SqlMapper;

/**
 * 登录登出控制器默认实现
 *
 * @author yangw
 * @since 1.0.0
 */
@Controller
public class LoginController extends LoginAndOutService {

	@Resource(name="sqlSession")
	private SqlSession sqlSession;

	@Value("${user.login.requireverifycode}")
	private boolean requiredVerifyCode = false;
	@Value("${user.login.forcemodifypswforward}")
	private String forceModifypswForward;
	@Value("${user.login.lockperiod}")
	private int lockPeriod = 5;
	@Value("${user.login.lockinputcount}")
	private int lockInputCount = 10;

	@Resource
	private AuthService authService;


	@RequestMapping(value="/verifycode", method=RequestMethod.GET)
	public void verifyCode(HttpServletRequest request, HttpServletResponse response){
		verifyCode(request, response, 85, 35, 4);
	}

	@RequestMapping(value="/login", method=RequestMethod.GET)
	public void login(ModelMap model){
	}

	@RequestMapping(value="/dologin", method=RequestMethod.POST)
	@ResponseBody
	public JsonResultDO loginajax(HttpServletRequest request, HttpServletResponse response, String verifyCode, UserDo user){
		JsonResultDO result = new JsonResultDO();
		ValidateException error = new ValidateException();

		if(!StringUtils.hasText(user.getUserPassword()) || !StringUtils.hasText(user.getUserMobile())){
			error.reject("base.login.error.noneinput");
		}else {
			if(requiredVerifyCode && !checkVerifyCode(request, verifyCode)){
				error.reject("base.login.error.verifycode");
			}else {
				loginIn(request, response, user, error);
			}
		}

		if(error.hasErrors()){
			result.addAttribute("validate", error);
			result.setError(R.message.msgcode_error_validate);
		}

		return result;
	}

	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(HttpServletRequest request, HttpServletResponse response, String verifyCode, UserDo user, ModelMap model){
		ValidateException result = new ValidateException();
		model.addAttribute("error", result);

		if((user.getUserCode() == null || user.getUserTenantCode() == null) && user.getUserMobile()==null){
			result.reject("base.login.error.noneinput");
			return "/login";
		}

		/*先进行验证码校验*/
		Map<String,Object> loginUser = null;
		if(requiredVerifyCode && !checkVerifyCode(request, verifyCode)){
			result.reject("base.login.error.verifycode");
		}else {
			loginUser = loginIn(request, response, user, result);
		}

		if(result.hasErrors()){
			return "/login";
		}else if(StringUtils.hasText(forceModifypswForward) && (Boolean)loginUser.get("userForcemodifypsw")){
			return "redirect:"+forceModifypswForward;
		}else{
			return "redirect:/main";
		}

	}

	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response, UserDo user){
		loginOut(request, response);
		return "redirect:/login";
	}


	/* (non-Javadoc)
	 * @see cn.evun.sweet.core.bussinesshelper.LoginAndOutService#checkLoginUser(java.lang.Object, cn.evun.sweet.core.exception.ValidateException)
	 */
	@Override
	protected Map<String, Object> checkLoginUser(Object userParam, ValidateException result) {
		Map<String,Object> userMap =  null;
		UserDo loginUser = (UserDo)userParam;
		SqlMapper sqlExcutor = new SqlMapper(sqlSession);
		try{
			if(StringUtils.hasText(loginUser.getUserMobile())){
				userMap = sqlExcutor.selectOne(getCheckUserByMobileSql(),loginUser.getUserMobile());
			}else {
				userMap = sqlExcutor.selectOne(getCheckUserByCodeSql(), loginUser);//使用Mybatis直接执行sql语句
			}
			userMap = sqlExcutor.mapKeyUnderscoreToCamelCase(userMap);
		}catch(TooManyResultsException e){
			/*用户code或手机号不唯一，帐号异常。*/
			result.reject("base.login.error.account");
			return null;
		}

		long remainTime = 0;
		if(userMap == null){
			/*账号不存在*/
			result.reject("base.login.error.notexist");
		}else if(lockInputCount > 0 && lockPeriod > 0 && userMap.get("userLocktime")!=null && (
				(remainTime = lockPeriod * 60000 -
						(System.currentTimeMillis()-((java.util.Date)userMap.get("userLocktime")).getTime())) > 0)){
			/*账号被锁定*/
			result.reject("base.login.error.lock", new String[]{String.valueOf(remainTime / 60000)} ,"");
		}else if(userMap.get("tenantStatus")==null  || !"1".equals(String.valueOf(userMap.get("tenantStatus")))
				|| userMap.get("orgStatus")==null || !"1".equals(String.valueOf(userMap.get("orgStatus")))
				|| userMap.get("userStatus")==null || !"1".equals(String.valueOf(userMap.get("userStatus")))){
			/*账号或组织被禁用*/
			result.reject("base.login.error.disabled");
		}else if(userMap.get("userPassword")!=null && !((String)userMap.get("userPassword")).equals(
				DigestUtil.shaDigestAsHex(loginUser.getUserPassword().getBytes()))){
			/*密码不匹配，注意：如果密码字段为null，则认为是无需密码。密码为SHA加密方式*/
			result.reject("base.login.error.password");
			/*记录错误次数，或锁定账户*/
			if(lockPeriod > 0 && lockInputCount > 0){
				String redisKey = R.cache.cache_login_errorinput + loginUser.getUserMobile();
				try{
					while (!DistributedLockUtil.pAcquireLock(redisKey + "_nx", 1500)){
					}
					Integer errCount = (Integer) RedisTemplateUtil.getCompat(redisKey);
					errCount = errCount == null ? 1 : errCount + 1;
					if (errCount >= lockInputCount) {
						sqlExcutor.update(getLockUserSql(userMap.get("userId").toString()));//锁定
						RedisTemplateUtil.delete(redisKey);//清除
					} else {
						RedisTemplateUtil.set(redisKey, errCount);
					}
				}
				finally {
					DistributedLockUtil.releaseLock(redisKey + "_nx");
				}
			}
		}else if(lockInputCount > 0 && lockPeriod > 0 && userMap.get("userLocktime")!=null){
			sqlExcutor.update(getUnlockUserSql(userMap.get("userId").toString()));//解锁
		}

		if(result.hasErrors()){
			return null;
		}else {
			CacheAccessor.doEvict(R.cache.cache_login_errorinput+loginUser.getUserCode());//清除
			return userMap;
		}

	}

	/* (non-Javadoc)
	 * @see cn.evun.sweet.core.bussinesshelper.LoginAndOutService#loadUserResources(java.util.Map, cn.evun.sweet.core.context.Context)
	 */
	@Override
	protected void loadUserResources(Map<String, Object> userMap, HttpSession session) {
		UserDo user = new UserDo();
		OrgDo org = new OrgDo();
		TenantDo tenant = new TenantDo();
		try {
			BeanUtils.populate(user, userMap);
			BeanUtils.populate(org, userMap);
			BeanUtils.populate(tenant, userMap);
		} catch (Exception e) {
			//由于map的数据比bean多，每次pop都会遍历map，不匹配的属性会出异常。这里不做任何处理。
		}
		session.setAttribute(R.session.user_info, user);
		session.setAttribute(R.session.org_info, org);
		session.setAttribute(R.session.tenant_info, tenant);
	}

	private String getCheckUserByMobileSql(){
		return "SELECT u.user_id, u.user_code, u.user_name, u.user_password, "
				+ "u.user_forcemodifypsw, u.user_mobile,u.user_email,u.user_img,u.user_sign,u.user_role,u.user_init,"
				+ "u.user_tenant_id,u.user_org_id,u.user_modifiedon,u.user_createdon,u.user_status, u.user_locktime, "
				+ "t.tenant_id, t.tenant_parent_id, t.tenant_code, "
				+ "t.tenant_shortname, t.tenant_name, t.tenant_createdon, t.tenant_modifiedon, t.tenant_status, "
				+ "o.org_id, o.org_parent_id, o.org_code, o.org_name, o.org_createdon, o.org_modifiedon, o.org_status "
				+ "FROM sweet_auth_tenant t, sweet_auth_user u "
				+ "LEFT JOIN  sweet_auth_org o "
				+ "ON (u.user_org_id  = o.org_id and o.org_isdel=0 ) "
				+ "WHERE u.user_tenant_id = t.tenant_id  and u.user_isdel=0 and t.tenant_isdel=0 and u.user_mobile=#{parameter}";
	}

	private String getCheckUserByCodeSql(){
		return "SELECT u.user_id, u.user_code, u.user_name, u.user_password, "
				+ "u.user_forcemodifypsw, u.user_mobile,u.user_email,u.user_img,u.user_sign,u.user_role,u.user_init,"
				+ "u.user_tenant_id,u.user_org_id,u.user_modifiedon,u.user_createdon,u.user_status, u.user_locktime, "
				+ "t.tenant_id, t.tenant_parent_id, t.tenant_code, "
				+ "t.tenant_shortname, t.tenant_name, t.tenant_createdon, t.tenant_modifiedon, t.tenant_status, "
				+ "o.org_id, o.org_parent_id, o.org_code, o.org_name, o.org_createdon, o.org_modifiedon, o.org_status "
				+ "FROM sweet_auth_tenant t, sweet_auth_user u "
				+ "LEFT JOIN  sweet_auth_org o "
				+ "ON (u.user_org_id  = o.org_id and o.org_isdel=0 ) "
				+ "WHERE u.user_tenant_id = t.tenant_id  and u.user_isdel=0 and t.tenant_isdel=0 "
				+ "and u.user_code=#{userCode} and t.tenant_code=#{userTenantCode}";
	}

	private String getLockUserSql(String userId){
		return "update sweet_auth_user set user_locktime=sysdate() where user_id="+userId;
	}

	private String getUnlockUserSql(String userId){
		return "update sweet_auth_user set user_locktime=null where user_id="+userId;
	}

}
