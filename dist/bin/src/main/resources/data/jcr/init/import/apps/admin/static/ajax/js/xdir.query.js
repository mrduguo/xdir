(function($){
$.xdir.query = $.xdir.query || {};
$.xdir.query = {
			querySection: function(options){
				options = $.extend({
								query: null,
								hideEmptyQuery: false,
								linkable: true,
								columnNames: null,
								linkIndex: null,
								maxsize: 20,
								sortList: null,
								parentPath: true
						  }, options);
				
				if(!options.container){
					if(options.containerSelector){
						options.container=$(options.containerSelector);
					}else{
						options.container=$("<div class=\"section\"></div>");
						$.xdir.jcr.activecontainer.append(options.container);
					}
				}
				
				
				if(options.title){
					if(options.linkable){
						options.title=$("<div class=\"section_title\"><a href=\"#query\" class=\"section_link\">&#9658;"+options.title+"</a></div>");
						options.container.append(options.title);
						$("a",options.title).click(function(){
							$.xdir.query.displayQuerySection(options);
							return false;
						});
						return;
					}else{
						options.title=$("<div class=\"section_title\">"+options.title+"</div>");
						options.container.append(options.title);
					}					
				}				
				$.xdir.query.displayQuerySection(options);
			},
			displayQuerySection: function(options){
				var queryType;
	            if(options.query.indexOf("select ")==0){
	            	queryType="sqlquery";
	            }else{
	            	queryType="xpathquery";
	            }
	            $.getJSON(pageContext+"/.json?action=ajax"+queryType, {timestamp:new Date().getTime(),
	            		query:options.query,maxsize:options.maxsize+1}, 
		 			function(data){
				   		if(data.status==0){
				   			   var _totalCount=0;
				   			   if(data.results){
				   			   		if(options.displayData){
				   			   			options.displayData(data,options);
				   			   		}else{
				   			   			$.xdir.query.displayQueryTable(options,data);
				   			   		}
				   			   		options.container.append("<div class=\"section\"></div>");
				   			   		_totalCount=data.results.length;
				   				}				   				
				   				$.xdir.query.displayQueryTitle(options,_totalCount);
				   		}else{
				   			alert("error status:"+ data.status);
				   		}
				 });
			},
			displayQueryTitle: function(options,totalCount){
				if(options.title){
					if(totalCount==0 && options.hideEmptyQuery==true){
						options.container.html("");
					}else{
						var linkTitle=$("a.section_link",options.title);
						if(linkTitle){
							linkTitle.remove();
							options.title.append(linkTitle.text().substr(1));
						}
						if(totalCount>options.maxsize){
							options.title.append(" ("+options.maxsize+"+)");
						}else{
							options.title.append(" ("+totalCount+")");
						}
					}
					if(options.titleDisplayCallback){
						options.titleDisplayCallback();
					}
				}
			},
			displayQueryTable: function(options,data){					   			   		
					var _tempVar=options.query.toLowerCase();
   			   		if(options.parentPath && options.parentPath == true && _tempVar.indexOf("jcr:path like '")>0){
   			   			_tempVar=_tempVar.substr(_tempVar.indexOf("jcr:path like '"));
   			   			_tempVar=_tempVar.substr(_tempVar.indexOf("'")+1);
   			   			options.parentPath=_tempVar.substr(0,_tempVar.indexOf("'")-2);
   			   		}else{
   			   			options.parentPath=false;
   			   		}
   			   		
   			   		
   			   		var tempHtml="<table class=\"tablesorter\">";
   			   		if(options.tableHeaderConstructor){
   			   			tempHtml+=options.tableHeaderConstructor(options,data);
   			   		}else{
   			   			tempHtml+="<thead><tr>";
		   				if(options.columnNames){
		   					$(options.columnNames).each(function(){
				   				tempHtml+="<th>"+this+"</th>";
			   				});
		   				}else{
		   					_tempVar=options.query;
		   					_tempVar=_tempVar.substr(7,_tempVar.indexOf(" from ")-7).split(",");
		   					options.columnNames=_tempVar;
		   					$(_tempVar).each(function(){		
		   						var _tempValue=this;	   				
				   				if(_tempValue=="jcr:displayName"){
				   					tempHtml+="<th>Title</th>";
				   				}else if(_tempValue=="jcr:pathName"){
				   					tempHtml+="<th>Name</th>";
				   				}else{
				   					if(_tempValue.indexOf(":")>0){
				   						_tempValue=_tempValue.substr(_tempValue.indexOf(":")+1);
				   					}
				   					tempHtml+="<th>"+$.xdir.jcr.formatPropertyName(_tempValue)+"</th>";
				   				}
			   				});
		   				}
		   				if(options.parentPath){
		   					if(options.parentPathName){
		   						tempHtml+="<th>"+options.parentPathName+"</th>";
		   					}else{
			   					tempHtml+="<th>Relative Path</th>";
			   				}
		   				}
		   				tempHtml+="</tr></thead>";
	   				}
   			   		tempHtml+="<tbody></tbody>";
   			   		if(options.tableFooterConstructor){
   			   			tempHtml+=options.tableFooterConstructor(options,data);
   			   		}
   			   		tempHtml+="</table>";
   			   		
					var tableElment=$(tempHtml);
					options.container.append(tableElment);
					var tbodyElment=$("tbody",tableElment);
				    $(data.results).each(function(rowIndex,rowData){
		   				if(options.tableRowConstructor){
	   			   			options.tableRowConstructor(options,tbodyElment,rowIndex,rowData);
	   			   		}else{
			   				var trElment=$("<tr></tr>");
				    		var linkIndex=options.linkIndex || 0;
							$(rowData).each(function(index,value){							
								if(index<options.columnNames.length){
									if(value==null){
										value="";
									}
									if(linkIndex==index){
										var tdElment=$("<td></td>");
										trElment.append(tdElment);
										createExpandableLink(rowData[rowData.length-1],value,"-1",tdElment);
									}else{
										value=$.xdir.utils.formatTime(value);
										trElment.append("<td class=\"value\">"+value+"</td>");
									}
								}
			   				});
			   				if(options.parentPath){
			   					var relPath=rowData[rowData.length-1];
			   					relPath=relPath.substr(pagePath.length);
			   					relPath=relPath.substr(0,relPath.lastIndexOf("/")+1);
				   				trElment.append("<td>"+relPath+"</td>");
			   				}
			   				tbodyElment.append(trElment);
		   				}
	   				 });
 				     $.xdir.page.filterReferenceLink(tableElment);
	   				 $.xdir.utils.tablesorterByText(tableElment,options.sortList);
			}
	}
})(jQuery);