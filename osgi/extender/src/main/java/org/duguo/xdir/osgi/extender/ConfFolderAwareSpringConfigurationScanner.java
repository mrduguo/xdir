package org.duguo.xdir.osgi.extender;

import org.eclipse.gemini.blueprint.extender.support.scanning.DefaultConfigurationScanner;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SystemPropertyUtils;

import java.io.File;
import java.util.*;

public class ConfFolderAwareSpringConfigurationScanner extends
        DefaultConfigurationScanner {
    private static final Logger logger = LoggerFactory
            .getLogger(ConfFolderAwareSpringConfigurationScanner.class);

    @Override
    public String[] getConfigurations(Bundle bundle) {
        List<String> confFiles = new ArrayList<String>();

        Map<String, String> scannedFiles = resolveScannedFiles(bundle);
        addConfigFileSortedByFileName(scannedFiles, confFiles);

        addLocalOverrideConfigFile(bundle, confFiles);

        if (logger.isDebugEnabled()) {
            for (String file : confFiles) {
                logger.debug("Using spring config file {}", file);
            }
        }
        return confFiles.toArray(new String[0]);
    }

    private void addLocalOverrideConfigFile(Bundle bundle, List<String> confFiles) {
        String localOverrideConfigFile = getLocalConfigFile(bundle, "spring-override.xml");
        if (localOverrideConfigFile != null) {
            confFiles.add(localOverrideConfigFile);
        }
    }

    private Map<String, String> resolveScannedFiles(Bundle bundle) {
        List<String> scannedFiles = new ArrayList<String>();
        for (String confFile : super.getConfigurations(bundle)) {
            scanSingleConfEntry(bundle,scannedFiles,confFile);
        }

        Map<String, String> scanedFiles = new HashMap<String, String>();
        for (String confFile :scannedFiles) {
            String resolvedConfig = SystemPropertyUtils.resolvePlaceholders(confFile);
            String configFileName = resolvedConfig.substring(resolvedConfig.lastIndexOf("/") + 1);
            String localConfigFile = getLocalConfigFile(bundle, configFileName);
            if (localConfigFile != null) {
                resolvedConfig = localConfigFile;
            }
            scanedFiles.put(configFileName, resolvedConfig);
        }
        return scanedFiles;
    }

    private void addConfigFileSortedByFileName(Map<String, String> scanedFiles, List<String> confFiles) {
        String[] configFileNames = scanedFiles.keySet().toArray(new String[0]);
        Arrays.sort(configFileNames);
        for (String fileName : configFileNames) {
            confFiles.add(scanedFiles.get(fileName));
        }
    }

    private String getLocalConfigFile(Bundle bundle, String configFileName) {
        File configFile = new File(System.getProperty("xdir.home") + "/data/conf/" + bundle.getSymbolicName() + "/" + configFileName);
        if (configFile.exists()) {
            return "file:" + configFile.getAbsoluteFile();
        } else {
            return null;
        }
    }

    private void scanSingleConfEntry(Bundle bundle, List<String> confFiles, String confEntry) {
        int pathStartIndex = confEntry.indexOf(":") + 1;
        int pathSplitIndex = confEntry.lastIndexOf("/") + 1;
        if (pathStartIndex > 0 && pathStartIndex < pathSplitIndex) {
            Enumeration<?> bundleEntries = bundle.findEntries(confEntry
                    .substring(pathStartIndex, pathSplitIndex), confEntry
                    .substring(pathSplitIndex), false);
            if (bundleEntries != null) {
                while (bundleEntries.hasMoreElements()) {
                    Object confFileObject = bundleEntries.nextElement();
                    confFiles.add(confFileObject.toString());
                }
            }
        }
    }
}
