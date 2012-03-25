package org.duguo.xdir.core.internal.server;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.duguo.xdir.jcr.utils.JcrNodeUtils;
import org.duguo.xdir.spi.util.bean.BeanUtil;
import org.duguo.xdir.spi.util.bean.BundleUtil;
import org.duguo.xdir.spi.util.datetime.DateTimeUtil;
import org.duguo.xdir.spi.util.thread.Action;
import org.duguo.xdir.spi.util.thread.ThreadUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.jcr.Node;
import javax.jcr.Session;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;


public class OsgiConfManagerApplication extends DefaultAdminApplication implements InitializingBean
{
    
    private static final Logger logger = LoggerFactory.getLogger( OsgiConfManagerApplication.class );
    public static final String SPRING_APPLICATION_CONTEXT_CLASS_NAME=ApplicationContext.class.getName();
    
    private long initDelay=4000;// 10 seconds by default
    private List<String> additionalConfigurations;
    private Pattern hashOnlyFiles;
    private Pattern ignoredFiles;


	public void afterPropertiesSet() throws Exception {
        if(initDelay<0){
            // startup conf scan disabled
            if(logger.isDebugEnabled())
                logger.debug("startup conf scan disabled");
            return;
        }
        
        ThreadUtil.delayedAction( initDelay, new Action()
        {
            public void execute() throws Exception
            {
                Session session=getJcrFactory().retrieveSession();
                try{
                    Node baseNode=session.getNode( getJcrBasePath());
                    scanConf( baseNode );
                }finally{
                    session.logout();
                }
            }
            public String getName()
            {
                return "scan-conf-changes";
            }
        });
    }
    
    public Map<String,Object> fileChangeStatus(Node fileNode) throws Exception {
        Map<String,Object> status=new HashMap<String, Object>();
        String fileContent=null;
        String fileType=JcrNodeUtils.getPropertyIfExist( fileNode, "_type" );
        String fileLocation=JcrNodeUtils.getPropertyIfExist( fileNode, "file_location" );
        String oldFileHash=JcrNodeUtils.getPropertyIfExist( fileNode, "file_hash" );
        Assert.notNull( fileLocation );
        fileLocation=getPropertiesService().resolvePlaceholders( fileLocation );
        if("conf_resource".equals( fileType )){
            fileContent=readResourceAsString( fileLocation );
            if(fileContent.length()>0){
                String currentHash=DigestUtils.md5DigestAsHex(fileContent.getBytes());
                if(!currentHash.equals( oldFileHash )){
                    status.put( "_file_content", fileContent );
                    status.put( "file_hash", currentHash );
                }
            }else{
                status.put( "notfound", "NOT FOUND" );
            }
        }else{
            File currentFile=new File(fileLocation);
            if(currentFile.exists()){
                String currentHash=DigestUtils.md5DigestAsHex( FileUtils.readFileToByteArray( currentFile ));
                if(!currentHash.equals( oldFileHash )){
                    status.put( "_file_last_modified", currentFile.lastModified() );
                    status.put( "file_hash", currentHash );
                    status.put( "file_location", fileLocation );
                }
            }else{
                status.put( "notfound", "NOT FOUND" );
            }
        }
        return status;
    }

    public void scanConf(Node baseNode) throws Exception {
        if(logger.isDebugEnabled())
            logger.debug("scan conf to node [{}] started",baseNode.getPath());
        if(hasChange(baseNode)){
            String snapshotBase="snapshots/"+DateTimeUtil.currentTimestampKey();
            String bundlesBase=getPropertiesService().resolvePlaceholders( "${xdir.home}/bundles");
            String confBase= getPropertiesService().resolvePlaceholders( "${xdir.home}/data/conf");
            JcrNodeUtils.setNodeProperty( baseNode, snapshotBase+"/_type","conf_snapshot");
            JcrNodeUtils.setNodeProperty( baseNode, snapshotBase+"/_mm_no_children","true");
            for(Bundle bundle:getBundleContext().getBundles()){
                Set<Object> scanFolders=new LinkedHashSet<Object>();
                String bundleStorageBase=null;
                if(bundle.getBundleId()==0){
                    bundleStorageBase=snapshotBase+"/framework";
                    if(additionalConfigurations!=null){
                        scanFolders.addAll( additionalConfigurations );
                    }
                    scanFolders.add( getPropertiesService().resolvePlaceholders( "${xdir.home}/boot") );
                    scanFolders.add( confBase);
                    JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/status/_type","conf_status_framework");
                    JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/status/_system_properties", buildPropertiesAsString(System.getProperties()) );
                }else{
                    String bundleGroup=null;
                    bundleGroup=bundle.getLocation();
                    bundleGroup=bundleGroup.substring( bundleGroup.indexOf( bundlesBase )+bundlesBase.length()+1,bundleGroup.lastIndexOf( "/" ) );
                    bundleStorageBase=snapshotBase+"/"+bundleGroup.replaceAll( "/", "_" );
                    JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/_title", bundleGroup);
                    JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/_search",bundleGroup);
                    JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/_type","conf_group");
                    
                    bundleStorageBase=bundleStorageBase+"/"+bundle.getSymbolicName();
                    JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/status/_type","conf_status_bundle");

                    URL manifest=bundle.getEntry( "/META-INF/MANIFEST.MF" );
                    scanFolders.add( manifest);
                    String bundleFile=bundle.getLocation();
                    if(bundleFile.startsWith( "reference:" )){
                        bundleFile=bundleFile.substring( "reference:".length() );
                    }
                    scanFolders.add(bundleFile );
                    scanFolders.add( confBase+"/"+bundle.getSymbolicName());
                    scanFolders.add( confBase+"/"+bundle.getSymbolicName()+"/"+bundle.getVersion());
                }
                attachBundleStatus(baseNode,bundle,bundleStorageBase,scanFolders);
                attachBundleFiles(baseNode,bundle,bundleStorageBase,scanFolders);
            }
            baseNode.getSession().save();
        }
        if(logger.isDebugEnabled())
            logger.debug("scan conf to node [{}] finished",baseNode.getPath());
    }

    protected void attachBundleStatus( Node baseNode, Bundle bundle, String bundleStorageBase, Set<Object> scanFolders)throws Exception 
    {
        JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/status/bundle_symbolic_name",bundle.getSymbolicName());
        JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/status/bundle_version",bundle.getVersion().toString());
        JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/status/bundle_id",String.valueOf(bundle.getBundleId()));
        JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/status/bundle_status",BundleUtil.displayBundleStatus( bundle.getState() ));
        ServiceReference[] serviceReferences = bundle.getRegisteredServices();
        if(serviceReferences!=null && serviceReferences.length>0){
            int i=0;
            for(ServiceReference serviceReference:serviceReferences){
                i++;
                String serviceId=String.valueOf(serviceReference.getProperty( Constants.SERVICE_ID ));
                String serviceBase=bundleStorageBase+"/services/"+serviceId;
                JcrNodeUtils.setNodeProperty( baseNode, serviceBase+"/service_id",serviceId);
                JcrNodeUtils.setNodeProperty( baseNode, serviceBase+"/_type","conf_service");
                StringBuilder properties=new StringBuilder();
                String serviceName=null;
                for(String key:serviceReference.getPropertyKeys()){
                    properties.append( key );
                    properties.append( "=" );
                    Object propertyValue=serviceReference.getProperty( key );
                    boolean isFirst=true;
                    if(propertyValue.getClass().isArray()){
                        Object[] arrayValue=(Object[])propertyValue;
                        for(Object value:arrayValue){
                            if(isFirst){
                                isFirst=false;
                                if("objectClass".equals( key ) && serviceName==null){
                                    serviceName=(String)value;
                                }
                            }else{
                                properties.append( "," );
                            }
                            if(SPRING_APPLICATION_CONTEXT_CLASS_NAME.equals( value )){
                                // is spring context service
                                Object applicationContext=bundle.getBundleContext().getService( serviceReference );
                                if(applicationContext!=null){
                                    String[] springConfigLocations=(String[])BeanUtil.retriveFieldValue( applicationContext, "configLocations" );
                                    if(springConfigLocations!=null){
                                        for(String location:springConfigLocations){
                                            if(location.startsWith( "file:" )){
                                                scanFolders.add( location );                                                
                                            }else{
                                                Resource resource=loadResource( location );
                                                if(resource!=null){
                                                    scanFolders.add(resource.getURL());                                                    
                                                }
                                            }
                                        }
                                        
                                    }
                                }
                                bundle.getBundleContext().ungetService( serviceReference );
                            }
                            properties.append( value );
                        }
                    }else{
                        if("org.springframework.osgi.bean.name".equals( key )){
                            serviceName=(String)serviceReference.getProperty( "org.springframework.osgi.bean.name" );
                        }
                        properties.append( propertyValue );
                    }
                    properties.append( "\n" );
                }
                JcrNodeUtils.setNodeProperty( baseNode, serviceBase+"/_title",serviceName);
                JcrNodeUtils.setNodeProperty( baseNode, serviceBase+"/_service_properties",properties.toString());
            }
        }
    }

    protected void attachBundleFiles( Node baseNode, Bundle bundle, String bundleStorageBase, Set<Object> scanFolders)throws Exception 
    {
        Object bundleName=bundle.getHeaders().get( "Bundle-Name" );
        if(bundleName!=null){
            JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/_title", bundleName.toString() );
            JcrNodeUtils.setNodeProperty(baseNode, bundleStorageBase + "/_search", bundle.getSymbolicName() + " " + bundleName);
        }else{
            JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/_search", bundle.getSymbolicName());
        }
        JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/_type","conf_bundle");
        bundleStorageBase=bundleStorageBase+"/files";
        int i=0;
        Set<String> addedLocalFile=new HashSet<String>();
        for(Object confItem:scanFolders){
            i++;
            if(confItem instanceof URL){
                String fileString= IOUtils.toString(((URL)confItem).openStream(), "utf-8");
                String filePath=confItem.toString();
                String fileName=filePath.substring( filePath.lastIndexOf( "/" )+1);
                String currentHash=DigestUtils.md5DigestAsHex(fileString.getBytes());
                
                JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/"+i+"/file_hash", currentHash );
                JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/"+i+"/_file_content", fileString );
                JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/"+i+"/file_location", filePath );
                JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/"+i+"/_title",fileName);
                JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/"+i+"/_type","conf_resource");
            }else{
                String filePath=(String)confItem;
                if(filePath.startsWith( "file:" )){
                    filePath=filePath.substring( "file:".length() );
                }
                File confRootFile=new File(filePath);
                if(addedLocalFile.contains(confRootFile.getPath())){
                    i--;
                   continue;
                }
                if(confRootFile.exists()){
                    filePath=getPropertiesService().resolveToRelativePath( filePath );
                    if(confRootFile.isFile()){
                        addedLocalFile.add( confRootFile.getPath() );
                        attachConfFile( baseNode, bundleStorageBase+"/"+i, filePath, confRootFile );
                    }else{
                        String[] childFiles=confRootFile.list();
                        if(childFiles!=null && childFiles.length>0){
                            i--;
                            for(String fileName:childFiles){
                                File childFile=new File(confRootFile,fileName);
                                if(childFile.isFile() && !addedLocalFile.contains( childFile.getPath() ) && !ignoredFiles.matcher( childFile.getPath() ).find() ){
                                    i++;
                                    addedLocalFile.add( childFile.getPath() );
                                    attachConfFile( baseNode, bundleStorageBase+"/"+i, filePath+"/"+fileName, childFile );
                                }
                            }   
                        }
                    }
                }
            }
        }
    }

    private void attachConfFile( Node baseNode, String bundleStorageBase, String filePath, File confFile )throws  Exception
    {
        String fileName=confFile.getName();
        String currentHash=DigestUtils.md5DigestAsHex( FileUtils.readFileToByteArray( confFile ));
        JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/file_hash", currentHash );
        JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/file_size",String.valueOf(confFile.length()));
        JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/_file_last_modified", String.valueOf(confFile.lastModified()) );
        JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/file_location", filePath );
        JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/_title", fileName );
        JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/_search", fileName );
        if(hashOnlyFiles.matcher( confFile.getPath() ).find()){
            JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/_type","conf_hash");
        }else{
            JcrNodeUtils.setNodeProperty( baseNode, bundleStorageBase+"/_type","conf_file");
            JcrNodeUtils.addFileNode( baseNode,bundleStorageBase, confFile );
        }
    }

    @SuppressWarnings("unchecked")
    protected String buildPropertiesAsString( Properties properties )
    {
        if(properties==null || properties.size()==0){
            return null;
        }
        
        StringBuilder sb=null;
        List keys=CollectionUtils.arrayToList( properties.keySet().toArray() );
        Collections.sort( keys );
        for(Object key:keys){
            if(sb==null){
                sb=new StringBuilder();
            }else{
                sb.append( "\n" );
            }
            sb.append( key );
            sb.append( "=" );
            sb.append( properties.get( key ) );
        }
        return sb.toString();
    }

    public boolean hasChange( Node baseNode ) throws Exception
    {
        String previousHash=JcrNodeUtils.getPropertyIfExist( baseNode, "conf_hash" );
        List<String> scanFolders=new ArrayList<String>();
        scanFolders.add( getPropertiesService().resolvePlaceholders( "${xdir.home}/boot") );
        scanFolders.add( getPropertiesService().resolvePlaceholders( "${xdir.home}/bundles") );
        scanFolders.add( getPropertiesService().resolvePlaceholders( "${xdir.home}/data/conf") );
        if(additionalConfigurations!=null){
            scanFolders.addAll(additionalConfigurations);
        }
        String currentHash=generateFoldersHash("", scanFolders);
        
        if(currentHash.equals( previousHash )){
            if(logger.isDebugEnabled())
                logger.debug("no change found from bundles and configuration files");
            return false;
        }else{
            JcrNodeUtils.setNodeProperty( baseNode, "conf_hash",currentHash );
            if(logger.isDebugEnabled())
                logger.debug("update conf hash from [{}] to [{}]",previousHash,currentHash);
            return true;
        }        
    }

    protected String generateFoldersHash(String hash, List<String> scanFolders ) throws IOException
    {
        Collections.sort( scanFolders );
        for(String folder:scanFolders){
            File currentFile=new File(folder);
            String currentHash=null;
            if(currentFile.isFile()){
                if(!ignoredFiles.matcher( currentFile.getPath() ).find()){
                    currentHash=DigestUtils.md5DigestAsHex( FileUtils.readFileToByteArray( currentFile ));
                    hash=DigestUtils.md5DigestAsHex( (hash+currentHash).getBytes() );   
                }
            }else{
                String[] subfolders=currentFile.list();
                if(subfolders!=null && subfolders.length>0){
                    List<String> subScanFolders=new ArrayList<String>();
                    for(int i=0;i<subfolders.length;i++){
                        subScanFolders.add( folder+"/"+subfolders[i]);
                    }
                    currentHash=generateFoldersHash("", subScanFolders);
                    if(logger.isDebugEnabled())
                        logger.debug("folder [{}] hash [{}]",folder,currentHash); 
                    hash=DigestUtils.md5DigestAsHex( (hash+currentHash).getBytes() );   
                }                
            }
        }
        return hash;
    }

    public long getInitDelay()
    {
        return initDelay;
    }

    public void setInitDelay( long initDelay )
    {
        this.initDelay = initDelay;
    }

    public List<String> getAdditionalConfigurations()
    {
        return additionalConfigurations;
    }

    public void setAdditionalConfigurations( List<String> additionalConfigurations )
    {
        this.additionalConfigurations = additionalConfigurations;
    }

    public Pattern getHashOnlyFiles()
    {
        return hashOnlyFiles;
    }

    public void setHashOnlyFiles( Pattern hashOnlyFiles )
    {
        this.hashOnlyFiles = hashOnlyFiles;
    }

    public Pattern getIgnoredFiles()
    {
        return ignoredFiles;
    }

    public void setIgnoredFiles( Pattern ignoredFiles )
    {
        this.ignoredFiles = ignoredFiles;
    }

}
