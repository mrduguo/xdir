package org.duguo.xdir.dist.plugin.helper;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.profiles.DefaultProfileManager;
import org.apache.maven.profiles.ProfileManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;


public class DefaultProjectHelper implements ProjectHelper, Contextualizable{

	private MavenProjectBuilder projectBuilder;

	private MavenProjectHelper mavenProjectHelper;
	
	private PlexusContainer plexusContainer;
	private ProfileManager profileManager;


	public MavenProject buildProject(Artifact artifact, MavenProject rootProject, ArtifactRepository localRepository){
		try {
			MavenProject newProject= projectBuilder.buildWithDependencies( artifact.getFile(), localRepository, getProfileManager(rootProject));
			return newProject;
		} catch (ArtifactResolutionException e) {
		} catch (ArtifactNotFoundException e) {
		} catch (ProjectBuildingException e) {
		} catch (Exception e) {
		}
		return null;
	}

	public void attachArtifact(MavenProject rootProject, String artifactType, String artifactClassifier, File artifactFile){
		mavenProjectHelper.attachArtifact(rootProject, artifactType, artifactClassifier, artifactFile);
	}




	public void contextualize(Context context) throws ContextException {
		plexusContainer = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
	}
	

	private ProfileManager getProfileManager(MavenProject rootProject) {
		if (profileManager == null) {
			profileManager = new DefaultProfileManager(plexusContainer, rootProject.getProperties());
		}
		return profileManager;
	}

}
