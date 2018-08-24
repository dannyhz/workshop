package cn.evun.sweet.core.file;


/**
 * FTP客户端常用的操作封装。
 * 
 * @see http://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html
 * @author zhouhb yangw
 * @since V1.0.0
 * @deprecated
 */
//@Service
public class FtpFileAccessService  { //extends FTPClient

//	protected static final Logger LOGGER = LogManager.getLogger();
//	
//	private String server_charset = FileUtils.getSystemEncoding();
//	private String os_separator = FileUtils.getFileSeparator();
//	private String extension_separator = ".";
//
//	@Value("${file.ftp.username}")
//	private String username;
//
//	@Value("${file.ftp.password}")
//	private String password;
//
//	@Value("${file.ftp.url}")
//	private String url;
//
//	@Value("${file.ftp.port}")
//	private int port;
//
//	
//	/** 
//	 * 连接服务器并登录,用于文件操作之前 
//	 */
//	public boolean connect() {
//		try {
//			super.setControlEncoding(server_charset);			
//			super.connect(url, port);
//			super.setFileType(FTP.BINARY_FILE_TYPE);
//
//			if (!super.login(username, password)) {
//				super.disconnect();
//				throw new SweetException(R.exception.excode_connect_login, "invalid account for ftp server.");
//			}
//
//			int reply = super.getReplyCode();
//			if (!FTPReply.isPositiveCompletion(reply)) {
//				super.disconnect();
//				throw new SweetException(R.exception.excode_connect_refused, "connect to ftp server refused.");
//			} else {
//				return true;
//			}
//		} catch (IOException e) {
//			throw new SweetException(R.exception.excode_connect_io, e.getMessage());
//		}
//	}
//
//	/** 
//	 * 退出登录及断开连接
//	 */
//	public void close() {
//		try {
//			super.logout();
//			if (super.isConnected()) {
//				super.disconnect();
//			}
//		} catch (IOException e) {
//			throw new SweetException(R.exception.excode_connect_io, e.getMessage());
//		}
//	}
//
//	/** 
//	 * 多个文件上传，文件在服务器中相对根目录的存储位置由文件名指定。
//	 * @return 上传失败的文件名，如果全部失败则为空
//	 */
//	public List<String> upload(Collection<MultipartFile> files) {
//		connect();
//		try {			
//			if(!CollectionUtils.isEmpty(files)){
//				List<String> failedPaths = new ArrayList<String>();
//				for (MultipartFile file : files) {
//					String fileName = file.getOriginalFilename();
//					/*获取带路径的文件名，指明文件在服务端的存储位置*/
//					if(file instanceof CommonsMultipartFile){
//						fileName = ((CommonsMultipartFile)file).getFileItem().getName();
//					}
//					try {			
//						uploadSingle(file.getInputStream(), fileName);				
//					}catch (Exception e) {
//						LOGGER.error("failed to upload file [{}].", fileName, e);
//						failedPaths.add(fileName);
//					}
//				}
//				return failedPaths.size()>0?failedPaths:null;
//			}			
//			return null;
//		} finally {
//			close();
//		}
//	}
//	
//	/** 
//	 * 单个文件上传，文件在服务器中相对根目录的存储位置由文件名指定。
//	 */
//	public boolean upload(MultipartFile file) {
//		Assert.notNull(file, "file must not be null.");
//		ArrayList<MultipartFile> files = new ArrayList<MultipartFile>();
//		files.add(file);
//		return upload(files) == null;
//	}
//	
//
//	/** 
//	 * 多个文件上传，文件在服务器中相对根目录的存储位置，由入参来指定。
//	 * 注意：最终的存放位置将由<code>remotePath</code>+<code>file.getOriginalFilename()</code>决定
//	 * @return 上传失败的文件名，如果全部失败则为空
//	 */
//	public List<String> upload(Collection<MultipartFile> files, String remotePath) {
//		connect();
//		try {			
//			if(!CollectionUtils.isEmpty(files)){
//				List<String> failedPaths = new ArrayList<String>();
//				for (MultipartFile file : files) {
//					String fileName = remotePath+extension_separator+file.getOriginalFilename();
//					try {			
//						uploadSingle(file.getInputStream(), fileName);				
//					}catch (Exception e) {
//						LOGGER.error("failed to upload file [{}].", fileName, e);
//						failedPaths.add(fileName);
//					}
//				}
//				return failedPaths.size()>0?failedPaths:null;
//			}			
//			return null;
//		} finally {
//			close();
//		}
//	}
//	
//	/** 
//	 * 单个文件上传，文件在服务器中相对根目录的存储位置，由入参来指定。
//	 * 注意：最终的存放位置将由<code>remotePath</code>+<code>getOriginalFilename</code>决定
//	 */
//	public boolean upload(MultipartFile file, String remotePath) {
//		Assert.notNull(file, "file must not be null.");
//		ArrayList<MultipartFile> files = new ArrayList<MultipartFile>();
//		files.add(file);
//		return upload(files, remotePath) == null;
//	}
//	
//	private void uploadSingle(InputStream input, String remotePath){
//		try {
//			/*解析出路径中的文件名和路径内容*/
//			String[] pathArr = FileUtils.resolvePathName(FileUtils.perHandlePath(remotePath,os_separator), this.os_separator);
//			
//			/*开始对path进行处理，例如，递归创建确保路径存在*/		
//			String path = pathArr[0];			
//			/*尝试将目录切换到目标路径下*/
//			if(!super.changeWorkingDirectory(path)){
//				makeDirCascade(path);//按指定的路径尝试（递归）创建目录,任意一级失败则抛出异常。
//			}
//			super.changeWorkingDirectory(path);//再次尝试切换目录
//		
//			if(!super.storeFile(pathArr[1], input)){
//				throw new SweetException("failed to upload file ["+remotePath+"]"); //上传失败后通过异常通知上层调用者
//			}
//		} catch (IOException e) {
//			throw new SweetException(R.exception.excode_file_upload, e.getMessage());
//		} finally {
//			try {
//				input.close();
//			} catch (IOException e) {
//			}
//		}
//	}
//	
//	/** 
//	 * 通过字节流上传单个文件，文件名及路径有参数指定
//	 */
//	public boolean upload(byte[] bytes, String remotePath) {
//		return upload(new ByteArrayInputStream(bytes), remotePath);
//	}
//
//	/** 
//	 * 通过流上传单个文件，文件名及路径有参数指定
//	 */
//	public boolean upload(InputStream input, String remotePath) {
//		connect();
//		try {
//			uploadSingle(input, remotePath);
//		}catch(Exception e){
//			LOGGER.error("failed to upload file [{}].", remotePath, e);
//			return false;
//		}finally{
//			close();
//		}
//
//		return true;
//	}
//	
//	
//	/*********************************************************************下载操作开始*************************************************************************/
//	
//	/**
//	 * 下载指定路径下的单个文件
//	 */
//	public boolean download(OutputStream os, String remotePath) {
//		connect();
//		try {
//			boolean result = super.retrieveFile(FileUtils.perHandlePath(remotePath, os_separator), os);
//			os.flush();
//			return result;
//		} catch (Exception e) {
//			LOGGER.error("failed to download file ["+remotePath+"].", e);
//			return false;
//		} finally {
//			try {
//				os.close();
//			} catch (Exception e) {
//			}
//			close();
//		}
//	}
//
//	/**
//	 * 下载指定路径下的单个文件
//	 */
//	public byte[] download(String remotePath) {		
//		InputStream in = null;
//		byte[] bytes = null;
//		connect();
//		try {
//			in = super.retrieveFileStream(FileUtils.perHandlePath(remotePath,this.os_separator));			
//			if(in == null){
//				throw new SweetException("failed to download file ["+remotePath+"], file not found.");
//			}
//			bytes = new byte[in.available()];
//			in.read(bytes);
//		} catch (Exception e) {
//			LOGGER.error("failed to download file ["+remotePath+"].", e);
//		} finally {
//			try {
//				in.close();
//			} catch (Exception e) {
//			}
//			close();
//		}
//		return bytes;
//	}
//	
//	/**
//	 * 打包下载指定路径下的多个文件
//	 */
//	public byte[] download(String[] remotePaths){
//		connect();
//		byte[] bytes = null;
//		try {
//			List<InputStream> iss = new ArrayList<InputStream>();
//			List<String> fileNames = new ArrayList<String>();
//			for (int i = 0; i < remotePaths.length; i++) {
//				String remotePath = remotePaths[i];
//				InputStream in = super.retrieveFileStream(FileUtils.perHandlePath(remotePath,os_separator));
//				if(in == null){
//					in = new ByteArrayInputStream(new byte[0]);
//					LOGGER.error("file ["+remotePath+"] not found.");
//				}
//				iss.add(in);
//				fileNames.add(remotePath.substring(remotePath.lastIndexOf(os_separator) + 1));
//			}
//			ByteArrayOutputStream os = new ByteArrayOutputStream();
//			FileUtils.pack(iss, fileNames, os);
//			
//			bytes = os.toByteArray();
//		} catch (Exception e) {
//			LOGGER.error("failed to download files ["+StringUtils.arrayToCommaDelimitedString(remotePaths)+"].", e);
//		} finally {
//			close();
//		}
//		return bytes;
//	}
//	
//	
//	/**
//	 * web环境下的一个或多个文件打包下载
//	 */
//	public void download(HttpServletRequest request, HttpServletResponse response, String downloadName, String... remotePath) {
//		Assert.notEmpty(remotePath, "parameter 'remotePath' required.");
//		byte[] bytes = remotePath.length==1?download(remotePath[0]):download(remotePath);
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
//				LOGGER.error("failed to download files ["+StringUtils.arrayToCommaDelimitedString(remotePath)+"].", e);
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
//	
//	/*********************************************************************删除等操作开始*************************************************************************/
//
//	/**
//	 * 删除指定的文件服务器上的文件
//	 */
//	public boolean delete(String remotePath) {
//		try {
//			this.connect();
//			return super.deleteFile(FileUtils.perHandlePath(remotePath,os_separator));
//		} catch (Exception e) {
//			LOGGER.error("failed to delete file ["+remotePath+"].", e);
//			throw new SweetException(R.exception.excode_file_delete, e.getMessage());
//		} finally {
//			this.close();
//		}
//	}
//
//	/**
//	 * 文件重命名
//	 */
//	public boolean rename(String remotePath, String newFileName) {
//		try {
//			this.connect();
//			return super.rename(FileUtils.perHandlePath(remotePath,os_separator), newFileName);
//		} catch (Exception e) {
//			LOGGER.error(R.exception.excode_file_rename, e.getMessage());
//			return false;
//		} finally {
//			this.close();
//		}
//	}
//	
//	
//	/*********************************************************************目录操作开始*************************************************************************/
//
//	/**
//	 * 在指定的路径下创建目录，如果目录已存在也返回false
//	 */
//	public boolean mkdir(String pathname) {
//		try {
//			this.connect();
//			return this.makeDirCascade(FileUtils.perHandlePath(pathname,os_separator));
//		} catch (Exception e) {
//			LOGGER.error(R.exception.excode_dir_create, e.getMessage());
//			return false;
//		} finally {
//			this.close();
//		}
//	}
//	
//	/**
//	 * 递归创建(如果不存在)该目录
//	 */
//	private boolean makeDirCascade(String originPath){
//		Assert.hasText(originPath, "paramter 'originPath' required.");
//		try{
//			/*先直接切换一次*/
//			boolean c = super.changeWorkingDirectory(originPath);
//			if(!c){
//				boolean m = super.makeDirectory(originPath);
//				if(m){
//					return super.changeWorkingDirectory(originPath);
//				}
//			}else{
//				return true;
//			}			
//			/*将目录分割成阶梯式 如 12,12/34,12/34/56*/
//			String[] pathArr = originPath.split(this.os_separator.replaceAll("\\\\", "\\\\\\\\"));
//			for (int i = 0; i < pathArr.length; i++) {
//				if(i != 0){
//					pathArr[i] = pathArr[i-1] + this.os_separator + pathArr[i] ;
//				}
//			}			
//			int i = 0;
//			/*计算出从哪个目录开始创建*/
//			for (i = pathArr.length - 1; i >= 0; i--) {
//				if(!StringUtils.isEmpty(pathArr[i]) && super.makeDirectory(pathArr[i])){
//					break;
//				}
//			}			
//			/*递归创建*/
//			if(i >= 0){
//				for (int j = i; j < pathArr.length; j++) {
//					super.makeDirectory(pathArr[j]);
//				}
//			}
//		}catch(Exception e){
//			
//		}
//		return true;
//	}
//
//	/**
//	 * 删除目录 如果该目录存在子目录或文件 将删除失败
//	 */
//	public boolean removedir(String pathname) {
//		try {
//			this.connect();
//			return super.removeDirectory(pathname);
//		} catch (Exception e) {
//			LOGGER.error(R.exception.excode_dir_delete, e.getMessage());
//			return false;
//		} finally {
//			this.close();
//		}
//	}
//	
//	/**
//	 * 清空ftp文件服务器上的文件(慎用)
//	 */
//	public boolean clear(){
//		try {
//			this.connect();
//			return this.delDirAndFileCascade("");
//		} catch (Exception e) {
//			LOGGER.error(R.exception.excode_dir_delete, e.getMessage());
//			return false;
//		} finally {
//			this.close();
//		}
//	}
//	
//	/**
//	 * 删除目录包括该目录的子目录和文件夹
//	 * 不允许删除根目录(请调用{@link FtpFileAccessService#clear})
//	 */
//	public boolean deldir(String pathname) {
//		Assert.isTrue(!(StringUtils.isEmpty(pathname) || this.os_separator.equals(pathname)), "root dir delete not allowed.");
//		try {
//			this.connect();
//			return this.delDirAndFileCascade(pathname);
//		} catch (Exception e) {
//			LOGGER.error(R.exception.excode_dir_delete, e.getMessage());
//			return false;
//		} finally {
//			this.close();
//		}
//	}
//
//	/**
//	 * 获得指定目录下的文件列表
//	 */
//	public List<FTPFile> getFileList(String remotePath){
//		return this.list(remotePath, FTPFile.FILE_TYPE);
//	}
//	
//	/**
//	 * 获得指定目录下的子目录列表
//	 */
//	public List<FTPFile> getDirectoryList(String remotePath){
//		return this.list(remotePath, FTPFile.DIRECTORY_TYPE);
//	}
//	
//	/**
//	 * 获得指定目录下的所有文件、目录和link列表
//	 */
//	public List<FTPFile> getAllList(String remotePath){
//		return this.list(remotePath, null);
//	}
//	
//	
//	/**
//	 * 递归删除(如果存在)目录和文件
//	 */
//	private boolean delDirAndFileCascade(String originPath) throws Exception{		
//		/*必须先切换至要删除的上一级目录*/
//		String[] pathArr = FileUtils.resolvePathName(originPath, this.os_separator);
//		if (StringUtils.hasText(pathArr[0])){
//			super.changeWorkingDirectory(pathArr[0]);
//			originPath = pathArr[1];
//		}		
//		/*切换至目录*/
//		if(!super.changeWorkingDirectory(originPath)){
//			throw new SweetException("不存在目录" + originPath);
//		}
//		FTPFile[] files = super.listFiles();
//		for (FTPFile ftpFile : files) {
//			super.deleteFile(ftpFile.getName());
//		}		
//		FTPFile[] dirs = super.listDirectories();
//		if (dirs == null || dirs.length == 0){//如果没有子目录			
//			super.changeToParentDirectory();//返回上级目录			
//			super.removeDirectory(originPath);//直接删除目录			
//		} else {
//			for (FTPFile ftpFile : dirs) {
//				this.delDirAndFileCascade(ftpFile.getName());
//			}
//			super.changeToParentDirectory();//切换回上一级目录
//			super.removeDirectory(originPath);
//		}		
//		return true;
//	}
//	
//	private List<FTPFile> list(String remotePath, Integer type){
//		List<FTPFile> files = new ArrayList<FTPFile>();
//		try {
//			this.connect();
//			FTPFile[] ftpFiles = super.listFiles(FileUtils.perHandlePath(remotePath,this.os_separator));
//			if(ftpFiles != null){
//				for (FTPFile ftpFile : ftpFiles) {					
//					if (type == null || ftpFile.getType() == type.intValue()){	
//						files.add(ftpFile);
//					}
//				}
//			}
//		} catch (Exception e) {
//			LOGGER.error(e);
//		} finally {
//			this.close();
//		}
//		return files;
//	}
}
