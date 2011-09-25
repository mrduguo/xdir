package org.duguo.xdir.core.internal.jcr;


import java.util.LinkedHashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.model.TextNode;
import org.duguo.xdir.core.internal.utils.RequestUtils;


public class QueryFactoryImpl implements QueryFactory{
	
	private int maxSize=500;
	private static final Logger log=LoggerFactory.getLogger(QueryFactoryImpl.class);
	
	public int getMaxSize(ModelImpl model) {
		String size=RequestUtils.getStringParameter(model, "maxsize");
		int maxSize=20;
		if(size!=null){
			maxSize=Integer.parseInt(size);
			if(maxSize>this.maxSize){
				maxSize=this.maxSize;
			}
		}
		return maxSize;
	}

	public Map<String,String[]> sqlQuery(ModelImpl model,String queryStr)throws Exception{
		return sqlQuery(model,queryStr,maxSize);
	}

	@SuppressWarnings("deprecation")
    public Map<String,String[]> sqlQuery(ModelImpl model,String queryStr,int maxSize)throws Exception{
		Map<String,String[]> results=new LinkedHashMap<String,String[]>();
		QueryManager aueryManager=model.getSession().getWorkspace().getQueryManager();
		//TODO: evaluate sql2
		Query query=aueryManager.createQuery(queryStr, Query.SQL);
		RowIterator rows = query.execute().getRows();
		for(int i=0;i<maxSize;i++){
			if(rows.hasNext()){
				Row row=(Row)rows.next();
				Value[] values=row.getValues();
				int rowLenthg=values.length-1;
				String[] rowStrings=new String[rowLenthg];
				for(int j=0;j<rowLenthg;j++){
					Value value=values[j];
					if(value!=null){
						try{
							rowStrings[j]=value.getString();
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
				results.put(model.internalBuildUrl(rowStrings[rowLenthg-1]),rowStrings);
			}else{
				break;
			}
		}
		return results;
	}

	public Map<String,TextNode> xpathQuery(ModelImpl model,String queryStr)throws Exception{
		return xpathQuery(model,queryStr,maxSize);
	}

    @SuppressWarnings("deprecation")
	public Map<String,TextNode> xpathQuery(ModelImpl model,String queryStr,int maxSize)throws Exception{
		Map<String,TextNode> results=new LinkedHashMap<String,TextNode>();
		QueryManager queryManager=model.getSession().getWorkspace().getQueryManager();
		//TODO: evaluate sql2
        Query query=queryManager.createQuery(queryStr, Query.XPATH);
		NodeIterator nodeIterator = query.execute().getNodes();
		for(int i=0;i<maxSize;i++){
			if(nodeIterator.hasNext()){
				Node node=(Node)nodeIterator.next();
				String linkPath=node.getPath();
				TextNode link=model.createTextLinkFromNode(node,linkPath);
				results.put(linkPath,link);
			}else{
				break;
			}
		}
		return results;
	}
	
	public String buildQueryString(ModelImpl model) throws Exception {
		String queryStr=RequestUtils.getStringParameter(model, "query");
		String queryBase=model.getNode()!=null?model.getNode().getPath():"/";
		if(queryBase.length()==1){
			if(queryStr==null){
				queryStr="//*[@jcr:primaryType='nt:unstructured'] order by @jcr:modified descending";
			}
		}else{
			queryBase="/jcr:root"+queryBase;
			if(queryStr==null){
				queryStr=queryBase+"//*[@jcr:primaryType='nt:unstructured'] order by @jcr:modified descending";
			}else{
				queryStr=queryBase+"//"+queryStr;
			}
		}
		return queryStr;
	}


    @SuppressWarnings("deprecation")
	public Object executeQuery(ModelImpl model,String queryStr,boolean isSqlQuery) throws Exception {
		log.debug("xpath query {}",queryStr);
		QueryManager aueryManager=model.getSession().getWorkspace().getQueryManager();
		if(isSqlQuery){
		  //TODO: evaluate sql2
	        Query query=aueryManager.createQuery(queryStr, Query.SQL);
			return query.execute().getRows();
		}else{
		  //TODO: evaluate sql2
	        Query query=aueryManager.createQuery(queryStr, Query.XPATH);
			return query.execute().getNodes();
		}
	}

	public Node loadNode(ModelImpl model, String path)throws Exception {
		Node node=null;
		if(path.indexOf("=")>0){
			node=searchSingleNode(model.getSession(),path);
		}else if(path.length()==1){
			node=model.getSession().getRootNode();
		}else{
			node=(Node)model.getSession().getItem(path);
		}
		return node;
	}

    @SuppressWarnings("deprecation")
	private Node searchSingleNode(Session session,String query)throws Exception {
		String[] queryArray=query.split("=");
		if(query.startsWith("@")){
			query="//*[@jcr:primaryType='nt:unstructured' and "+queryArray[0]+"='"+queryArray[1]+"']";
		}else{
			query="//*[@jcr:primaryType='nt:unstructured' and @jcr:pathName='"+queryArray[1]+"' and @jcr:nodeType='"+queryArray[0]+"']";
		}
		//TODO: evaluate sql2
        NodeIterator nodeIterator=session.getWorkspace().getQueryManager().createQuery(query, Query.XPATH).execute().getNodes();
		long totalNumber=nodeIterator.getSize();
		if(totalNumber==1){
			return nodeIterator.nextNode();
		}else{
			if(totalNumber==0){
				throw new PathNotFoundException(query);
			}else{
				throw new PathNotFoundException("Multiple recoreds match "+query);
			}
		}
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

}
