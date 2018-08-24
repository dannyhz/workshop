package cn.evun.sweet.auth.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import cn.evun.sweet.auth.dao.ItemMapper;
import cn.evun.sweet.auth.dao.ItemPckMapper;
import cn.evun.sweet.auth.dao.MenuMapper;
import cn.evun.sweet.auth.dao.OrgMapper;
import cn.evun.sweet.auth.dao.PostMapper;
import cn.evun.sweet.auth.dao.PostPckMapMapper;
import cn.evun.sweet.auth.dao.TenantMapper;
import cn.evun.sweet.auth.dao.TenantMenuUnvisibleMapper;
import cn.evun.sweet.auth.dao.UserMapper;
import cn.evun.sweet.auth.dao.UserPckMapMapper;
import cn.evun.sweet.auth.model.ItemDo;
import cn.evun.sweet.auth.model.ItemPckDo;
import cn.evun.sweet.auth.model.MenuDo;
import cn.evun.sweet.auth.model.OrgDo;
import cn.evun.sweet.auth.model.PostDo;
import cn.evun.sweet.auth.model.PostPckMapDo;
import cn.evun.sweet.auth.model.TenantDo;
import cn.evun.sweet.auth.model.TenantMenuUnvisibleDo;
import cn.evun.sweet.auth.model.UserDo;
import cn.evun.sweet.auth.model.UserPckMapDo;
import cn.evun.sweet.auth.util.MenuNode;
import cn.evun.sweet.auth.util.OrgNode;
import cn.evun.sweet.auth.util.UserContext;
import cn.evun.sweet.common.datastructure.TreeNode;
import cn.evun.sweet.common.util.Assert;
import cn.evun.sweet.common.util.CollectionUtils;
import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.common.util.web.WebUtils;
import cn.evun.sweet.core.bussinesshelper.GeneralObjectAccessService;
import cn.evun.sweet.core.cache.CacheAccessor;
import cn.evun.sweet.core.cas.ContextHolder;
import cn.evun.sweet.core.cas.SSOConfig;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.mybatis.general.Example;
import cn.evun.sweet.core.service.RegistyMethod;
import cn.evun.sweet.core.service.RegistyService;
import cn.evun.sweet.core.spring.SpringContext;

/**
 * 权限模块（用户、组织、租户、菜单、功能、功能组等）相关服务.
 * 
 *
 * @author yangw
 * @since 1.0.0
 */
//@CacheRequired
//@CacheConfig(cacheNames={R.cache.cache_region_main}) 
@RegistyService
public class AuthService {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	@Resource
	private GeneralObjectAccessService objAccessService;
	@Resource
	private MenuMapper menuMapper;
	@Resource
	private OrgMapper orgMapper;
	@Resource
	private PostMapper postMapper;
	@Resource
	private TenantMapper tenantMapper;
	@Resource
	private TenantMenuUnvisibleMapper tenantMenuUnvisibleMapper;
	@Resource
	private UserMapper userMapper;
	@Resource
	private ItemMapper itemMapper;
	@Resource
	private ItemPckMapper itemPckMapper;
	@Resource
	private UserPckMapMapper userPckMapMapper;
	@Resource
	private PostPckMapMapper postPckMapMapper;
	
	/**租户不可见菜单表的数据在缓存中的位置*/
	public final static String unvisible_menu_catche_key = "unvisi_menu_catche";
	
	/**菜单表的数据在缓存中的位置*/
	public final static String menu_catche_key = "menu_catche";

	/**
	 * 访问权限的判断逻辑 
	 */
	public boolean authCheck(HttpServletRequest request) {
		String reqURL = WebUtils.resolutionUrl(request.getRequestURI(), request.getContextPath());
		if(inIgnoreURL(reqURL)){//把免权限过滤的逻辑提前
			return true;
		}
		UserDo currentUser = (UserDo)request.getSession(false).getAttribute(R.session.user_info);
		if(currentUser.getUserRole().intValue() == UserDo.USER_ROLE_SYSTEM || reqURL.startsWith("direct")){
			return true;//羡慕万能的平台管理员啊
		}
		for(MenuDo menu : getAllMenuRecord()){ 
			if(menu.getMenuStatus() && menu.getItemURL()!=null && matchUrl(reqURL,menu.getItemURL())){//&& menu.getItemURL().equalsIgnoreCase(reqURL) ){
				if((StringUtils.hasText(menu.getSweetAuthItem().getItemRequestmethod())
						&& menu.getSweetAuthItem().getItemRequestmethod().equalsIgnoreCase(request.getMethod()))
						|| !StringUtils.hasText(menu.getSweetAuthItem().getItemRequestmethod())){//Method的匹配
					if(menu.getMenuVisibility()){
						return true;//公共资源无需过滤
					}else if(ifUnvisibleMenu(menu, getTenantUnvisibleMenu(currentUser.getUserTenantId()))
							|| menu.getMenuAuthLevel().intValue() == UserDo.USER_ROLE_SYSTEM){ 
						return false;//不可见菜单或平台级菜单果断阻止访问。
					}else if(currentUser.getUserRole().intValue() == UserDo.USER_ROLE_ADMIN
							|| (currentUser.getUserRole().intValue() == UserDo.USER_ROLE_NORMAL
									&& getCurrentUserMenuTree().findChild(menu.getMenuId()) != null)){
						return true;//租户管理员随便过，普通用户需要匹配自己的菜单。
					}
					continue;
				}
			}
		}
		return false;
	}	
	
	/**
	 * 匹配请求的url
	 * 
	 * @param reqUrl 请求的url
	 * @param localUrl  权限url 
	 */
	public static boolean matchUrl(String reqUrl, String localUrl) {
		boolean flag = false;
		if (StringUtils.hasText(reqUrl) && StringUtils.hasText(localUrl)) {
			if (localUrl.contains("{") || localUrl.contains("}")) { // restful // api
				String[] reqURLs = reqUrl.split("/");
				String[] localUrls = localUrl.split("/");
				if (reqURLs.length == localUrls.length) {
					StringBuilder sbLocal = new StringBuilder();
					StringBuilder sbReq = new StringBuilder();
					for (int i = 0; i < localUrls.length; i++) {
						String localURL = localUrls[i];
						if (!localURL.contains("{") && !localURL.contains("}")) {
							sbLocal.append(localURL);
							sbReq.append(reqURLs[i]);
						}
					}
					flag = sbLocal.toString().equalsIgnoreCase(sbReq.toString());
				}
			} else {
				flag = localUrl.equalsIgnoreCase(reqUrl);
			}
		}
		return flag;
	}

	/**
	 * Redis管理下的Session过期处理。由KeyExpiresMessageListener调用
	 * @param sessionId
	 */
	@RegistyMethod("session")
	public void sessionExpired(String sessionId){
		LOGGER.debug("Session[{}] epired!", sessionId);
	}
	
	/**
	 * 新增租户。包括建立租户记录以及租户的初始化管理员(一个事务内)。
	 * 注意：服务调用者需要保证数据的有效性。
	 */
	public void addTenant(TenantDo tenant){		
		tenantMapper.insertSelective(tenant);
		UserDo user = new UserDo();
		user.setUserCode("admin");
		user.setUserName(tenant.getTenantContacter());
		user.setUserMobile(tenant.getTenantPhone());
		user.setUserRole(UserDo.USER_ROLE_ADMIN);
		user.setUserInit(true);
		user.setUserTenantId(tenant.getTenantId());
		user.initDefaultInfo();
		userMapper.insertSelective(user);
	}
	
	public void modifyTenant(TenantDo tenant){		
		tenant.setTenantModifiedon(new Date());			
		tenantMapper.updateByPrimaryKeySelective(tenant);
	}
	
	public void modifyTenantStatus(Long tenantId, Boolean status){		
		TenantDo tenant = new TenantDo();
		tenant.setTenantId(tenantId);
		tenant.setTenantStatus(status);
		tenant.setTenantModifiedon(new Date());
		tenantMapper.updateByPrimaryKeySelective(tenant);
	}
	
	public void modifyTenantPhone(Long tenantId, String tenantPhone){		
		TenantDo tenant = new TenantDo();
		tenant.setTenantId(tenantId);
		tenant.setTenantPhone(tenantPhone);
		tenant.setTenantModifiedon(new Date());
		tenantMapper.updateByPrimaryKeySelective(tenant);
	}
	
	/**
	 * 删除租户，包括组织、用户数据。软删除方式。
	 */
	public void delTenant(Long tenantId){
		TenantDo tenant = new TenantDo();
		tenant.setTenantIsdel(true);
		Example example = new Example(TenantDo.class);
		example.createCriteria().andEqualTo("tenantId", tenantId);
		tenantMapper.updateByExampleSelective(tenant, example);
		
		example = new Example(OrgDo.class);
		example.createCriteria().andEqualTo("orgTenantId", tenantId);
		OrgDo org = new OrgDo();
		org.setOrgIsdel(true);
		orgMapper.updateByExampleSelective(org, example);
		
		example = new Example(UserDo.class);
		example.createCriteria().andEqualTo("userTenantId", tenantId);
		UserDo user = new UserDo();
		user.setUserIsdel(true);
		userMapper.updateByExampleSelective(user, example);
	}
	
	/**
	 * 查询租户不可见菜单项。
	 */
	public List<TenantMenuUnvisibleDo> getTenantUnvisibleMenu(Long tenantId){
		Assert.notNull(tenantId, "tenantid must not be null.");
		List<TenantMenuUnvisibleDo> unVisibleMenu = getAllUnvisibleMenu();
		List<TenantMenuUnvisibleDo> result = new ArrayList<TenantMenuUnvisibleDo>();
		if(!CollectionUtils.isEmpty(unVisibleMenu)){
			for(TenantMenuUnvisibleDo menu : unVisibleMenu){
				if(menu.getTenantId().longValue() == tenantId.longValue()){
					result.add(menu);
				}
			}
		}
		return result;
	}
	
	/**
	 * 查询所有的租户不可见菜单项。
	 * 如果没有则创建到缓存中，否则直接读取缓存中的资源
	 */
	//@Cacheable(key = "#root.target.unvisible_menu_catche_key")
	@SuppressWarnings("unchecked")
	public List<TenantMenuUnvisibleDo> getAllUnvisibleMenu(){
		Object result = CacheAccessor.doGet(unvisible_menu_catche_key);
		if(result == null){
			result = objAccessService.query(new TenantMenuUnvisibleDo());
			if(result == null){
				result = new ArrayList<TenantMenuUnvisibleDo>();
			}
			CacheAccessor.doPut(unvisible_menu_catche_key, result);
		}
		return (List<TenantMenuUnvisibleDo>)result;
	}
	
	/**
	 * 设置租户不可见菜单项（覆盖原有设置）。
	 */
	//@CachePut(key = "#root.target.unvisible_menu_catche_key")
	public void addTenantUnvisibleMenu(Long tenantId, Long[] menuIds){
		TenantMenuUnvisibleDo unVisibleMenu = new TenantMenuUnvisibleDo();
		unVisibleMenu.setTenantId(tenantId);
		/*先删除原有的菜单项*/
		tenantMenuUnvisibleMapper.delete(unVisibleMenu);
		/*小数据量，直接使用循环插入。*/
		for(int i=0; i<menuIds.length; i++){
			unVisibleMenu.setId(null);
			unVisibleMenu.setMenuId(menuIds[i]);
			tenantMenuUnvisibleMapper.insertSelective(unVisibleMenu);
		}
		CacheAccessor.doPut(unvisible_menu_catche_key, objAccessService.query(new TenantMenuUnvisibleDo()));//最新的数据放入到缓存中
	}
	
	/**
	 * 获取租户的组织树结构。
	 */
	public OrgNode getTenantOrgTree(){
		OrgDo org = new OrgDo();
		org.setOrgTenantId(UserContext.getTenantId());
		org.setOrgIsdel(false);
		List<OrgDo> orgList = objAccessService.query(org);
		org.setOrgName(UserContext.getTenant().getTenantName());
		return OrgNode.genOrgNode(orgList, org);
	}
	
	/**
	 * 批量新增子组织机构。
	 */
	public List<OrgDo> addOrgWithParent(Long parentId, String[] orgNames){
		List<OrgDo> result = new ArrayList<OrgDo>();
		OrgDo org = new OrgDo();
		org.setOrgTenantId(UserContext.getTenantId());
		org.setOrgId(parentId);
		if(parentId.longValue()==2 || !CollectionUtils.isEmpty(objAccessService.query(org))){//验证数据有效性
			org.setOrgParentId(parentId);		
			org.setOrgStatus(true);
			org.setOrgIsdel(false);
			int childCount = objAccessService.count(org);			
			for(int i=0; i<orgNames.length; i++){
				org.setOrgId(null);
				org.setOrgSerialno(++childCount);
				org.setOrgName(orgNames[i]);
				orgMapper.insertSelective(org);
				OrgDo temp = new OrgDo();
				BeanUtils.copyProperties(org, temp);
				result.add(temp);
			}
		}
		return result;
	}
	
	public OrgDo addOrg(OrgDo org){
		orgMapper.insertSelective(org);
		return org;
	}
	
	public void modifyOrg(OrgDo org){
		Example example = new Example(OrgDo.class);			
		example.createCriteria().andEqualTo("orgTenantId", org.getOrgTenantId()).andEqualTo("orgId", org.getOrgId());
		org.setOrgModifiedon(new Date());
		orgMapper.updateByExampleSelective(org, example);
	}
	
	/**
	 * 批量更新用户的组织部门
	 */
	public void modifyOrgUsers(Long orgId, List<Object> userids){
		Example example = new Example(UserDo.class);
		example.createCriteria().andIn("userId", userids);
		UserDo user = new UserDo();
		user.setUserOrgId(orgId);
		user.setUserModifiedon(new Date());
		userMapper.updateByExampleSelective(user, example);
	}
	
	
	/**
	 * 删除组织及其下属子组织。
	 * 注意：被解除组织关系的用户，其组织字段不会为null，而是为0.
	 * 
	 * @param userDel TRUE,同时删除其下所有用户;FALSE,同时断开其下所有用户的组织关系 
	 */
	public void delOrg(Long orgId, final Boolean userDel){
		Assert.notNull(orgId, "orgid must not be null.");
		OrgNode orgTree = getTenantOrgTree();
		orgTree = (OrgNode)orgTree.findChild(orgId);//找到要删除的节点
		orgTree.eachAllChild(orgTree, new TreeNode.Callback(){		
			@Override
			public void excute(TreeNode node) {
				orgMapper.deleteByPrimaryKey(node.getId());
				Example example = new Example(UserDo.class);
				example.createCriteria().andEqualTo("userOrgId", node.getId());
				UserDo user = new UserDo();
				user.setUserOrgId(2L);//解除组织与用户的关系
				if(userDel==null || userDel){
					user.setUserIsdel(true);
				}
				userMapper.updateByExampleSelective(user, example);
			}
		});		
	}
	
	public PostDo addPost(PostDo post){
		postMapper.insertSelective(post);
		return post;
	}
	
	public void modifyPostName(PostDo post){
		Assert.notNull(post.getPostId(), "postid required for modify post.");
		post.setPostModifiedon(new Date());
		postMapper.updateByPrimaryKeySelective(post);
	}
	
	public void delPost(Long postId){	
		Assert.notNull(postId, "postId must not be null.");
		PostDo post = new PostDo();
		post.setPostId(postId);		
		post.setPostIsdel(true);
		postMapper.updateByPrimaryKeySelective(post);
		PostPckMapDo ppm = new PostPckMapDo();
		ppm.setPostId(postId);
		postPckMapMapper.delete(ppm);
	}
	
	public void addPostItemPcks(Long postId, Long[] pckIds){
		Assert.notNull(postId, "postId must not be null.");
		PostPckMapDo ppm = new PostPckMapDo();
		ppm.setPostId(postId);
		postPckMapMapper.delete(ppm);	//先删除原有项	
		for(int i=0; i<pckIds.length; i++){
			ppm.setId(null);
			ppm.setPckId(pckIds[i]);
			postPckMapMapper.insertSelective(ppm);
		}
	}
	
	
	/**
	 * 删除用户(软删除方式)及其功能组的关联关系。
	 */
	public void delUser(Long userId){	
		Assert.notNull(userId, "userid must not be null.");
		Example example = new Example(UserDo.class);
		example.createCriteria().andEqualTo("userId", userId).andEqualTo("userInit", Boolean.FALSE);
		UserDo user = new UserDo();
		user.setUserIsdel(true);
		userMapper.updateByExampleSelective(user, example);
		UserPckMapDo upm = new UserPckMapDo();
		upm.setUserId(userId);
		userPckMapMapper.delete(upm);
	}
	
	public UserDo addUser(UserDo user){	
		user.initDefaultInfo();
		userMapper.insertSelective(user);
		return user;
	}
	
	public void modifyUser(UserDo user){
		Assert.notNull(user.getUserId(), "userid required for modifyuser.");
		user.setUserModifiedon(new Date());
		userMapper.updateByPrimaryKeySelective(user);
	}
	
	/**
	 * 查询用户的功能组列表
	 */
	public List<UserPckMapDo> getUserItemPcks(Long userId){
		Assert.notNull(userId, "userid must not be null.");
		UserPckMapDo upm = new UserPckMapDo();
		upm.setUserId(userId);
		List<UserPckMapDo> result = objAccessService.queryWithRelation(upm);
		return result==null?new ArrayList<UserPckMapDo>():result;
	}
	
	/**
	 * 查询用户岗位的功能组列表
	 */
	public List<PostPckMapDo> getUserPostItemPcks(Long userId){
		Assert.notNull(userId, "userid must not be null.");
		UserDo user = userMapper.selectByPrimaryKey(userId);		
		if(null != user.getUserPostId()){
			PostPckMapDo ppm = new PostPckMapDo();
			ppm.setPostId(user.getUserPostId());
			List<PostPckMapDo> result = objAccessService.queryWithRelation(ppm);
			return result==null?new ArrayList<PostPckMapDo>():result;
		}
		return new ArrayList<PostPckMapDo>();
	}
	
	/**
	 * 设置用户关联的功能组（覆盖原有设置）
	 */
	public void addUserItemPcks(Long userId, Long[] pckIds){
		Assert.notNull(userId, "userid must not be null.");
		UserPckMapDo upm = new UserPckMapDo();
		upm.setUserId(userId);
		UserDo result = userMapper.selectByPrimaryKey(userId);
		upm.setTenantId(result.getUserTenantId());
		userPckMapMapper.delete(upm);	//先删除原有项	
		for(int i=0; i<pckIds.length; i++){
			upm.setId(null);
			upm.setPckId(pckIds[i]);
			userPckMapMapper.insertSelective(upm);
		}
	}
	
	/**
	 * 当前用户的菜单树
	 */
	public MenuNode getCurrentUserMenuTree(){
		MenuNode menuTree = UserContext.getUserMenu();
		if(menuTree == null){
			if(UserContext.getUser().getUserRole().intValue() == UserDo.USER_ROLE_SYSTEM){
				menuTree = getGlobalMenuTree();
			}else if(UserContext.getUser().getUserRole().intValue() == UserDo.USER_ROLE_ADMIN){
				menuTree = getTenantMenuTree(UserContext.getTenantId());
			}else if(UserContext.getUser().getUserRole().intValue() == UserDo.USER_ROLE_NORMAL){
				menuTree = getUserMenuTree(UserContext.getUserId(), UserContext.getTenantId());
			}
			ContextHolder.getSession().setAttribute(R.session.menu_info, menuTree);
		}
		return menuTree;
	}
	
	/**
	 * 普通用户的菜单树
	 */
	public MenuNode getUserMenuTree(Long userId, Long tenantId){
		MenuDo menu = new MenuDo();
		menu.setMenuName(UserContext.getUser().getUserName());	
		return MenuNode.genMenuNode(getUserMenuList(userId, tenantId), menu, 0);
	}
	
	/**
	 * menuSerialNo 小的排在前面，null表示最大值
	 * @param menus
	 */
	private void sortMenuBySerialNo(List<MenuDo> menus){
		if(menus == null){
			return;
		}
		Collections.sort(menus, new Comparator<MenuDo>() {

			@Override
			public int compare(MenuDo o1, MenuDo o2) {
				Integer serialNo1 = o1.getMenuSerialNo(),
						serialNo2 = o2.getMenuSerialNo(),
						max = 999999999;
				if(serialNo1 == null){
					serialNo1 = max;
				}
				if(serialNo2 == null){
					serialNo2 = max;
				}
				int sort = 0;
				if(serialNo1.intValue() > serialNo2.intValue() ){
					sort = 1;
				} else if(serialNo1.intValue() < serialNo2.intValue() ){
					sort = -1;
				}
				return sort;
			}
		});
	}
	
	/**
	 * 用户的菜单列表
	 */
	public List<MenuDo> getUserMenuList(Long userId, Long tenantId){
		Assert.notNull(userId, "userid must not be null.");
		Set<String> useMenuIds = new HashSet<String>(); //用户所有的菜单权限
		for(UserPckMapDo userpck : getUserItemPcks(userId)){//用户的功能组列表
			String[] menuArray = StringUtils.delimitedListToStringArray(
					userpck.getSweetAuthItemPck().getPckMenuId(), ",");//每个功能组包含的功能
			for(int i=0; i<menuArray.length; i++){
				if(StringUtils.hasText(menuArray[i])){
					useMenuIds.add(menuArray[i]);//对所有功能求并集
				}
			}
		}
		for(PostPckMapDo postpck : getUserPostItemPcks(userId)){//用户岗位的功能组列表
			String[] menuArray = StringUtils.delimitedListToStringArray(
					postpck.getSweetAuthItemPck().getPckMenuId(), ",");//每个功能组包含的功能
			for(int i=0; i<menuArray.length; i++){
				if(StringUtils.hasText(menuArray[i])){
					useMenuIds.add(menuArray[i]);//对所有功能求并集
				}
			}
		}
		
		List<MenuDo> result = new ArrayList<MenuDo>();
		List<MenuDo> allMenus = getAllMenuRecord();
		List<TenantMenuUnvisibleDo> unvisible = getTenantUnvisibleMenu(tenantId);
		for(MenuDo menu : allMenus){ 
			if((menu.getMenuVisibility() || useMenuIds.contains(menu.getMenuId().toString()))
					&& menu.getMenuStatus() && !ifUnvisibleMenu(menu, unvisible)
					&& menu.getMenuAuthLevel() != MenuDo.MENU_AUTHLEVEL_SYSTEM.intValue()){
				result.add(menu); //用户菜单列表
			}
		}
		sortMenuBySerialNo(result);
		return result;
	}
	
	/**
	 * 租户的菜单树
	 */
	public MenuNode getTenantMenuTree(Long tenantId){		
		MenuDo menu = new MenuDo();
		menu.setMenuName(UserContext.getTenant().getTenantName());	
		return MenuNode.genMenuNode(getTenantMenuList(tenantId), menu, 0);
	}
	
	/**
	 * 租户的菜单列表
	 */
	public List<MenuDo> getTenantMenuList(Long tenantId){
		Assert.notNull(tenantId, "tenantid must not be null.");
		List<MenuDo> result = new ArrayList<MenuDo>();
		List<MenuDo> allMenus = getAllMenuRecord();
		List<TenantMenuUnvisibleDo> unvisible = getTenantUnvisibleMenu(tenantId);
		for(MenuDo menu : allMenus){ 
			if(menu.getMenuAuthLevel() != MenuDo.MENU_AUTHLEVEL_SYSTEM.intValue()
					&& menu.getMenuStatus() && !ifUnvisibleMenu(menu, unvisible)){
				result.add(menu);
			}
		}
		sortMenuBySerialNo(result);
		return result;
	}
	
	/**
	 * 全局的菜单树(包含被禁用的菜单)
	 */
	public MenuNode getGlobalMenuTree(){
		MenuDo menu = new MenuDo();
		menu.setMenuName("global_menu");	
		return MenuNode.genMenuNode(getAllMenuRecord(), menu, 0);
	}
	
	/**
	 * 查询所有的菜单项（包含被禁用的菜单）。
	 */
	//@Cacheable(key = "#root.target.menu_catche_key")
	@SuppressWarnings("unchecked")
	public List<MenuDo> getAllMenuRecord(){
		Object result = CacheAccessor.doGet(menu_catche_key);
		if(result == null){
			result = objAccessService.queryWithRelation(new MenuDo());
			sortMenuBySerialNo((List<MenuDo>)result);
			CacheAccessor.doPut(menu_catche_key, result);
		}
		return (List<MenuDo>)result;
	}
	
	/**
	 * 新增菜单项。
	 */
	//@CachePut(key = "#root.target.menu_catche_key")
	public void addMenu(MenuDo menu){
		menuMapper.insertSelective(menu);
		LOGGER.debug("new menu[id:{},name:{},herf:{},item:{}] created.", 
				menu.getMenuId(),menu.getMenuName(), menu.getMenuHref(), menu.getMenuItemId());
		/*更新缓存*/
		List<MenuDo> menuList = getAllMenuRecord();
		menuList.add(objAccessService.queryByIdWithRelation(MenuDo.class, menu.getMenuId()));
		CacheAccessor.doPut(menu_catche_key, menuList);
	}
	
	/**
	 * 更新菜单项。
	 */
	//@CachePut(key = "#root.target.menu_catche_key")
	public void modifyMenu(MenuDo menu){
		menuMapper.updateByPrimaryKeySelective(menu);
		LOGGER.debug("modified menu[id:{},name:{},herf:{},item:{}] success.", 
				menu.getMenuId(),menu.getMenuName(), menu.getMenuHref(), menu.getMenuItemId());
		/*更新缓存*/
		List<MenuDo> menuList = getAllMenuRecord();
		menuList.remove(menu);
		menuList.add(objAccessService.queryByIdWithRelation(MenuDo.class, menu.getMenuId()));
		CacheAccessor.doPut(menu_catche_key, menuList);
	}
	
	/**
	 * 删除菜单项。
	 */
	//@CacheEvict(key = "#root.target.menu_catche_key")
	public void delMenu(Long menuId){
		MenuNode root = getGlobalMenuTree();
		MenuNode node = (MenuNode)root.findChild(menuId);
		node.eachAllChild(node, new TreeNode.Callback() {			
			@Override
			public void excute(TreeNode node) {
				menuMapper.deleteByPrimaryKey(node.getId());
			}
		});
		CacheAccessor.doEvict(menu_catche_key);
	}
	
	public List<String> queryItemTabList(){
		return itemMapper.selectItemTabList();
	}
	
	/**
	 * 更新功能项。
	 */
	public void modifyItem(ItemDo item){
		itemMapper.updateByPrimaryKeySelective(item);
		/*URL变动后需要更新缓存*/
		if(StringUtils.hasText(item.getItemUrl())){
			for(MenuDo menu : getAllMenuRecord()){
				if(menu.getMenuItemId()!=null && item.getItemId().longValue() == menu.getMenuItemId().longValue()){
					menu.getSweetAuthItem().setItemUrl(item.getItemUrl());
				}
			}
		}
	}
	
	public ItemDo addItem(ItemDo item){	
		itemMapper.insertSelective(item);
		return item;
	}
	
	/**
	 * 删除功能项。
	 */
	//@CachePut(key = "#root.target.menu_catche_key")
	public void delItem(Long itemId){
		itemMapper.deleteByPrimaryKey(itemId);
		Example example = new Example(MenuDo.class);
		example.createCriteria().andEqualTo("menuItemId", itemId);
		MenuDo m = new MenuDo();
		m.setMenuItemId(0L);//解除菜单与功能的关系
		menuMapper.updateByExampleSelective(m, example);
		/*更新缓存*/
		List<MenuDo> menuList = getAllMenuRecord();
		for(MenuDo menu : menuList){
			if(menu.getMenuItemId()!=null && itemId.longValue() == menu.getMenuItemId().longValue()){
				menu.setMenuItemId(null);
				menu.setSweetAuthItem(null);
			}
		}
		CacheAccessor.doPut(menu_catche_key, menuList);
	}
	
	/**
	 * 新增功能组（用户组）。
	 */
	public ItemPckDo addItemPck(ItemPckDo pck){
		itemPckMapper.insertSelective(pck);
		return pck;
	}
		
	/**
	 * 删除功能组（用户组）及其和用户的关联。
	 */
	public void delItemPck(Long pckId){
		itemPckMapper.deleteByPrimaryKey(pckId);
		UserPckMapDo upm = new UserPckMapDo();
		upm.setPckId(pckId);
		userPckMapMapper.delete(upm);
	}
	
	public void modifyItemPck(ItemPckDo pck){
		Assert.notNull(pck.getPckId(), "pckid required for modifyItemPck.");
		pck.setPckModifiedon(new Date());
		itemPckMapper.updateByPrimaryKeySelective(pck);
	}
	
	/**
	 * 设置用户关联的功能组（覆盖原有设置）
	 */
	public void addItemPckUsers(Long pckId, Long[] userIds){
		Assert.notNull(pckId, "pckid must not be null.");
		UserPckMapDo upm = new UserPckMapDo();
		upm.setPckId(pckId);
		userPckMapMapper.delete(upm);	//先删除原有项	
		for(int i=0; i<userIds.length; i++){
			upm.setId(null);
			upm.setUserId(userIds[i]);
			userPckMapMapper.insertSelective(upm);
		}
	}

	/**
	 * 判断菜单对某个租户是否是不可见的
	 */
	private boolean ifUnvisibleMenu(MenuDo menu, List<TenantMenuUnvisibleDo> unvisible){
		if(CollectionUtils.isEmpty(unvisible)){
			return false;
		}else {
			for(TenantMenuUnvisibleDo unvisiMenu : unvisible){
				if(unvisiMenu.getMenuId().longValue() == menu.getMenuId().longValue()){
					return true;
				}
			}
		}
		return false;
	}	
	
	/**
	 * 判断请求URL是否可以不做权限检查
	 */
	private boolean inIgnoreURL(String url) {
		String[] urlArr = SpringContext.getBean(SSOConfig.class).SESS_UNAUTHURL.split(";");
		if(StringUtils.hasText(url)) {
			for (int i = 0 ; i < urlArr.length ; i++) {
				if (url.equals(urlArr[i])) {
					return true;
				}
			}
		}
		return false;
	}
}
