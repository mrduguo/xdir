package org.duguo.xdir.dist.plugin;


import java.util.List;

public class FileSet {
    private String outputDirectory;
    private List<org.apache.maven.model.Dependency> dependencies;

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public List<org.apache.maven.model.Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<org.apache.maven.model.Dependency> dependencies) {
        this.dependencies = dependencies;
    }
}
