package cn.evun.sweet.core.file;

/**
 * FastDFS文件系统操作基础服务
 * 
 * @see https://github.com/happyfish100/fastdfs-client-java/blob/master/src/org/csource/fastdfs/StorageClient1.java
 * @author yangw
 * @since V1.0.0
 */
@Deprecated
//@Service
//public class FdfsFileAccessService extends StorageClient1 implements InitializingBean{
public class FdfsFileAccessService {

//	protected static final Logger LOGGER = LogManager.getLogger();
//	
//	@Value("${file.fdfs.connect_timeout:5}")
//	private Integer  g_connect_timeout; 
//	@Value("${file.fdfs.network_timeout:30}")
//	private Integer  g_network_timeout;
//	@Value("${file.fdfs.charset:UTF-8}")
//	private String   g_charset;
//	@Value("${file.fdfs.http.tracker_http_port:80}")
//	private Integer  g_tracker_http_port;
//	@Value("${file.fdfs.http.anti_steal_token:false}")
//	private Boolean  g_anti_steal_token; 
//	@Value("${file.fdfs.http.secret_key:FastDFS1234567890}")
//	private String   g_secret_key;	
//	@Value("${file.fdfs.tracker_server:noserver}")
//	private String[] g_tracker_server;
//	@Value("${file.fdfs.http.tracker_domain:nodomain}")
//	private String tracker_domain;
//	@Value("${file.fdfs.filesuffix.forbidden:cgi,exe}")
//	private String filesuffix_forbidden;
//	
//	public static final int DEFAULT_CONNECT_TIMEOUT = 5;  //second
//	public static final int DEFAULT_NETWORK_TIMEOUT = 30; //second	
//	public static String DOMAIN = null;
//	
//
//	/* 
//	 * 初始化相关配置
//	 */
//	@Override
//	public void afterPropertiesSet() throws Exception {
//		if("noserver".equals(this.g_tracker_server[0])){ //表示不启用fastDFS
//			return;
//		}
//		ClientGlobal.g_connect_timeout = g_connect_timeout <0 ?DEFAULT_CONNECT_TIMEOUT:g_connect_timeout;
//  		ClientGlobal.g_connect_timeout *= 1000; //millisecond
//  		
//  		ClientGlobal.g_network_timeout = g_network_timeout < 0?DEFAULT_NETWORK_TIMEOUT:g_network_timeout;
//  		ClientGlobal.g_network_timeout *= 1000; //millisecond
//
//  		ClientGlobal.g_charset = g_charset;
//  		if (g_charset == null || g_charset.length() == 0){
//  			ClientGlobal.g_charset = "ISO8859-1";
//  		}
//  		
//  		String[] szTrackerServers = g_tracker_server; 		
//  		if (szTrackerServers == null){
//  			throw new MyException("Fdfs config paramter 'tracker_server' not found");
//  		}  		
//  		String[] parts;
//  		InetSocketAddress[] tracker_servers = new InetSocketAddress[szTrackerServers.length];
//  		for (int i=0; i<szTrackerServers.length; i++){
//  			parts = szTrackerServers[i].split("\\:", 2);
//  			if (parts.length != 2){
//  				throw new MyException("the value of item 'tracker_server' is invalid, the correct format is host:port");
//  			}
//  			tracker_servers[i] = new InetSocketAddress(parts[0].trim(), Integer.parseInt(parts[1].trim()));
//  		}
//  		ClientGlobal.g_tracker_group = new TrackerGroup(tracker_servers);
//  		
//  		ClientGlobal.g_tracker_http_port = g_tracker_http_port;
//  		ClientGlobal.g_anti_steal_token = g_anti_steal_token;
//  		if (g_anti_steal_token){
//  			ClientGlobal.g_secret_key = g_secret_key;
//  		}
//  		DOMAIN = tracker_domain+(ClientGlobal.g_tracker_http_port==80?"":":"+ClientGlobal.g_tracker_http_port);
//	}
//	
//	/********************************************************基础辅助操作开始 *************************************************************/
//	
//	public void connect() throws Exception{
//    	try {
//			super.trackerServer = new TrackerClient().getConnection();
//		} catch (Exception e) {
//			LOGGER.error("failed to get connect to fastdfs server.");
//			throw e;
//		}
//    }
//    
//    public void close(){
//    	try {
//    		if(super.trackerServer != null){
//    			super.trackerServer.close();
//    		}
//		} catch (Exception e) {
//			LOGGER.error("failed to close trackerServer", e);
//		}
//    }
//    
//    
//    /********************************************************上传下载操作开始*************************************************************/
//    /**
//	 * 上传文件
//	 * @param contents 文件字节数组
//	 * @param fileSuffix 后缀
//	 * @return fileId[0]表示fileId,如果为""表示上传失败;fileId[1]表示http浏览地址(主要针对图片)
//	 */
//    public String[] upload(byte[] contents, String fileSuffix){
//    	String[] result = new String[2];
//    	filterFileSuffix(fileSuffix);
//    	try {
//    		connect();
//    		String fileId = super.upload_file1(contents, fileSuffix, null);
//    		result[0] = fileId;
//			result[1] = DOMAIN + "/" + fileId;
//		} catch (Exception e) {
//			LOGGER.error(e);
//		} finally{
//			this.close();
//		}
//    	return result;
//    }
//    
//    /**
//	 * 上传文件
//	 * @return fileId[0]表示fileId,如果为""表示上传失败;fileId[1]表示http浏览地址(主要针对图片),
//	 */
//    public String[] upload(InputStream in, String fileSuffix){    	
//		try {
//			byte[] bytes = new byte[in.available()];
//			in.read(bytes);
//			return upload(bytes, fileSuffix);
//		} catch (IOException e) {
//			LOGGER.error(e);
//			return new String[2];
//		}		
//    }
//    
//    /**
//	 * 上传文件
//	 * @return fileId[0]表示fileId,如果为""表示上传失败;fileId[1]表示http浏览地址(主要针对图片),
//	 */
//    public String[] upload(MultipartFile file){
//    	try {
//    		return this.upload(file.getBytes(), FileUtils.resolveSuffix(file.getOriginalFilename()));
//		} catch (IOException e) {
//			LOGGER.error("failed to upload file {}", file.getOriginalFilename(), e);
//			return new String[2];
//		} finally {
//			try {
//				file.getInputStream().close();
//			} catch (Exception e) {
//			}
//		}
//    }
//    
//    /**
//	 * 任意文件上传漏洞，对高危的exe、cgi等可执行文件进行限制
//	 */
//	public void filterFileSuffix(String fileSuffix){
//		if(StringUtils.hasText(fileSuffix) && StringUtils.hasText(filesuffix_forbidden)){
//			String[] suffixs = filesuffix_forbidden.split(",");
//			for (String suffix : suffixs) {
//				if(suffix.toLowerCase().equals(fileSuffix.toLowerCase())){
//					throw new SweetException("出于安全性考虑，不允许添加."+fileSuffix+"后缀的文件");
//				}
//			}
//		}
//	}
//      
//	
//	
//	
//	/**
//	 * 下载文件，获取文件的字节流。
//	 */
//	public byte[] download(String fileId) {
//		try {
//			connect();
//			return super.download_file1(fileId);			
//		} catch (Exception e) {
//			LOGGER.error("failed to download file {}", fileId, e);
//			return null;
//		} finally {
//			close();
//		}
//	}
//	
//	/**
//	 * 通过输出流下载文件。
//	 */
//    public void download(OutputStream os, String fileId) {
//    	try {
//			os.write(download(fileId));
//			os.flush();
//		} catch (IOException e) {
//			LOGGER.error(e);
//		} finally {
//			try {
//				os.close();
//			} catch (Exception ec) {
//			}
//		}
//	}
//    
//    /**
//	 * 多个文件打包下载。
//	 */
//    public byte[] download(String[] fileIds, List<String> fileNames){		
//		byte[] bytes = null;
//		try {
//			connect();
//			List<InputStream> iss = new ArrayList<InputStream>();
//			for (int i = 0; i < fileIds.length; i++) {
//				bytes = super.download_file1(fileIds[i]);
//				iss.add(new ByteArrayInputStream(bytes == null?new byte[0]:bytes));
//			}
//			ByteArrayOutputStream os = new ByteArrayOutputStream();
//			FileUtils.pack(iss, fileNames, os);			
//			bytes = os.toByteArray();
//		} catch (Exception e) {
//			LOGGER.error(e);
//		} finally {
//			close();
//		}
//		return bytes;
//	}
//    
//    /**
//	 * web环境下的文件下载
//	 */
//	public void download(HttpServletRequest request, HttpServletResponse response, String downloadName, byte[] bytes) {
//		response.setCharacterEncoding("UTF-8");
//		response.setContentType("application/octet-stream;charset=UTF-8");
//		response.setHeader("Content-disposition","attachment;" + FileUtils.getDownLoadFileName(downloadName, request));	
//		if(bytes.length > 0){
//			OutputStream rOut = null;
//			BufferedOutputStream bOut = null;
//			try{
//				response.addHeader("Content-Length", String.valueOf(bytes.length));
//				BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(bytes));
//				rOut = response.getOutputStream();
//				bOut = new BufferedOutputStream(rOut);
//				int length = 0;
//				byte[] buf = new byte[10 * 1024];
//				while ((length = bis.read(buf)) != -1) {
//					bOut.write(buf, 0, length);
//				}
//				bOut.flush();				
//				bis.close();
//				rOut.close();
//				bOut.close();
//			} catch (Exception e) {
//				throw new SweetException(R.exception.excode_file_download, e.getMessage());
//			} finally{
//				try{
//					rOut.close();
//					bOut.close();
//				}catch(Exception ex){
//				}
//			}
//		}
//	}
//    
//    /**
//	 * web环境下的单个文件下载
//	 */
//	public void download(HttpServletRequest request, HttpServletResponse response, String fileName, String fileId) {
//		Assert.hasText(fileId, "parameter 'fileId' required.");
//		Assert.hasText(fileName, "parameter 'fileName' required.");
//		download(request, response, fileName, download(fileId));		
//	}
//    
//	/**
//	 * web环境下的多个文件下载
//	 */
//	public void download(HttpServletRequest request, HttpServletResponse response, 
//			String[] fileIds, List<String> fileNames, String zipName) {
//		Assert.notEmpty(fileIds, "parameter 'fileIds' required.");
//		Assert.notEmpty(fileNames, "parameter 'fileNames' required.");
//		download(request, response, zipName, download(fileIds, fileNames));		
//	}
//	
//		
//	/**
//	 * 删除服务器上的文件
//	 */
//	public boolean delete(String fileId) {
//		try {
//			connect();
//			return super.delete_file1(fileId) > 0;
//		} catch (Exception e) {
//			LOGGER.error(e);
//			return false;
//		} finally {
//			close();
//		}
//	}
}
