package cn.evun.sweet.common.ws.soap;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import cn.evun.sweet.common.util.Assert;

/**
 * <p>基于SOAP直连的webservice通讯方案，可作为web服务业务扩展基础。</p>  
 * 
 * @author  yangw
 * @since   v1.0.0
 */
public class SOAPClient{
	
	/**web服务目标地址*/
	private String destinationURL;
	
	/**
	 * 根据请求实体获取字符串形式应答内容<br>
	 * @param request SOAP请求实体
	 * @return 
	 */
	public String execute(SOAPMessage request){
		SOAPMessage reply = execute4SOAPMessage(request);
		if(null != reply){
			try {
				Source source = reply.getSOAPPart().getContent();
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
	            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	            transformer.transform(source,new StreamResult(outStream));
	            return outStream.toString("UTF-8");
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 根据方法名称和参数调用web服务（rpc样式报文）获取获取字符串形式应答内容<br>
	 * @param method 目标web服务方法名称
	 * @param params 封装的请求的参数和报文结构对应
	 * @return 
	 */
	public String execute(String method, LinkedHashMap<String,Object> params){
		SOAPRequestImpl soapReq = new SOAPRequestImpl();
		return execute(soapReq.getSoapRequest(method, params));
	}
	
	public String execute(String method, LinkedHashMap<String,Object> params, String soapAction){
		SOAPRequestImpl soapReq = new SOAPRequestImpl();
		SOAPMessage soapMsg = soapReq.getSoapRequest(method, params);
		if(null != soapAction){//设置soapaction信息，跨平台调用时可能需要
			 MimeHeaders headers = soapMsg.getMimeHeaders();
			 headers.setHeader("SOAPAction", soapAction);
		}
		return execute(soapMsg);
	}
	
	/**
	 * 根据方法名称和参数调用web服务（document样式报文）获取获取字符串形式应答内容<br>
	 * @param method 目标web服务方法名称
	 * @param params 封装的请求的参数和报文结构对应
	 * @param xsdMap 封装的XSD元素声明，按层次顺序
	 * @return 
	 */
	public String execute(String method, LinkedHashMap<String,Object> params, LinkedHashMap<String, String> xsdMap){
		SOAPRequestImpl soapReq = new SOAPRequestImpl("document");
		soapReq.setXsdMap(xsdMap);
		return execute(soapReq.getSoapRequest(method, params));
	}
	
	public String execute(String method, LinkedHashMap<String,Object> params, LinkedHashMap<String, String> xsdMap, String soapAction){
		SOAPRequestImpl soapReq = new SOAPRequestImpl("document");
		soapReq.setXsdMap(xsdMap);
		SOAPMessage soapMsg = soapReq.getSoapRequest(method, params);
		if(null != soapAction){//设置soapaction信息，跨平台调用时可能需要
			 MimeHeaders headers = soapMsg.getMimeHeaders();
			 headers.setHeader("soapActionString", soapAction);
		}
		return execute(soapMsg);
	}

	/**
	 * 根据请求实体获取原始应答内容<br>
	 * @param request SOAP请求实体
	 * @return 
	 */
	public SOAPMessage execute4SOAPMessage(SOAPMessage request){
		Assert.notNull(destinationURL, "No destination URL has bean gaven!");
		Assert.notNull(request, "No destination URL has bean gaven!");
		
		SOAPConnection connection = null;
		try {
			SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
			connection = soapConnFactory.createConnection();
			//request.writeTo(System.out);System.out.println();
			SOAPMessage msg = connection.call(request, destinationURL);
			
			SOAPBody soapBody = msg.getSOAPPart().getEnvelope().getBody();	
			if(soapBody.hasFault()){
				SOAPFault fault = soapBody.getFault();
				throw new RuntimeException(fault.getFaultCode()+"["+fault.getFaultString()+"]");
			}
			
			return msg;
		} catch (SOAPException e) {
			return null;
		}finally{
			if(null != connection){
				try {
					connection.close();
				} catch (SOAPException e) { 
				}
			}
		}
	}
	
	public void setDestinationURL(String destinationURL) {
		this.destinationURL = destinationURL;
	}
	
	
	public static void main(String[] args){
		//http://www.webxml.com.cn/WebServices/WeatherWebService.asmx
		SOAPClient soapCilent = new SOAPClient();
		soapCilent.setDestinationURL("http://www.webservicex.net/globalweather.asmx");
		LinkedHashMap<String,Object> params = new LinkedHashMap<String,Object>();
		params.put("CityName", "london");
		params.put("CountryName", "");
		LinkedHashMap<String, String> xsdmap = new LinkedHashMap<String, String>();
		xsdmap.put("web", "http://www.webserviceX.NET");
		String res = soapCilent.execute("GetWeather", params, xsdmap, "http://www.webserviceX.NET/GetWeather");
		System.out.println(res);
	}
		
}
