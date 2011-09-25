package org.duguo.xdir.core.internal.jcr;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.resource.FileUploadHttpServletRequest;
import org.duguo.xdir.core.internal.resource.MultipartRequestResolver;
import org.duguo.xdir.core.internal.utils.JcrNodeUtils;
import org.duguo.xdir.core.internal.utils.JcrPathUtils;
import org.duguo.xdir.core.internal.utils.RequestUtils;
import org.duguo.xdir.util.datetime.DateTimeUtil;

public abstract class AbstractUpdateJcrService extends AbstractQueryJcrService{


    private static final Logger logger=LoggerFactory.getLogger(AbstractUpdateJcrService.class);
    private MultipartRequestResolver multipartRequestResolver;
    
	public void exportToFile(ModelImpl model,String nodePath,String targetFolder) throws Exception{
		File targetFile=new File(targetFolder);
		if(!targetFile.exists()){
			targetFile.mkdirs();
		}
		targetFile=new File(targetFile,nodePath.substring(nodePath.lastIndexOf("/")+1)+"-"+DateTimeUtil.currentTimestampKey()+".xml");
		FileOutputStream fileOutputStream=new FileOutputStream(targetFile);
		try{
			model.getSession().exportSystemView(nodePath, fileOutputStream, false, false);
		}finally{
			try{
				fileOutputStream.close();
			}catch(Exception ignore){}
		}
	}	
	
	// updates
	public void createNode(ModelImpl model) throws Exception {
		String path=RequestUtils.getPath( model);
		if(path==null){
			model.setStatus("Path name is required");
		}else{
			path=JcrPathUtils.normalizePathName(path);
			if(!model.getNode().hasNode(path)){
				Node newNode=JcrNodeUtils.loadOrCreatePath(model.getNode(),path);
				path=newNode.getName();
				model.addUpdate("_name", path);
				model.addUpdate(JcrNodeUtils.JCR_NODE_SEARCH,path);
				model.setNode(newNode);
		        String nodeType=RequestUtils.getStringParameter( model, "_type");
		        if(nodeType!=null && nodeType.length()>0){
		            model.addUpdate("_type", nodeType);
		        }
				updateNewNode(model);
			}else{
				model.setStatus("Path name already exist");
			}
		}
	}
	
	public void deleteNode(ModelImpl model) throws Exception {		
		if(RequestUtils.isParamTrue(model,"childrenonly")){
			Node nodeTobeDeleted=model.getNode();
			backupDeletion(model,nodeTobeDeleted);
			NodeIterator nodeIterator=nodeTobeDeleted.getNodes();
			while(nodeIterator.hasNext()){
				Node node=(Node)nodeIterator.next();
				deleteNode(node,model);
			}
		}else{
	        String pathName=RequestUtils.getPath( model);
			try{
				Node nodeTobeDeleted=model.getNode().getNode(pathName);
				backupDeletion(model,nodeTobeDeleted);
				deleteNode(nodeTobeDeleted,model);
			}catch(PathNotFoundException ignore){					
			}
		}
		saveNode(model);
	}

	
	public void copyNode(ModelImpl model) throws Exception{
		boolean includeChildren=RequestUtils.isParamTrue( model, "includechildren");
		Node newNode=null;
		if(includeChildren){
			File tempFile=File.createTempFile("xdir.copy.", ".data");
			try{
				FileOutputStream out=new FileOutputStream(tempFile);
				model.getSession().exportSystemView(model.getNode().getPath(), out, false, false);
				out.close();
				Node tempNode=model.getNode().addNode("copy-"+System.currentTimeMillis());
				importNode(tempNode,tempFile);
				newNode=tempNode.getNodes().nextNode();
				String targetPath = getCopyOrMoveTargetPath(model);
				model.getSession().move(newNode.getPath(), targetPath);
				newNode=(Node)model.getSession().getItem(targetPath);
				model.setNode(newNode);
				tempNode.remove();
			}finally{
				tempFile.delete();
			}
		}else{
			newNode = copyNodeWithoutChildren(model);
			model.setNode(newNode);
		}
		updateNewNode(model);
	}


	public void moveNode(ModelImpl model) throws Exception {
		String oldPathName=RequestUtils.getStringParameter(model, "oldPathName");
		Node sourceNode=model.getNode().getNode(oldPathName);
		model.setNode(sourceNode);
		String targetPath = getCopyOrMoveTargetPath(model);
		model.getSession().move(sourceNode.getPath(), targetPath);
		sourceNode=(Node)model.getSession().getItem(targetPath);
		sourceNode.setProperty("jcr:pathName", sourceNode.getName());
		model.setNode(sourceNode);
	}
	
	public void initProperties(ModelImpl model) throws Exception{
		Map<String, String> properties=RequestUtils.getProperties(model);
		for(Map.Entry<String, String> entry: properties.entrySet()){
			model.addUpdate(entry.getKey(), entry.getValue());
		}
	}
	
	public void initSafeProperties(ModelImpl model,String allowedProperties) throws Exception{
		Map<String, String> properties=RequestUtils.getProperties(model);
		for(Map.Entry<String, String> entry: properties.entrySet()){
			if(entry.getKey().indexOf(":")<0 || allowedProperties.indexOf("["+entry.getKey()+"]")>=0){
				model.addUpdate(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public void storeUpdates(ModelImpl model) throws Exception{
		Node node=model.getNode();
		//versionManager.nodeOnChange(node,model.getUserId());
		Map<String, String> properties=model.getUpdates();
		for(String key:properties.keySet()){
			JcrNodeUtils.setNodeProperty(node,key,properties.get(key));
            if(logger.isDebugEnabled())
                logger.debug( "update property [{}]",key);
		}
		saveNode(model);
    }
    
    public void resolveMultipart(ModelImpl model)throws Exception {
        HttpServletRequest fileUploadHttpServletRequest=multipartRequestResolver.resolveMultipartRequest( model.getRequest() );
        model.setRequest( fileUploadHttpServletRequest );
	}

	public void attachFile(ModelImpl model, String fileFieldName)throws Exception {
	    if(!(model.getRequest() instanceof FileUploadHttpServletRequest)){
	        resolveMultipart( model );
	    }
	    
		String pathName=RequestUtils.getPath( model);
		File file=null;
		boolean deleteAfterStore=true;
		if(pathName==null || pathName.trim().length()==0){
			pathName=(String)model.getRequest().getParameterMap().get(fileFieldName+"fileName");
		}else{
		    // look for local file file first
		    if(pathName.indexOf( File.separator )>0){
		        file=new File(pathName);
		        if(file.exists() && file.isFile()){
	                pathName=file.getName();
	                deleteAfterStore=false;
		        }else{
                    file=null;
		        }
		    }
		}
        if(file==null){
            file=(File)model.getRequest().getParameterMap().get(fileFieldName);
        }
        if(file==null){
            model.setStatus("File is required");
            return;
        }
        
		Node fileNode;
		if(model.getNode().hasNode(pathName)){
			fileNode = model.getNode().getNode(pathName);
			//versionManager.nodeOnChange(fileNode,model.getUserId());
		}else{
			fileNode=JcrNodeUtils.loadOrCreatePath(model.getNode(),pathName);
			fileNode.setPrimaryType( "nt:xfile" );
		}
		model.setNode(fileNode);
		storeFile(model,file,deleteAfterStore);
	}

	
	public void importFile(ModelImpl model, String fileFieldName)throws Exception {
		String pathName=RequestUtils.getPath( model);
		FileInputStream fileInputStream=null;
		try{
			File localFile;
			boolean isMindMap=false;
			if(pathName!=null){
				localFile=new File(pathName);
				if(pathName.toLowerCase().endsWith(".mm")){
					isMindMap=true;
				}
			}else{
				localFile=(File)model.getRequest().getParameterMap().get(fileFieldName);
				String originalFileName=(String)model.getRequest().getParameterMap().get(fileFieldName+"fileName");
				if(originalFileName.toLowerCase().endsWith(".mm")){
					isMindMap=true;
				}
			}
			if(!localFile.exists()){
				model.setStatus("file "+localFile.getAbsolutePath()+" doesn't exist");
				return;
			}
			fileInputStream= new FileInputStream(localFile);
			if(isMindMap){
				getMmImportor().importMm(model.getNode(), fileInputStream);
			}else{
				model.getSession().importXML(model.getNode().getPath(), fileInputStream, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
			}
		}finally{
			if(fileInputStream!=null){
				fileInputStream.close();
			}
		}
		saveNode(model);
	}

	
	
	public void storeFile(ModelImpl model,File file,boolean deleteAfterStore) throws Exception{
        Node contentNode=model.getNode();
        if(contentNode.hasNode( "jcr:content" )){
            contentNode=contentNode.getNode( "jcr:content" );
        }else{
            contentNode=contentNode.addNode( "jcr:content");//, "nt:resource" );
        }
		FileInputStream fileInputStream=null;
		try{
			fileInputStream= new FileInputStream(file);
			JcrNodeUtils.setStreamProperty(contentNode,fileInputStream);
		}finally{
			if(fileInputStream!=null){
				fileInputStream.close();
				if(deleteAfterStore){
				    file.delete();
				}
			}
		}
	}

    public void editNode(ModelImpl model) throws Exception{
        updateTitle(model);
        initProperties(model);
        updateVersionable(model);
        storeUpdates(model);
    }
    
    public void updateVersionable( ModelImpl model ) throws Exception
    {
        String versionable=model.getRequest().getParameter( "_versionable" );
        if(versionable!=null){
            if(logger.isDebugEnabled())
                logger.debug( "updateVersionable with value [{}]", versionable);
            if("true".equals( versionable )){
                model.getNode().addMixin(JcrNodeUtils.JCR_VERSIONING_NAME);
            }else if("false".equals( versionable )){
                model.getNode().removeMixin(JcrNodeUtils.JCR_VERSIONING_NAME);
            }
        }
    }

    public void updateTitle(ModelImpl model) throws Exception{
		String title=RequestUtils.getTitle(model);
		if(title!=null){
		    if(ModelImpl.NULL_STRING.equals( title )){
		        title="";
		    }
            model.addUpdate(JcrNodeUtils.JCR_NODE_TITLE,title);
            internalInitSearch(model,title);
		}
	}

	public void initSearch(ModelImpl model) throws Exception {
		internalInitSearch(model,JcrNodeUtils.getNodeTitle( model.getNode()));
	}



	private void backupDeletion(ModelImpl model,Node nodeTobeDeleted) throws RepositoryException, FileNotFoundException, IOException, PathNotFoundException {
		if(deletionBackupFolder!=null){
			File deletionBackupFile=new File(deletionBackupFolder);
			if(!deletionBackupFile.exists()){
				deletionBackupFile.mkdirs();
			}
			try{
				deletionBackupFile=new File(deletionBackupFile,DateTimeUtil.currentTimestampKey()+"-"+nodeTobeDeleted.getName().replaceAll(":", "-")+"-"+model.getUserId()+".xml");
				FileOutputStream out=new FileOutputStream(deletionBackupFile);
				model.getSession().exportSystemView(nodeTobeDeleted.getPath(), out, false, false);
				out.close();
			}catch(Exception ignore){
				ignore.printStackTrace();
			}
		}
	}
	
	private void deleteNode(Node node,ModelImpl model) throws Exception{
		logger.info("{} deleted by {}",node.getPath(),model.getUserId());
		try{
		    node.remove();
		}catch(Exception ex){
		    logger.error( "failed to delete node",ex );
		    model.setStatus( "failed to delete node:"+ex.getMessage() );
		}
	}

	private void internalInitSearch(ModelImpl model, String title) throws Exception {
		String searchStrings=model.getNode().getName().toLowerCase();
		if(title!=null && title.length()>0 && !searchStrings.equals(title.toLowerCase())){
			Set<String> searchStringsSet=new HashSet<String>();
			searchStringsSet.add(searchStrings);
			for(String searchString:title.toLowerCase().split(" ")){
				searchStringsSet.add(searchString);
			}
			StringBuffer searchStringsSb=null;
			for(String searchString:searchStringsSet){
				if(searchStringsSb==null){
					searchStringsSb=new StringBuffer();
				}else{
					searchStringsSb.append(",");
				}
				searchStringsSb.append(searchString);
			}
			searchStrings=searchStringsSb.toString();
		}
		model.addUpdate(JcrNodeUtils.JCR_NODE_SEARCH,searchStrings);
	}
	private void saveNode(ModelImpl model) throws Exception {
		model.getNode().getSession().save();
		if(logger.isDebugEnabled())
		    logger.debug( "save node [{}]",model.getNode().getPath() );
	}


	private void importNode(Node parentNode, File xmlFile) throws Exception {
		FileInputStream fileInputStream=null;
		try{
			if(!xmlFile.exists()){
				throw new RuntimeException("file "+xmlFile.getAbsolutePath()+" doesn't exist");
			}
			fileInputStream= new FileInputStream(xmlFile);
			parentNode.getSession().importXML(parentNode.getPath(), fileInputStream, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
		}finally{
			if(fileInputStream!=null){
				fileInputStream.close();
			}
		}
	}

	private void updateNewNode(ModelImpl model) throws Exception {
		Calendar now = Calendar.getInstance();
		model.getNode().setProperty("_name", model.getNode().getName());
		model.getNode().setProperty("_created", now);
		model.getNode().setProperty("_createdBy", model.getUserId());
	}
	
	private String getCopyOrMoveTargetPath(ModelImpl model) throws Exception {
		String targetPath=RequestUtils.getPath( model);
		String parentPath=RequestUtils.getStringParameter( model, "parentPath");
		if(parentPath!=null && parentPath.trim().length()>0){
			parentPath=model.internalGetPathFromUrl(parentPath);
			if(parentPath.length()==1){
				targetPath=parentPath+targetPath;
			}else{
				targetPath=parentPath+"/"+targetPath;
			}
		}else{
			if(!targetPath.startsWith("/")){
				targetPath=model.getNode().getParent().getPath()+"/"+targetPath;
			}
		}
		int pathSpliter=targetPath.lastIndexOf("/");
		if(pathSpliter>0){
			JcrNodeUtils.loadOrCreatePath(model.getSession().getRootNode(), targetPath.substring(1,pathSpliter));
		}
		return targetPath;
	}

	
	private Node copyNodeWithoutChildren(ModelImpl model) throws Exception{
		Node newNode;
		String targetPath = getCopyOrMoveTargetPath(model);
		newNode=JcrNodeUtils.loadOrCreatePath(model.getSession().getRootNode(), targetPath.substring(1));
		PropertyIterator propertyIterator=model.getNode().getProperties();
		while(propertyIterator.hasNext()){
			Property property=(Property)propertyIterator.next();
			String propertyName=property.getName();
			if(propertyName.equals("jcr:isCheckedOut")){
				newNode.addMixin(JcrNodeUtils.JCR_VERSIONING_NAME);
			}else if(!JcrNodeUtils.PROTECTED_PROPERTIES.contains(propertyName)){
				newNode.setProperty(propertyName, property.getValue());
			}
		}
		return newNode;
	}

    public void setMultipartRequestResolver( MultipartRequestResolver multipartRequestResolver )
    {
        this.multipartRequestResolver = multipartRequestResolver;
    }
}
