package org.duguo.xdir.core.internal.cache;

import java.io.File;
import java.io.Serializable;

public class WebPageCache  implements Serializable{
	
	private static final long serialVersionUID = -4514554131963696967L;

	private long timestamp;
	private String contentType;
	private String[][] headers;
	private File content;
	

	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String[][] getHeaders() {
		return headers;
	}
	public void setHeaders(String[][] headers) {
		this.headers = headers;
	}
	public File getContent() {
		return content;
	}
	public void setContent(File content) {
		this.content = content;
	}

    public boolean isGzipEnabled() {
        return contentType!=null && contentType.startsWith("text");
    }
}
