package cn.evun.sweet.common.ws.soap;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import cn.evun.sweet.common.util.CollectionUtils;

/**
 * <p>SOAPRequest的缺省实现。</p>  
 * 
 * @author  yangw
 * @since   v1.0.0
 */
public class SOAPRequestImpl implements SOAPRequest{
	
	/**soap消息的style，见wsdl中的binding部分,默认RPC*/
	private String soapStyle = "rpc";
	
	/**document样式下使用的XSD元素声明，必须按层级顺序指定*/
	private LinkedHashMap<String, String> xsdMap ;
	
	/**解决跨平台调用时的请求识别问题*/
	private String soapAction;

	public SOAPRequestImpl() {	
	}
	
	public SOAPRequestImpl(String soapStyle) {
		if(null != soapStyle){
			this.soapStyle = soapStyle;
		}
	}

	public SOAPMessage getSoapRequest(String method, LinkedHashMap<String,Object> params){
		try {
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage message = messageFactory.createMessage();//创建soap请求
			SOAPPart soapPart = message.getSOAPPart();	        
			SOAPEnvelope envelope = soapPart.getEnvelope();
	        
			creatSoapHeader(envelope.getHeader());
			
	        //根据样式构建报文体
			if("rpc".equals(this.soapStyle)){
				creatRpcSoapStr(envelope, method, params);
			}else{
				creatDocumentSoapStr(envelope, method, params);
			}
			message.saveChanges();
			return message;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 默认不考虑header信息,需要时可以由子类覆盖该方法进行处理
	 * @param header
	 */
	protected void creatSoapHeader(SOAPHeader header){
		header.detachNode();
	}
	
	private void creatRpcSoapStr(SOAPEnvelope envelope, String method, LinkedHashMap<String,Object> params) throws SOAPException{
		SOAPElement bodyElement = envelope.getBody().addChildElement(method);
		creatRpcParam(bodyElement, params);
	}
	
	@SuppressWarnings("unchecked")
	private void creatRpcParam(SOAPElement element, LinkedHashMap<String,Object> params) throws SOAPException{
		if(null == params){
			return ;
		}
		for(Entry<String,Object> entry : params.entrySet()){
			SOAPElement e = element.addChildElement(entry.getKey());
			if(LinkedHashMap.class.isInstance(entry.getValue())){//有层级参数封装的情况
				creatRpcParam(e, (LinkedHashMap<String,Object>)entry.getValue());
			}else {
				e.addTextNode((String)entry.getValue());
			}
		}
	}
	
	private void creatDocumentSoapStr(SOAPEnvelope envelope, String method, LinkedHashMap<String,Object> params) throws SOAPException{
		if(CollectionUtils.isEmpty(xsdMap)){
			throw new IllegalArgumentException("No XSD elements dinfinded for SOAP Message!");
		}
		
		//增加XSD声明
		String[] XSDArray = new String[xsdMap.keySet().size()];
		int i = 0;
		for(Entry<String,String> xsd : xsdMap.entrySet()){
			envelope.addNamespaceDeclaration(xsd.getKey(), xsd.getValue());
			XSDArray[i++] = xsd.getKey();
		}
		
		SOAPElement bodyElement = envelope.getBody().addChildElement(method, XSDArray[0]);
		creatDocumentParam(envelope, bodyElement, XSDArray, 0, params);
	}
	
	@SuppressWarnings("unchecked")
	private void creatDocumentParam(SOAPEnvelope envelope, SOAPElement element, String[]xsds, int index,
			LinkedHashMap<String,Object> params) throws SOAPException{
		if(null == params){
			return ;
		}
		index++;
		for(Entry<String,Object> entry : params.entrySet()){
			SOAPElement e;
			if(LinkedHashMap.class.isInstance(entry.getValue())){//有层级参数封装的情况
				if(index >= xsds.length){
					e = element.addChildElement(entry.getKey());
				}else {
					e = element.addChildElement(entry.getKey(), xsds[index]);
				}
				creatDocumentParam(envelope, e, xsds, index, (LinkedHashMap<String,Object>)entry.getValue());
			}else {
				e = element.addChildElement(entry.getKey(),xsds[index-1]);
				e.addTextNode((String)entry.getValue());
			}
		}
	}
	
	public void setXsdMap(LinkedHashMap<String, String> xsdMap) {
		this.xsdMap = xsdMap;
	}

	public void setSoapStyle(String soapStyle) {
		this.soapStyle = soapStyle;
	}

	public String getSoapAction() {
		return soapAction;
	}

	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}
}
