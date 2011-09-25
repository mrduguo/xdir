package org.duguo.xdir.core.internal.account;


import javax.jcr.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.core.internal.model.ModelImpl;


public class ProfilesApplication extends AccountManagerApplication
{
    private static final Logger logger = LoggerFactory.getLogger( ProfilesApplication.class );
    
    protected Node resolveNode( ModelImpl model ) throws Exception
    {
    	String userId=model.getPathInfo().getCurrentPath();
    	if(logger.isDebugEnabled())
    		logger.debug("userId:"+userId);
    	if(userId!=null){
    		Node account=retriveAccount(model.getSession(),userId);
        	if(account!=null){
        		if(logger.isDebugEnabled())
            		logger.debug("account found for userId [{}]",userId);
            	model.put(getAccountModelKey(),account);
        		model.getPathInfo().moveToNextPath();
        		String profileApp=model.getPathInfo().getCurrentPath();
        		if(logger.isDebugEnabled())
            		logger.debug("profile app [{}]",profileApp);
        		if(profileApp!=null){
        			model.setNodeType(profileApp);
            		model.getPathInfo().moveToNextPath();
        		}else{
        			model.setNodeType(NODE_TYPE_USER);        			
        		}
        	}
    	}
        return super.resolveNode(model);
    }

}
