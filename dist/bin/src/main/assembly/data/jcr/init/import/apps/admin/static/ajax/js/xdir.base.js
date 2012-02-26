(function($){

$.xdir.sessiontimestamp=new Date().getTime();
$.xdir.baseurl="http://localhost";
$.xdir.contextpath="/xdir";
$.xdir.init=function(s){
				this.baseurl=window.location.protocol+"//"+window.location.host;
				this.contextpath=initParams.contextpath;
				$.xdir.jcr.init(s);
			};

})(jQuery);