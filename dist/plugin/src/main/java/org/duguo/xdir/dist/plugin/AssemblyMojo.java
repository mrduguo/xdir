package org.duguo.xdir.dist.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.tar.TarArchiver;
import org.codehaus.plexus.archiver.tar.TarArchiver.TarCompressionMethod;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;

/**
 * pack the XDir binary distribution as zip or tar.gz
 * 
 * @goal assembly
 * @phase package
 */
public class AssemblyMojo extends AbstractAssemblyMojo {

	/**
	 * enable zip output file with value true or classifier, e.g. bin
	 * 
	 * @parameter expression="${zip}" default-value="true"
	 */
	private String zip;

	/**
	 * enable tar.gz output file with value true or classifier, e.g. bin
	 * 
	 * @parameter expression="${tar}"
	 */
	private String tar;

	/**
	 * attach output artifact to current build project
	 * 
	 * @parameter expression="${attachArtifact}" default-value="true"
	 */
	private boolean attachArtifact;

	/**
	 * shell script, which will be marked as executable. 
	 * 
	 * @parameter
	 */
	protected String[] shellScripts;
	

	/**
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			packFormats();
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void packFormats() throws Exception {
		if (zip != null) {
			ZipArchiver archiver = new ZipArchiver();
			archiver.setCompress(true);
			addAdditionalFormat(archiver, "zip", zip.trim());
		}
		if (tar != null) {
			TarArchiver archiver = new TarArchiver();
			TarCompressionMethod tarCompressionMethod = new TarCompressionMethod();
			tarCompressionMethod.setValue("gzip");
			archiver.setCompression(tarCompressionMethod);
			addAdditionalFormat(archiver, "tar.gz", tar.trim());
		}
	}

	private void addAdditionalFormat(Archiver archiver, String artifactType,
			String artifactClassifier) throws Exception {
		File baseWorkingDirFile=new File(assemblyWorkingDir);
		if (artifactClassifier.length() == 0
				|| artifactClassifier.equals("true")) {
			artifactClassifier = null;
		}
		String artifactClassifierStr;
		if (artifactClassifier == null) {
			artifactClassifierStr = "";
		} else {
			artifactClassifierStr = "-" + artifactClassifier;
		}

		File archiverFile = new File(project.getBasedir(), "target/"
				+ project.getArtifactId() + "-" + project.getVersion()
				+ artifactClassifierStr + "." + artifactType);
		archiver.setDestFile(archiverFile);
		archiver.setIncludeEmptyDirs(true);
		
		addFilesToArchiver(archiver, baseWorkingDirFile);
		
		archiver.createArchive();
		if (archiverFile.length() > 0) {
			if (attachArtifact) {
				projectHelper.attachArtifact(project, artifactType, artifactClassifier,archiverFile);
			}
			if(getLog().isDebugEnabled()){
				for(Object fileName:archiver.getFiles().keySet()){
					getLog().debug("PACKED:"+archiverFile.getName()+":"+fileName);	
					
				}		
			}
		}
	}

	private void addFilesToArchiver(Archiver archiver, File baseWorkingDirFile) throws ArchiverException {
		archiver.addDirectory(baseWorkingDirFile,null,shellScripts);
		
		DirectoryScanner directoryScanner = new DirectoryScanner();
		directoryScanner.setIncludes(shellScripts);
		directoryScanner.setBasedir(baseWorkingDirFile);
		directoryScanner.scan();
		for (String relativeFilePath : directoryScanner.getIncludedFiles()) {
			archiver.addFile(new File(baseWorkingDirFile, relativeFilePath), relativeFilePath, 493); // mode is 755 = 7*8*8 + 5*8 + 5 = 493
		}
	}

}