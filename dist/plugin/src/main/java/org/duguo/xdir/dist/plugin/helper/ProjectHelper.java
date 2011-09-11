package org.duguo.xdir.dist.plugin.helper;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.MavenProject;

public interface ProjectHelper {
	
	public void attachArtifact(MavenProject rootProject, String artifactType, String artifactClassifier, File artifactFile);
	public MavenProject buildProject(Artifact artifact, MavenProject rootProject, ArtifactRepository localRepository);
}
