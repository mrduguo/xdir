package org.duguo.xdir.core.internal.utils;


import org.springframework.util.Assert;

public class JcrRepositoryUtils
{
    
    public static RepoPath parseRepoPath(String path){
        return new RepoPath(path);
    }
    
    public static class RepoPath{
        private String repositoryName;
        private String workspaceName;
        private String absPath;
        
        public RepoPath(String path){
            String[] paths=path.split( "/", 3 );
            Assert.state(paths.length==3 ,"path ["+path+"] is invalid jcr path"); 
            repositoryName=paths[0];
            workspaceName=paths[1];
            absPath="/"+paths[2];
        }
        
        public String getRepositoryName()
        {
            return repositoryName;
        }
        public String getAbsPath()
        {
            return absPath;
        }

        public String getWorkspaceName()
        {
            return workspaceName;
        }
        
    }

}
