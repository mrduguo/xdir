package org.duguo.xdir.core.internal.resource;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Hashtable;
import java.util.List;

public class MultipartRequestResolverImpl implements MultipartRequestResolver {
    
    
    private static final Logger logger = LoggerFactory.getLogger( MultipartRequestResolverImpl.class );

	private File tempDir;
	
	public File createTempFile() throws Exception{
	    File file=File.createTempFile("xdir.upload.", ".data",tempDir);
	    return file;
	}
	
	@SuppressWarnings("unchecked")
	public HttpServletRequest resolveMultipartRequest(HttpServletRequest httpServletRequest) throws Exception{
	    HttpServletRequest fileUploadHttpServletRequest=null;
		Hashtable<String, Object> params=new Hashtable<String, Object>(httpServletRequest.getParameterMap());
		if(ServletFileUpload.isMultipartContent(httpServletRequest)){
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> items = upload.parseRequest(httpServletRequest);
			for(FileItem fileItem:items){
				if(fileItem.isFormField()){
					params.put(fileItem.getFieldName(), fileItem.getString());
		            if(logger.isDebugEnabled())
		                logger.debug("multipart param [{}] resolved",fileItem.getFieldName());
				}else{
					String orginalName=fileItem.getName();
					int lastIndexOfSlash=orginalName.lastIndexOf("/");
					if(lastIndexOfSlash<0){
						lastIndexOfSlash=orginalName.lastIndexOf("\\");
					}
					if(lastIndexOfSlash>=0){
						orginalName=orginalName.substring(lastIndexOfSlash+1);
					}
					if(orginalName==null || orginalName.trim().length()==0){
					    // empty file upload field
                        if(logger.isDebugEnabled())
                            logger.debug("multipart empty file [{}]",fileItem.getFieldName());
					    continue;
					}
					
                    File file=createTempFile();
                    fileItem.write(file);
                    params.put(fileItem.getFieldName(), file);
					params.put(fileItem.getFieldName()+"fileName", orginalName);
					params.put(fileItem.getFieldName()+"fileSize", file.length());
		            if(logger.isDebugEnabled())
                        logger.debug("multipart file [{}] resolved",orginalName);
				}
			}
			fileUploadHttpServletRequest=new FileUploadHttpServletRequest(httpServletRequest,params);
			if(logger.isDebugEnabled())
                logger.debug("multipart request resolved");
		}else{
		    logger.warn( "not multipart request, will treat as normal request" );
		    fileUploadHttpServletRequest=httpServletRequest;
		}
        return fileUploadHttpServletRequest;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = new File(tempDir);
		if(!this.tempDir.exists()){
			this.tempDir.mkdirs();
		}
	}
}
