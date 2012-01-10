package org.duguo.xdir.core.internal.jcr.importor;

import java.io.InputStream;

import javax.jcr.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.duguo.xdir.jcr.utils.JcrPathUtils;

public class MmImportorImpl implements MmImportor {

	public void importMm(Node node, InputStream inputStream) throws Exception{

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse (inputStream);
        MmNode mmNode=new MmNode();
        loadMmElement(mmNode,doc.getDocumentElement());
        if(mmNode.getChildren()!=null){
        	mmNode=mmNode.getChildren().get(0);
        	mmNode.setPath(JcrPathUtils.normalizePathName(mmNode.getName()));
            storeMmNode(mmNode,node);
        }
	}


	private void storeMmNode(MmNode mmNode,Node jcrNode) throws Exception{
		Node newNode=jcrNode.addNode(mmNode.getPath());
		newNode.setProperty("jcr:pathName", mmNode.getPath());
		newNode.setProperty("jcr:displayName", mmNode.getName());
		newNode.setProperty("jcr:search", mmNode.getPath()+","+mmNode.getName());
		
		
		if(mmNode.getChildren()!=null){
			MmNode propertiesNode=null;
			boolean reOrder=false;
			String previousPath=null;
			for(MmNode childNode:mmNode.getChildren()){
				if(childNode.getPath().equals("properties")){
					propertiesNode=childNode;
				}else{
					if(previousPath!=null){
						if(previousPath.compareTo(childNode.getPath())>0){
							reOrder=true;
						}
					}
					previousPath=childNode.getPath();
				}
			}
			
			addChildren(mmNode, newNode, propertiesNode, reOrder);
			addProperties(propertiesNode,newNode);
		}
	}

	private void addChildren(MmNode mmNode, Node jcrNode, MmNode propertiesNode, boolean reOrder) throws Exception{
		int currentIndex=0;
		int totalChildren=mmNode.getChildren().size();
		if(propertiesNode!=null){
			totalChildren=totalChildren-1;
		}
		String numberFormat="%0"+String.valueOf(totalChildren).length()+"d";
		for(MmNode childNode:mmNode.getChildren()){
				if(!childNode.getPath().equals("properties")){
					if(reOrder){
						currentIndex++;
						String newPath=String.format(numberFormat, currentIndex);
						newPath+="_"+childNode.getPath();
						childNode.setPath(newPath);
					}
					storeMmNode(childNode,jcrNode);
				}
		}
	}

	private void addProperties(MmNode propertiesNode,Node newNode) throws Exception{
		if(propertiesNode!=null && propertiesNode.getChildren()!=null){
			for(MmNode propertyNode:propertiesNode.getChildren()){
				if(propertyNode.getChildren()!=null){
					String propertyName=propertyNode.getPath();
					String propertyValue=propertyNode.getChildren().get(0).getName();
					newNode.setProperty(propertyName, propertyValue);
				}
				
			}
		}
	}

	private void loadMmElement(MmNode mmNode,org.w3c.dom.Node node) throws Exception  {
		if(node.hasAttributes()){
			NamedNodeMap attributes = node.getAttributes();
			org.w3c.dom.Node textAttribute=attributes.getNamedItem("TEXT");
			if(textAttribute!=null){
				MmNode newMmNode=mmNode.addChild(textAttribute.getNodeValue());
				if(node.hasChildNodes()){
					NodeList children=node.getChildNodes();
					for(int i=0;i<children.getLength();i++){
						loadMmElement(newMmNode,children.item(i));
					}
				}
				return;
			}
		}
		if(node.hasChildNodes()){
			NodeList children=node.getChildNodes();
			for(int i=0;i<children.getLength();i++){
				loadMmElement(mmNode,children.item(i));
			}
		}
	}
}
