/**
 * <#--include "ajax/js/jquery_button.js"-->
 * $('.button').makebutton();
 * credit: http://stopdesign.com/archive/2009/02/04/recreating-the-button.html
 */

 (function($) {
	$.extend({
		makebutton: new function() {
			
			/* public methods */
			this.construct = function(settings) {
				return this.each(function() {					
					var $this = $(this);
					if($this.hasClass("btn")){
						return;
					}
					$this.addClass("btn");
					$this.wrapInner("<span><span></span></span>");				
					$this.click(function(e) {
						$this.blur();
					});
				});
			};
		}
	});
	
	// extend plugin scope
	$.fn.extend({
        makebutton: $.makebutton.construct
	});	
})(jQuery);