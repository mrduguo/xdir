package org.duguo.xdir.core.internal.jcr;


import org.duguo.xdir.core.internal.jcr.importor.MmImportor;

public abstract class AbstractQueryJcrService{

	protected String deletionBackupFolder;
	private MmImportor mmImportor;

	public void setDeletionBackupFolder(String deletionBackupFolder) {
		this.deletionBackupFolder = deletionBackupFolder;
	}

    public MmImportor getMmImportor()
    {
        return mmImportor;
    }

    public void setMmImportor( MmImportor mmImportor )
    {
        this.mmImportor = mmImportor;
    }
}
