package org.duguo.xdir.core.internal.jcr.importor;

import java.util.LinkedList;
import java.util.List;

import org.duguo.xdir.jcr.utils.JcrPathUtils;

public class MmNode {
	private String name;
	private String path;
	private List<MmNode> children;
	
	public MmNode() {
	}
	
	public MmNode(String name) {
		setName(name);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		if(name.lastIndexOf("::")>0){
			String[] nameAndPath=name.split("::", 2);
			this.name =nameAndPath[0];
			this.path=nameAndPath[1];
		}else{
			this.name = name;
			this.path= JcrPathUtils.normalizePathName(name);
		}
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<MmNode> getChildren() {
		return children;
	}
	public MmNode addChild(String childName) {
		MmNode newMmNode=new MmNode(childName);
		if(children==null){
			children=new LinkedList<MmNode>();
		}
		children.add(newMmNode);
		return newMmNode;
	}
}
