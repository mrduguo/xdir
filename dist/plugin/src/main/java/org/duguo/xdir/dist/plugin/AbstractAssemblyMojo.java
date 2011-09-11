package org.duguo.xdir.dist.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.duguo.xdir.dist.plugin.helper.ProjectHelper;

public abstract class AbstractAssemblyMojo extends AbstractMojo {

	 

	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;
	
	/**
	 * 
	 * @component
	 */
	protected ProjectHelper projectHelper; 

	/**
	 * assembly working dir
	 * 
	 * @parameter default-value="${basedir}/target/server"
	 */
	protected String assemblyWorkingDir;


}