(function($){
$.xdir = $.xdir || {};
$.xdir.page = {
		tempVar: function(value){
			if(value){
				this._tempVar=value;
				return;
			}
			return this._tempVar;
		},
		displayOverview: function(container,data){
			$.xdir.jcr.sortProperties(data);
		 	 if(data.overview){
  				container.setTemplate(data.overview);
		 	 }else if(data.properties["jcr:overview"]){
  				container.setTemplate(data.properties["jcr:overview"]);
		 	 }else{
  				container.setTemplateElement("template_xdir_content_over_view");
		 	 }
		 	 container.processTemplate(data);
		 	 return;
			 this.filterReferenceLink(container);
		},
		displayOverview2: function(container,path,callback){			
			$.getJSON(path+".json?action=ajaxproperties",
		 		{timestamp:new Date().getTime()},
			   	  function(data){
			   		if(data.status==0){				
			   			$.xdir.page.displayOverview(container,data);
						 if(callback){
						 	callback();
						 }
			   		}else{
			   			alert("error status:"+ data.status);
			   		}
			});
		},
		generateFileOverview: function(data){
			var path=$.xdir.jcr.getPathFromData(data);
			path=path.substring(0,path.lastIndexOf("/"))+"/_/f"+path.substring(path.lastIndexOf("/"));
			var overviewHtml="<a target=\"_blank\" href=\""+path+"\">"+path+"</a>";
			if($.xdir.utils.isImageFile(path)){
				overviewHtml+="<br/><img src=\""+path+"\"/>";
			}else if($.xdir.utils.isTextFile(path)){
				overviewHtml+="<br/><iframe width=\"100%\" height=\"500\" src=\""+path+"\"/>";
			}
			return overviewHtml;
		},
		buildNodeLink: function(path,text){
			var _html="<a href=\""+path+".ajax\" onclick=\"return $.xdir.page.loadNodeLink(this);\">"+text+"</a>";
			return _html;
		},
		loadNodeLink: function(aElement){
			var path=$(aElement).attr("href");
			loadXdirNode(path);
			return false;
		},
		formatProperty: function(path,key,value){
			if(value && value!=null){
				if(key=="template" || key=="script" || key=="jcr:data"){
					return "<pre>"+$.xdir.utils.escapeHTML(value)+"</pre>";
				}else if(value.indexOf("<")<0 && /\n/.test(value)){
					return "<pre>"+value+"</pre>";
				}else{
					return value;
				}
			}
			return "";
						
		},//end of formatProperty
		filterReferenceLink: function(tableElment){
			$("tbody>tr>td.value",tableElment).each(function(){
				var tdElement=$(this);
				var value=tdElement.html();
				if(value.indexOf("<")<0 && value.indexOf("=")>0 && value.indexOf("&")<0){
					tdElement.html("");
					if(value.indexOf("path=")==0){
						value=value.substr(5);
					}
					createExpandableLink("?path="+encodeURIComponent(value),value.substr(value.indexOf("=")+1),"-1",tdElement);
				}
			});
		},
		addActionLink: function(actions,actionKey,defaultName,defaultTitle,defaultParams,requiredRole){
			if($.xdir.jcr.paths.length==1){
				if(actionKey!="new" && actionKey!="import"){
					return "";
				}
			}
			if(actions && actions[actionKey]){
				defaultName=actions[actionKey]["jcr:displayName"];
				if(!defaultName){
					return "";
				}
				defaultName=actions[actionKey]["jcr:displayName"];
				defaultTitle=actions[actionKey]["title"];
				defaultParams=actions[actionKey]["params"];
				requiredRole=actions[actionKey]["role"];
			}
			if(requiredRole && !$.xdir.jcr.hasRole(requiredRole)){
				return "";
			}
			
			var linkHtml="<a href=\""+$.xdir.jcr.path+".json?";
			linkHtml+="action=ajaxactionform&actionname="+actionKey+"&"+defaultParams+"\"";
			linkHtml+=" class=\"xdiractionlink\" title=\""+defaultTitle+"\">"+defaultName+"</a>";
            return linkHtml;
		},
		showReference: function(query,title,expanded){
			var container=$("<div></div>");
			$("#xdir_content_over_view").append(container);
			
			if(expanded && expanded=="expanded"){
				$.xdir.jcr.retriveReference(query,container,title);	
			}else{			
				var aElement=$("<div class=\"section_title\"><a href=\"#query\">&#9658;"+title+"</a></div>");
				container.append(aElement);
				$("a",aElement).click(function(){
					$.xdir.jcr.retriveReference(query,container,title);
					return false;
				});
			}
		},
		showQueryTable: function(query,title,expanded,columnNames,linkIndex,maxsize,noRelPath){	
			alert("depreciated! please use xdir.query.querySection");	
			var queryContainer=$("<div></div>");
			$.xdir.jcr.activecontainer.append(queryContainer);
			
			if(expanded && expanded=="expanded"){
				$.xdir.jcr.retriveQueryTable(query,queryContainer,title,columnNames,linkIndex,maxsize,noRelPath);	
			}else{
				var aElement=$("<div class=\"section_title\"><a href=\"#query\">&#9658;"+title+"</a></div>");
				queryContainer.append(aElement);
				$("a",aElement).click(function(){
					$.xdir.jcr.retriveQueryTable(query,queryContainer,title,columnNames,linkIndex,maxsize,noRelPath);
					return false;
				});
			}
		},
		embedChildNode: function(data,childKey){
			if($.xdir.jcr.hasChildNode(data,childKey)){
				var path=data.paths[data.paths.length-1].path+"/"+childKey;
		 		var queryContainer=$("<div></div>");
				$.xdir.jcr.activecontainer.append(queryContainer);
				this.embedNode(queryContainer,path);
			}
		},
		embedNode: function(nodeContainer,path){
				$.getJSON(path+".json?action=properties",
			 		{timestamp:new Date().getTime()},
				   	  function(data){
				   		if(data.status==0){
				 	 		$.xdir.page.displayOverview(nodeContainer,data);
				   		}else{
				   			alert("error status:"+ data.status);
				   		}
				});
		},
		compareTwoNodes: function(container,node1,node2){
		 	var node1Container=$("<table style=\"border-top:2px solid gray;\"><tr><td class=\"node1\" style=\"border-right:2px solid gray;vertical-align:top;\"></td><td class=\"node2\" style=\"vertical-align:top;\"></td></tr></table>");
			container.append(node1Container);
			var node2Container=$(".node2",node1Container);
			node1Container=$(".node1",node1Container);
			$.xdir.jcr.activecontainer=node1Container;
			$.xdir.page.displayOverview2(node1Container,node1,function(){
				$.xdir.jcr.activecontainer=node2Container;
				$.xdir.page.displayOverview2(node2Container,node2);
			});
		}
	};
})(jQuery);