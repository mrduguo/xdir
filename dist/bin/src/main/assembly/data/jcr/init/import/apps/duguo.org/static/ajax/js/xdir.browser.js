(function($){
$.xdir.browser = $.xdir.browser || {};
$.xdir.browser = {
			noneFireFoxWarning: function(){
				if(navigator.userAgent.indexOf("Firefox")<0){
					$("body").append("<div style=\"font-weight:bold;color:red;\">XDir AJAX interface is developed and tested under Firefox, there are some known issuses when you authoring content in IE and Chrome. Reading is almost fine.<br/>Please use <a href=\"http://www.mozilla-europe.org/en/products/firefox/\" target=\"_blank\">Firefox</a>	before the browser compatibility test passed in none Firefox browser!</div>");
				}
			}
	}
})(jQuery);