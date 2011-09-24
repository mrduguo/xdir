package org.duguo.xdir.osgi.extender.conf;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.gemini.blueprint.extender.support.scanning.DefaultConfigurationScanner;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfFolderAwareSpringConfigurationScanner extends
        DefaultConfigurationScanner {
	private static final Logger logger = LoggerFactory
			.getLogger(ConfFolderAwareSpringConfigurationScanner.class);

	@Override
	public String[] getConfigurations(Bundle bundle) {
		List<String> confFiles = new ArrayList<String>();
		for (String confFile : super.getConfigurations(bundle)) {
			scanSingleConfEntry(bundle, confFiles, confFile, true);

		}
		confFiles=scanAdditionalFiles(bundle, confFiles);

		String[] allConfFiles = new String[confFiles.size()];
		if (allConfFiles.length > 0) {
			for (int i = 0; i < allConfFiles.length; i++) {
				allConfFiles[i] = confFiles.get(i);
			}
		}
		return allConfFiles;
	}

	protected List<String> scanAdditionalFiles(Bundle bundle, List<String> confFiles) {
		ConfFolderAwareScannerUtils.scanLocalConfigurations(bundle, confFiles,
				ConfFolderAwareScannerUtils.VALUE_XDIR_EXTENDER_CONF_XML);
		return confFiles;
	}

	protected void scanSingleConfEntry(Bundle bundle, List<String> confFiles,
			String confEntry, boolean addMissingFile) {
		int pathStartIndex = confEntry.indexOf(":") + 1;
		int pathSplitIndex = confEntry.lastIndexOf("/") + 1;
		boolean foundFile = false;
		if (pathStartIndex > 0 && pathStartIndex < pathSplitIndex) {
			Enumeration<?> bundleEntries = bundle.findEntries(confEntry
					.substring(pathStartIndex, pathSplitIndex), confEntry
					.substring(pathSplitIndex), false);
			if (bundleEntries != null) {
				while (bundleEntries.hasMoreElements()) {
					Object confFileObject = bundleEntries.nextElement();
					confFiles.add(confFileObject.toString());
					foundFile = true;
					if (logger.isDebugEnabled())
						logger
								.debug("conf file found [" + confFileObject
										+ "]");
				}
			}
		}
		if (!foundFile && addMissingFile) {
			logger.warn("conf file not found for confEntry [" + confEntry
					+ "] will add as it is");
			confFiles.add(confEntry);
		}
	}
}
