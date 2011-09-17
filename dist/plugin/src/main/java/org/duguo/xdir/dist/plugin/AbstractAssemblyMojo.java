package org.duguo.xdir.dist.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

public abstract class AbstractAssemblyMojo extends AbstractMojo {

	 

	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;


}