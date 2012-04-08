(function($){
$.xdir = $.xdir || {};
$.xdir.actions = {
		bindSearchField: function(target,callback,nodeType){
			target.focus().autocomplete({delay:300,height:400,timeout:10000,cache:false, ajax_get : function(searchValue,autocomplateCallback){
				  var params={query:searchValue,timestamp:new Date().getTime()};
				  if(nodeType){
						params.nodeType=nodeType;
				  }
		          $.getJSON(pagePath+".json?action=suggest", params, 
		 			function(data){
				   		var results=[];
				   		if(data.status==0 && data.results){
						   $(data.results).each(function(){
				   				var info=this.path;
				   				if(info.length>48){
				   					info=info.substr(info.length-45);
				   					info="..."+info;
				   				}
				   				results.push({id:this.path, value:this.pathname, info:info, extra:this.path});
			   				});
				   		}else{
				   			//alert("error status:"+ data.status);
				   		}
				        autocomplateCallback(results);
					});
			}, callback:callback});
		}
};
})(jQuery);