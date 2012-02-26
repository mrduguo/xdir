(function($){
	
	$.fn.inlineeditable = function(options) {
    // Add double click handler to all matching elements
    return this.each(function() {
      // Use default options, if necessary
      var settings = $.extend({}, options);
      var element = $(this);
      
      element.focus(function() {
        if (element.hasClass("editing")) {
        	element.addClass("inline_editable_edit_pending");
        }
      });
      
      element.dblclick(function() {
        // Prevent multiple clicks, and check if inplace editing is disabled
        if (element.hasClass("editing")) {
            return;
        }
        element.addClass("inline_editable_edit_pending");
        element.addClass("editing");
        element.old_html = element.html();
        
        element.attr({contentEditable:true});
        element.focus();

		element.blur(updateIfchanged);
        element.keyup(escapseKeyup);        
      });
      
      function updateIfchanged(event){
          if(element.html()!=element.old_html){	        
	        if(settings.onchange) {
	          	settings.onchange.apply(element,element.old_html);
	        }else{
	        	defaultOnchange();
	        }
	        element.old_html = element.html();
          }
          element.removeClass("inline_editable_edit_pending");
          return false;
      }
      
      function escapseKeyup(event){
          var keycode = event.which;          
          if(keycode == 27)	{	     // escape
            element.html(element.old_html);
          }
          return false;
      }
      
	  function unescapeHTML(txt) {
				if(txt && txt !=null)
					return txt.replace(/<br>/g,'\n').replace(/&amp;/g,'&').replace(/&gt;/g,'>').replace(/&lt;/g,'<').replace(/&quot;/g,'"').replace(/&#39;/g,"'");
	  }
      
      function defaultOnchange() {
      	var submitUrl=settings.submitUrl;
      	var action=settings.action;
      	var propertyName=settings.propertyName;
      	if(submitUrl==null){
      		submitUrl=window.location.href;
	        if(submitUrl.lastIndexOf(".")>0){
	        	submitUrl=submitUrl.substring(0,submitUrl.lastIndexOf("."))+".json";
	        }else if(submitUrl.indexOf(".ajax")>0){
	        	//submitUrl=$.xdir.jcr.path+".json";
	        }
      	}
      	if(propertyName==null){
      		propertyName=element.parent().prev().html()
      	}
      	if(action==null){
      		action="put"
      	}
        var params={
        			action:action,
        			propertiesName:propertyName,
        			propertiesValue:unescapeHTML(element.html())
        		   };
        element.addClass("loading_small");
        $.post(submitUrl, params, function(data){
        	element.removeClass("loading_small");
        	if(data && data.update && data.status!=0){
        		alert("update "+params.propertiesName+" failed:\n"+data.status);
        	}
        }, "json");
      };
    });    
  };

})(jQuery);