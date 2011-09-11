

$(function() {

	if(initParams.contextpath.lastIndexOf("/")>10){
		initParams.contextpath=initParams.contextpath.substr(initParams.contextpath.lastIndexOf("/"));
	}else{
		initParams.contextpath="";
	}
	
	$.requireConfig.routeJs="${pageContext}/_/s/${plugins.cache.version}/";
	$.requireConfig.routeCss="${pageContext}/_/s/${plugins.cache.version}/";
	

	
	if (document.cookie.length>0){
		var allCookies=document.cookie.split(";");
		for(i in document.cookie.split(";")){
			var currentCookie=allCookies[i].split("=");
			if(currentCookie[0].replace(/^\s+|\s+$/g, '')=="username"){
				var username=unescape(currentCookie[1]);
				initParams.username=username.substr(1,username.length-2);
			}
		}
	}
	
 	var pluginsScripts=new Array();
 	for(i in $.xdir.plugins){
 		if(typeof $.xdir.plugins[i] == 'string'){
 			pluginsScripts.push($.xdir.plugins[i]);
 		}
 	}
 	$(pluginsScripts).each(function(key,value){
		//$.requireJs(value+"?timestamp="+pageSessionId);
	});
	
		
		$.xdir.init();
		
	
	
		$.validator.addMethod("nospace", function(value) { 
		  return !/\s/.test(value); 
		}, "No space allowed");
		$.validator.addMethod("noslash", function(value) { 
			if(value){
				return value.indexOf("/")<0;
			}
		  	return true; 
		}, "No slash allowed");
	
	    $("#xdir_paths").setTemplateElement("paths_template");
	    $("#xdir_content").setTemplateElement("content_template", null, {filter_data: false,filter_params:false});
		setupLoadingStatus();
		
		loadXdirNode($.xdir.jcr.path);
		
		$.xdir.browser.noneFireFoxWarning();
		
  
});

function loadXdirNode(path){
	if(path==pageContext){
		path+="/";
	}
	if(path.lastIndexOf(".ajax")>0){
		path=path.substring(0,path.length-5)+".json";
	}else if(path.lastIndexOf(".json")<0){
		path=path+".json";
	}
	$.getJSON(path,{timestamp:new Date().getTime()},
   	  function(data){
   		if(data.status==0){
			 $.xdir.jcr.update(data);
		 	 $("#xdir_content").processTemplate(data);
		 	 $("#xdir_paths").processTemplate(data);
			 $.xdir.jcr.activecontainer=$("#xdir_content_over_view");
			 var tempContainer=$("<div></div>");
			 $.xdir.jcr.activecontainer.append(tempContainer);
		 	 $.xdir.page.displayOverview(tempContainer,data);
		 	 $(".content_tabs > ul").tabs().bind('select.ui-tabs', function(e, ui) {
		 	 	var uiPanel=$(ui.panel);
		 	 	if(!uiPanel.hasClass("inited")){
		 	 		var panelId=uiPanel.attr("id");
			        uiPanel.addClass("inited");
			        if(panelId=="xdir_content_change_history"){
		 	 			$.xdir.jcr.showHistory(uiPanel);
		 	 		}else{
		 	 			$.xdir.jcr.activecontainer=uiPanel;
	    				uiPanel.setTemplateElement("template_"+panelId);
			 	 		uiPanel.processTemplate(data);
		 	 		}
		 	 	}
			 });
		 	 
		 	 initSearchFiled();
		 	 $("a.xdiractionlink").click(function () {
		 	 	var dialogTitle=$(this).attr("title");
 	 			if(!dialogTitle){
 	 				dialogTitle=$(this).text();
 	 			}
		 	 	triggerActionLink($(this).attr("href"),dialogTitle);
			    return false;
			 });
			    
			 
		 	 $("a.xdirnodelink").each(function () {
		 	 	bindNodeLink($(this));
			 });
		 	 $("#xdir_content a.expandableLink").each(function () {
		 	 	  var linkElement=$(this);
		 	 	  var plusLink=createExpandableLink(linkElement.attr("href"),linkElement.html(),linkElement.attr("title"),linkElement.parent());
		 	 	  if(linkElement.hasClass("expanded")){
		 	 	  	plusLink.click();
		 	 	  }
		 	 	  linkElement.remove();
			 });
		 	 $("#xdir_actions div.more_actions").hover(function () {
		 	 		var tempOffset=$(this).offset();
	   				tempOffset.top=tempOffset.top+18;
	   				tempOffset.left=tempOffset.left-35;
	   				$("div",$(this)).css(tempOffset).show();
		 	    },function () {
		 	 		$("div",$(this)).hide();
		 	 });
		 	 $("#xdir_paths button.xdirnodedropdown").click(function () {
				var path=$("a",$(this).parent()).attr("href");
		 	 	var linkElement=$(this);
		 	 	linkElement.blur();
		 	 	retriveChildrends(path,function(data){
	   				var divElment=$("<div class=\"dropdown\"></div>");
					linkElement.parent().prepend(divElment);
	   				var dropdownBox=$(".dropdown",linkElement.parent());
	   				var tempOffset=linkElement.parent().offset();
	   				tempOffset.top=tempOffset.top+18;
	   				dropdownBox.css(tempOffset);
	   				dropdownBox.hover(function(){},function(e){
	   					var position=dropdownBox.position();
	   					if(e.pageX<position.left || e.pageX>position.left+dropdownBox.width()+10 ||
	   					e.pageY<position.top || e.pageY>position.top+dropdownBox.height()+10){
	   						dropdownBox.remove();
	   					}
	   				});
		 	 		if(data.children){
						var ulElment=$("<ul class=\"nodeul\"></ul>");
						dropdownBox.append(ulElment);
						return ulElment;
		 	 		}else{
		 	 			divElment.html("empty");
		 	 		}
		 	 	});
			    return false;
			 });
   		}else{
   			alert("error status:"+ data.status);
   		}
   });
}

function triggerActionLink(targetUrl,dialogTitle){
 		panelBackground.show();
 		$("body").prepend("<div id=\"action_dialog\" class=\"flora\">Loading...</div>");
 		var dialogOptions=parseQuery(targetUrl);
 		dialogOptions.title=dialogTitle;
 		dialogOptions.position="header";
 		dialogOptions.close=function(){
	 			panelBackground.hide();
	 			$("#action_dialog").remove();
 			};
 		$("#action_dialog").dialog(dialogOptions);
 		
 		if(targetUrl.indexOf("jcr:")<0){
	 		$.getJSON(targetUrl,
	 		{timestamp:new Date().getTime()},
		   	  function(data){
		   		window.actionData=data;
		   		if(data.status==0){
		   			var actionContainer=$("#action_dialog");
		   			if(data.template){
		   				actionContainer.setTemplate(data.template);
		   				actionContainer.processTemplate(data);
		   				var dialogTitle=$(".action_form_form",actionContainer).attr("title");
		   				if(dialogTitle && dialogTitle!=""){
		   					$(".ui-dialog-title").html(dialogTitle);
		   				}				
		   			}else{
		   				actionContainer.setTemplateURL("${pageContext}/_/s/${plugins.cache.version}/jtemplates/actions/"+dialogOptions.actionname+".html?timestamp="+new Date().getTime());
		   				actionContainer.processTemplate(data);
		   			}
		   		}else{
		   			alert("error status:"+ data.status);
		   		}
		});
	}else{					
  			var actionContainer=$("#action_dialog");
  			actionContainer.setTemplateURL("${pageContext}/_/s/${plugins.cache.version}/jtemplates/actions/"+dialogOptions.actionname+".html?timestamp="+new Date().getTime());
  			actionContainer.processTemplate($.xdir.jcr);
	}
}

function filterExpandableLink(links){
	 links.each(function () {
 	 	  var linkElement=$(this);
 	 	  createExpandableLink(linkElement.attr("href"),linkElement.html(),linkElement.attr("title"),linkElement.parent(),null);
 	 	  linkElement.remove();
	 });
}

function retriveChildrends(path,callback,linkCallBack){
	if(path.lastIndexOf(".ajax")==path.length-5){
		path=path.substring(0,path.length-5);
	}
 	  $.getJSON(path+".json?action=ajaxchildren",
 		{timestamp:new Date().getTime()},
	   	  function(data){
	   		if(data.status==0){
	   				var ulElment=callback(data);
	   				if(data.children){
			   				$(data.children).each(function(){
				   				if(this.path.indexOf("jcr:")<0 || this.path.indexOf("jcr:")<this.path.lastIndexOf("/") || $.xdir.jcr.path.indexOf("jcr:")>0){
				   					var name=$.xdir.jcr.getNameFromNode(this);
					   				createExpandableLink(this.path,name,this.childcount,ulElment,linkCallBack);
				   				}
			   				});
	   				}
	   		}else{
	   			alert("error status:"+ data.status);
	   		}
	});
}


function bindNodePlusSign(linkElement,linkCallBack){
	linkElement.click(function(data){
		var linkElement=$(this);
		linkElement.blur();
		if(linkElement.hasClass("button_minus")){
			linkElement.addClass("button_plus");
			linkElement.removeClass("button_minus");
			$("ul",linkElement.parent()).remove();
		}else if(linkElement.hasClass("button_plus")){
				$("li:first",$(this).parent()).remove();
				var path=$("a",$(this).parent()).attr("href");
		 	 	retriveChildrends(path,function(data){
		 	 		if(data.children){
		 	 			linkElement.addClass("button_minus");
						linkElement.removeClass("button_plus");
						var ulElment=$("<ul class=\"nodeul\"></ul>");
						linkElement.parent().append(ulElment);
						return ulElment;
		 	 		}else{
		 	 			linkElement.addClass("button_blank");
						linkElement.removeClass("button_plus");
		 	 		}
		 	 	},linkCallBack);
			      return false;
		}
		return false;
   });
}

function bindNodeInfoSign(linkElement){
	linkElement.click(function(data){
		linkElement.blur();
		var path=$("a",$(this).parent()).attr("href");
		if(path.lastIndexOf(".ajax")==path.length-5){
			path=path.substring(0,path.length-5);
		}
		
 		var divElment=$("<div id=\"tips_div\" style=\"border:1px #50A029 solid;background:white;position:absolute;z-index:1001;padding:5px;\"></div>");
		$("body").append(divElment);
		var tipsBox=$("#tips_div");
		var tempOffset=linkElement.offset();
		tipsBox.css(tempOffset);
		tipsBox.hover(function(){},function(e){
			var position=tipsBox.position();
			if(e.pageX<position.left || e.pageX>position.left+tipsBox.width() ||
			e.pageY<position.top || e.pageY>position.top+tipsBox.height()){
				$("#tips_div").remove();
			}
		});	   			
		$.xdir.jcr.activecontainer=tipsBox;
		
		$.xdir.page.displayOverview2(tipsBox,path);
		return false;
   });
}

function bindNodeLink(linkElement,linkCallBack){
	linkElement.click(function () {
		if(linkCallBack){
			return linkCallBack(this);
		}else{
			loadXdirNode($(this).attr("href"));
		}
		return false;
    });
}
function createExpandableLink(path,name,childcount,container,linkCallBack){
	if(container.attr("tagName")!="UL"){
		var ulElment=$("<ul class=\"nodeul\"></ul>");
		container.append(ulElment);
		container=ulElment;
	}
	var liElement=$("<li></li>");
	container.append(liElement);
	
	var plusLink=$("<button class=\"xdir_ajax_button "+(childcount=="0"?"button_blank":"button_plus")+"\"/>");
	liElement.append(plusLink);
	bindNodePlusSign(plusLink,linkCallBack);
	var content="<a class=\"xdirnodelink\" href=\""+path+".ajax\">";
	content+=name;
	if(childcount!="0" && childcount!="-1"){
		content+=" ("+childcount+")";
	}
	content+="</a>";
	var nodeLink=$(content);
	liElement.append(nodeLink);
	bindNodeLink(nodeLink,linkCallBack);	
	
	var infoLink=$("<button class=\"xdir_ajax_button button_info\"/>");
	liElement.append(infoLink);
	bindNodeInfoSign(infoLink);
	return plusLink;
	
}

function refreshNodeView(){
	loadXdirNode($.xdir.jcr.path);
}

function parseQuery ( query ) {
   var Params = {};
   if ( ! query ) {return Params;}// return empty object
   if(query.indexOf("?")>=0){
   		query=query.substr(query.indexOf("?")+1);
   }
   var Pairs = query.split(/[;&]/);
   for ( var i = 0; i < Pairs.length; i++ ) {
      var KeyVal = Pairs[i].split('=');
      if ( ! KeyVal || KeyVal.length != 2 ) {continue;}
      var key = unescape( KeyVal[0] );
      var val = unescape( KeyVal[1] );
      val = val.replace(/\+/g, ' ');
      Params[key] = val;
   }
   return Params;
}


var panelBackground={
	count:0,
	show:function(){
		this.count=this.count+1;
		if(this.count==1){
			$("body").prepend("<div id=\"jquery_panel_bg\" style=\"position:fixed;z-index:9;top: 0px;left: 0px;height:100%;width:100%;background-color:#000;filter:alpha(opacity=75);-moz-opacity: 0.75;opacity: 0.75;\"> </div>");
		}
	},
	hide:function(){
		this.count=this.count-1;
		if(this.count==0){
			this.clear();
		}
	},
	clear:function(){
		this.count=0;
		$("#jquery_panel_bg").remove();
	}
}


function setupLoadingStatus(){
	$("body").prepend("<div id=\"jquery_loading_status\" style=\"position:fixed;z-index:9999;top:0px;left:0px;height:15px;width:100%;text-align:right;\">Loading...</div>");
	$("#jquery_loading_status").ajaxStart(function(){
	  $(this).show();
	});
	$("#jquery_loading_status").ajaxStop(function(){
	  $(this).hide();
	});
}
