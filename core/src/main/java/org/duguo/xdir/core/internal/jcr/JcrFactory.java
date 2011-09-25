package org.duguo.xdir.core.internal.jcr;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.duguo.xdir.core.internal.model.ModelImpl;

public interface JcrFactory
{
    
    public void bindValueToObject(Node sourceNode,Object beanInstance,String bindPrefix) throws Exception;

    public Node retriveNode( ModelImpl model ) throws RepositoryException;
    
    public Node resolveBestNode( ModelImpl model ) throws RepositoryException;

    public Session retriveSession( ModelImpl model ) throws RepositoryException, LoginException;
    
    public Session retriveSession(String repositoryName,String workspaceName) throws RepositoryException, LoginException;
    
    public Repository retriveRepository( String repositoryName ) throws RepositoryException;
}