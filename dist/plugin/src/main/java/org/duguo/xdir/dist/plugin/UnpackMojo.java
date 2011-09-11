package org.duguo.xdir.dist.plugin;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * unpack resources/dependencies to assembly working folder
 * 
 * @goal unpack
 * @requiresDependencyResolution
 * @phase generate-sources
 */
public class UnpackMojo extends AbstractUnpackMojo {

	/**
	 * local variable to store dependencies scan result.
	 */
	private Map<String, Artifact> allDependencies = new LinkedHashMap<String, Artifact>();
    private Map<String, String> dependenciesScopes = new LinkedHashMap<String, String>();
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			clearPackDir();

			scanDependencies(project,null);
			
			packDependencies();
			
			copyAssemblyResource();
			
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}


	private void scanDependencies(MavenProject currentProject,String basePath) throws Exception {
		if(getLog().isDebugEnabled()){
			getLog().debug("SCAN:"+currentProject.getArtifact().getId());			
		}
		if (inExcludeList(currentProject.getArtifact().getId())) {
			return;
		}
		for (Object artifactObj : currentProject.getDependencyArtifacts()) {
			Artifact artifact = (Artifact) artifactObj;
            String currentScope=artifact.getScope();
            if(!isDefaultScope(currentScope)){
                if(!currentScope.startsWith( "/" )){
                    currentScope=basePath+"/"+currentScope;
                }
            }else{
                currentScope=basePath;
            }
            
			if (artifact.getType().equals("pom")) {
				MavenProject newProject = projectHelper.buildProject(artifact, project, localRepository);
				scanDependencies(newProject,currentScope);
			}else{
				if(getLog().isDebugEnabled()){
					getLog().debug("SCAN:"+artifact.getId());			
				}
				if (!inExcludeList(artifact.getId())) {
					addSingleArtifact(artifact,currentScope);
				}
			}
		}
	}


	private void addSingleArtifact(Artifact artifact,String basePath) throws Exception {
		String artifactIdentifier = artifact.getGroupId() + ":" + artifact.getArtifactId();
		if (allDependencies.containsKey(artifactIdentifier)) {
			Artifact existArtifact = allDependencies .get(artifactIdentifier);
			if (existArtifact.getVersion().equals( artifact.getVersion())) {
				return;
			}
			getLog().info( "Multiple version detected [" + existArtifact.getId() + "]:[" + artifact.getId() + "]");
			if (allowMultipleVersion) {
				artifactIdentifier += ":" + artifact.getVersion();
			} else {
				if (isVersionNewer(existArtifact.getSelectedVersion(), artifact.getSelectedVersion())) {
					return;
				}
			}
		}
		allDependencies.put(artifactIdentifier, artifact);
		dependenciesScopes.put( artifactIdentifier, basePath!=null?basePath:"" );
	}

	private void packDependencies() throws Exception {
		for (String key : allDependencies.keySet()) {
			Artifact artifact = allDependencies.get(key);
			if (isResourcesScope(artifact.getScope())) {
				unpackDependencyResource(artifact);
			} else if (isBootScope(artifact.getScope())) {
				doCopyArtifact(new File(assemblyWorkingDir, "boot"), artifact);
			} else {
				doCopyArtifact(new File(assemblyWorkingDir, bundlesBasePath + dependenciesScopes.get( key )), artifact);
            }
		}
	}


}