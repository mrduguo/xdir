package org.duguo.xdir.dist.plugin;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.*;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.tar.TarArchiver;
import org.codehaus.plexus.archiver.tar.TarArchiver.TarCompressionMethod;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * pack the XDir binary distribution as zip or tar.gz
 *
 * @goal assembly
 * @phase package
 */
public class AssemblyMojo extends AbstractAssemblyMojo {


    /**
     * The files will put under root folder
     *
     * @parameter expression="${rootResourceFolder}" default-value="${basedir}/src/main/assembly"
     */
    protected File rootResourceFolder;

    /**
     * Output file name, support zip and tar.gz extension
     *
     * @parameter expression="${outputFile}" default-value="${project.build.directory}/${project.build.finalName}.tar.gz"
     */
    protected File outputFile;

    /**
     * Any script file need set 755 permission, no need to set if they are bin/*.sh.
     *
     * @parameter expression="${executableScripts}"
     */
    protected String[] executableScripts;

    /**
     * attach output artifact to current build project
     *
     * @parameter expression="${attachArtifact}" default-value="true"
     */
    private boolean attachArtifact;


    /**
     * bundle groups to assemble
     *
     * @parameter expression="${dependencies}"
     */
    protected BundleGroup[] bundleGroups;

    /**
     * @component
     * @readonly
     */
    private MavenProjectHelper projectHelper;


    /**
     * @component
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * @component
     * @readonly
     */
    private ArtifactResolver artifactResolver;

    /**
     * @parameter expression="${localRepository}"
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * @component
     */
    private MavenProjectBuilder mavenProjectBuilder;
    /**
     * @component
     */
    protected ArchiverManager archiverManager;


    protected File workingDir;
    protected String outputFileExtension;


    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            init();
            unpackageProjectDependencies();
            copyProjectAssemblyResources();
            collectBundles();
            packFormats();
        } catch (MojoExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void unpackageProjectDependencies() throws Exception {
        if (project.getDependencies().size() > 0) {
            for (org.apache.maven.model.Dependency dependency : project.getDependencies()) {
                Artifact artifact = resolveDependencyAsArtifact(dependency);
                File tempWorkingDir = new File(project.getBuild().getDirectory(), "unpack/" + artifact.getFile().getName());
                tempWorkingDir.mkdirs();
                UnArchiver unArchiver = archiverManager.getUnArchiver(artifact.getFile());
                unArchiver.setSourceFile(artifact.getFile());
                unArchiver.setDestDirectory(tempWorkingDir);
                unArchiver.extract();
                if (!"jar".equals(dependency.getType())) {
                    tempWorkingDir = tempWorkingDir.listFiles()[0];
                }
                FileUtils.copyDirectory(tempWorkingDir, workingDir);
            }
            getLog().error(project.getDependencies().toString());
        }
    }

    private void copyProjectAssemblyResources() throws Exception {
        if (rootResourceFolder.exists()) {
            FileUtils.copyDirectory(rootResourceFolder, workingDir);
        }
    }

    private void init() throws Exception {
        if (outputFile.getName().endsWith("zip")) {
            outputFileExtension = "zip";
        } else if (outputFile.getName().endsWith("tar.gz")) {
            outputFileExtension = "tar.gz";
        } else {
            throw new MojoExecutionException("Not supported file name [" + outputFile.getName() + "], only support zip and tar.gz file extension");
        }
        workingDir = new File(outputFile.getAbsoluteFile().getParentFile(), outputFile.getName().substring(0, outputFile.getName().length() - outputFileExtension.length() - 1));
    }

    private void collectBundles() throws Exception {
        for (BundleGroup bundleGroup : bundleGroups) {
            collectBundleGroupDependencies(bundleGroup.getDependencies(), bundleGroup.getOutputDirectory());
        }
    }

    private void collectBundleGroupDependencies(List<org.apache.maven.model.Dependency> dependencies, String outputDirectory) throws Exception {
        for (org.apache.maven.model.Dependency dependency : dependencies) {
            if ("pom".equals(dependency.getType())) {
                Artifact projectArtifact = artifactFactory.createProjectArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
                artifactResolver.resolve(projectArtifact, project.getRemoteArtifactRepositories(), localRepository);
                MavenProject pomProject = mavenProjectBuilder.buildFromRepository(projectArtifact, project.getRemoteArtifactRepositories(), localRepository);
                collectBundleGroupDependencies(pomProject.getDependencies(), outputDirectory);
            } else {
                copyDependencyToOutputDirectory(dependency, outputDirectory);
            }
        }
    }

    private void copyDependencyToOutputDirectory(org.apache.maven.model.Dependency dependency, String outputDirectory) throws Exception {
        Artifact artifact = resolveDependencyAsArtifact(dependency);
        FileUtils.copyFileToDirectory(artifact.getFile(), new File(workingDir, outputDirectory));
    }

    private Artifact resolveDependencyAsArtifact(org.apache.maven.model.Dependency dependency) throws ArtifactResolutionException, ArtifactNotFoundException, MojoExecutionException {
        Artifact artifact;
        if (dependency.getClassifier() != null) {
            artifact = artifactFactory.createArtifactWithClassifier(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency.getType(), dependency.getClassifier());
        } else {
            artifact = artifactFactory.createArtifactWithClassifier(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency.getType(), null);
        }
        artifactResolver.resolve(artifact, project.getRemoteArtifactRepositories(), localRepository);

        if (artifact.getFile() == null) {
            throw new MojoExecutionException("Not found required dependency: " + dependency);
        }
        getLog().debug("resolved dependency: " + dependency);
        return artifact;
    }

    private void packFormats() throws Exception {
        if (outputFileExtension.equals("zip")) {
            ZipArchiver archiver = new ZipArchiver();
            archiver.setCompress(true);
            addAdditionalFormat(archiver);
        } else if (outputFileExtension.equals("tar.gz")) {
            TarArchiver archiver = new TarArchiver();
            TarCompressionMethod tarCompressionMethod = new TarCompressionMethod();
            tarCompressionMethod.setValue("gzip");
            archiver.setCompression(tarCompressionMethod);
            addAdditionalFormat(archiver);
        } else {
            throw new MojoExecutionException("Not supported file name [" + outputFile.getName() + "], only support zip and tar.gz file extension");
        }
    }

    private void addAdditionalFormat(Archiver archiver) throws Exception {

        archiver.setDestFile(outputFile);
        archiver.setIncludeEmptyDirs(true);

        addFilesToArchiver(archiver);
        archiver.createArchive();
        if (attachArtifact) {
            projectHelper.attachArtifact(project, outputFileExtension, outputFile);
        }
        if (getLog().isDebugEnabled()) {
            ResourceIterator resources = archiver.getResources();
            while (resources.hasNext()) {
                ArchiveEntry archiveEntry = resources.next();
                getLog().debug("PACKED:" + outputFile.getName() + ":" + archiveEntry.getName());
            }
        }
    }

    private void addFilesToArchiver(Archiver archiver) throws ArchiverException {
        if (executableScripts.length == 0) {
            executableScripts = new String[]{"bin/*.sh"};
        }
        archiver.addDirectory(workingDir, workingDir.getName() + "/", null, executableScripts);

        DirectoryScanner directoryScanner = new DirectoryScanner();
        directoryScanner.setIncludes(executableScripts);
        directoryScanner.setBasedir(workingDir);
        directoryScanner.scan();
        for (String relativeFilePath : directoryScanner.getIncludedFiles()) {
            archiver.addFile(new File(workingDir, relativeFilePath), workingDir.getName() + "/" + relativeFilePath, 493); // mode is 755 = 7*8*8 + 5*8 + 5 = 493
        }
    }

}