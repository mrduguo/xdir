package org.duguo.xdir.dist.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.duguo.xdir.dist.plugin.helper.MD5Helper;

public abstract class AbstractUnpackMojo extends AbstractAssemblyMojo {

	/**
	 * Local maven repository.
	 * 
	 * @parameter expression="${localRepository}"
	 * @required
	 * @readonly
	 */
	protected ArtifactRepository localRepository;

	/**
	 * generate md5 checksum for all artifact
	 * 
	 * @parameter expression="${generateMD5Checksum}" default-value="false"
	 */
	protected boolean generateMD5Checksum;

	/**
	 * allow multiple version to be packaged, otherwise, latest version will be
	 * used
	 * 
	 * @parameter expression="${allowMultipleVersion}" default-value="false"
	 */
	protected boolean allowMultipleVersion;

	/**
	 * assembly resources, those resources will override any exist files in assembly working dir
	 * 
	 * @parameter default-value="${basedir}/src/assembly"
	 */
	protected String assemblyResourceDir;

    /**
     * bundles base path
     * 
     * @parameter default-value="bundles"
     */
    protected String bundlesBasePath;

	/**
	 * exclude any matched artifact id [groupId:artifactid:version]
	 * 
	 * @parameter
	 */
	protected String[] excludeDependencies;



	protected void unpackDependencyResource(Artifact artifact) throws Exception {
		if(getLog().isDebugEnabled()){
			getLog().debug("UNPACK ARTIFACT:"+artifact.getId());			
		}
		File zipFile=artifact.getFile();
		if(zipFile!=null){
	        ZipInputStream zipStream = new ZipInputStream(new FileInputStream( zipFile));
	        ZipEntry ze = null;
	        while ((ze = zipStream.getNextEntry()) != null) {
	            if(!ze.getName().startsWith("META-INF")){
	                File outFile = new File(assemblyWorkingDir, ze.getName());
	                if (ze.isDirectory()) {
	                    if(!outFile.exists()){
	                        outFile.mkdirs();
	                    }
	                } else {
	                    if (!outFile.getParentFile().exists()) {
	                        outFile.getParentFile().mkdirs();
	                    }
	                    FileOutputStream fout = new FileOutputStream(outFile);
	                    for (int c = zipStream.read(); c != -1; c = zipStream.read()) {
	                        fout.write(c);
	                    }
	                    zipStream.closeEntry();
	                    fout.close();
	                }
	            }
	        }
		}
	}

	protected void doCopyArtifact(File targetFolder, Artifact artifact)
			throws Exception {
		if(getLog().isDebugEnabled()){
			getLog().debug("COPY ARTIFACT:"+artifact.getId()+" to ["+targetFolder.getPath()+"]");			
		}
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		File sourceFile=artifact.getFile();
		File targetFile = new File(targetFolder, sourceFile.getName());
		FileUtils.copyFile(sourceFile, targetFile);

		if (generateMD5Checksum) {
			File md5TargetFile = new File(targetFile.getPath() + ".md5");
			MD5Helper.storeMD5Checksum(sourceFile, md5TargetFile);
		}
	}

	protected boolean isKeyStartInArray(String key, String[] targetArray) {
		if (targetArray != null && targetArray.length > 0) {
			for (String includeString : targetArray) {
				if (key.startsWith(includeString)) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	protected boolean isVersionNewer(ArtifactVersion selectedVersion,
			ArtifactVersion selectedVersion2) {
		return selectedVersion.compareTo(selectedVersion2) > 0;
	}

	protected void copyAssemblyResource() throws Exception {
		File assemblyDir = new File(assemblyResourceDir);
		copyDirectoryStructure(assemblyDir, new File(assemblyWorkingDir));
	}

	protected void copyDirectoryStructure(File sourceDir, File targetDir)
			throws Exception {
		if (sourceDir.exists() && sourceDir.isDirectory()) {
			DirectoryScanner directoryScanner = new DirectoryScanner();
			directoryScanner.addDefaultExcludes();
			directoryScanner.setBasedir(sourceDir);
			directoryScanner.scan();
			for (String dir : directoryScanner.getIncludedDirectories()) {
				File dirFile = new File(targetDir, dir);
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				}
			}
			for (String file : directoryScanner.getIncludedFiles()) {
				FileUtils.copyFile(new File(sourceDir, file), new File(
						targetDir, file));
			}
		}
	}

	protected void clearPackDir() {
		File packFileDir = new File(assemblyWorkingDir);
		if (packFileDir.exists() && packFileDir.isDirectory()) {
			try {
				FileUtils.deleteDirectory(packFileDir);
			} catch (IOException ignore) {
				getLog().warn(ignore.getMessage(), ignore);
			}
		}
	}


    protected boolean isResourcesScope( String scope )
    {
        return "resource".equals( scope );
    }


    protected boolean isBootScope( String scope )
    {
        return "boot".equals( scope );
    }

    protected boolean isDefaultScope( String scope )
    {
        return Artifact.SCOPE_COMPILE.equals( scope );
    }

	protected boolean inExcludeList(String id) {
		if (excludeDependencies != null && excludeDependencies.length > 0) {
			for (String exclude : excludeDependencies) {
				if (id.matches(exclude)) {
					getLog().info("excluded bundle " + id);
					return true;
				}
			}
		}
		return false;
	}
}