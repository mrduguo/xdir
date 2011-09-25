package org.duguo.xdir.core.internal.jcr;

import java.util.Map;

import javax.jcr.Node;


import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.model.TextNode;

public interface QueryFactory {
	
	public static final int MAX_ROW=1000;

	public int getMaxSize(ModelImpl model);

	public String buildQueryString(ModelImpl model) throws Exception;

	public Object executeQuery(ModelImpl model, String queryStr, boolean isSqlQuery) throws Exception;

	public Map<String,String[]> sqlQuery(ModelImpl model,String queryStr)throws Exception;
	
	public Map<String,String[]> sqlQuery(ModelImpl model,String queryStr,int maxSize)throws Exception;

	public Map<String,TextNode> xpathQuery(ModelImpl model,String queryStr)throws Exception;
	
	public Map<String,TextNode> xpathQuery(ModelImpl model,String queryStr,int maxSize)throws Exception;
	
	public Node loadNode(ModelImpl model, String path)throws Exception;

}