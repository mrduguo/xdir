package org.xdir.platform.osgi.extender.conf;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.osgi.spi.conditional.ConditionalService;

public class ConfFolderAwareConditionalSpringConfigurationScanner extends
		ConfFolderAwareSpringConfigurationScanner {
	private static final Logger logger = LoggerFactory
			.getLogger(ConfFolderAwareConditionalSpringConfigurationScanner.class);

	public ConditionalService conditionalService;

	protected List<String> scanAdditionalFiles(Bundle bundle,
			List<String> confFiles) {
		confFiles = super.scanAdditionalFiles(bundle, confFiles);
		confFiles = filterOutByConditionalService(confFiles);
		return confFiles;
	}

	protected List<String> filterOutByConditionalService(List<String> confFiles) {
		if (confFiles.size() > 0) {
			List<String> conditionalFiles = new ArrayList<String>(confFiles);
			for (String confFile : confFiles) {
				String fileName = confFile
						.substring(confFile.lastIndexOf("/") + 1);
				if (conditionalService.isConditionalString(fileName)) {
					conditionalFiles.remove(confFile);
					fileName = fileName.substring(0, fileName.lastIndexOf("."));
					if (conditionalService.eval(fileName)) {
						conditionalFiles.add(confFile);
						logger.debug("enable conditional configuration file:"
								+ confFile);
					} else {
						logger.debug("disable conditional configuration file:"
								+ confFile);
					}
				}
			}
			return conditionalFiles;
		}
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

	public void setConditionalService(ConditionalService conditionalService) {
		this.conditionalService = conditionalService;
	}

}
