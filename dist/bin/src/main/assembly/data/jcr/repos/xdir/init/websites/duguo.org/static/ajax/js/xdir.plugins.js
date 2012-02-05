(function($){
$.xdir = $.xdir || {};
$.xdir.plugins = {
			lightbox: function(url,title) {
				if(!url){
					this._lightbox=$.xdir.utils.timestampString();
					return;
				}
				if(title){
					url=$.xdir.contextpath+"/_/s/"+url;
					return "<a href=\""+url+"\" target=\"_blank\" class=\"lightbox\" rel=\"lightbox_"+this._lightbox+"\" title=\""+title+"\"><img src=\""+url+"\" height=\"100\"/>"+title+"</a>";
				}else{
					if($.fn.lightbox){
						$("a[@rel=lightbox_"+$.xdir.plugins._lightbox+"]").lightbox();
					}else{
						$.requireJs("/js/jquery/jquery.lightbox.js?timestamp="+pageSessionId,function(){
							$("a[@rel=lightbox_"+$.xdir.plugins._lightbox+"]").lightbox();
						});
					}
					return;
				}
			}
		 };
})(jQuery);