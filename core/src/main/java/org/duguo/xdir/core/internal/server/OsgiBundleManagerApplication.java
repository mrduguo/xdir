package org.duguo.xdir.core.internal.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.resource.MultipartRequestResolver;

public class OsgiBundleManagerApplication extends DefaultAdminApplication
{
    
    private static final Logger logger = LoggerFactory.getLogger( OsgiBundleManagerApplication.class );
    private MultipartRequestResolver multipartRequestResolver;


    private Map<String,Object> copySourceFilesToGroup( String targetGroup, List<File> filesToDeploy )
        throws IOException
    {
        String bundlesBase= getPropertiesService().resolveStringValue( "${xdir.dir.bundles}");
        Map<String,Object> bundlesToDeploy=new HashMap<String,Object>();
        for(File currentFile:filesToDeploy){
            JarFile jarFile=new JarFile(currentFile );
            Manifest manifest = jarFile.getManifest();
            Assert.notNull( manifest );
            String bundleSymbolicName=manifest.getMainAttributes().getValue( Constants.BUNDLE_SYMBOLICNAME );
            Assert.notNull( bundleSymbolicName,"bunde symbolic name is required");
            String bundleVersion=manifest.getMainAttributes().getValue( Constants.BUNDLE_VERSION );
            Assert.notNull( bundleVersion,"bundle version is required" );
            String targetFileName=null;
            
            Bundle existBundle=retriveBundleByName( bundleSymbolicName+"-"+bundleVersion );
            if(existBundle!=null){
                targetFileName=existBundle.getLocation().substring( "reference:file:".length() );
            }else{
                targetFileName=bundlesBase+"/"+targetGroup+"/"+bundleSymbolicName+"-"+bundleVersion+".jar";
            }                     
            if(logger.isDebugEnabled())
                logger.debug("target file name resolved as [{}]",targetFileName);
            
            File targetFile=new File(targetFileName);
            if(!targetFile.getParentFile().exists()){
                targetFile.getParentFile().mkdirs();
            }
            FileUtils.copyFile( currentFile, targetFile );
            bundlesToDeploy.put(targetFile.getPath(),existBundle!=null?existBundle:"");
        }        
        return bundlesToDeploy;
    }

    protected List<File> scanSourceFilesToDeploy( File sourceFile )
    {
        List<File> filesToDeploy=new ArrayList<File>();
        if(sourceFile.isFile()){
            filesToDeploy.add( sourceFile);
            if(logger.isDebugEnabled())
                logger.debug("found source file [{}]",sourceFile.getPath());
        }else{
            File[] files=sourceFile.listFiles();
            if(files!=null && files.length>0){
                for(File file:files){
                    if(file.isFile()){
                        filesToDeploy.add( file);
                        if(logger.isDebugEnabled())
                            logger.debug("found source file [{}]",file.getPath());
                    }
                }
            }
        }
        Assert.state( filesToDeploy.size()>0, "no file found to deploy");
        return filesToDeploy;
    }


    public void performBundleAction(final String action, final String bundleInfo)throws Exception{
        Bundle bundle=retriveBundle(  bundleInfo );
        Assert.notNull( bundle );
        Method actionMethod=Bundle.class.getMethod( action, new Class[0] );
        Assert.notNull( actionMethod );
        actionMethod.invoke( bundle, new Object[0] );
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
