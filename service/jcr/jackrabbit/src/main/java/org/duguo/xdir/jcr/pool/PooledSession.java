package org.duguo.xdir.jcr.pool;

import org.apache.commons.pool.KeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.jcr.*;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.retention.RetentionManager;
import javax.jcr.security.AccessControlManager;
import javax.jcr.version.VersionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;

public class PooledSession implements Session {
    
    
    private static final Logger logger = LoggerFactory.getLogger( PooledSession.class );

	private Session session;
	private KeyedObjectPool pool;
	private Object key;
	
	public PooledSession(Session session,KeyedObjectPool pool,Object key) {
		this.session=session;
		this.pool=pool;
		this.key=key;
	}
	
	public void logout() {
	    if(session!=null){
            try
            {
                try{
                    if ( session.hasPendingChanges() )
                    {
                        logger.warn("session has unsaved changes, will refresh session to discard changes");
                        session.refresh( false );
                    }
                    pool.returnObject(key, session);
                } catch (Exception e) {
                    pool.invalidateObject(key, session);
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }finally{
                session=null;
                key=null;
                pool=null;
            }
	    }
	}
	
	public Session getSession(){
		if(session==null){
			throw new RuntimeException("session already been logged out");
		}
		return session;
	}

    // delegate methods to repository
    @SuppressWarnings("deprecation")
    public void addLockToken( String lt )
    {
        getSession().addLockToken( lt );
    }

    public void checkPermission( String absPath, String actions ) throws AccessControlException, RepositoryException
    {
        getSession().checkPermission( absPath, actions );
    }

    public void exportDocumentView( String absPath, ContentHandler contentHandler, boolean skipBinary, boolean noRecurse )
        throws PathNotFoundException, SAXException, RepositoryException
    {
        getSession().exportDocumentView( absPath, contentHandler, skipBinary, noRecurse );
    }

    public void exportDocumentView( String absPath, OutputStream out, boolean skipBinary, boolean noRecurse )
        throws IOException, PathNotFoundException, RepositoryException
    {
        getSession().exportDocumentView( absPath, out, skipBinary, noRecurse );
    }

    public void exportSystemView( String absPath, ContentHandler contentHandler, boolean skipBinary, boolean noRecurse )
        throws PathNotFoundException, SAXException, RepositoryException
    {
        getSession().exportSystemView( absPath, contentHandler, skipBinary, noRecurse );
    }

    public void exportSystemView( String absPath, OutputStream out, boolean skipBinary, boolean noRecurse )
        throws IOException, PathNotFoundException, RepositoryException
    {
        getSession().exportSystemView( absPath, out, skipBinary, noRecurse );
    }

    public AccessControlManager getAccessControlManager() throws UnsupportedRepositoryOperationException,
        RepositoryException
    {
        return getSession().getAccessControlManager();
    }

    public Object getAttribute( String name )
    {
        return getSession().getAttribute( name );
    }

    public String[] getAttributeNames()
    {
        return getSession().getAttributeNames();
    }

    public ContentHandler getImportContentHandler( String parentAbsPath, int uuidBehavior )
        throws PathNotFoundException, ConstraintViolationException, VersionException, LockException,
        RepositoryException
    {
        return getSession().getImportContentHandler( parentAbsPath, uuidBehavior );
    }

    public Item getItem( String absPath ) throws PathNotFoundException, RepositoryException
    {
        return getSession().getItem( absPath );
    }

    @SuppressWarnings("deprecation")
    public String[] getLockTokens()
    {
        return getSession().getLockTokens();
    }

    public String getNamespacePrefix( String uri ) throws NamespaceException, RepositoryException
    {
        return getSession().getNamespacePrefix( uri );
    }

    public String[] getNamespacePrefixes() throws RepositoryException
    {
        return getSession().getNamespacePrefixes();
    }

    public String getNamespaceURI( String prefix ) throws NamespaceException, RepositoryException
    {
        return getSession().getNamespaceURI( prefix );
    }

    public Node getNode( String absPath ) throws PathNotFoundException, RepositoryException
    {
        return getSession().getNode( absPath );
    }

    public Node getNodeByIdentifier( String id ) throws ItemNotFoundException, RepositoryException
    {
        return getSession().getNodeByIdentifier( id );
    }

    @SuppressWarnings("deprecation")
    public Node getNodeByUUID( String uuid ) throws ItemNotFoundException, RepositoryException
    {
        return getSession().getNodeByUUID( uuid );
    }

    public Property getProperty( String absPath ) throws PathNotFoundException, RepositoryException
    {
        return getSession().getProperty( absPath );
    }

    public Repository getRepository()
    {
        return getSession().getRepository();
    }

    public RetentionManager getRetentionManager() throws UnsupportedRepositoryOperationException, RepositoryException
    {
        return getSession().getRetentionManager();
    }

    public Node getRootNode() throws RepositoryException
    {
        return getSession().getRootNode();
    }

    public String getUserID()
    {
        return getSession().getUserID();
    }

    public ValueFactory getValueFactory() throws UnsupportedRepositoryOperationException, RepositoryException
    {
        return getSession().getValueFactory();
    }

    public Workspace getWorkspace()
    {
        return getSession().getWorkspace();
    }

    public boolean hasCapability( String methodName, Object target, Object[] arguments ) throws RepositoryException
    {
        return getSession().hasCapability( methodName, target, arguments );
    }

    public boolean hasPendingChanges() throws RepositoryException
    {
        return getSession().hasPendingChanges();
    }

    public boolean hasPermission( String absPath, String actions ) throws RepositoryException
    {
        return getSession().hasPermission( absPath, actions );
    }

    public Session impersonate( Credentials credentials ) throws LoginException, RepositoryException
    {
        return getSession().impersonate( credentials );
    }

    public void importXML( String parentAbsPath, InputStream in, int uuidBehavior ) throws IOException,
        PathNotFoundException, ItemExistsException, ConstraintViolationException, VersionException,
        InvalidSerializedDataException, LockException, RepositoryException
    {
        getSession().importXML( parentAbsPath, in, uuidBehavior );
    }

    public boolean isLive()
    {
        return getSession().isLive();
    }

    public boolean itemExists( String absPath ) throws RepositoryException
    {
        return getSession().itemExists( absPath );
    }
    
    public void move( String srcAbsPath, String destAbsPath ) throws ItemExistsException, PathNotFoundException,
        VersionException, ConstraintViolationException, LockException, RepositoryException
    {
        getSession().move( srcAbsPath, destAbsPath );
    }

    public boolean nodeExists( String absPath ) throws RepositoryException
    {
        return getSession().nodeExists( absPath );
    }

    public boolean propertyExists( String absPath ) throws RepositoryException
    {
        return getSession().propertyExists( absPath );
    }

    public void refresh( boolean keepChanges ) throws RepositoryException
    {
        getSession().refresh( keepChanges );
    }

    public void removeItem( String absPath ) throws VersionException, LockException, ConstraintViolationException,
        AccessDeniedException, RepositoryException
    {
        getSession().removeItem( absPath );
    }

    @SuppressWarnings("deprecation")
    public void removeLockToken( String lt )
    {
        getSession().removeLockToken( lt );
    }

    public void save() throws AccessDeniedException, ItemExistsException, ReferentialIntegrityException,
        ConstraintViolationException, InvalidItemStateException, VersionException, LockException,
        NoSuchNodeTypeException, RepositoryException
    {
        getSession().save();
    }

    public void setNamespacePrefix( String prefix, String uri ) throws NamespaceException, RepositoryException
    {
        getSession().setNamespacePrefix( prefix, uri );
    }
	

}
