package cn.evun.sweet.core.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.evun.sweet.common.util.network.StreamUtils;
import cn.evun.sweet.core.common.JsonResultDO;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;


/**   
 * 实现Oss服务的服务端上传等功能，同时为APP应用提供STS服务。
 * 
 * @author yangw   
 * @since V1.0.0   
 */
@Controller
public class OssController implements InitializingBean{
	
	@Value("${oss.app.accesskeyid:LTAIr5ZM1joF4l0H}")
	private String accessKeyID;
	
	@Value("${oss.app.accesskeysecret:QWxZ1tM0UqUETQvJ4FRwCygRZnX8Mn}")
	private String accessKeySecret;
	
	@Value("${oss.app.tokenexpiretime:900}")
	private Long tokenExpireTime;
	
	@Value("${oss.app.rolearn:default}")
	private String roleArn;
	
	@Value("${oss.app.policyfile:conf/policy/all_policy.txt}")
	private String policyFile;
	
	@Value("${oss.pc.accesskeyid:LTAIIE8ZBBmmasI6}")
	private String pcAccessKeyID;
	
	@Value("${oss.pc.accesskeysecret:tueA3XLdM87ASk5y1l3bRFHw4aMkcx}")
	private String pcAccessKeySecret;
	
	@Value("${oss.endpoint:oss-cn-hangzhou.aliyuncs.com}")
	private String endpoint;
	
	@Value("${oss.bucket:default}")
	private String defaultBucket;

	@Value("${oss.callback.url:http://localhost:7090/oss/callback}")
    private String callbackUrl;

	private String callbackString;
	public static final String REGION_CN_HANGZHOU = "cn-hangzhou";
	public static final String STS_API_VERSION = "2015-04-01";
	
	protected static final Logger LOGGER = LogManager.getLogger();

    /**
     * 初始化callback的Json体，避免每次调用signature都生成一次
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        JsonResultDO callbackParams = new JsonResultDO();
        callbackParams.addAttribute("callbackUrl", this.callbackUrl);
        callbackParams.addAttribute("callbackBody", "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
        callbackParams.addAttribute("callbackBodyType", "application/x-www-form-urlencoded");

        this.callbackString = callbackParams.toJson();
    }

    /**
	 * 为APP应用获取STS凭证
	 * @param roleSess  用于标识用户，主要用于审计，或者用于区分Token颁发给谁，注意RoleSessionName的长度和规则，不要有空格，只能有'-' '_' 字母和数字等字符
	 * @throws IOException 
	 * @throws ClientException 
	 */
	@RequestMapping(value="/oss/ststoken/{roleSess}", method=RequestMethod.GET)
	@ResponseBody
	public JsonResultDO ststoken(@PathVariable("roleSess")String roleSessionName) throws IOException, ClientException{	
		JsonResultDO result = new JsonResultDO();
		String policy = StreamUtils.copyToString(
				new DefaultResourceLoader().getResource(policyFile).getInputStream(), Charset.forName("UTF-8"));
		
		final AssumeRoleResponse stsResponse = assumeRole(roleSessionName, policy);
		
		result.addAttribute("AccessKeyId", stsResponse.getCredentials().getAccessKeyId());
		result.addAttribute("AccessKeySecret", stsResponse.getCredentials().getAccessKeySecret());
		result.addAttribute("SecurityToken", stsResponse.getCredentials().getSecurityToken());
		result.addAttribute("Expiration", stsResponse.getCredentials().getExpiration());	
		return result;
	}
	
	/**
	 * 为PC应用获取Policy凭证
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value="/oss/policy/{dir}", method=RequestMethod.GET)
	@ResponseBody
	public JsonResultDO policy(@PathVariable("dir")String dir, HttpServletResponse response) throws UnsupportedEncodingException {
		JsonResultDO result = new JsonResultDO();
		Long policyExpireTime = 30L; //凭证有效时间，单位秒
		
        String host = "http://" + defaultBucket + "." + endpoint;       
    	long expireEndTime = System.currentTimeMillis() + policyExpireTime * 1000;
        Date expiration = new Date(expireEndTime);
        
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

        OSSClient client = new OSSClient(endpoint, pcAccessKeyID, pcAccessKeySecret);
        String postPolicy = client.generatePostPolicy(expiration, policyConds);
        byte[] binaryData = postPolicy.getBytes("utf-8");
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = client.calculatePostSignature(postPolicy);
        
        result.addAttribute("accessid", pcAccessKeyID);
        result.addAttribute("policy", encodedPolicy);
        result.addAttribute("signature", postSignature);
        result.addAttribute("dir", dir);
        result.addAttribute("host", host);
        result.addAttribute("expire", String.valueOf(expireEndTime / 1000));
        result.addAttribute("callback", BinaryUtil.toBase64String(callbackString.getBytes("utf-8")));

        //设置header信息
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        response.setStatus(HttpServletResponse.SC_OK);
		return result;
	}

	/**
	 * 应用上传回调服务
	 */
	@RequestMapping(value="/oss/callback", method=RequestMethod.POST)
	@ResponseBody
	public String callback(HttpEntity<String> httpEntity, HttpServletRequest request, HttpServletResponse response){
		String ossCallbackBody = null;
		try{
            ossCallbackBody = httpEntity.getBody();
			if (verifyOSSCallbackRequest(request, ossCallbackBody)){
                response.addHeader("Content-Length", String.valueOf(ossCallbackBody.length()));
                response.setStatus(HttpServletResponse.SC_OK);
                return ConvertOSSCallbackBodyToJson(ossCallbackBody);
            }
		}catch(Exception ex){
			LOGGER.error(ex);
		}
		return null;
	}

    /**
     * 用来将OSS回调字符串转为Json格式。OSS回调字符串的格式为"key1=val1&key2=val2"
     * @param callbackBody
     * @return
     */
    private static String ConvertOSSCallbackBodyToJson(String callbackBody) {
        String splitor = "&";
        String equalSign = "=";

        HashMap<String, String> result = new LinkedHashMap<>();
        String[] elements = callbackBody.split(splitor);
        for (String element : elements) {
            String[] kv = element.split(equalSign);
            if (kv.length != 2) {
                LOGGER.error("Not a valid OSS callback body: " + callbackBody);
                return null;
            }
            result.put(kv[0], kv[1]);
        }

        return JSONObject.fromObject(result).toString();
    }
	
	/*******************************************************************************************************************/
	
	/**
	 * 与STS服务通讯获得Token
	 */
	protected AssumeRoleResponse assumeRole(String roleSessionName, String policy) throws ClientException{
		// 创建一个 Aliyun Acs Client, 用于发起 OpenAPI 请求
		IClientProfile profile = DefaultProfile.getProfile(REGION_CN_HANGZHOU, accessKeyID, accessKeySecret);
		DefaultAcsClient client = new DefaultAcsClient(profile);

		// 创建一个 AssumeRoleRequest 并设置请求参数
		final AssumeRoleRequest request = new AssumeRoleRequest();
		request.setVersion(STS_API_VERSION);
		request.setMethod(MethodType.POST);
		request.setProtocol(ProtocolType.HTTPS);
		request.setRoleArn(roleArn);
		request.setRoleSessionName(roleSessionName);
		request.setPolicy(policy);
		request.setDurationSeconds(tokenExpireTime);

		// 发起请求，并得到response
		final AssumeRoleResponse response = client.getAcsResponse(request);
		return response;
	}
	
	
	/**
	 * 判断这个请求是来自OSS
	 */
	protected boolean verifyOSSCallbackRequest(HttpServletRequest request, String ossCallbackBody) throws NumberFormatException, IOException{
		boolean ret = false;	
		String autorizationInput = new String(request.getHeader("Authorization"));
		String pubKeyInput = request.getHeader("x-oss-pub-key-url");
		byte[] authorization = BinaryUtil.fromBase64String(autorizationInput);
		byte[] pubKey = BinaryUtil.fromBase64String(pubKeyInput);
		String pubKeyAddr = new String(pubKey);
		if (!pubKeyAddr.startsWith("http://gosspublic.alicdn.com/") && !pubKeyAddr.startsWith("https://gosspublic.alicdn.com/")){
			LOGGER.error("verify OSS callback request[{}]: pub key addr must be oss addrss", pubKeyAddr);
			return false;
		}
		String retString = executeGet(pubKeyAddr);
		retString = retString.replace("-----BEGIN PUBLIC KEY-----", "");
		retString = retString.replace("-----END PUBLIC KEY-----", "");
		String queryString = request.getQueryString();
		String uri = request.getRequestURI();
		String decodeUri = java.net.URLDecoder.decode(uri, "UTF-8");
		String authStr = decodeUri;
		if (queryString != null && !queryString.equals("")) {
			authStr += "?" + queryString;
		}
		authStr += "\n" + ossCallbackBody;
		ret = doCheck(authStr, authorization, retString);
		return ret;
	}
	
	@SuppressWarnings("finally")
	public String executeGet(String url) {
		BufferedReader in = null;
		String content = null;
		CloseableHttpClient client =  HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);  	
		try {
			HttpResponse response = client.execute(request);		
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			content = sb.toString();
		} catch (Exception e) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					LOGGER.error(e);
				}
				try {
					client.close();
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
			return content;
		}
	}

	
	public static boolean doCheck(String content, byte[] sign, String publicKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = BinaryUtil.fromBase64String(publicKey);
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
			java.security.Signature signature = java.security.Signature.getInstance("MD5withRSA");
			signature.initVerify(pubKey);
			signature.update(content.getBytes());
			boolean bverify = signature.verify(sign);
			return bverify;
		} catch (Exception e) {
			LOGGER.error("verify OSS callback request:", e);
		}

		return false;
	}
}
