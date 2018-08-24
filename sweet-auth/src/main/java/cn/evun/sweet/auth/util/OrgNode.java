package cn.evun.sweet.auth.util;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import cn.evun.sweet.auth.model.OrgDo;
import cn.evun.sweet.common.datastructure.TreeNode;

/**
 * 组织机构的树结构的存储实现
 *
 * @author yangw
 * @since 1.0.0
 */
public class OrgNode extends TreeNode{
	
	private static final long serialVersionUID = 1L;

	public OrgNode(Long id, String name) {
		super(id, name);
	}
	
	public OrgNode(Long id, String name, Boolean leaf) {
		super(id, name, leaf);
	}
	
	public String  orgCode;   
	public Long    orgResponsibleId;
	public Integer orgSerialno;
	public Date    orgCreatedon;     	
	public Date    orgModifiedon;    	
	
	/**
     * 生成组织树
     * 
     * @param orglist 原始的组织数据列表
     * @param org 根组织的信息
     */
    public static OrgNode genOrgNode(List<OrgDo> orglist, OrgDo org){
    	if(org == null || org.getOrgIsdel()) {
            return null;
        }
    	
    	/*创建当前节点*/
    	OrgNode node = new OrgNode(org.getOrgId(), org.getOrgName());
    	BeanUtils.copyProperties(org, node);
    	node.setParentId(org.getOrgParentId());
    	if(org.getOrgId() == null){
    		node.setId(2L);//2为虚构的顶级部门的ID
    	}
    	   
    	/*生成子节点*/
    	for(OrgDo orgDo : orglist){
    		if(orgDo.getOrgParentId() != null && node.getId().longValue() == orgDo.getOrgParentId().longValue()){
    			OrgNode childNode = genOrgNode(orglist, orgDo);	
    			if(childNode != null){
    				node.addChild(childNode);
    			}
    		}
    	}
    	return node;
    }
    
	
	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public Long getOrgResponsibleId() {
		return orgResponsibleId;
	}

	public void setOrgResponsibleId(Long orgResponsibleId) {
		this.orgResponsibleId = orgResponsibleId;
	}

	public Date getOrgCreatedon() {
		return orgCreatedon;
	}

	public void setOrgCreatedon(Date orgCreatedon) {
		this.orgCreatedon = orgCreatedon;
	}

	public Date getOrgModifiedon() {
		return orgModifiedon;
	}

	public void setOrgModifiedon(Date orgModifiedon) {
		this.orgModifiedon = orgModifiedon;
	}

	public Integer getOrgSerialno() {
		return orgSerialno;
	}
	public void setOrgSerialno(Integer orgSerialNo) {
		this.orgSerialno = orgSerialNo;
	}
}
