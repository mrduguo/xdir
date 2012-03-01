package org.duguo.xdir.osgi.extender.conf;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfFolderAwareConditionalSpringConfigurationScanner extends
		ConfFolderAwareSpringConfigurationScanner {
	private static final Logger logger = LoggerFactory
			.getLogger(ConfFolderAwareConditionalSpringConfigurationScanner.class);


	protected List<String> scanAdditionalFiles(Bundle bundle,
			List<String> confFiles) {
		confFiles = super.scanAdditionalFiles(bundle, confFiles);
		return confFiles;
	}

	protected void scanSingleConfEntry(Bundle bundle, List<String> confFiles,
			String confEntry, boolean addMissingFile) {
		super.scanSingleConfEntry(bundle, confFiles, confEntry, addMissingFile);
		if (addMissingFile) {
			int wildcardPosition = confEntry.lastIndexOf("*");
			if (wildcardPosition > 0) {
				String conditionalFiles = confEntry.substring(0,
						wildcardPosition)
						+ "conditional/"
						+ confEntry.substring(wildcardPosition);
				super.scanSingleConfEntry(bundle, confFiles, conditionalFiles,
						false);
			}
		}
	}


}
