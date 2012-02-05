(function($){
$.xdir.jcr = $.xdir.jcr || {};
$.xdir.jcr = {workspace: null,
			 path:null,
			 pathname: null,
			 username: null,
			 versionable: true,
			 relpath:null,
			 role: null,
			 paths:null,
			 children:null,
			 properties:null,
			init:function(s){
				this.versionable=initParams.defaultVersionable;
				this.workspace=initParams.workspace;
				if(initParams.path=="/"){
					this.path=window.location.href;
					if(this.path.indexOf("#")>0){
						this.path=this.path.substr(this.path.indexOf("#")+1);
						this.path=decodeURIComponent(this.path);
					}else if(this.path.indexOf("?path=")>0){
						this.path=this.path.substr(this.path.indexOf("?")+6);
						this.path=decodeURIComponent(this.path);
					}else{
						this.path=initParams.path;
					}
				}else{
					this.path=initParams.path;
				}
				this.username=initParams.username;
				this.role=initParams.role;
				$("#xdir_user").html(this.username);
			},
			update:function(data){
				this.normalizeName(data.paths);
				this.normalizeName(data.children);

				$.xdir.jcr.path=data.pagePath;
				pagePath=data.pagePath;
				pageTitle=data.pageTitle;
				$.xdir.jcr.relpath=data.pagePath.substring(pageContext.length);
				$.xdir.jcr.pathname=data.pageTitle;
				$.xdir.jcr.paths=data.paths;
				$.xdir.jcr.children=data.children || null;
				$.xdir.jcr.sortProperties(data);
				$.xdir.jcr.properties=data.properties || null;
				
				window.document.title="XDir:"+pageTitle;
			},
			parentPath: function(){
				return this.path.substr(0,this.path.lastIndexOf("/"));
			},
			isCreator: function(){
				return this.hasRole("CREATOR");
			},
			hasRole: function(role){
				if(role=="SUPERUSER"){
					return this.role=="SUPERUSER";
				}else if(role=="NORMALUSER"){
					return this.role=="SUPERUSER" || this.role=="NORMALUSER";
				}else if(role=="CREATOR"){
					return this.role=="SUPERUSER" || this.role=="NORMALUSER" || this.role=="CREATOR";
				}else if(role=="GUEST"){
					return this.role=="SUPERUSER" || this.role=="NORMALUSER" || this.role=="CREATOR" || this.role=="GUEST";
				}
				return false;
			},
			hasHistory: function(node){
				if(node.properties){
					var jcrMixinTypes=node.properties["jcr:mixinTypes"];				
					if(jcrMixinTypes && jcrMixinTypes.indexOf("mix:versionable")>=0){
						var jcrCreated=node.properties["jcr:created"];
						var jcrModified=node.properties["jcr:modified"];
						if(jcrCreated && jcrModified && jcrCreated!=jcrModified){
							return true;
						}
					}
				}
				return false;
			},
			showHistory: function(container){
 	 			var query="select jcr:modifiedBy,jcr:modified from nt:frozenNode where jcr:frozenUuid = '"+$.xdir.jcr.properties["jcr:uuid"]+"' and jcr:path LIKE '%/jcr:frozenNode'";
	           $.xdir.query.querySection({
					query:query,
					maxsize:50,
					container:container,
					columnNames: ["Version","Modified By","Time"],
					sortList:[[2,1]],
					parentPath:false,
					tableRowConstructor: function(options,tbodyElment,rowIndex,rowData){
						if(rowData[1]!=null){
		   					var versionName=rowData[2].substr(0,rowData[2].lastIndexOf("/"));
		   					versionName=versionName.substr(versionName.lastIndexOf("/")+1);
							$.xdir.jcr.createHistoryRow(tbodyElment,rowData[2],versionName,rowData[0],rowData[1]);
						}else{
							$.xdir.jcr.createHistoryRow(tbodyElment,$.xdir.jcr.path,"Latest",$.xdir.jcr.properties["jcr:modifiedBy"],$.xdir.jcr.properties["jcr:modified"]);
				   		}
					}
				});
				return;
			},
			createHistoryRow: function(tbodyElment,path,name,modifiedBy,modifiedTime){
 	 			var trElment=$("<tr></tr>");
				tbodyElment.append(trElment);
				var tdElment=$("<td></td>");
				trElment.append(tdElment);
				createExpandableLink(path,name,"-1",tdElment);
				tdElment=$("<td>"+modifiedBy+"</td>");
				trElment.append(tdElment);
				tdElment=$("<td>"+$.xdir.utils.formatTime(modifiedTime)+"</td>");
				trElment.append(tdElment);				
			},
			retriveReference: function(query,container,title){
				container.html("<div class=\"section_title\">"+title+"</div>");
	            $.getJSON(pageContext+"/.json?action=ajaxxpathquery", {timestamp:new Date().getTime(),query:query}, 
		 			function(data){
				   		var results=[];
				   		if(data.status==0){
				   			   if(data.results){
									var ulElment=$("<ul class=\"nodeul\"></ul>");
									container.append(ulElment);
								   $(data.results).each(function(){
						   				createExpandableLink(this.path,$.xdir.jcr.getNameFromNode(this),"-1",ulElment);
					   				});
				   				}else{
				   					container.append("No record found");
				   				}
				   		}else{
				   			alert("error status:"+ data.status);
				   		}
				 });
			},
			retriveQueryTable: function(query,container,title,columnNames,linkIndex,maxsize,noRelPath){
				var isLinkQuery=$("div.section_title a",container).size()>0;
				container.html("<div class=\"section_title\">"+title+"</div>");
				maxsize= maxsize || "";
	            $.getJSON(pageContext+"/.json?action=ajaxsqlquery", {timestamp:new Date().getTime(),query:query,workspace:$.xdir.jcr.workspace,maxsize:maxsize}, 
		 			function(data){
				   		if(data.status==0){
				   			   if(data.results){
									$("div.section_title",container).append(" ("+data.results.length+")");
				   			   		var parentPath=query.toLowerCase();
				   			   		if(!noRelPath && parentPath.indexOf("jcr:path like '")>0){
				   			   			parentPath=parentPath.substr(parentPath.indexOf("jcr:path like '"));
				   			   			parentPath=parentPath.substr(parentPath.indexOf("'")+1);
				   			   			parentPath=parentPath.substr(0,parentPath.indexOf("'")-2);
				   			   		}else{
				   			   			parentPath=null;
				   			   		}
				   			   		var tempHtml="<table class=\"tablesorter\"><thead><tr>";
				   			   		$(columnNames).each(function(){
						   				tempHtml+="<th>"+this+"</th>";
					   				});
					   				if(parentPath!=null){
						   				tempHtml+="<th>Relative Path</th>";					   				
					   				}
				   			   		tempHtml+="</tr></thead><tbody></tbody></table>";
									var tableElment=$(tempHtml);
									container.append(tableElment);
									var tbodyElment=$("tbody",tableElment);
									var coloumns=query.substr(7).toLowerCase();
									coloumns=coloumns.substr(0, coloumns.indexOf(" from "));
									coloumns=coloumns.split(",");
								   $(data.results).each(function(rowIndex,rowData){
						   				var trElment=$("<tr></tr>");
										tbodyElment.append(trElment);
										$(rowData).each(function(index,value){
											if(index<columnNames.length){
												if(value==null){
													value="";
												}
												if(linkIndex==index){
													var tdElment=$("<td></td>");
													trElment.append(tdElment);
													createExpandableLink(rowData[rowData.length-2],value,"-1",tdElment);
												}else{
													if(coloumns[index]=="jcr:nodetype"){
														value=$.xdir.jcr.formatPropertyName(value);
													}else if(coloumns[index]=="jcr:modified"){
														value=$.xdir.utils.formatTime(value);
													}
													trElment.append("<td class=\"value\">"+value+"</td>");
												}
											}
						   				});
						   				if(parentPath!=null){
						   					var relPath=rowData[rowData.length-2];
						   					relPath=relPath.substr(0,relPath.lastIndexOf("/")+1);
						   					relPath=relPath.substr(parentPath.length);
							   				trElment.append("<td>"+relPath+"</td>");			   				
						   				}
					   				});
			 						$.xdir.page.filterReferenceLink(tableElment);
				   					$.xdir.utils.tablesorterByText(tableElment);
				   				}else{
				   					if(isLinkQuery){
				   						$("div.section_title",container).append(" (0)");
				   					}else{
										container.html("");				   					
				   					}
				   				}
				   		}else{
				   			alert("error status:"+ data.status);
				   		}
				 });
			},
			hasChildNode: function(data,childKey){
				var result=false;
				if(data && data.children){
					 $(data.children).each(function () {
					 	var path=this.path;
					 	if(path.lastIndexOf(childKey)==path.length-childKey.length){
					 		result=true;
					 	}
					 });
				}
				return result;
			},
			sortProperties: function(data){
	   			 if(data.properties){
	   			 	var oldProperties=data.properties;
	   			 	var newProperties={};
	   			 	var propertiesKeys=new Array();
	   			 	
	   			 	for(i in oldProperties){
	   			 		propertiesKeys.push(i);
	   			 	}
					propertiesKeys=propertiesKeys.sort(function(aname,bname){
						aname=aname.toUpperCase();
						bname=bname.toUpperCase();
						var aindex=aname.indexOf(":");
						var bindex=bname.indexOf(":");
						if(aindex>0){
							if(bindex>0){
								return aname>bname?1:aname<bname?-1:0;								
							}else{
								return 1;
							}
						}else if(bindex>0){
							return -1;
						}else{
							return aname>bname?1:aname<bname?-1:0;
						}					 	
					});
					$(propertiesKeys).each(function(key,value){
						newProperties[value]=oldProperties[value];
					});
	   			 	data.properties=newProperties;
	   			 }
			},
			getPathFromUrl: function(href){
				if(href.indexOf("?path=")>0){
					return decodeURIComponent(href.substr(href.indexOf("?path=")+6));
				}else{
					return href=href.substr(href.indexOf(this.workspace)+this.workspace.length);
				}
			},
			getWorkspaceSiteResource: function(resource){
				return $.xdir.contextpath+"/_/static"+resource;
			},
			buildLinkFromPath: function(linkType,linkPath){
				if(linkPath.indexOf($.xdir.baseurl)==0){
					linkPath=linkPath.substr($.xdir.baseurl.length);
				}
				if(linkType=="file"){
					return $.xdir.baseurl+$.xdir.contextpath+"/"+this.workspace+"/_/file"+linkPath;
				}else{
					return $.xdir.baseurl+$.xdir.contextpath+"/"+this.workspace+linkPath+"."+linkType;
				}
			},
			getAsFilePath: function(linkPath){
				return linkPath.substring(0,linkPath.lastIndexOf("/"))+"/_/f"+linkPath.substring(linkPath.lastIndexOf("/"));
			},
			normalizeName: function(nodes){
				if(nodes){
					$(nodes).each(function(){
						if(!this.pathname){
							this.pathname=$.xdir.jcr.getNameFromNode(this);
						}
					});
				}
			},
			getNameFromNode: function(node){
				if(node.pathname){
					return node.pathname;
				}else{
					return this.getNameFromPath(node.path);
				}
			},
			getPathFromData: function(data){
				var path=null;
				if(data.paths){
					path=data.paths[data.paths.length-1].path;
				}else{
					path=data.path;
				}
				if(path=="/"){
					path="";
				}
				return path;
			},
			getNameFromData: function(data){
				if(data.properties['jcr:displayName']){
					return data.properties['jcr:displayName'];
				}else if(data.properties['jcr:pathName']){
					return data.properties['jcr:pathName'];
				}else{
					return this.getNameFromPath(this.getPathFromData(data));
				}
			},
			getNameFromPath: function(pathStr){
				if(pathStr && pathStr.length>1){
					return pathStr.substr(pathStr.lastIndexOf("/")+1);
				}else{
					return $.xdir.jcr.workspace;
				}
			},
			normalizeRelativePath: function(path){
				while(path.indexOf("..")>=0){
					var firstDotDot=path.indexOf("..");
					path=path.substring(0,path.substring(0,firstDotDot-1).lastIndexOf("/")+1)+path.substring(firstDotDot+3);
				}
				return path;
			},
			formatPropertyName: function(propertyName){
				if(propertyName.indexOf('"')==0){
					propertyName=propertyName.substring(1,propertyName.length-1);
				}
				var allparts=propertyName.split("_");
				var  newName=null;
				$(allparts).each(function(key,value){
					if(newName==null){
						if(value.match(/\d+/)){
							return;
						}else{
							newName="";
						}
					}else{
						newName+=" ";
					}
					newName+=value.substring(0,1).toUpperCase();
					newName+=value.substring(1);
				});
				return newName;
			},
			normalizePathName: function(sourceValue){
				var allparts=sourceValue.split(" ");
				var  newName=null;
				$(allparts).each(function(key,value){
					if(newName==null){
						newName="";
					}else{
						newName+="_";
					}
					newName+=value.substring(0,1).toLowerCase();
					newName+=value.substring(1);
				});
				return newName;
			},
			displayValue: function(key,value){
				if(typeof key != 'string'){
					var realKey=value;
					value=key[realKey];
					key=realKey;
				}
				if(value ==null){
					return "";
				}
				
				var htmlResult="<tr class=\""+$.xdir.utils.cycleevenodd()+"row\">";
				htmlResult+="<td class=\"name\">"+$.xdir.jcr.formatPropertyName(key)+"</td><td class=\"value\">";
				if(pagePath.indexOf("jcr:")>0 || key=="jcr:data" || (value.indexOf("\n")>0 && value.indexOf("&lt;")<0)){
					htmlResult+= "<pre>"+value+"</pre>";
				}else if(value.indexOf("http")==0){
					htmlResult+= "<a target=\"_blank\" href=\""+value+"\">"+value+"</a>";
				}else{
					htmlResult+= $.xdir.utils.unescapeHTML(value);
				}
				htmlResult+="</td></tr>";
				return htmlResult;
			},
			displayDefaultEditValue: function(key,value,inputType){
				if(typeof key != 'string'){
					var realKey=value;
					value=key[realKey];
					key=realKey;
				}
				var htmlResult="<tr><td class=\"name\">"+this.formatPropertyName(key);
				htmlResult+="<input class=\"xdirinput\" name=\"properties"+key+"\" value=\""+key+"\" type=\"hidden\"/></td><td>";
				value=$.xdir.utils.escapeHTML(value);
				if(value ==null){
					value="";
				}
				if((inputType && inputType=="textarea") || value.indexOf("\n")>0 || value.indexOf("&lt;")>=0 || value.indexOf(">")>=0 || key=="comments"){
					htmlResult+="<textarea class=\"xdirinput\" name=\"properties"+key+"Value\">"+value+"</textarea></td><td><div class=\"button expand\">&or;</div>";
				}else if(value ==""){
					htmlResult+="<input name=\"properties"+key+"Value\" class=\"xdirinput\" value=\""+value+"\"/><td>";
				}else{
					htmlResult+="<input name=\"properties"+key+"Value\" class=\"xdirinput";
					var equalSignIndex=value.indexOf("=");
					if(equalSignIndex>0 && value.indexOf("&")<0){
						var searchValue=value.split("=",2);
						htmlResult+=" search\" value=\""+value.substr(equalSignIndex+1)+"\" title=\""+value.substr(0,equalSignIndex)+"\"/><td>";						
					}else{
						htmlResult+="\" value=\""+value+"\"/><td>";
					}
				}
				htmlResult+="</td></tr>";
				return htmlResult;
			}
	}
})(jQuery);