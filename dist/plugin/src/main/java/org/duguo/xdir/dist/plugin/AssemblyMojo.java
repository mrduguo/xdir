package org.duguo.xdir.dist.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Exclusion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.*;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.tar.TarArchiver;
import org.codehaus.plexus.archiver.tar.TarArchiver.TarCompressionMethod;
import org.codehaus.plexus.archiver.tar.TarLongFileMode;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * pack the XDir binary distribution as zip or tar.gz
 *
 * @goal assembly
 * @phase package
 */
public class AssemblyMojo extends AbstractMojo {


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
     * dependencies file sets to assemble
     *
     * @parameter expression="${fileSets}"
     */
    protected FileSet[] fileSets;

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

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;


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
                getLog().debug("unpackaging dependency: " + project.getDependencies().toString());
                Artifact artifact = resolveDependencyAsArtifact(dependency);
                File tempWorkingDir = new File(project.getBuild().getDirectory(), "unpack/" + artifact.getFile().getName());
                tempWorkingDir.mkdirs();
                UnArchiver unArchiver = archiverManager.getUnArchiver(artifact.getFile());
                unArchiver.setSourceFile(artifact.getFile());
                unArchiver.setDestDirectory(tempWorkingDir);
                unArchiver.extract();

                List<Exclusion> exclusions = dependency.getExclusions();
                if ("jar".equals(dependency.getType())) {
                    Exclusion exclusionMetaInf = new Exclusion();
                    exclusionMetaInf.setGroupId("META-INF");
                    exclusions.add(exclusionMetaInf);
                } else {
                    tempWorkingDir = tempWorkingDir.listFiles()[0];
                }
                DirectoryScanner directoryScanner = new DirectoryScanner();
                directoryScanner.setBasedir(tempWorkingDir);
                if (dependency.getExclusions().size() > 0) {
                    List<String> excludeList = new ArrayList();
                    for (Exclusion exclusion : dependency.getExclusions()) {
                        excludeList.add("**/" + exclusion.getGroupId() + "/**");
                        if (exclusion.getArtifactId() != null && exclusion.getArtifactId().length() > 0) {
                            excludeList.add("**/" + exclusion.getArtifactId() + "*");
                        }
                    }
                    directoryScanner.setExcludes(excludeList.toArray(new String[0]));
                }
                directoryScanner.scan();
                for (String fileRelativePath : directoryScanner.getIncludedFiles()) {
                    getLog().debug("coping : " + fileRelativePath);
                    FileUtils.copyFile(new File(tempWorkingDir, fileRelativePath), new File(workingDir, fileRelativePath));
                }
            }
        }
    }

    private void copyProjectAssemblyResources() throws Exception {
        if (rootResourceFolder.exists()) {
            FileUtils.copyDirectoryStructure(rootResourceFolder, workingDir);
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
        for (FileSet fileSet : fileSets) {
            collectFileSetDependencies(fileSet.getDependencies(), fileSet.getOutputDirectory());
        }
    }

    private void collectFileSetDependencies(List<org.apache.maven.model.Dependency> dependencies, String outputDirectory) throws Exception {
        for (org.apache.maven.model.Dependency dependency : dependencies) {
            if ("pom".equals(dependency.getType())) {
                Artifact projectArtifact = artifactFactory.createProjectArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
                artifactResolver.resolve(projectArtifact, project.getRemoteArtifactRepositories(), localRepository);
                MavenProject pomProject = mavenProjectBuilder.buildFromRepository(projectArtifact, project.getRemoteArtifactRepositories(), localRepository);
                collectFileSetDependencies(pomProject.getDependencies(), outputDirectory);
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
            TarLongFileMode tarLongFileMode = new TarLongFileMode();
            tarLongFileMode.setValue(TarLongFileMode.GNU);
            archiver.setLongfile(tarLongFileMode);
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