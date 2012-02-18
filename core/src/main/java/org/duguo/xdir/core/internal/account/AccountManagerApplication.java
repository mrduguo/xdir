package org.duguo.xdir.core.internal.account;


import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.RowIterator;

import org.duguo.xdir.jcr.utils.JcrNodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.security.AccountService;
import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.spi.security.Role;
import org.duguo.xdir.spi.security.User;
import org.duguo.xdir.core.internal.app.JcrTemplateAwareApplication;
import org.duguo.xdir.core.internal.jcr.SessionCallback;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.util.bean.BeanUtil;
import org.duguo.xdir.util.bean.BeanUtil.GetterSetterCallback;
import org.duguo.xdir.util.codec.IdUtil;


public class AccountManagerApplication extends JcrTemplateAwareApplication implements AccountService
{
    private static final Logger logger = LoggerFactory.getLogger( AccountManagerApplication.class );
    

	public static final String NODE_TYPE_USER = "user";
    
    private String accountModelKey="account";
    private String accountNewUserBase="/websites/admin/org/users/new";
    private String accountSqlQuery="SELECT * FROM [nt:base] as n WHERE _user_id='%1$s' AND _type='user' AND ISDESCENDANTNODE(n,[/websites/admin/org/users])";
    private String loginSqlQuery="SELECT * FROM [nt:base] as n WHERE _user_name='%1$s' AND _type='login' AND ISDESCENDANTNODE(n,[/websites/admin/org/users])";
	private String groupRoleQuery="SELECT _role FROM [nt:base] as n WHERE _user_id='%1$s' AND _type='member' AND ISDESCENDANTNODE(n,[%2$s])";
    
	public int login(final LoginEvent loginEvent){
		final int[] result=new int[]{LoginEvent.LOGIN_USER_NAME_NOT_FOUND};
		doInDefaultSession(new SessionCallback() { public void execute(Session session) throws Exception {
			Node loginNode=retriveLogin(session,loginEvent.getUserName());
			if(loginNode!=null){
				String storedPassword=null;
				if(loginEvent.getPassword()!=null){
					storedPassword=JcrNodeUtils.getPropertyIfExist(loginNode, "_password");
				}
				if(loginEvent.getPassword()==null || (storedPassword!=null && storedPassword.equals(loginEvent.getPassword()))){
					Node accountNode=loginNode.getParent().getParent();
					UserImpl user=new UserImpl();
					
					String role=JcrNodeUtils.getPropertyIfExist(loginNode, "_role");
					if(role==null){
						role=JcrNodeUtils.getPropertyIfExist(accountNode, "_role");
					}
					if(role!=null){
						user.setRole(Integer.parseInt(role));
					}

					String userId=JcrNodeUtils.getPropertyIfExist(accountNode, "_user_id");
					user.setUserId(userId);

					String displayName=JcrNodeUtils.getPropertyIfExist(accountNode, "_title");
					user.setDisplayName(displayName);

					PropertyIterator restrictions = loginNode.getProperties("_restriction*");
					if(restrictions.hasNext()){
						Map<Object, Object> attributes = loginEvent.getAttributes();
						if(attributes==null){
							attributes=new HashMap<Object, Object>();
							loginEvent.setAttributes(attributes);
						}
						while(restrictions.hasNext()){
							Property property = restrictions.nextProperty();
							attributes.put(property.getName(), property.getString());
						}
			    		if(logger.isDebugEnabled())
			    			logger.debug("_restriction* loaded for [{}]",loginEvent.getUserName());
					}
					loginEvent.setUser(user);
					result[0]=LoginEvent.LOGIN_SUCCESS;
					
		    		if(logger.isDebugEnabled())
		    			logger.debug("login for [{}] result [userId:"+userId+",displayName:"+displayName+",role:"+role+"]",loginEvent.getUserName());
				}else{
					result[0]=LoginEvent.LOGIN_WRONG_PASSWORD;
				}
			}else{
				if(logger.isDebugEnabled())
					logger.debug("login not found for userName [{}]",loginEvent.getUserName());
			}
		}});
		return result[0];
	}

	public void onLogin(final LoginEvent loginEvent){
		final User user=loginEvent.getUser();
		if(!(user instanceof UserImpl)){
			doInDefaultSession(new SessionCallback() { public void execute(Session session) throws Exception {
				Node account=retriveAccount(session,user.getUserId());
				if(account==null){
					Map<String, String> userAttributes =new HashMap<String, String>();
					
			    	String email=null;
			    	if(BeanUtil.hasField(user,"email")){
			    		email=BeanUtil.retriveFieldValue(user, String.class, "email");
			    	}
			    	if(email==null && user.getUserId().indexOf("@")>0){
			    		email= user.getUserId();
			    	}
			    	if(email!=null){
			        	JcrNodeUtils.setNodeProperty(account, "_email", email);
						userAttributes.put("_email", email);
			    	}
			    	
			    	userAttributes.put("_title", user.getDisplayName());
			    	userAttributes.put("_role", String.valueOf(user.getRole()));

					userAttributes.put("_source", loginEvent.getName());
					account=createAccount(user.getUserId(),userAttributes,session);
				}else{
		    		GetterSetterCallback<String> displayNameGetterSetter = BeanUtil.getBeanGetterSetterIfAvailable(user, String.class, "displayName");
			    	if(displayNameGetterSetter!=null){
			    		String realDisplayName=JcrNodeUtils.getPropertyIfExist(account, "_title");
			    		if(realDisplayName!=null){
			    			displayNameGetterSetter.set(realDisplayName);
			    		}
			    	}
		    		GetterSetterCallback<Integer> roleGetterSetter = BeanUtil.getBeanGetterSetterIfAvailable(user, Integer.class, "role");
			    	if(roleGetterSetter!=null){
			    		String realRole=JcrNodeUtils.getPropertyIfExist(account, "_role");
			    		if(realRole!=null){
			    			roleGetterSetter.set(Integer.parseInt(realRole));
			    		}
			    	}
				}
			}});
		}
		if(logger.isDebugEnabled())
			logger.debug("onLogin for userId [{}] via [{}]",user.getUserId(),loginEvent.getName());
	}

	public int groupRole(final String userId,final String groupPath, Object... aclInfo){
		final int[] result=new int[]{Role.ANONYMOUS};
		if(aclInfo.length>1 && aclInfo[0] instanceof Session){
			try {
				return retriveRole((Session)aclInfo[0], userId,groupPath);
			} catch (Exception e) {
				logger.error("failed to retrive group role",e);
			}
		}else{
			doInDefaultSession(new SessionCallback() { public void execute(Session session) throws Exception {
				result[0]=retriveRole(session, userId,groupPath);
			}});
		}
		return result[0];
	}

	public Object loadUser(final String userId) {
		final Object[] result=new Object[1];
		doInDefaultSession(new SessionCallback() { public void execute(Session session) throws Exception {
			Node userNode=retriveAccount(session,userId);
			result[0]=userNode;
		}});
		return result[0];
	}

	public Object createUser(final String userId, final Map<String, String> userAttributes) {
		final Object[] result=new Object[1];
		doInDefaultSession(new SessionCallback() { public void execute(Session session) throws Exception {
			Node userNode=retriveAccount(session,userId);
			if(userNode==null){
				userNode=createAccount(userId,userAttributes,session);
				result[0]=userNode;
			}else{
				logger.warn("Cannot create user, user id [{}] already exist",userId);
			}
		}});
		return result[0];
	}

	public Object loadLogin(final String userName) {
		final Object[] result=new Object[1];
		doInDefaultSession(new SessionCallback() { public void execute(Session session) throws Exception {
			Node loginNode=retriveLogin(session,userName);
			result[0]=loginNode;
		}});
		return result[0];
	}

	public Object createLogin(final String userId, final String userName,final Map<String, String> loginAttributes) {
		final Object[] result=new Object[1];
		doInDefaultSession(new SessionCallback() { public void execute(Session session) throws Exception {
			Node userNode=retriveAccount(session,userId);
			if(userNode!=null){
				Node loginNode=retriveLogin(session,userName);
				if(loginNode==null){
					String loginId=IdUtil.normalizePathId(userName);
					loginNode=JcrNodeUtils.loadOrCreatePath(userNode, "logins/"+loginId);
			    	JcrNodeUtils.setNodeProperty(loginNode, "_type", "login");
			    	JcrNodeUtils.setNodeProperty(loginNode, "_user_name", userName);
			    	for(Map.Entry<String, String> attribute:loginAttributes.entrySet()){
			        	JcrNodeUtils.setNodeProperty(loginNode, attribute.getKey(), attribute.getValue());
			    	}
			    	session.save();
					result[0]=loginNode;
				}else{
					logger.warn("Cannot create login, user name [{}] already exist",userName);
				}
				result[0]=loginNode;
				result[0]=userNode;
			}else{
				logger.warn("Cannot create login, user id [{}] not found",userId);
			}
		}});
		return result[0];
	}

	public int retriveRole(Session session,String userId,String groupPath) throws Exception{
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		String finalQuery=String.format(groupRoleQuery,userId,groupPath);
		if(logger.isDebugEnabled())
			logger.debug("retrive role sql [{}]",finalQuery);
		RowIterator rowIterator = queryManager.createQuery(finalQuery, Query.JCR_SQL2).execute().getRows();
		
		int role=0;
		if(rowIterator.hasNext()){
			Value[] values = rowIterator.nextRow().getValues();
			if(values.length>0){
				role=(int)rowIterator.nextRow().getValues()[0].getLong();				
			}else{
				role=Role.USER;
			}
			if(rowIterator.hasNext()){
				logger.warn("more roles at group {}",groupPath);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("retrived role [{}] in group [{}]",role,groupPath);
    	return role;
    }

	public void loadAccount(ModelImpl model) throws Exception{
    	Node account=retriveAccount(model.getSession(),model.getUserId());
    	model.put(accountModelKey, account);
    }

    public Node createAccount(String userId,Map<String, String> userAttributes,Session session)  throws Exception{
    	StringBuilder userPath = buildUserPath(userId);
    	Node account=JcrNodeUtils.loadOrCreatePath(session.getRootNode(), userPath.substring(1));    	
    	JcrNodeUtils.setNodeProperty(account, "_type", "user");
    	JcrNodeUtils.setNodeProperty(account, "_user_id", userId);
    	
    	for(Map.Entry<String, String> attribute:userAttributes.entrySet()){
        	JcrNodeUtils.setNodeProperty(account, attribute.getKey(), attribute.getValue());    		
    	}
    	session.save();
		return account;
	}

	public Node retriveAccount(Session session,String userId) throws Exception{
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		String finalQuery=String.format(accountSqlQuery,userId);
		if(logger.isDebugEnabled())
			logger.debug("retrive account sql [{}]",finalQuery);
		NodeIterator nodeIterator = queryManager.createQuery(finalQuery, Query.JCR_SQL2).execute().getNodes();
		Node account=null;
		if(nodeIterator.hasNext()){
			account=nodeIterator.nextNode();
			if(nodeIterator.hasNext()){
				logger.error("account found at path {}",account.getPath());
				while(nodeIterator.hasNext()){
					logger.error("account found at path {}",nodeIterator.nextNode().getPath());
				}				
				throw new RuntimeException("Multi account found for ["+userId+"]");
			}
		}
		if(account==null && logger.isDebugEnabled())
			logger.debug("account not found for [{}]",userId);
    	return account;
    }

	public Node retriveLogin(Session session,String userName) throws Exception{
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		String finalQuery=String.format(loginSqlQuery,userName);
		if(logger.isDebugEnabled())
			logger.debug("retrive login sql [{}]",finalQuery);
		NodeIterator nodeIterator = queryManager.createQuery(finalQuery, Query.JCR_SQL2).execute().getNodes();
		Node loginNode=null;
		if(nodeIterator.hasNext()){
			loginNode=nodeIterator.nextNode();
			if(nodeIterator.hasNext()){
				logger.error("login found at path {}",loginNode.getPath());
				while(nodeIterator.hasNext()){
					logger.error("login found at path {}",nodeIterator.nextNode().getPath());
				}				
				throw new RuntimeException("Multi login found for ["+userName+"]");
			}
		}
		if(loginNode==null && logger.isDebugEnabled())
			logger.debug("login not found for [{}]",userName);
    	return loginNode;
    }


	protected StringBuilder buildUserPath(String userId) {
		StringBuilder userPath=new StringBuilder(accountNewUserBase);
		String email=null;
		if(userId.indexOf("@")>0){
			email=userId;
		}
		if(email!=null){
			if(logger.isDebugEnabled())
				logger.debug("user email [{}]",email);
			
			String[] emailDomains=email.substring(email.indexOf("@")+1).split("\\.");
			for(int i=emailDomains.length;i>0;i--){
				userPath.append("/");
				userPath.append(emailDomains[i-1]);
			}
			userId=email.substring(0,email.indexOf("@"));
		}
        userPath.append("/");
        userPath.append(userId);
		if(logger.isDebugEnabled())
			logger.debug("built user path [{}]",userPath.toString());
		return userPath;
	}

	public String getAccountModelKey() {
		return accountModelKey;
	}

	public void setAccountModelKey(String accountModelKey) {
		this.accountModelKey = accountModelKey;
	}

	public String getAccountNewUserBase() {
		return accountNewUserBase;
	}

	public void setAccountNewUserBase(String accountNewUserBase) {
		this.accountNewUserBase = accountNewUserBase;
	}

	public String getAccountSqlQuery() {
		return accountSqlQuery;
	}

	public void setAccountSqlQuery(String accountSqlQuery) {
		this.accountSqlQuery = accountSqlQuery;
	}

	public String getGroupRoleQuery() {
		return groupRoleQuery;
	}

	public void setGroupRoleQuery(String groupRoleQuery) {
		this.groupRoleQuery = groupRoleQuery;
	}
}
