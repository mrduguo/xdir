package org.duguo.xdir.core.internal.jcr;

public abstract class AbstractQueryJcrService{

	protected String deletionBackupFolder;

	public void setDeletionBackupFolder(String deletionBackupFolder) {
		this.deletionBackupFolder = deletionBackupFolder;
	}
}
