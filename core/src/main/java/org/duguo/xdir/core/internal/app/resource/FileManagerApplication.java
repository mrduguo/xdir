package org.duguo.xdir.core.internal.app.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.duguo.xdir.core.internal.server.DefaultAdminApplication;
import org.duguo.xdir.jcr.utils.JcrNodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.duguo.xdir.core.internal.model.FormatService;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.resource.MultipartRequestResolver;
import org.duguo.xdir.core.internal.template.TemplateEngine;
import org.duguo.xdir.core.internal.utils.RequestUtils;

public class FileManagerApplication extends DefaultAdminApplication
{
    
    private static final Logger logger = LoggerFactory.getLogger( FileManagerApplication.class );
    private MultipartRequestResolver multipartRequestResolver;

    protected int processTemplate( ModelImpl model ) throws Exception
    {
        String basePath= JcrNodeUtils.getPropertyIfExist(model.getNode(), "_fs_path");
        if(basePath!=null){
            basePath=getPropertiesService().resolveStringValue( basePath );
            File baseFolder=new File(basePath);
            if(baseFolder.exists()){
                model.put( "baseFolder", baseFolder );
                String filePath=model.getPathInfo().getRemainPath();
                if(filePath!=null){
                    File file=new File(baseFolder,filePath);
                    if(!file.exists()){
                        file=new File(baseFolder,filePath+model.getFormat());
                        if(logger.isDebugEnabled())
                            logger.debug("check file [{}]",model.getFormat(),file.getPath());
                        if(!file.exists()){
                            file=new File(baseFolder,filePath+"/"+TemplateEngine.TEMPLATE_DEFAULT+model.getFormat());
                            if(logger.isDebugEnabled())
                                logger.debug("check file [{}]",model.getFormat(),file.getPath());
                            if(!file.exists()){
                                return STATUS_PAGE_NOT_FOUND;
                            }
                        }
                        model.setFormat( FormatService.FORMAT_FOLDER );
                    }else if(!file.isFile()){
                        // to support index.format file access
                        if(model.getRequest().getRequestURI().endsWith( "/"+ TemplateEngine.TEMPLATE_DEFAULT+model.getFormat() )){
                            file=new File(baseFolder,filePath+"/"+TemplateEngine.TEMPLATE_DEFAULT+model.getFormat());
                            if(logger.isDebugEnabled())
                                logger.debug("check index file [{}]",file.getPath());
                            if(!file.exists()){
                                return STATUS_PAGE_NOT_FOUND;
                            }
                            model.setFormat( FormatService.FORMAT_FOLDER );
                        }
                    }
                    if(file.isFile()){
                        model.put( "file", file );
                        if(logger.isDebugEnabled())
                            logger.debug("resolved file to path [{}]",file.getPath());
                    }
                }
            }
        }
        return super.processTemplate( model );
    }
    
    
    public void attachFile(ModelImpl model, String fileFieldName)throws Exception {
        HttpServletRequest fileUploadHttpServletRequest=multipartRequestResolver.resolveMultipartRequest( model.getRequest() );
        model.setRequest( fileUploadHttpServletRequest );
        File uploadedFile=(File)model.getRequest().getParameterMap().get(fileFieldName);
        if(uploadedFile==null){
            model.setStatus("File is required");
            return;
        }
        String uploadedFileName=(String)model.getRequest().getParameterMap().get(fileFieldName+"fileName");
        if(logger.isDebugEnabled())
            logger.debug("user uploaded file [{}]",uploadedFileName);
        
        File targetFile=(File)model.get( "file" );
        if(targetFile==null){
            String pathName=RequestUtils.getPath( model);
            File baseFolder=(File)model.get( "baseFolder" );
            if(pathName==null || pathName.trim().length()==0){
                Assert.notNull( baseFolder );
                targetFile=new File(uploadedFileName);
                targetFile=new File(baseFolder,targetFile.getName());                
            }else{
                targetFile=new File(baseFolder,pathName);      
                if(!targetFile.getParentFile().exists()){
                    if(logger.isDebugEnabled())
                        logger.debug("created folder for file [{}]",targetFile.getPath());
                    targetFile.getParentFile().mkdirs();
                }
            }
        }

        FileInputStream inputStream=new FileInputStream(uploadedFile);
        try{
            FileOutputStream fileOuttputStream=new FileOutputStream( targetFile );
            try{
                IOUtils.copy( inputStream, fileOuttputStream );
                if(logger.isDebugEnabled())
                    logger.debug("attached file to local path [{}]",targetFile.getPath());
            }finally{
                fileOuttputStream.close();
            }
        }finally{
            inputStream.close();
            uploadedFile.delete();
        }
        
    }
    public MultipartRequestResolver getMultipartRequestResolver()
    {
        return multipartRequestResolver;
    }

    public void setMultipartRequestResolver( MultipartRequestResolver multipartRequestResolver )
    {
        this.multipartRequestResolver = multipartRequestResolver;
    }

}
