package org.duguo.xdir.jcr.pool;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

public class CredentialsWorkspaceKey {

	private Credentials credentials;
	private String workspace;
	
	public CredentialsWorkspaceKey(Credentials credentials,
			String workspace) {
		if(credentials!=null){
			this.credentials=(Credentials)credentials;
		}
		this.workspace=workspace;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public String getWorkspace() {
		return workspace;
	}
	
	
	public boolean equals(Object obj) {
		if(obj==null){
			return false;
		}
		return hashCode()==obj.hashCode();
	}

	public int hashCode() {
		int thisHashCode=0;
		if(credentials!=null && credentials instanceof SimpleCredentials){
			thisHashCode+=((SimpleCredentials)credentials).getUserID().hashCode();
		}
		if(workspace!=null){
			thisHashCode+=workspace.hashCode();
		}
		return thisHashCode;
	}

}
