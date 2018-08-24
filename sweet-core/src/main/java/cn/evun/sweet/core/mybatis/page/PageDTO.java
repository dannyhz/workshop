/*
 * Copyright 2015-2019 Evun Technology. 
 * 
 * This software is the confidential and proprietary information of
 * Evun Technology. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with evun.cn.
 */
package cn.evun.sweet.core.mybatis.page;

import java.io.Serializable;

/**
 * Page对象在使用fastjson反序列化时，无法转换其附属的分页信息。所以单独使用DTO传输给前端
 *
 * @author yangw
 * @since 1.0.0
 */
public class PageDTO implements Serializable {

	private static final long serialVersionUID = 7931872027782602140L;

	/**页码，从1开始*/
    public int pageNum;
    
    /**页面大小*/
    public int pageSize;
    
    /**起始行*/
    public int startRow;
    
    /**末行*/
    public int endRow;
    
    /**总数*/
    public long total;
    
    /**总页数*/
    public int pages;

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() {
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}
       
}
