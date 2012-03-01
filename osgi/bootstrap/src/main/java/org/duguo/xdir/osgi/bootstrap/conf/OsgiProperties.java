package org.duguo.xdir.osgi.bootstrap.conf;

import java.io.File;



public class OsgiProperties extends AbstractProperties
{

    private static final long serialVersionUID = -2348510592741219080L;

    public void putXdirOsgiDebug(String value){
        put( KEY_XDIR_OSGI_CMD_DEBUG, value );
    }
    public void putXdirOsgiConsole(String value){
        put( KEY_XDIR_OSGI_CMD_CONSOLE, value );
    }
    public void putXdirOsgiClean(String value){
        put( KEY_XDIR_OSGI_CMD_CLEAN, value );
    }
  
    public void setXdirDirHome(String value){
        putNewValueIfNotExist(KEY_XDIR_DIR_HOME,value);
    }
  
    public String getXdirDirHome(){
        String dir=getProperty(KEY_XDIR_DIR_HOME);
        return dir;
    }
    
    public String retriveXdirDirConf(){
        return retriveHomeRelatedDir( KEY_XDIR_DIR_CONF,"data/classes");
    }
    
    public String retriveXdirDirData(){
        return retriveHomeRelatedDir(KEY_XDIR_DIR_DATA,"data");
    }
    
    public String retriveXdirDirVar(){
        return retriveHomeRelatedDir(KEY_XDIR_DIR_VAR,"var");
    }
    
    public String retriveXdirDirLogs(){
        String dir=getProperty(KEY_XDIR_DIR_LOGS);
        if(dir==null){
            dir=retriveXdirDirVar()+"/logs";
            System.setProperty( KEY_XDIR_DIR_LOGS, dir );
            // setup default derby.log location
            if(System.getProperty("derby.stream.error.file")==null){
                System.setProperty("derby.stream.error.file",dir+"/derby.log");
            }
        }
        return dir;
    }
    
    public String retriveXdirOsgiBundlesCache(){
        String dir=getProperty(KEY_XDIR_OSGI_BUNDLES_CACHE);
        if(dir==null){
            dir=retriveXdirDirVar()+"/bundlescache";
            putNewValueIfNotExist( KEY_XDIR_OSGI_BUNDLES_CACHE, dir );
        }
        return dir;
    }
    
    public String retriveXdirOsgiCmdConf(){
        String confFileName=getProperty(KEY_XDIR_OSGI_CMD_CONF);
        if(confFileName==null){
            confFileName=retriveXdirDirConf()+"/"+VALUE_XDIR_OSGI_CMD_CONF;
        }else{
            // to support relative path, equal to ${xdir.dir.conf}/confFileName
            File confFile=new File( confFileName );
            if(!confFile.exists()){
                confFileName=retriveXdirDirConf()+"/"+confFileName;             
            }
        }
        put( KEY_XDIR_OSGI_CMD_CONF, confFileName );
        return confFileName;
    }
  
    public String getXdirOsgiCmdPoll(){
        String value=getProperty(KEY_XDIR_OSGI_CMD_POLL);
        return value;
    }
  
    public void setXdirOsgiCmdPoll(String value){
        putNewValueIfNotExist(KEY_XDIR_OSGI_CMD_POLL,value);
    }
  
    public String getXdirOsgiCmdTimeout(){
        String value=getProperty(KEY_XDIR_OSGI_CMD_TIMEOUT);
        return value;
    }
  
    public void setXdirOsgiCmdTimeout(String value){
        putNewValueIfNotExist(KEY_XDIR_OSGI_CMD_TIMEOUT,value);
    }
  
    public String getXdirOsgiCmdImpl(String extension){
        String value=getProperty(KEY_XDIR_OSGI_CMD_IMPL+"."+extension);
        return value;
    }
  
    public void setXdirOsgiCmdImpl(String extension,String value){
        putNewValueIfNotExist(KEY_XDIR_OSGI_CMD_IMPL+"."+extension,value);
    }
    
    public String retriveXdirOsgiBundlesBase(){
        return retriveHomeRelatedDir(KEY_XDIR_DIR_BUNDLES,"bundles");
    }
    
    public String retriveXdirOsgiBundlesSystem(){
        String groups=getProperty(KEY_XDIR_OSGI_BUNDLES_SYSTEM);
        if(groups==null){
            groups=VALUE_XDIR_OSGI_BUNDLES_SYSTEM;
            putNewValueIfNotExist( KEY_XDIR_OSGI_BUNDLES_SYSTEM, groups );
        }
        return groups;
    }
    
    public String retriveXdirOsgiBundlesUser(){
        String groups=getProperty(KEY_XDIR_OSGI_BUNDLES_USER);
        if(groups==null){
            groups=VALUE_XDIR_OSGI_BUNDLES_USER;
            putNewValueIfNotExist( KEY_XDIR_OSGI_BUNDLES_USER, groups );
        }
        return groups;
    }
    
    public boolean isConsoleEnabled(){
        return isKeyValueTrue(KEY_XDIR_OSGI_CMD_CONSOLE,false);
    }
    
    public boolean isCleanEnabled(){
        return isKeyValueTrue(KEY_XDIR_OSGI_CMD_CLEAN,false);
    }
    
    public boolean isStopCommandEnabled(){
        return isKeyValueTrue(KEY_XDIR_OSGI_CMD_STOP,true);
    }
    
    public boolean isDebugEnabled(){
        return isKeyValueTrue(KEY_XDIR_OSGI_CMD_DEBUG,false);
    }
    

}
