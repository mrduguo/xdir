package org.duguo.xdir.core.internal.resource.jdbc;


import org.duguo.xdir.core.internal.app.BestPathMatchApplication;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryFactory;
import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class JdbcManagerApplication extends BestPathMatchApplication implements BundleContextAware
{
    private static final Logger logger = LoggerFactory.getLogger( JdbcManagerApplication.class );

    private RepositoryFactory repositoryFactory;
    private BundleContext bundleContext;
    
    @Override
    protected int handleInSession( ModelImpl model, int handleStatus ) throws Exception
    {
        try{
            return super.handleInSession( model, handleStatus );
        }finally{
            if(model.getMap().containsKey( "connection")){
                ((Connection)model.get( "connection" )).close();
                if(logger.isDebugEnabled())
                    logger.debug( "connection closed" );
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String,Map> listDataSources() throws Exception{
        Map<String,Map> dataSources=new HashMap<String,Map>();
        scanJcrDerbyDb(dataSources);
        scanDataSources(dataSources);
        return dataSources;
    }

    public Connection retriveConnection(ModelImpl model,String dataSourceName,String username,String password) throws Exception{
        ServiceReference[] datasourceReferences = bundleContext.getServiceReferences( DataSource.class.getName(), null );
        if(datasourceReferences!=null && datasourceReferences.length>0){
            for(ServiceReference datasourceReference:datasourceReferences){
                String beanName=(String)datasourceReference.getProperty( "beanName" );
                if(dataSourceName.equals( beanName )){
                    DataSource dataSource=(DataSource)bundleContext.getService( datasourceReference );
                    if(dataSource!=null){
                        try{
                            Connection connection=null;
                            if(username!=null){
                                connection=dataSource.getConnection(username,password);
                            }else{
                                connection=dataSource.getConnection();
                            }
                            model.put( "connection", connection );
                            
                            if(logger.isDebugEnabled())
                                logger.debug( "connection retrived from datasource [{}]", dataSourceName);
                            return connection;
                        }finally{
                            bundleContext.ungetService( datasourceReference );
                        }                        
                    }                     
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void scanDataSources( Map<String, Map> dataSources ) throws Exception
    {
        ServiceReference[] datasourceReferences = bundleContext.getServiceReferences( DataSource.class.getName(), null );
        if(datasourceReferences!=null){
            for(ServiceReference datasourceReference:datasourceReferences){
                String beanName=(String)datasourceReference.getProperty( "beanName" );
                if(beanName!=null){
                    String vendor=(String)datasourceReference.getProperty( "vendor" );
                    if(vendor==null){
                        vendor="general";
                    }
                    addDataSourceEntry( dataSources, beanName, vendor);
                }
            }
        }        
    }

    @SuppressWarnings("unchecked")
    private void scanJcrDerbyDb( Map<String, Map> dataSources ) throws Exception
    {
        Map repositoryList=new HashMap();
        repositoryList.put( "action", "repositoryList" );
        repositoryFactory.getRepository( repositoryList );

        Map repositories=(Map)repositoryList.get( "repositories" );
        for ( Object repositoryObj : repositories.keySet())
        {
            String repositoryName=(String)repositoryObj;
            Map repositoryProperties=(Map)repositories.get( repositoryName );
            String repositoryBase=(String)repositoryProperties.get("repositoryBase");
            if(repositoryBase!=null){
                File repositoryVersionFolder=new File(repositoryBase,"version/db");
                addDerbyDbIfExist(dataSources,"jcr_"+repositoryName+"_version",repositoryVersionFolder);
                
                File repositoryWorkspacesFolder=new File(repositoryBase,"workspaces");
                if(repositoryWorkspacesFolder.exists()){
                    for(String workspaceName:repositoryWorkspacesFolder.list()){
                        File workspaceDbFolder=new File(repositoryWorkspacesFolder,workspaceName+"/db");
                        addDerbyDbIfExist(dataSources,"jcr_"+repositoryName+"_"+workspaceName,workspaceDbFolder);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addDerbyDbIfExist( Map<String, Map> dataSources, String dbKey, File dbFolder )
    {
        if(dbFolder.exists() && dbFolder.isDirectory()){
            addDataSourceEntry( dataSources, dbKey, "derby", "path", dbFolder.getPath() );
        }        
    }

    @SuppressWarnings("unchecked")
    private void addDataSourceEntry( Map<String, Map> dataSources, String dbKey,String vendor, String... params)
    {
        Map dsProperties=new HashMap();
        dsProperties.put( "vendor", vendor);
        if(params.length>1){
            for(int i=0;i<params.length/2;i++){
                dsProperties.put( params[i*2], params[i*2+1]);                
            }
        }
        dataSources.put( dbKey, dsProperties);
    }

    public void setRepositoryFactory( RepositoryFactory repositoryFactory )
    {
        this.repositoryFactory = repositoryFactory;
    }

    public void setBundleContext( BundleContext bundleContext )
    {
        this.bundleContext=bundleContext;
        
    }

}
