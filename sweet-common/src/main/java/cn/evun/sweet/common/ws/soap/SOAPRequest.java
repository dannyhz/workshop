/*
 * Copyright 2009-2012 Evun Technology. 
 * 
 * This software is the confidential and proprietary information of
 * Evun Technology. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with evun.cn.
 */
package cn.evun.sweet.common.ws.soap;

import java.util.LinkedHashMap;

import javax.xml.soap.SOAPMessage;

/**
 * <p>目标webservice方法的soap请求报文封装接口。</p>  
 * 
 * @author  yangw
 * @created 2013-11-12 下午5:20:18
 * @since   v1.3.2
 */
public interface SOAPRequest{

	/**
	 * 根据配置生成soap请求报文，调用时需要为其具体的实现注入相关的基础内容<br>
	 * @param method 目标方法名称
	 * @param params 所需参数，LinkedHashMap和xml报文参数结构对应，不限层次
	 * @return 
	 */
	SOAPMessage getSoapRequest(String method, LinkedHashMap<String,Object> params);
}
