package org.duguo.xdir.core.internal.model;

import javax.jcr.Node;

public class TextNode {
	private Node node;
	private String url;
	private String description;
	private String title;
	private String nodeType;
	private String styleClass;
	private long childcount=-1; 

	public TextNode(Node node,String url, String title) {
		this.node = node;
		this.url = url;
		this.title = title;
	}

	public long getChildcount() throws Exception {
		if(childcount==-1){
			childcount=node.getNodes().getSize();
		}
		return childcount;
	}
	
	public Node getNode() {
		return node;
	}	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStyleClass() {
		return styleClass;
	}
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }
	
}
